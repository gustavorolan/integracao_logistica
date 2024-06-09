package com.luizalabs.integracao_logistica.core.order.service.impl


import com.luizalabs.integracao_logistica.core.dto.OrderBatchResponse
import com.luizalabs.integracao_logistica.core.dto.OrderMessageDto
import com.luizalabs.integracao_logistica.core.dto.UserResponse
import com.luizalabs.integracao_logistica.core.exceptions.OrderNotFoundException
import com.luizalabs.integracao_logistica.core.exceptions.OrderParseException
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderEntity
import com.luizalabs.integracao_logistica.core.order.repository.OrderRepository
import com.luizalabs.integracao_logistica.core.order.service.*
import com.luizalabs.integracao_logistica.infrastructure.messaging.order.OrderProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader


@Service
class OrderServiceImpl(
    private val orderProducer: OrderProducer,
    private val orderMapper: OrderMapper,
    private val orderRepository: OrderRepository,
    private val userService: UserService,
    private val productService: ProductService,
    private val orderBatchService: OrderBatchService
) : OrderService {

    companion object {
        private val SORT_DIRECTION = Sort.Direction.DESC
        private const val PAGE_SIZE = 10
        private const val PROPERTY_TO_SORT = "purchaseDate"
        private val orderRequest: (Int) -> PageRequest = { page ->
            PageRequest.of(
                page, PAGE_SIZE, SORT_DIRECTION,
                PROPERTY_TO_SORT
            )
        }
    }

    override fun insertOrders(file: MultipartFile): OrderBatchResponse {
        val orderBatchEntity = orderBatchService.save(OrderBatchEntity(success = true))

        CoroutineScope(Dispatchers.IO).launch {
            BufferedReader(InputStreamReader(file.inputStream)).use { reader ->
                reader.lines().skip(0).toList().forEachIndexed { lineNumber, line ->
                    try {
                        verifyLine(line)
                        orderProducer.send(orderMapper.parseLine(line, orderBatchEntity.id, lineNumber))
                    } catch (ex: Exception) {
                        orderBatchService.updateException(
                            orderLine = lineNumber,
                            orderBatchId = orderBatchEntity.id,
                            e = ex
                        )
                    }

                }
            }
        }

        return orderMapper.orderBatchEntityToResponse(orderBatchEntity)
    }

    override fun consumeOrderMessage(orderMessageDto: OrderMessageDto) {
        try {
            val userEntity =
                userService.upsertByUserExternalId(orderMapper.userDtoToUserEntity(orderMessageDto.userDto))

            val orderEntity =
                upsertByExternalId(orderMapper.orderMessageDtoToOrderEntity(orderMessageDto.orderDto, userEntity))

            productService.upsertByExternalIdAndValue(
                orderMapper.productDtoToProductEntity(
                    productDto = orderMessageDto.productDto,
                    orderEntity
                )
            )
        } catch (ex: Exception) {
            orderBatchService.updateException(
                orderLine = orderMessageDto.lineNumber,
                orderBatchId = orderMessageDto.orderBatchId,
                e = ex
            )
        }

    }

    override fun findAll(page: Int): List<UserResponse> {
        val pageRequest = orderRequest(page)

        return orderRepository.findAll(pageRequest)
            .groupBy { it.user }
            .map { orderMapper.orderEntityToUserResponse(it.key, it.value) }
    }

    override fun findByExternalId(externalId: Long): Any {
        val orderEntity = orderRepository.findByExternalId(externalId) ?: throw OrderNotFoundException()
        return orderMapper.orderEntityToUserResponse(orderEntity.user, listOf(orderEntity))
    }

    override fun findBatchById(orderBatchId: String): OrderBatchEntity {
        return orderBatchService.findById(orderBatchId)
    }

    private fun upsertByExternalId(orderEntity: OrderEntity): OrderEntity {

        val order = orderRepository.findByExternalId(orderEntity.externalId)
            ?.let {
                if (orderEntity.user != it.user) throw OrderParseException()
                orderEntity.copy(id = it.id)
            }
            ?: orderEntity

        return orderRepository.save(order)
    }

    private fun verifyLine(line: String) {
        if (line.length != 95) throw OrderParseException()
    }
}




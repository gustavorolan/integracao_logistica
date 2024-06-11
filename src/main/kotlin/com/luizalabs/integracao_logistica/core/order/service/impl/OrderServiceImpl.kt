package com.luizalabs.integracao_logistica.core.order.service.impl


import com.luizalabs.integracao_logistica.core.dto.OrderBatchCompleteResponse
import com.luizalabs.integracao_logistica.core.dto.OrderBatchResponse
import com.luizalabs.integracao_logistica.core.dto.OrderMessageDto
import com.luizalabs.integracao_logistica.core.dto.UserResponse
import com.luizalabs.integracao_logistica.core.exceptions.AlreadyExistsUserWithThisOrderParseException
import com.luizalabs.integracao_logistica.core.exceptions.OrderNotFoundException
import com.luizalabs.integracao_logistica.core.exceptions.OrderParseException
import com.luizalabs.integracao_logistica.core.extensions.logErrorAndReturnsException
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderBatchStatus
import com.luizalabs.integracao_logistica.core.model.OrderEntity
import com.luizalabs.integracao_logistica.core.order.repository.OrderRepository
import com.luizalabs.integracao_logistica.core.order.service.*
import com.luizalabs.integracao_logistica.infrastructure.messaging.order.OrderProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.util.UUID


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
                page, PAGE_SIZE, SORT_DIRECTION, PROPERTY_TO_SORT
            )
        }
    }

    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    override fun insertOrders(file: MultipartFile): OrderBatchResponse {
        val orderBatchEntity = orderBatchService.save(OrderBatchEntity(status = OrderBatchStatus.WAITING))

        CoroutineScope(Dispatchers.IO).launch {
            BufferedReader(InputStreamReader(file.inputStream)).use { reader ->
                val lines = reader.lines().skip(1).toList()
                lines.forEachIndexed { lineNumber, line ->
                    try {
                        sendLineAsMessage(line, orderBatchEntity, lineNumber, lines.size)
                        logger.info("Sent order message line: $lineNumber, orderBatchId: ${orderBatchEntity.id}.")
                    } catch (ex: Exception) {
                        orderBatchService.updateException(
                            orderLine = lineNumber,
                            orderBatchId = orderBatchEntity.id,
                            e = ex,
                            totalLines = lines.size
                        )
                        logger.error("Error order message line:$lineNumber, orderBatchId:${orderBatchEntity.id}.", ex)
                    }

                }
            }
        }

        return orderMapper.orderBatchEntityToResponse(orderBatchEntity)
    }


    private fun sendLineAsMessage(
        line: String, orderBatchEntity: OrderBatchEntity, lineNumber: Int, totalLines: Int
    ) {
        verifyLine(line)
        orderProducer.send(
            orderMapper.parseLine(
                line, orderBatchEntity.id, lineNumber, totalLines
            )
        )
    }

    override fun consumeOrderMessage(orderMessageDto: OrderMessageDto) {
        try {

            logger.info("Starting to consume order message line: ${orderMessageDto.lineNumber}, orderBatchId: ${orderMessageDto.orderBatchId}")

            val userEntity =
                userService.upsertByUserExternalId(orderMapper.userDtoToUserEntity(orderMessageDto.userDto))

            val orderEntity =
                upsertByExternalId(
                    orderMapper.orderMessageDtoToOrderEntity(
                        orderMessageDto.orderDto,
                        userEntity,
                        orderMessageDto.orderBatchId
                    )
                )


            productService.save(
                orderMapper.productDtoToProductEntity(
                    productDto = orderMessageDto.productDto, orderEntity
                )
            )

            updateSuccessful(orderMessageDto)

            logger.info("Consumed order message line successfully: ${orderMessageDto.lineNumber}, orderBatchId: ${orderMessageDto.orderBatchId}")

        } catch (ex: Exception) {
            orderBatchService.updateException(
                orderLine = orderMessageDto.lineNumber,
                orderBatchId = orderMessageDto.orderBatchId,
                e = ex,
                totalLines = orderMessageDto.totalLines
            )

            logger.error(
                "Error while trying consuming order message line: ${orderMessageDto.lineNumber}, orderBatchId: ${orderMessageDto.orderBatchId}.",
                ex
            )
        }

    }

    private fun updateSuccessful(orderMessageDto: OrderMessageDto) {
        // -1  its for index correction
        if (orderMessageDto.totalLines - 1 == orderMessageDto.lineNumber) {
            orderBatchService.updateSuccessful(
                orderLine = orderMessageDto.lineNumber,
                orderBatchId = orderMessageDto.orderBatchId,
                totalLines = orderMessageDto.totalLines
            )
        }
    }

    override fun findAll(page: Int, initialDate: LocalDate?, finalDate: LocalDate?): Page<UserResponse> {

        logger.info("Consulting Orders with these params: page: $page, initalDate: $initialDate, finalDate: $finalDate")

        val pageRequest = orderRequest(page)

        val ordersEntityPage =
            orderRepository.findAll(pageable = pageRequest, initialDate = initialDate, finalDate = finalDate)

        val userResponses =
            ordersEntityPage.content
                .groupBy { it.user }
                .map { orderMapper.orderEntityToUserResponse(it.key, it.value) }

        return PageImpl(userResponses, pageRequest, ordersEntityPage.totalElements)

    }

    override fun findByExternalId(externalId: Long): Any {
        logger.info("Consulting Order with externalId: $externalId")
        val orderEntity = orderRepository.findByExternalId(externalId)
            ?: throw logger.logErrorAndReturnsException(
                "Order externalId: $externalId was not found! ",
                OrderNotFoundException()
            )

        return orderMapper.orderEntityToUserResponse(orderEntity.user, listOf(orderEntity))
    }

    override fun findBatchById(orderBatchId: UUID): OrderBatchCompleteResponse {
        logger.info("Consulting OrderBatch with orderBatchId: $orderBatchId")
        val orderBatchEntity = orderBatchService.findById(orderBatchId)
        return orderMapper.orderBatchEntityToOrderBatchCompleteResponse(orderBatchEntity)
    }

    private fun upsertByExternalId(orderEntity: OrderEntity): OrderEntity {
        val order = orderRepository.findByExternalId(orderEntity.externalId)?.let {
            if (orderEntity.user != it.user) throw logger.logErrorAndReturnsException(
                "Already exists a user with this order.",
                AlreadyExistsUserWithThisOrderParseException()
            )
            orderEntity.copy(id = it.id)
        } ?: orderEntity

        return orderRepository.save(order)
    }

    private fun verifyLine(line: String) {
        if (line.length != 95) throw logger.logErrorAndReturnsException(
            "Message line: $line has the format incorrect",
            OrderParseException()
        )
    }

}




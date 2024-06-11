package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.dto.*
import com.luizalabs.integracao_logistica.core.model.*
import com.luizalabs.integracao_logistica.core.order.service.OrderMapper
import org.springframework.stereotype.Component
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class OrderMapperImpl() : OrderMapper {

    override fun parseLine(line: String, orderBatchId: UUID, lineNumber: Int, totalLines: Int): OrderMessageDto {

        val userId = line.substring(0, 10).trim().toLong()
        val name = line.substring(10, 55).trim()
        val orderId = line.substring(55, 65).toLong()
        val productId = line.substring(65, 75).toLong()
        val productValue = line.substring(75, 87).trim().toBigDecimalRounded()
        val purchaseDate = line.substring(87, 95).trim().toLocalDate()

        val userDto = UserDto(userId = userId, name = name)
        val order = OrderDto(orderId = orderId, date = purchaseDate)
        val productDto = ProductDto(productId = productId, value = productValue)

        return OrderMessageDto(
            userDto = userDto,
            orderDto = order,
            productDto = productDto,
            orderBatchId = orderBatchId,
            lineNumber = lineNumber,
            totalLines = totalLines,
        )

    }

    override fun orderMessageDtoToOrderEntity(orderDto: OrderDto, userEntity: UserEntity, orderBatchId: UUID): OrderEntity {
        return OrderEntity(externalId = orderDto.orderId, user = userEntity, purchaseDate = orderDto.date, orderBatchId = orderBatchId)
    }

    override fun productDtoToProductEntity(productDto: ProductDto, orderEntity: OrderEntity): ProductEntity =
        ProductEntity(externalId = productDto.productId, value = productDto.value, order = orderEntity)

    override fun userDtoToUserEntity(userDto: UserDto): UserEntity =
        UserEntity(externalId = userDto.userId, name = userDto.name)

    private fun String.toLocalDate(): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return LocalDate.parse(this, formatter)
    }

    override fun orderBatchEntityToResponse(orderBatchEntity: OrderBatchEntity) =
        OrderBatchResponse(orderBatchId = orderBatchEntity.id.toString())


    override fun orderEntityToUserResponse(
        userEntity: UserEntity, orderWithProducts: List<OrderEntity>
    ): UserResponse = UserResponse(
        user_id = userEntity.externalId,
        name = userEntity.name,
        orders = orderWithProducts.map { it.toOrderResponse() }
    )

    override fun orderBatchEntityToOrderBatchCompleteResponse(orderBatchEntity: OrderBatchEntity) =
        OrderBatchCompleteResponse(orderBatchId = orderBatchEntity.id.toString(),
            status = orderBatchEntity.status,
            totalErrorLines = orderBatchEntity.totalErrorLines,
            totalLines = orderBatchEntity.totalLines,
            metadatas = orderBatchEntity.ordersMetadata.map { it.toMetadataResponse() })

    private fun OrderEntity.toOrderResponse(): OrderResponse = OrderResponse(order_id = this.externalId,
        total = this.products.sumOf { it.value },
        date = this.purchaseDate,
        products = this.products.map { it.toProductResponse() })

    private fun ProductEntity.toProductResponse(): ProductResponse =
        ProductResponse(product_id = this.externalId, value = this.value)

    private fun OrderMetadataEntity.toMetadataResponse() =
        OrderMetadataResponse(errorLine = this.errorLine, errorMessage = this.errorMessage)

    private fun String.toBigDecimalRounded() = this.toBigDecimal().setScale(2, RoundingMode.DOWN)
}
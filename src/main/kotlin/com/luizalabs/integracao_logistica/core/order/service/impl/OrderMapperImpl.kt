package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.dto.*
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderEntity
import com.luizalabs.integracao_logistica.core.model.ProductEntity
import com.luizalabs.integracao_logistica.core.model.UserEntity
import com.luizalabs.integracao_logistica.core.order.service.OrderMapper
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class OrderMapperImpl() : OrderMapper {

    override fun parseLine(line: String, orderBatchId: String, lineNumber: Int): OrderMessageDto {

        val userId = line.substring(0, 10).trim().toLong()
        val name = line.substring(10, 55).trim()
        val orderId = line.substring(55, 65).toLong()
        val productId = line.substring(65, 75).toLong()
        val productValue = line.substring(75, 87).trim().toBigDecimal()
        val purchaseDate = line.substring(87, 95).trim().toLocalDate()

        val userDto = UserDto(userId = userId, name = name)
        val order = OrderDto(orderId = orderId, date = purchaseDate)
        val productDto = ProductDto(productId = productId, value = productValue)

        return OrderMessageDto(
            userDto = userDto,
            orderDto = order,
            productDto = productDto,
            orderBatchId = orderBatchId,
            lineNumber = lineNumber
        )

    }

    override fun orderMessageDtoToOrderEntity(orderDto: OrderDto, userEntity: UserEntity): OrderEntity {
        return OrderEntity(externalId = orderDto.orderId, user = userEntity, purchaseDate = orderDto.date)
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
        OrderBatchResponse(orderBatchId = orderBatchEntity.id)


    override fun orderEntityToUserResponse(
        userEntity: UserEntity,
        orderWithProducts: List<OrderEntity>
    ): UserResponse =
        UserResponse(
            user_id = userEntity.externalId,
            name = userEntity.name,
            orders = orderWithProducts.map { it.toOrderResponse() }
        )

    private fun OrderEntity.toOrderResponse(): OrderResponse =
        OrderResponse(
            order_id = this.externalId,
            total = this.products.sumOf { it.value },
            date = this.purchaseDate,
            products = this.products.map { it.toProductResponse() }
        )

    private fun ProductEntity.toProductResponse(): ProductResponse =
        ProductResponse(product_id = this.externalId, value = this.value)
}
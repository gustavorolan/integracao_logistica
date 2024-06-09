package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.dto.*
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderEntity
import com.luizalabs.integracao_logistica.core.model.ProductEntity
import com.luizalabs.integracao_logistica.core.model.UserEntity

interface OrderMapper {
    fun parseLine(line: String, orderBatchId: String, lineNumber: Int): OrderMessageDto
    fun productDtoToProductEntity(productDto: ProductDto, orderEntity: OrderEntity): ProductEntity
    fun orderMessageDtoToOrderEntity(orderDto: OrderDto, userEntity: UserEntity): OrderEntity
    fun userDtoToUserEntity(userDto: UserDto): UserEntity
    fun orderEntityToUserResponse(
        userEntity: UserEntity,
        orderWithProducts: List<OrderEntity>
    ): UserResponse

    fun orderBatchEntityToResponse(orderBatchEntity: OrderBatchEntity): OrderBatchResponse
}
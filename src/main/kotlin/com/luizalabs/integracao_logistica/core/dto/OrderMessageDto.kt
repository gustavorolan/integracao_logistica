package com.luizalabs.integracao_logistica.core.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class OrderMessageDto(
    val userDto: UserDto,
    val orderDto: OrderDto,
    val productDto: ProductDto,
    val orderBatchId: UUID,
    val lineNumber:Int,
    val totalLines:Int
)

data class UserDto(
    val userId: Long,
    val name: String,
)

data class OrderDto(
    val orderId: Long,
    val date: LocalDate,
)

data class ProductDto(
    val productId: Long,
    val value: BigDecimal,
)
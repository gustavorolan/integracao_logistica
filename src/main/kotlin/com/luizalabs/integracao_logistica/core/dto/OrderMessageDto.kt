package com.luizalabs.integracao_logistica.core.dto

import java.math.BigDecimal
import java.time.LocalDate

data class OrderMessageDto(
    val userDto: UserDto,
    val orderDto: OrderDto,
    val productDto: ProductDto,
    val orderBatchId: String,
    val lineNumber:Int
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
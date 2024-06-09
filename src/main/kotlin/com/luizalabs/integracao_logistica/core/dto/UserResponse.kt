package com.luizalabs.integracao_logistica.core.dto

import java.math.BigDecimal
import java.time.LocalDate


data class ProductResponse(
    val product_id: Long,
    val value: BigDecimal
)


data class OrderResponse(
    val order_id: Long,
    val total: BigDecimal,
    val date: LocalDate,
    val products: List<ProductResponse>
)


data class UserResponse(
    val user_id: Long,
    val name: String,
    val orders: List<OrderResponse>
)


package com.luizalabs.integracao_logistica.factory

import com.luizalabs.integracao_logistica.core.dto.OrderResponse
import com.luizalabs.integracao_logistica.core.dto.ProductResponse
import com.luizalabs.integracao_logistica.core.dto.UserResponse
import java.math.BigDecimal

class UserResponseFactory {
    companion object {
        fun getUserResponseWithFourOrders(): UserResponse {
            val productResponse = ProductResponse(
                product_id = 0, value = BigDecimal.TEN
            )

            val orderResponse = OrderResponse(
                order_id = 1, total = BigDecimal.TEN, date = OrderFactory.localDate(), products = listOf(productResponse)
            )

            return UserResponse(
                user_id = 0,
                name = "Generic Name",
                orders = listOf(orderResponse, orderResponse, orderResponse, orderResponse)
            )
        }

        fun getUserResponseWithOneOrder(): UserResponse {
            val productResponse = ProductResponse(
                product_id = 0, value = BigDecimal.TEN
            )

            val orderResponse = OrderResponse(
                order_id = 1, total = BigDecimal.TEN, date = OrderFactory.localDate(), products = listOf(productResponse)
            )

            return UserResponse(
                user_id = 0,
                name = "Generic Name",
                orders = listOf(orderResponse)
            )
        }
    }
}
package com.luizalabs.integracao_logistica.factory

import com.luizalabs.integracao_logistica.core.dto.ProductDto
import com.luizalabs.integracao_logistica.core.model.OrderEntity
import com.luizalabs.integracao_logistica.core.model.ProductEntity
import java.math.BigDecimal
import java.util.*

class ProductFactory {
    companion object {
        fun getEntity(
            id: UUID = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea32"),
            externalId: Long = 0,
            value: BigDecimal = BigDecimal.TEN,
            orderEntity: OrderEntity = OrderEntity()
        ) =
            ProductEntity(id = id, externalId = externalId, value = value, orderEntity)

        fun getDto(productId: Long = 0, value: BigDecimal = BigDecimal.TEN) =
            ProductDto(productId = productId, value = value)
    }
}
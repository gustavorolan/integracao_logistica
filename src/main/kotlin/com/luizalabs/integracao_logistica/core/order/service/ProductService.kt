package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.model.ProductEntity
import java.util.UUID

interface ProductService {
    fun save(productEntity: ProductEntity): ProductEntity
    fun findByOrderIdIn(ordersIds: List<UUID>): List<ProductEntity>
}
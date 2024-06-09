package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.model.ProductEntity

interface ProductService {
    fun upsertByExternalIdAndValue(productEntity: ProductEntity): ProductEntity
    fun findByOrderIdIn(ordersIds: List<String>): List<ProductEntity>
}
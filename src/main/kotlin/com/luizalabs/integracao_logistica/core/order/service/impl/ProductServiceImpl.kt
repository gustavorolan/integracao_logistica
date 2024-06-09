package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.model.ProductEntity
import com.luizalabs.integracao_logistica.core.order.repository.ProductRepository
import com.luizalabs.integracao_logistica.core.order.service.ProductService
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
) : ProductService {

    override fun upsertByExternalIdAndValue(productEntity: ProductEntity): ProductEntity {

        val product = productRepository.findByExternalIdAndValueAndOrderId(productEntity.externalId, productEntity.value, productEntity.order.id)
            ?.let { productEntity.copy(id = it.id) } ?: productEntity

        return productRepository.save(product)
    }

    override fun findByOrderIdIn(ordersIds: List<String>): List<ProductEntity> =
        productRepository.findByOrderIdIn(ordersIds)
}
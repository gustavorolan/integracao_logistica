package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.model.ProductEntity
import com.luizalabs.integracao_logistica.core.order.repository.ProductRepository
import com.luizalabs.integracao_logistica.core.order.service.ProductService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
) : ProductService {

    override fun save(productEntity: ProductEntity): ProductEntity =
        productRepository.save(productEntity)


    override fun findByOrderIdIn(ordersIds: List<UUID>): List<ProductEntity> =
        productRepository.findByOrderIdIn(ordersIds)
}
package com.luizalabs.integracao_logistica.core.order.repository

import com.luizalabs.integracao_logistica.core.model.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
interface ProductRepository : JpaRepository<ProductEntity, UUID> {
    fun findByExternalIdAndValueAndOrderId(externalId: Long, value: BigDecimal, orderId: UUID): ProductEntity?
    fun findByOrderIdIn(orderIds: List<UUID>): List<ProductEntity>
}
package com.luizalabs.integracao_logistica.core.order.repository

import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderBatchRepository : JpaRepository<OrderBatchEntity, UUID> {
}
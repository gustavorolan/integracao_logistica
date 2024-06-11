package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import java.util.UUID

interface OrderBatchService {

    fun save(orderBatchEntity: OrderBatchEntity): OrderBatchEntity
    fun findById(orderBatchId: UUID): OrderBatchEntity
    fun updateSuccessful(orderLine: Int, orderBatchId: UUID, totalLines: Int)
    fun updateException(orderLine: Int, orderBatchId: UUID, e: Exception, totalLines: Int): OrderBatchEntity
}
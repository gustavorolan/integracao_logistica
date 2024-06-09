package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity

interface OrderBatchService {

    fun save(orderBatchEntity: OrderBatchEntity): OrderBatchEntity
    fun updateException(orderLine: Int, orderBatchId: String, e: Exception): OrderBatchEntity
    fun findById(orderBatchId: String): OrderBatchEntity
}
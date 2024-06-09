package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.exceptions.OrderBatchNotFoundException
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderMetadataEntity
import com.luizalabs.integracao_logistica.core.order.repository.OrderBatchRepository
import com.luizalabs.integracao_logistica.core.order.service.OrderBatchService
import com.luizalabs.integracao_logistica.core.order.service.OrderMetadataService
import org.springframework.stereotype.Service

@Service
class OrderBatchServiceImpl(
    private val orderBatchRepository: OrderBatchRepository,
    private val orderMetadataService: OrderMetadataService
) : OrderBatchService {
    override fun updateException(orderLine: Int, orderBatchId: String, e: Exception): OrderBatchEntity {

        val orderBatchEntity = findById(orderBatchId)

        saveMetadata(orderLine, e, orderBatchEntity)

        val orderException = orderBatchEntity.copy(
            success = false,
            //Increasing totalErrors by one
            totalErrorLines = orderBatchEntity.totalErrorLines + 1
        )

        return orderBatchRepository.save(orderException)
    }

    private fun saveMetadata(
        orderLine: Int,
        e: Exception,
        orderBatchEntity: OrderBatchEntity
    ) {

        val orderMetadataEntity = OrderMetadataEntity(
            //Index correction to show correct line
            errorLine = orderLine + 1,
            errorMessage = e.message.toString(),
            orderBatch = orderBatchEntity
        )

        orderMetadataService.save(orderMetadataEntity)
    }

    override fun findById(orderBatchId: String): OrderBatchEntity =
        orderBatchRepository.findById(orderBatchId)
            .orElseThrow { OrderBatchNotFoundException() }

    override fun save(orderBatchEntity: OrderBatchEntity): OrderBatchEntity =
        orderBatchRepository.save(orderBatchEntity)
}
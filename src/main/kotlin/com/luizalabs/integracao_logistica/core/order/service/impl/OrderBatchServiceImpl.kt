package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.exceptions.OrderBatchNotFoundException
import com.luizalabs.integracao_logistica.core.extensions.logErrorAndReturnsException
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderBatchStatus
import com.luizalabs.integracao_logistica.core.model.OrderMetadataEntity
import com.luizalabs.integracao_logistica.core.order.repository.OrderBatchRepository
import com.luizalabs.integracao_logistica.core.order.service.OrderBatchService
import com.luizalabs.integracao_logistica.core.order.service.OrderMetadataService
import com.luizalabs.integracao_logistica.core.order.service.OrderService
import org.jetbrains.annotations.VisibleForTesting
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrderBatchServiceImpl(
    private val orderBatchRepository: OrderBatchRepository,
    private val orderMetadataService: OrderMetadataService
) : OrderBatchService {

    private val logger = LoggerFactory.getLogger(OrderBatchService::class.java)

    override fun updateException(orderLine: Int, orderBatchId: UUID, e: Exception, totalLines: Int): OrderBatchEntity {

        val orderBatchEntity = findById(orderBatchId)

        saveMetadata(orderLine, e, orderBatchEntity)

        val orderException = orderBatchEntity.copy(
            status = OrderBatchStatus.FAILED,
            //Increasing totalErrors by one
            totalErrorLines = orderBatchEntity.totalErrorLines + 1,
            totalLines = totalLines
        )


        return orderBatchRepository.save(orderException)
    }

    override fun updateSuccessful(orderLine: Int, orderBatchId: UUID, totalLines: Int) {

        val orderBatchEntity = findById(orderBatchId)

        if (orderBatchEntity.status != OrderBatchStatus.FAILED) {
            val orderBatchEntityToSave = orderBatchEntity.copy(
                status = OrderBatchStatus.SUCCESSFUL,
                totalLines = totalLines
            )
            orderBatchRepository.save(orderBatchEntityToSave)

            logger.info("OrderBatch has been inserted successfully orderBatchId: $orderBatchId")
        }
    }

    fun saveMetadata(
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

    override fun findById(orderBatchId: UUID): OrderBatchEntity =
        orderBatchRepository.findById(orderBatchId)
            .orElseThrow { logger.logErrorAndReturnsException("Order not found with this orderBatchId: $orderBatchId.",OrderBatchNotFoundException()) }

    override fun save(orderBatchEntity: OrderBatchEntity): OrderBatchEntity =
        orderBatchRepository.save(orderBatchEntity)
}
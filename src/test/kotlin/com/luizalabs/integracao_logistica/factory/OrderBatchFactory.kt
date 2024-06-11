package com.luizalabs.integracao_logistica.factory

import com.luizalabs.integracao_logistica.core.dto.OrderBatchCompleteResponse
import com.luizalabs.integracao_logistica.core.dto.OrderBatchResponse
import com.luizalabs.integracao_logistica.core.dto.OrderMetadataResponse
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderBatchStatus
import com.luizalabs.integracao_logistica.core.model.OrderMetadataEntity
import java.util.UUID

class OrderBatchFactory {
    companion object {
        fun get(
            id: UUID = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea32"),
            status: OrderBatchStatus = OrderBatchStatus.SUCCESSFUL,
            totalErrorLines: Int = 0,
            ordersMetadata: MutableList<OrderMetadataEntity> = mutableListOf(),
            totalLines: Int = 0
        ) = OrderBatchEntity(
            id = id,
            status = status,
            totalErrorLines = totalErrorLines,
            ordersMetadata = ordersMetadata,
            totalLines = totalLines
        )

        fun getResponse(
            orderBatchId: String = "584f9284-72ee-4045-b111-7a4e6599ea32",
            status: OrderBatchStatus = OrderBatchStatus.SUCCESSFUL,
            totalLines: Int = 0,
            totalErrorLines: Int = 0,
            metadatas: List<OrderMetadataResponse> = mutableListOf()
        ) = OrderBatchCompleteResponse(
            orderBatchId = orderBatchId,
            status = status,
            totalLines = totalLines,
            totalErrorLines = totalErrorLines,
            metadatas = metadatas
        )

    }
}
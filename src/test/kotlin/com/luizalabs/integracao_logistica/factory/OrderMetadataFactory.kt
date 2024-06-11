package com.luizalabs.integracao_logistica.factory

import com.luizalabs.integracao_logistica.core.dto.OrderMetadataResponse
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderMetadataEntity
import java.util.UUID

class OrderMetadataFactory {
    companion object {
        fun get(
            id: UUID = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea35"),
            errorLine: Int = 0,
            errorMessage: String = "Generic Message",
            orderBatch: OrderBatchEntity = OrderBatchFactory.get()
        ) = OrderMetadataEntity(
            id = id,
            errorLine = errorLine,
            errorMessage = errorMessage,
            orderBatch = orderBatch
        )

        fun getResponse(
            errorLine: Int = 0,
            errorMessage: String = "Generic Message",
        ) = OrderMetadataResponse(
            errorLine = errorLine,
            errorMessage = errorMessage,
            )
    }
}
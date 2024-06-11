package com.luizalabs.integracao_logistica.core.dto

import com.luizalabs.integracao_logistica.core.model.OrderBatchStatus
import jakarta.persistence.Id
import java.util.*

data class OrderBatchResponse(
    val orderBatchId: String
)

data class OrderBatchCompleteResponse(
    val orderBatchId: String,
    val status: OrderBatchStatus,
    val totalErrorLines: Int,
    val totalLines: Int,
    val metadatas: List<OrderMetadataResponse>
)


data class OrderMetadataResponse(
    val errorLine: Int,
    val errorMessage: String,
)
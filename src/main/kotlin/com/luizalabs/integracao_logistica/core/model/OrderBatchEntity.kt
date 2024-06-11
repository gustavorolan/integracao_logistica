package com.luizalabs.integracao_logistica.core.model

import jakarta.persistence.*

import java.util.*

@Entity
data class OrderBatchEntity(
    @Id val id: UUID = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    val status: OrderBatchStatus = OrderBatchStatus.WAITING,

    val totalErrorLines: Int = 0,

    val totalLines: Int = 0,

    @OneToMany(mappedBy = "orderBatch", cascade = [(CascadeType.ALL)])
    val ordersMetadata: MutableList<OrderMetadataEntity> = mutableListOf(),
)

enum class OrderBatchStatus {
    WAITING, FAILED, SUCCESSFUL
}



package com.luizalabs.integracao_logistica.core.model

import jakarta.persistence.*

import java.util.*

@Entity
data class OrderBatchEntity(
    @Id val id: String = UUID.randomUUID().toString(),

    val success: Boolean = false,

    val totalErrorLines: Int = 0,

    @OneToMany(mappedBy = "orderBatch", cascade = [(CascadeType.ALL)])
    val ordersMetadata: MutableList<OrderMetadataEntity> = mutableListOf(),
)



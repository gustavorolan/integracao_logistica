package com.luizalabs.integracao_logistica.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
data class OrderMetadataEntity(
    @Id val id: String = UUID.randomUUID().toString(),

    val errorLine: Int = 0,

    val errorMessage: String = "",

    @ManyToOne
    @JoinColumn(name = "order_batch_id")
    @JsonIgnore
    val orderBatch: OrderBatchEntity = OrderBatchEntity(),
)



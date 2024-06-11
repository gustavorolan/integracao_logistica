package com.luizalabs.integracao_logistica.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
data class ProductEntity(

    @Id val id: UUID = UUID.randomUUID(),

    val externalId: Long = Long.MIN_VALUE,

    val value: BigDecimal = BigDecimal.ZERO,

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    val order: OrderEntity = OrderEntity()
)
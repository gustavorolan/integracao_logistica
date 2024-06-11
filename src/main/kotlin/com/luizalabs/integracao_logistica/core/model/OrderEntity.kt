package com.luizalabs.integracao_logistica.core.model

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
data class OrderEntity(

    @Id val id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    val externalId: Long = Long.MIN_VALUE,

    @JoinColumn(name = "user_id")
    @ManyToOne
    val user: UserEntity = UserEntity(),

    val purchaseDate: LocalDate = LocalDate.now(),

    @OneToMany(mappedBy = "order", cascade = [(CascadeType.ALL)])
    val products: MutableList<ProductEntity> = mutableListOf(),

    val orderBatchId: UUID = UUID.randomUUID(),

    )



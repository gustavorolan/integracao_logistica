package com.luizalabs.integracao_logistica.core.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.LocalDate

import java.util.UUID

@Entity
data class OrderEntity(

    @Id
    val id: String= UUID.randomUUID().toString(),

    @Column(unique = true)
    val externalId: Long = Long.MIN_VALUE,

    @JoinColumn(name = "user_id")
    @ManyToOne
    val user: UserEntity = UserEntity(),

    val purchaseDate: LocalDate = LocalDate.now(),

    @OneToMany(mappedBy = "order", cascade = [(CascadeType.ALL)])
    val products: MutableList<ProductEntity> = mutableListOf(),

    )



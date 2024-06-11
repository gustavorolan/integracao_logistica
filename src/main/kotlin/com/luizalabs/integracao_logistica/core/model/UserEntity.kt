package com.luizalabs.integracao_logistica.core.model


import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class UserEntity(

    @Id val id: UUID = UUID.randomUUID(),

    val externalId: Long = Long.MIN_VALUE,

    val name: String = String(),
)

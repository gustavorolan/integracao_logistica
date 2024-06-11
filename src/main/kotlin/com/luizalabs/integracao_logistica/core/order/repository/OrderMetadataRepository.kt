package com.luizalabs.integracao_logistica.core.order.repository

import com.luizalabs.integracao_logistica.core.model.OrderMetadataEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderMetadataRepository : JpaRepository<OrderMetadataEntity, UUID>
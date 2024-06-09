package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.model.OrderMetadataEntity
import com.luizalabs.integracao_logistica.core.order.repository.OrderMetadataRepository
import com.luizalabs.integracao_logistica.core.order.service.OrderMetadataService
import org.springframework.stereotype.Service

@Service
class OrderMetadataServiceImpl(
    private val orderMetadataRepository: OrderMetadataRepository
) : OrderMetadataService {
    override fun save(orderMetadataEntity: OrderMetadataEntity): OrderMetadataEntity {
        return orderMetadataRepository.save(orderMetadataEntity)
    }
}
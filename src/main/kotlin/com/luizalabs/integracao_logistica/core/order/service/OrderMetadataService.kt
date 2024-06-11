package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.model.OrderMetadataEntity

interface OrderMetadataService {
    fun save(orderMetadataEntity: OrderMetadataEntity): OrderMetadataEntity
}
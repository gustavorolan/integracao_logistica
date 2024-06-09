package com.luizalabs.integracao_logistica.infrastructure.messaging.order

import com.luizalabs.integracao_logistica.core.dto.OrderMessageDto

interface OrderProducer {
    fun send(orderMessageDto: OrderMessageDto)
}
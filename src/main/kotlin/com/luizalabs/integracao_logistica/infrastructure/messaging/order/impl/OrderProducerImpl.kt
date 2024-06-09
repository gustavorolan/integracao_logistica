package com.luizalabs.integracao_logistica.infrastructure.messaging.order.impl

import com.luizalabs.integracao_logistica.infrastructure.messaging.order.OrderProducer
import com.luizalabs.integracao_logistica.core.dto.OrderMessageDto
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service


@Service
class OrderProducerImpl(
    private val rabbitTemplate: RabbitTemplate
) : OrderProducer {
    override fun send(orderMessageDto: OrderMessageDto) {
        rabbitTemplate.convertAndSend("", "order", orderMessageDto);
    }
}


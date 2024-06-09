package com.luizalabs.integracao_logistica.infrastructure.messaging.order.impl

import com.luizalabs.integracao_logistica.infrastructure.messaging.order.OrderConsumer
import com.luizalabs.integracao_logistica.core.dto.OrderMessageDto
import com.luizalabs.integracao_logistica.core.order.service.OrderService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service


@Service
class OrderConsumerImpl(
    private val rabbitTemplate: RabbitTemplate,
    private val orderService: OrderService
) : OrderConsumer {

    @RabbitListener(queues = ["\${spring.rabbitmq.queue.order.name}"])
    override fun consume(orderMessageDto: OrderMessageDto) {
        orderService.consumeOrderMessage(orderMessageDto)
    }
}
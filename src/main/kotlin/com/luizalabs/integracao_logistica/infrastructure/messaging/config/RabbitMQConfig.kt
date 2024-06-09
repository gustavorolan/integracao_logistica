package com.luizalabs.integracao_logistica.infrastructure.messaging.config


import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig (
    @Value("\${spring.rabbitmq.queue.order.name}")
    private val orderQueue:String
) {

    @Bean
    fun jsonMessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun orderQueue(): Queue {
        return Queue(orderQueue)
    }
}
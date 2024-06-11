package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.dto.OrderBatchCompleteResponse
import com.luizalabs.integracao_logistica.core.dto.OrderBatchResponse
import com.luizalabs.integracao_logistica.core.dto.OrderMessageDto
import com.luizalabs.integracao_logistica.core.dto.UserResponse
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderEntity
import com.luizalabs.integracao_logistica.core.model.UserEntity
import org.springframework.data.domain.Page
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.UUID

interface OrderService {
    fun insertOrders(file: MultipartFile): OrderBatchResponse
    fun consumeOrderMessage(orderMessageDto: OrderMessageDto)
    fun findByExternalId(externalId: Long): Any
    fun findBatchById(orderBatchId: UUID): OrderBatchCompleteResponse
    fun findAll(page: Int, initialDate: LocalDate?, finalDate: LocalDate?): Page<UserResponse>
}
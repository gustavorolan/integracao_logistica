package com.luizalabs.integracao_logistica.core.order.repository

import com.luizalabs.integracao_logistica.core.model.OrderEntity
import org.springframework.data.domain.Page

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

import java.time.LocalDate
import java.util.*

@Repository
interface OrderRepository : JpaRepository<OrderEntity, UUID> {
    fun findByExternalId(externalId: Long): OrderEntity?

    @Query(
        "SELECT o FROM OrderEntity o WHERE " +
                "(COALESCE(:initialDate,cast('1970-01-01' as date) ) <= o.purchaseDate) AND " +
                "(COALESCE(:finalDate, cast('9999-12-31' as date)) >= o.purchaseDate)"
    )
    fun findAll(
        @Param("initialDate") initialDate: LocalDate?,
        @Param("finalDate") finalDate: LocalDate?,
        pageable: Pageable,
    ): Page<OrderEntity>
}
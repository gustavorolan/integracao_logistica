package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.model.UserEntity
import org.springframework.data.domain.Page

interface UserService {
    fun upsertByUserExternalId(userEntity: UserEntity): UserEntity
}
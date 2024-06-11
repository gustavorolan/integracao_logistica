package com.luizalabs.integracao_logistica.core.order.service

import com.luizalabs.integracao_logistica.core.model.UserEntity

interface UserService {
    fun upsertByUserExternalId(userEntity: UserEntity): UserEntity
}
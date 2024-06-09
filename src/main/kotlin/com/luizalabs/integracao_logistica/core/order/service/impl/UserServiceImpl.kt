package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.model.UserEntity
import com.luizalabs.integracao_logistica.core.order.repository.UserRepository
import com.luizalabs.integracao_logistica.core.order.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override fun upsertByUserExternalId(userEntity: UserEntity): UserEntity {

        val user = userRepository.findByExternalId(userEntity.externalId)
            ?.let { userEntity.copy(id = it.id) } ?: userEntity

        return userRepository.save(user)
    }
}
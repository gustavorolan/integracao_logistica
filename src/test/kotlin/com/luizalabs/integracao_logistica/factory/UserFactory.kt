package com.luizalabs.integracao_logistica.factory

import com.luizalabs.integracao_logistica.core.dto.UserDto
import com.luizalabs.integracao_logistica.core.model.UserEntity
import java.util.UUID

class UserFactory {
    companion object {
        fun getEntity(id: UUID = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea32"), externalId: Long = 0, name: String = "Generic Name") =
            UserEntity(id = id, externalId = externalId, name = name)

        fun getDto(userId: Long = 0, name: String = "Generic Name") = UserDto(name = name, userId = userId)
    }
}
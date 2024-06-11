package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.config.any
import com.luizalabs.integracao_logistica.core.model.UserEntity
import com.luizalabs.integracao_logistica.core.order.repository.UserRepository
import com.luizalabs.integracao_logistica.factory.UserFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.mockito.Mockito
import java.util.UUID

class UserServiceImplTest {

    private val userRepository = Mockito.mock(UserRepository::class.java)

    private val userService = UserServiceImpl(userRepository)

    @Test
    fun `Should update correctly`() {
        val entity = UserFactory.getEntity()

        Mockito.`when`(userRepository.findByExternalId(entity.externalId)).thenReturn(null)
        Mockito.`when`(userRepository.save(entity)).thenReturn(entity)

        val response = userService.upsertByUserExternalId(entity)

        Mockito.verify(userRepository).findByExternalId(entity.externalId)
        Mockito.verify(userRepository).save(entity)

        Assertions.assertEquals(entity, response)
    }

    @Test
    fun `Should save correctly`() {
        val entity = UserFactory.getEntity()
        val entity2 = UserFactory.getEntity(id = UUID.randomUUID())

        Mockito.`when`(userRepository.findByExternalId(entity.externalId)).thenReturn(entity2)
        Mockito.`when`(userRepository.save(any(UserEntity::class.java))).thenReturn(entity2)

        val response = userService.upsertByUserExternalId(entity)

        Mockito.verify(userRepository).findByExternalId(entity.externalId)
        Mockito.verify(userRepository).save(any(UserEntity::class.java))

        Assertions.assertInstanceOf(UUID::class.java, response.id)
        Assertions.assertNotEquals(entity.id, response.id)
        Assertions.assertEquals(entity.externalId, response.externalId)
        Assertions.assertEquals(entity.name, response.name)
    }
}
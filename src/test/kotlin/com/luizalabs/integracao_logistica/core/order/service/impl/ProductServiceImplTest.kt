package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.order.repository.ProductRepository
import com.luizalabs.integracao_logistica.core.order.service.ProductService
import com.luizalabs.integracao_logistica.factory.ProductFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class ProductServiceImplTest {

    private val productRepository = Mockito.mock(ProductRepository::class.java)

    private val productService: ProductService = ProductServiceImpl(productRepository)

    @Test
    fun `should save correctly`() {
        val entity = ProductFactory.getEntity()

        Mockito.`when`(productRepository.save(entity)).thenReturn(entity)

        val save = productService.save(entity)

        Mockito.verify(productRepository).save(entity)

        Mockito.verifyNoMoreInteractions(productRepository)

        Assertions.assertEquals(entity, save)
    }

    @Test
    fun `should return correctly findByOrderIn`() {
        val entity = ProductFactory.getEntity()
        val entities = listOf(entity)
        val ids = listOf(entity.id)


        Mockito.`when`(productRepository.findByOrderIdIn(ids))
            .thenReturn(entities)

        val response = productService.findByOrderIdIn(listOf(entity.id))

        Mockito.verify(productRepository).findByOrderIdIn(ids)

        Mockito.verifyNoMoreInteractions(productRepository)

        Assertions.assertEquals(entities, response)
    }
}
package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.core.exceptions.OrderBatchNotFoundException
import com.luizalabs.integracao_logistica.core.exceptions.OrderParseException
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderBatchStatus
import com.luizalabs.integracao_logistica.core.model.OrderMetadataEntity
import com.luizalabs.integracao_logistica.core.order.repository.OrderBatchRepository
import com.luizalabs.integracao_logistica.core.order.repository.OrderMetadataRepository
import com.luizalabs.integracao_logistica.factory.OrderBatchFactory
import com.luizalabs.integracao_logistica.factory.OrderMetadataFactory
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import java.util.*
import kotlin.test.Test


class OrderBatchServiceImplTest {

    private val orderBatchRepository = Mockito.mock(OrderBatchRepository::class.java)

    private val orderMetadataRepository = Mockito.mock(OrderMetadataRepository::class.java)

    private val orderMetadataService = OrderMetadataServiceImpl(orderMetadataRepository = orderMetadataRepository)

    private val orderBatchService = OrderBatchServiceImpl(
        orderBatchRepository = orderBatchRepository,
        orderMetadataService = orderMetadataService
    )

    private val metadataCaptor: ArgumentCaptor<OrderMetadataEntity> = ArgumentCaptor.forClass(OrderMetadataEntity::class.java)

    private val orderBatchCaptor: ArgumentCaptor<OrderBatchEntity> = ArgumentCaptor.forClass(OrderBatchEntity::class.java)

    @Test
    fun `Should test if is executing save operation`() {
        val orderBatchEntity = OrderBatchFactory.get()

        Mockito.`when`(orderBatchRepository.save(orderBatchEntity))
            .thenReturn(orderBatchEntity)

        orderBatchService.save(orderBatchEntity)

        Mockito.verify(orderBatchRepository).save(orderBatchCaptor.capture())

        Mockito.verifyNoMoreInteractions(orderBatchRepository)

        Assertions.assertEquals(orderBatchEntity, orderBatchCaptor.value)
    }

    @Test
    fun `Should throw an exception when trying to findById`() {
        val id = UUID.randomUUID()

        Mockito.`when`(orderBatchRepository.findById(id))
            .thenReturn(Optional.empty())

        assertThatThrownBy { orderBatchService.findById(id) }
            .isInstanceOf(OrderBatchNotFoundException::class.java)
            .hasMessageContaining("Order batch not found!")

        Mockito.verify(orderBatchRepository).findById(id)

        Mockito.verifyNoMoreInteractions(orderBatchRepository)
    }

    @Test
    fun `Should test if is executing findById operation`() {
        val orderBatchEntity = OrderBatchFactory.get()

        Mockito.`when`(orderBatchRepository.findById(orderBatchEntity.id))
            .thenReturn(Optional.of(orderBatchEntity))

        orderBatchService.findById(orderBatchEntity.id)

        Mockito.verify(orderBatchRepository).findById(orderBatchEntity.id)

        Mockito.verifyNoMoreInteractions(orderBatchRepository)
    }

    @Test
    fun `should update correctly when it has failed`() {
        val id = UUID.randomUUID()
        val orderParseException = OrderParseException()
        val orderBatchEntity = OrderBatchFactory.get(totalLines = 20, status = OrderBatchStatus.FAILED, id = id)
        val orderMetadataEntity = OrderMetadataFactory.get(
            id = id,
            errorMessage = orderParseException.message.toString(),
            orderBatch = orderBatchEntity
        )
        val orderBatchStatusExpected = OrderBatchStatus.FAILED
        val orderBatchTotalErrorLinesExpected = 1
        val errorLine = 1
        val errorMessage = "Error trying to parse order information."


        Mockito.`when`(orderBatchRepository.findById(orderBatchEntity.id)).thenReturn(Optional.of(orderBatchEntity))
        Mockito.`when`(orderMetadataRepository.save(Mockito.any())).thenReturn(orderMetadataEntity)
        Mockito.`when`(orderBatchRepository.save(Mockito.any())).thenReturn(orderBatchEntity)

        orderBatchService.updateException(
            orderLine = orderMetadataEntity.errorLine,
            e = orderParseException,
            orderBatchId = orderBatchEntity.id,
            totalLines = orderBatchEntity.totalLines
        )

        Mockito.verify(orderBatchRepository).save(orderBatchCaptor.capture())

        Mockito.verify(orderMetadataRepository).save(metadataCaptor.capture())

        Mockito.verify(orderBatchRepository).findById(Mockito.any())

        Mockito.verifyNoMoreInteractions(orderMetadataRepository, orderBatchRepository)

        Assertions.assertEquals(errorLine, metadataCaptor.value.errorLine)
        Assertions.assertEquals(errorMessage, metadataCaptor.value.errorMessage)
        Assertions.assertEquals(orderBatchTotalErrorLinesExpected, orderBatchCaptor.value.totalErrorLines)
        Assertions.assertEquals(orderBatchStatusExpected, orderBatchCaptor.value.status)
    }

    @Test
    fun `should update correctly when it has been successful`() {
        val id = UUID.randomUUID()
        val orderLine = 49
        val orderBatchEntity = OrderBatchFactory.get(totalLines = 49, status = OrderBatchStatus.SUCCESSFUL, id = id)

        val orderBatchStatusExpected = OrderBatchStatus.SUCCESSFUL
        val orderBatchTotalErrorLinesExpected = 0


        Mockito.`when`(orderBatchRepository.findById(orderBatchEntity.id)).thenReturn(Optional.of(orderBatchEntity))
        Mockito.`when`(orderBatchRepository.save(Mockito.any())).thenReturn(orderBatchEntity)

        orderBatchService.updateSuccessful(
            orderLine = orderLine,
            orderBatchId = orderBatchEntity.id,
            totalLines = orderBatchEntity.totalLines
        )

        Mockito.verify(orderBatchRepository).save(orderBatchCaptor.capture())

        Mockito.verify(orderBatchRepository).findById(Mockito.any())

        Mockito.verifyNoMoreInteractions(orderMetadataRepository, orderBatchRepository)

        Assertions.assertEquals(orderBatchTotalErrorLinesExpected, orderBatchCaptor.value.totalErrorLines)
        Assertions.assertEquals(orderBatchStatusExpected, orderBatchCaptor.value.status)
        Assertions.assertEquals(orderLine, orderBatchCaptor.value.totalLines)
        Assertions.assertTrue(orderBatchCaptor.value.ordersMetadata.isEmpty())
    }
}
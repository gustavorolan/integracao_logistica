package com.luizalabs.integracao_logistica.core.order.service.impl

import com.luizalabs.integracao_logistica.config.any
import com.luizalabs.integracao_logistica.config.captureNonNull
import com.luizalabs.integracao_logistica.core.exceptions.AlreadyExistsUserWithThisOrderParseException
import com.luizalabs.integracao_logistica.core.exceptions.OrderNotFoundException
import com.luizalabs.integracao_logistica.core.exceptions.OrderParseException
import com.luizalabs.integracao_logistica.core.model.OrderBatchEntity
import com.luizalabs.integracao_logistica.core.model.OrderEntity
import com.luizalabs.integracao_logistica.core.model.ProductEntity
import com.luizalabs.integracao_logistica.core.model.UserEntity

import com.luizalabs.integracao_logistica.core.order.repository.OrderRepository
import com.luizalabs.integracao_logistica.core.order.service.*
import com.luizalabs.integracao_logistica.factory.*
import com.luizalabs.integracao_logistica.infrastructure.messaging.order.OrderProducer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions

import org.mockito.ArgumentCaptor

import org.mockito.Mockito
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.*

import kotlin.test.Test

class OrderServiceImplTest {
    private val orderBatchService: OrderBatchService = Mockito.mock(OrderBatchService::class.java)
    private val orderMapper: OrderMapper = OrderMapperImpl()
    private val orderProducer: OrderProducer = Mockito.mock(OrderProducer::class.java)
    private val orderRepository: OrderRepository = Mockito.mock(OrderRepository::class.java)
    private val userService: UserService = Mockito.mock(UserService::class.java)
    private val productService: ProductService = Mockito.mock(ProductService::class.java)
    private val orderService: OrderService = OrderServiceImpl(
        orderProducer = orderProducer,
        orderMapper = orderMapper,
        orderRepository = orderRepository,
        userService = userService,
        productService = productService,
        orderBatchService = orderBatchService
    )

    private val orderCaptor: ArgumentCaptor<OrderEntity> = ArgumentCaptor.forClass(OrderEntity::class.java)
    private val userCaptor: ArgumentCaptor<UserEntity> = ArgumentCaptor.forClass(UserEntity::class.java)
    private val productCaptor: ArgumentCaptor<ProductEntity> = ArgumentCaptor.forClass(ProductEntity::class.java)

    @Test
    fun `should test if its inserting correctly from txt`() = runBlocking {
        val orderMessages = OrderFactory.getOrderMessages()
        val file = OrderFactory.getMultiPartFile()
        val orderBatchEntity = OrderBatchFactory.get()
        val sendDeferred1 = CompletableDeferred<Unit>()
        val sendDeferred2 = CompletableDeferred<Unit>()
        val sendDeferred3 = CompletableDeferred<Unit>()

        Mockito.`when`(orderBatchService.save(any(OrderBatchEntity::class.java)))
            .thenReturn(orderBatchEntity)

        Mockito.`when`(orderProducer.send(orderMessages[0])).then {
            sendDeferred1.complete(Unit)
        }
        Mockito.`when`(orderProducer.send(orderMessages[1])).then {
            sendDeferred2.complete(Unit)
        }
        Mockito.`when`(orderProducer.send(orderMessages[2])).then {
            sendDeferred3.complete(Unit)
        }

        val insertOrders = orderService.insertOrders(file)

        sendDeferred1.await()
        sendDeferred2.await()
        sendDeferred3.await()

        Assertions.assertEquals(orderBatchEntity.id.toString(), insertOrders.orderBatchId)

        Mockito.verify(orderProducer).send(orderMessages[0])
        Mockito.verify(orderProducer).send(orderMessages[1])
        Mockito.verify(orderProducer).send(orderMessages[2])
    }

    @Test
    fun `should save an error while trying to insert from txt`() {

        runBlocking {
            val file = OrderFactory.getMultiPartFileBroken()
            val orderBatchEntity = OrderBatchFactory.get()
            val sendDeferred = CompletableDeferred<Unit>()

            Mockito.`when`(orderBatchService.save(any(OrderBatchEntity::class.java)))
                .then { orderBatchEntity }

            Mockito.`when`(
                orderBatchService.updateException(
                    Mockito.anyInt(),
                    any(UUID::class.java),
                    any(Exception::class.java),
                    Mockito.anyInt()
                )
            ).thenAnswer {
                sendDeferred.complete(Unit)
                orderBatchEntity
            }

            orderService.insertOrders(file)

            sendDeferred.await()

            Mockito.verify(
                orderBatchService
            ).updateException(
                Mockito.anyInt(),
                any(UUID::class.java),
                any(OrderParseException::class.java),
                Mockito.anyInt()
            )

            Mockito.verify(orderBatchService).save(any(OrderBatchEntity::class.java))

            Mockito.verifyNoMoreInteractions(orderBatchService)
        }


    }


    @Test
    fun `Should consume messages correctly, order already exists`() {
        val orderMessageDto = OrderFactory.getMessageDto()
        val userEntity = UserFactory.getEntity()
        val orderEntity = OrderFactory.getEntity(user = userEntity)
        val productEntity = ProductFactory.getEntity(orderEntity = orderEntity)

        Mockito.`when`(userService.upsertByUserExternalId(any(UserEntity::class.java))).thenReturn(userEntity)
        Mockito.`when`(orderRepository.findByExternalId(any(Long::class.java))).thenReturn(orderEntity)
        Mockito.`when`(orderRepository.save(any(OrderEntity::class.java))).thenReturn(orderEntity)
        Mockito.`when`(productService.save(any(ProductEntity::class.java))).thenReturn(productEntity)

        orderService.consumeOrderMessage(orderMessageDto)

        Mockito.verify(userService).upsertByUserExternalId(userCaptor.captureNonNull())
        Mockito.verify(orderRepository).save(orderCaptor.captureNonNull())
        Mockito.verify(orderRepository).findByExternalId(any(Long::class.java))
        Mockito.verify(productService).save(productCaptor.captureNonNull())

        Mockito.verifyNoMoreInteractions(userService, orderRepository, productService)

        Assertions.assertEquals(orderEntity.user, orderCaptor.value.user)
        Assertions.assertEquals(orderEntity.products, orderCaptor.value.products)
        Assertions.assertEquals(orderEntity.externalId, orderCaptor.value.externalId)
        Assertions.assertEquals(orderEntity.purchaseDate, orderCaptor.value.purchaseDate)
        Assertions.assertEquals(userEntity.externalId, userCaptor.value.externalId)
        Assertions.assertEquals(userEntity.name, userCaptor.value.name)
        Assertions.assertEquals(productEntity.value, productCaptor.value.value)
        Assertions.assertEquals(productEntity.externalId, productCaptor.value.externalId)

        Assertions.assertInstanceOf(UUID::class.java, orderCaptor.value.id)
        Assertions.assertInstanceOf(UUID::class.java, userCaptor.value.id)
        Assertions.assertInstanceOf(UUID::class.java, productCaptor.value.id)
    }

    @Test
    fun `Should consume messages correctly, order doesnt exist`() {
        val orderMessageDto = OrderFactory.getMessageDto()
        val userEntity = UserFactory.getEntity()
        val orderEntity = OrderFactory.getEntity(user = userEntity)
        val productEntity = ProductFactory.getEntity(orderEntity = orderEntity)

        Mockito.`when`(userService.upsertByUserExternalId(any(UserEntity::class.java))).thenReturn(userEntity)
        Mockito.`when`(orderRepository.findByExternalId(any(Long::class.java))).thenReturn(null)
        Mockito.`when`(orderRepository.save(any(OrderEntity::class.java))).thenReturn(orderEntity)
        Mockito.`when`(productService.save(any(ProductEntity::class.java))).thenReturn(productEntity)

        orderService.consumeOrderMessage(orderMessageDto)

        Mockito.verify(userService).upsertByUserExternalId(userCaptor.captureNonNull())
        Mockito.verify(orderRepository).save(orderCaptor.captureNonNull())
        Mockito.verify(orderRepository).findByExternalId(any(Long::class.java))
        Mockito.verify(productService).save(productCaptor.captureNonNull())

        Mockito.verifyNoMoreInteractions(userService, orderRepository, productService)

        Assertions.assertEquals(orderEntity.user, orderCaptor.value.user)
        Assertions.assertEquals(orderEntity.products, orderCaptor.value.products)
        Assertions.assertEquals(orderEntity.externalId, orderCaptor.value.externalId)
        Assertions.assertEquals(orderEntity.purchaseDate, orderCaptor.value.purchaseDate)
        Assertions.assertEquals(userEntity.externalId, userCaptor.value.externalId)
        Assertions.assertEquals(userEntity.name, userCaptor.value.name)
        Assertions.assertEquals(productEntity.value, productCaptor.value.value)
        Assertions.assertEquals(productEntity.externalId, productCaptor.value.externalId)

        Assertions.assertInstanceOf(UUID::class.java, orderCaptor.value.id)
        Assertions.assertInstanceOf(UUID::class.java, userCaptor.value.id)
        Assertions.assertInstanceOf(UUID::class.java, productCaptor.value.id)
    }

    @Test
    fun `Should throw an error while trying consume messages `() {
        val orderMessageDto = OrderFactory.getMessageDto()
        val userEntity = UserFactory.getEntity()
        val orderEntity = OrderFactory.getEntity(user = UserEntity())

        Mockito.`when`(userService.upsertByUserExternalId(any(UserEntity::class.java))).thenReturn(userEntity)
        Mockito.`when`(orderRepository.findByExternalId(any(Long::class.java))).thenReturn(orderEntity)

        orderService.consumeOrderMessage(orderMessageDto)

        Mockito.verify(userService).upsertByUserExternalId(userCaptor.captureNonNull())
        Mockito.verify(orderRepository).findByExternalId(any(Long::class.java))
        Mockito.verify(
            orderBatchService
        ).updateException(
            Mockito.anyInt(),
            any(UUID::class.java),
            any(AlreadyExistsUserWithThisOrderParseException::class.java),
            Mockito.anyInt()
        )

        Mockito.verifyNoMoreInteractions(userService, orderRepository, productService)
    }

    @Test
    fun `should test if findAll is working correctly`() {

        val orderEntity =
            OrderFactory.getEntity(user = UserFactory.getEntity(), products = mutableListOf(ProductFactory.getEntity()))

        val entities = listOf(
            orderEntity,
            orderEntity,
            orderEntity,
            orderEntity
        )
        val page: Page<OrderEntity> = PageImpl<OrderEntity>(entities)

        val userResponse = UserResponseFactory.getUserResponseWithFourOrders()

        val responseExpected = listOf(userResponse)

        Mockito.`when`(
            orderRepository.findAll(
                any(LocalDate::class.java),
                any(LocalDate::class.java),
                any(Pageable::class.java)
            )
        )
            .thenReturn(page)

        val pageResponse = orderService.findAll(page = 1, entities[0].purchaseDate, entities[0].purchaseDate)

        Mockito.verify(orderRepository)
            .findAll(any(LocalDate::class.java), any(LocalDate::class.java), any(Pageable::class.java))

        Mockito.verifyNoMoreInteractions(orderRepository)

        Assertions.assertEquals(responseExpected, pageResponse.content)
    }

    @Test
    fun `Should return a batch by id correctly mapped to response`() {

        val orderBatch = OrderBatchFactory.get(ordersMetadata = mutableListOf(OrderMetadataFactory.get()))
        val responseExpected = OrderBatchFactory.getResponse(metadatas = mutableListOf(OrderMetadataFactory.getResponse()))

        Mockito.`when`(orderBatchService.findById(orderBatch.id)).thenReturn(orderBatch)

        val response = orderService.findBatchById(orderBatch.id)

        Mockito.verify(orderBatchService).findById(orderBatch.id)

        Mockito.verifyNoMoreInteractions(orderBatchService)

        Assertions.assertEquals(responseExpected, response)
    }


    @Test
    fun `should find order by external id and map correctly`() {
        val entity =
            OrderFactory.getEntity(user = UserFactory.getEntity(), products = mutableListOf(ProductFactory.getEntity()))
        val userResponse = UserResponseFactory.getUserResponseWithOneOrder()

        Mockito.`when`(orderRepository.findByExternalId(entity.externalId)).thenReturn(entity)

        val response = orderService.findByExternalId(entity.externalId)

        Mockito.verify(orderRepository).findByExternalId(entity.externalId)

        Mockito.verifyNoMoreInteractions(orderRepository)

        Assertions.assertEquals(userResponse, response)
    }


    @Test
    fun `should throw an exception while trying to find order by external id`() {
        val entity =
            OrderFactory.getEntity(user = UserFactory.getEntity(), products = mutableListOf(ProductFactory.getEntity()))


        Mockito.`when`(orderRepository.findByExternalId(entity.externalId)).thenReturn(null)

        assertThatThrownBy { orderService.findByExternalId(entity.externalId) }
            .isInstanceOf(OrderNotFoundException::class.java)
            .hasMessageContaining("Order not found!")

        Mockito.verify(orderRepository).findByExternalId(entity.externalId)

        Mockito.verifyNoMoreInteractions(orderRepository)
    }
}





package com.luizalabs.integracao_logistica.factory

import com.luizalabs.integracao_logistica.core.dto.OrderDto
import com.luizalabs.integracao_logistica.core.dto.OrderMessageDto
import com.luizalabs.integracao_logistica.core.dto.ProductDto
import com.luizalabs.integracao_logistica.core.dto.UserDto
import com.luizalabs.integracao_logistica.core.model.OrderEntity
import com.luizalabs.integracao_logistica.core.model.ProductEntity
import com.luizalabs.integracao_logistica.core.model.UserEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*

class OrderFactory {
    companion object {
        private const val dataInsert = "\n" +
                "0000000016                             Mr. Paulini conn30000001190000000013     1826.7120210610\n" +
                "0000000012                             Mr. Pauline conn40000001190000000015     1826.7120210610\n" +
                "0000000020                             Mr. Paulina conn70000001190000000017     1826.7120210610\n"

        fun getMultiPartFile(): MultipartFile = MockMultipartFile(
            "data.txt",
            "data.txt",
            "text/plain",
            dataInsert.toByteArray()
        )

        private const val dataInsertBroken = "\n" +
                "0000000016                             Mrssssss. Paulini conn30000001190000000013     1826.7120210610\n"

        fun getMultiPartFileBroken(): MultipartFile = MockMultipartFile(
            "data.txt",
            "data.txt",
            "text/plain",
            dataInsertBroken.toByteArray()
        )

        private val orderMessage1 = OrderMessageDto(
            userDto = UserDto(userId = 16, name = "Mr. Paulini conn"),
            orderDto = OrderDto(orderId = 3000000119, date = localDate()),
            productDto = ProductDto(productId = 13, value = BigDecimal(1826.71).round()),
            orderBatchId = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea32"),
            lineNumber = 0,
            totalLines = 3
        )

        private val orderMessage2 = OrderMessageDto(
            userDto = UserDto(userId = 12, name = "Mr. Pauline conn"),
            orderDto = OrderDto(orderId = 4000000119, date = localDate()),
            productDto = ProductDto(productId = 15, value = BigDecimal(1826.71).round()),
            orderBatchId = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea32"),
            lineNumber = 1,
            totalLines = 3
        )

        private val orderMessage3 = OrderMessageDto(
            userDto = UserDto(userId = 20, name = "Mr. Paulina conn"),
            orderDto = OrderDto(orderId = 7000000119, date = localDate()),
            productDto = ProductDto(productId = 17, value = BigDecimal(1826.71).round()),
            orderBatchId = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea32"),
            lineNumber = 2,
            totalLines = 3
        )

        fun getOrderMessages() = listOf(orderMessage1, orderMessage2, orderMessage3)

        private fun BigDecimal.round() = this.setScale(2, RoundingMode.DOWN)

        fun getEntity(
            id: UUID = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea32"),
            externalId: Long = 1,
            user: UserEntity = UserEntity(),
            purchaseDate: LocalDate = localDate(),
            products: MutableList<ProductEntity> = mutableListOf()
        ) = OrderEntity(
            id = id,
            externalId = externalId,
            user = user,
            purchaseDate = purchaseDate,
            products = products
        )

        fun getMessageDto(
            orderBatchId: UUID = UUID.fromString("584f9284-72ee-4045-b111-7a4e6599ea32"),
            userDto: UserDto = UserFactory.getDto(),
            productDto: ProductDto = ProductFactory.getDto(),
            orderDto: OrderDto = getDto(),
            lineNumber: Int = 1,
            totalLines: Int = 1
        ) = OrderMessageDto(
            userDto = userDto,
            orderDto = orderDto,
            productDto = productDto,
            orderBatchId = orderBatchId,
            lineNumber = lineNumber,
            totalLines = totalLines
        )

        private fun getDto(orderId: Long = 1, date: LocalDate = localDate()) = OrderDto(orderId = orderId, date = date)

        fun localDate(): LocalDate = LocalDate.of(2021, 6, 10)
    }
}
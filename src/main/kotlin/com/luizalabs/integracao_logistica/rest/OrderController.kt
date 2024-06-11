package com.luizalabs.integracao_logistica.rest

import com.luizalabs.integracao_logistica.core.order.service.OrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/api/v0/orders")
class OrderController(
    private val orderService: OrderService,
) {

    @PostMapping("/batch/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile) = orderService.insertOrders(file)

    @GetMapping()
    fun findAll(
        @RequestParam("page") page: Int,
        @RequestParam("initial_date") initialDate: LocalDate?,
        @RequestParam("final_date") finalDate: LocalDate?
    ) =
        orderService.findAll(page, initialDate, finalDate)

    @GetMapping("/{externalId}")
    fun findById(@PathVariable("externalId") externalId: Long) = orderService.findByExternalId(externalId)

    @GetMapping("/batch/{orderBatchId}")
    fun findBatchById(@PathVariable("orderBatchId") orderBatchId: UUID) = orderService.findBatchById(orderBatchId)

}

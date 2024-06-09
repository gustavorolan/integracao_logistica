package com.luizalabs.integracao_logistica.core.dto

import org.springframework.http.HttpStatus

data class ErrorResponse(
    val name:String,
    val message: String,
    val httpStatus: HttpStatus
)

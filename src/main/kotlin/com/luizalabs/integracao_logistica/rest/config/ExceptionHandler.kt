package com.luizalabs.integracao_logistica.rest.config

import com.luizalabs.integracao_logistica.core.dto.ErrorResponse
import com.luizalabs.integracao_logistica.core.exceptions.MainException
import com.luizalabs.integracao_logistica.core.exceptions.MainHttpException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class ExceptionHandlerAdvice : ResponseEntityExceptionHandler() {

    companion object{
        private const val GENERIC_MESSAGE = "Internal Server Error"
        private const val GENERIC_EXCEPTION = "Exception"
    }

    @ExceptionHandler(
        MainHttpException::class,
    )
    fun mainHttpExceptionHandler(exception: MainHttpException): ResponseEntity<ErrorResponse> =
        ResponseEntity<ErrorResponse>(exception.toErrorResponse(), exception.httpStatus)

    @ExceptionHandler(
        Throwable::class,
        RuntimeException::class,
        MainException::class,
    )
    fun throwableHandler(throwable: Throwable): ResponseEntity<ErrorResponse> {
        return ResponseEntity<ErrorResponse>(throwable.toErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders?,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {
        val errorResponse = ErrorResponse(
            name = ex::class.simpleName.toString(),
            message = ex.message.toString(),
            httpStatus = HttpStatus.valueOf(status.value())
        )

        return super.handleExceptionInternal(ex, errorResponse, headers, status, exchange)
    }

    fun MainHttpException.toErrorResponse(): ErrorResponse = ErrorResponse(
        name = this::class.simpleName ?: GENERIC_EXCEPTION,
        message = this.message ?: GENERIC_MESSAGE,
        httpStatus = this.httpStatus
    )

    fun Throwable.toErrorResponse(): ErrorResponse = ErrorResponse(
        name = this::class.simpleName ?: GENERIC_MESSAGE,
        message = this.message ?: GENERIC_MESSAGE,
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR
    )
}




package com.luizalabs.integracao_logistica.core.extensions

import org.slf4j.Logger

fun Logger.logErrorAndReturnsException(message: String, ex: Throwable): Throwable {
    this.error(message, ex)
    return ex
}
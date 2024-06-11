package com.luizalabs.integracao_logistica.config

import org.mockito.ArgumentCaptor
import org.mockito.Mockito


fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

@Suppress("UNCHECKED_CAST")
fun <T> ArgumentCaptor<T>.captureNonNull(): T = this.capture()

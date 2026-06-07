package com.example.todomaster.core.network

import kotlin.test.Test
import kotlin.test.assertNotNull

class HttpClientTest {
    @Test
    fun `verify httpClient factory creates instance`() {
        val client = HttpClientFactory.create(enableLogging = false)
        assertNotNull(client)
    }
}
package com.soundletter.app.core.network

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class GeminiServiceTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `test recommendation logic success mapping`() = runTest {
        val mockResponse = """{"candidates": [{"content": {"parts": [{"text": "happy pop"}]}}]}"""
        val mockEngine = MockEngine {
            respond(content = mockResponse, status = HttpStatusCode.OK, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json(json) } }
        
        val service = GeminiService(client, "fake_key")
        assertEquals("happy pop", service.getSongRecommendations("Message"))
    }

    @Test
    fun `test recommendation fallback when api key is missing`() = runTest {
        val client = HttpClient(MockEngine { respondOk() })
        // Test key kosong atau "YOUR_..."
        val service = GeminiService(client, "")
        assertEquals("chill", service.getSongRecommendations("Message"))
        
        val service2 = GeminiService(client, "YOUR_API_KEY")
        assertEquals("chill", service2.getSongRecommendations("Message"))
    }

    @Test
    fun `test recommendation fallback when api returns error`() = runTest {
        val mockEngine = MockEngine { respondError(HttpStatusCode.BadRequest) }
        val client = HttpClient(mockEngine) { install(ContentNegotiation) { json(json) } }
        
        val service = GeminiService(client, "fake_key")
        assertEquals("chill", service.getSongRecommendations("Message"))
    }

    @Test
    fun `test recommendation fallback when network exception occurs`() = runTest {
        val mockEngine = MockEngine { throw Exception("No Internet") }
        val client = HttpClient(mockEngine)
        
        val service = GeminiService(client, "fake_key")
        // Catch block mengembalikan "ambient"
        assertEquals("ambient", service.getSongRecommendations("Message"))
    }
}

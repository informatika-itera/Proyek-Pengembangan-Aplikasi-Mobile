package com.example.Roomie.data.remote.ai

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

class GeminiServiceTest {
    
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `processUserCommand - Success - should decode JSON result`() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = """{"candidates":[{"content":{"parts":[{"text":"{\"capacity\": 50, \"buildingName\": \"GKU2\"}"}]}}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        
        val service = GeminiService(httpClient, json)
        
        val result = service.processUserCommand("Cari ruang GKU2 50 orang")
        assertNotNull(result)
        assertEquals(50, result.capacity)
        assertEquals("GKU2", result.buildingName)
    }

    @Test
    fun `processUserCommand - API Error - should return null`() = runTest {
        val mockEngine = MockEngine {
            respond("Internal Server Error", HttpStatusCode.InternalServerError)
        }
        val service = GeminiService(HttpClient(mockEngine), json)
        
        val result = service.processUserCommand("test")
        assertNull(result)
    }

    @Test
    fun `processUserCommand - Exception - should handle catch block`() = runTest {
        val mockEngine = MockEngine { throw Exception("No Internet") }
        val service = GeminiService(HttpClient(mockEngine), json)
        
        val result = service.processUserCommand("test")
        assertNull(result)
    }
}

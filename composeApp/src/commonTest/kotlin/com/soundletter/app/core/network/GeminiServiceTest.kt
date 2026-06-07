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
    fun `test recommendation logic mapping`() = runTest {
        // Simulasi respon dari Gemini AI
        val mockResponse = """
            {
                "candidates": [{
                    "content": { "parts": [{ "text": "Hindia - Evaluasi" }] }
                }]
            }
        """.trimIndent()

        val mockEngine = MockEngine {
            respond(
                content = mockResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }

        val service = GeminiService(client)
        val result = service.getSongRecommendations("Sedang sedih")
        
        // Memastikan hasil parsing benar (Ini akan menaikkan coverage folder network)
        assertEquals("Hindia - Evaluasi", result)
    }
}

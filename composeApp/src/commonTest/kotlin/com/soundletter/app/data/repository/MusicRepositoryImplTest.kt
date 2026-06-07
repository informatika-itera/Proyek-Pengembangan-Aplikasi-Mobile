package com.soundletter.app.data.repository

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MusicRepositoryImplTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `searchSongs returns fallback data when API fails`() = runTest {
        // Simulasi API Error
        val mockEngine = MockEngine {
            respondError(HttpStatusCode.InternalServerError)
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }

        val repository = MusicRepositoryImpl(client)
        val result = repository.searchSongs("any query")

        // Verifikasi data cadangan (fallback) muncul
        assertTrue(result.isNotEmpty())
        assertEquals("Creative Commons Melody", result[0].title)
        assertEquals("Jamendo Artist (Fallback)", result[0].artist)
    }

    @Test
    fun `searchSongs returns mapped tracks when API succeeds`() = runTest {
        val mockResponse = """
            {
                "results": [
                    {
                        "name": "Test Song",
                        "artist_name": "Test Artist",
                        "image": "https://image.url",
                        "audio": "https://audio.url"
                    }
                ]
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

        val repository = MusicRepositoryImpl(client)
        val result = repository.searchSongs("test")

        assertEquals(1, result.size)
        assertEquals("Test Song", result[0].title)
        assertEquals("Test Artist", result[0].artist)
    }
}

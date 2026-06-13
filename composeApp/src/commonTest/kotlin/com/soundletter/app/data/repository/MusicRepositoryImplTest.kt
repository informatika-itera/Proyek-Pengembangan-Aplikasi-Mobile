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
        val mockEngine = MockEngine {
            respondError(HttpStatusCode.InternalServerError)
        }
        
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }
        
        val repository = MusicRepositoryImpl(client)
        val result = repository.searchSongs("any query")

        assertTrue(result.isNotEmpty())
        assertEquals("Ambient Gold", result[0].title)
        assertEquals("AudioCoffee", result[0].artist)
    }

    @Test
    fun `searchSongs formats tags correctly with plus sign`() = runTest {
        var capturedUrl = ""
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond(
                content = """{"results": [{"name":"S","artist_name":"A","image":"i","audio":"a"}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }

        val repository = MusicRepositoryImpl(client)
        // Test spasi menjadi + (sad pop -> sad+pop)
        repository.searchSongs("sad pop")

        // Verifikasi URL mengandung fuzzytags=sad+pop
        assertTrue(capturedUrl.contains("fuzzytags=sad%2Bpop") || capturedUrl.contains("fuzzytags=sad+pop"))
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
    }
}

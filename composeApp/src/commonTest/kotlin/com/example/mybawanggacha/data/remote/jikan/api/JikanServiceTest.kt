package com.example.mybawanggacha.data.remote.jikan.api

import com.example.mybawanggacha.data.remote.jikan.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JikanServiceTest {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private fun createMockClient(
        createResponse: (requestUrl: String, queryParams: Map<String, List<String>>) -> Pair<HttpStatusCode, String>
    ): HttpClient {
        val mockEngine = MockEngine { request ->
            val urlString = request.url.toString()
            val queryParams = request.url.parameters.entries().associate { it.key to it.value }

            val (status, responseBody) = createResponse(urlString, queryParams)

            respond(
                content = responseBody,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(jsonConfig)
            }
        }
    }

    @Test
    fun fetchCurrentSeasonAnime_shouldSendCorrectUrlAndPageParameter() = runTest {
        val expectedPage = 2
        var capturedUrl = ""
        var capturedPageParam: String? = null

        val mockJsonResponse = """{"data":[]}"""

        val client = createMockClient { url, queryParams ->
            capturedUrl = url
            capturedPageParam = queryParams["page"]?.firstOrNull()
            Pair(HttpStatusCode.OK, mockJsonResponse)
        }
        val service = JikanService(client)

        val result = service.fetchCurrentSeasonAnime(page = expectedPage)

        assertTrue(capturedUrl.contains("https://api.jikan.moe/v4/seasons/now"))
        assertEquals(expectedPage.toString(), capturedPageParam)
    }

    @Test
    fun fetchSeasonAnime_shouldConstructDynamicUrlCorrectly() = runTest {
        val year = 2024
        val season = "fall"
        var capturedUrl = ""

        val client = createMockClient { url, _ ->
            capturedUrl = url
            Pair(HttpStatusCode.OK, """{"data":[]}""")
        }
        val service = JikanService(client)

        service.fetchSeasonAnime(year = year, season = season, page = 1)

        val expectedUrl = "https://api.jikan.moe/v4/seasons/$year/$season?page=1"
        assertEquals(expectedUrl, capturedUrl)
    }

    @Test
    fun fetchRelationEntryPreview_whenTypeIsManga_shouldRequestMangaEndpoint() = runTest {
        val id = 101
        var capturedUrl = ""

        val client = createMockClient { url, _ ->
            capturedUrl = url
            Pair(HttpStatusCode.OK, """{}""")
        }
        val service = JikanService(client)

        service.fetchRelationEntryPreview(id = id, type = "Manga")

        val expectedUrl = "https://api.jikan.moe/v4/manga/$id"
        assertEquals(expectedUrl, capturedUrl)
    }

    @Test
    fun fetchRelationEntryPreview_whenTypeIsFallbackOrNull_shouldRequestAnimeEndpoint() = runTest {
        val id = 202
        var capturedUrl = ""

        val client = createMockClient { url, _ ->
            capturedUrl = url
            Pair(HttpStatusCode.OK, """{}""")
        }
        val service = JikanService(client)

        service.fetchRelationEntryPreview(id = id, type = null)
        assertEquals("https://api.jikan.moe/v4/anime/$id", capturedUrl)

        service.fetchRelationEntryPreview(id = id, type = "novel")
        assertEquals("https://api.jikan.moe/v4/anime/$id", capturedUrl)
    }
}

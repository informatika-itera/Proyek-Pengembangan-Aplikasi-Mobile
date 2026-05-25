package com.example.mybawanggacha.data.repository.search

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.remote.jikan.api.JikanRateLimiter
import com.example.mybawanggacha.data.remote.jikan.api.JikanService
import com.example.mybawanggacha.data.remote.jikan.source.JikanSearchRemoteDataSource
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.SearchMediaType
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRepositoryImplTest {
    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @BeforeTest
    fun setUp() {
        JikanRateLimiter.resetForTest(enabled = false)
    }

    @AfterTest
    fun tearDown() {
        JikanRateLimiter.resetForTest(enabled = true)
    }

    @Test
    fun search_withAnimeFilters_shouldRequestAnimeEndpointAndMapPage() = runTest {
        var capturedUrl = ""
        var capturedQuery: String? = null
        var capturedPage: String? = null
        var capturedOrderBy: String? = null

        val client = createMockClient { url, queryParams ->
            capturedUrl = url
            capturedQuery = queryParams["q"]?.firstOrNull()
            capturedPage = queryParams["page"]?.firstOrNull()
            capturedOrderBy = queryParams["order_by"]?.firstOrNull()
            Pair(
                HttpStatusCode.OK,
                """
                    {
                      "pagination": { "has_next_page": true, "current_page": 2 },
                      "data": [
                        { "mal_id": 10, "title": "Frieren", "type": "TV", "episodes": 28, "score": 9.1 }
                      ]
                    }
                """.trimIndent()
            )
        }
        val repository = createRepository(client)

        val result = repository.search(
            filters = MediaSearchFilters(
                mediaType = SearchMediaType.Anime,
                query = "frieren",
                orderBy = "score",
                sort = "desc"
            ),
            page = 2
        )

        assertTrue(capturedUrl.contains("https://api.jikan.moe/v4/anime"))
        assertEquals("frieren", capturedQuery)
        assertEquals("2", capturedPage)
        assertEquals("score", capturedOrderBy)
        assertTrue(result.hasNextPage)
        assertEquals(3, result.nextPage)
        assertEquals(1, result.items.size)
        assertEquals("Frieren", result.items.first().title)
    }

    @Test
    fun search_withMangaFilters_shouldRequestMangaEndpointAndMapPage() = runTest {
        var capturedUrl = ""
        var capturedMagazine: String? = null

        val client = createMockClient { url, queryParams ->
            capturedUrl = url
            capturedMagazine = queryParams["magazines"]?.firstOrNull()
            Pair(
                HttpStatusCode.OK,
                """
                    {
                      "pagination": { "has_next_page": false, "current_page": 1 },
                      "data": [
                        { "mal_id": 20, "title": "Manga Result", "type": "Manga", "chapters": 50, "volumes": 5, "score": 8.2 }
                      ]
                    }
                """.trimIndent()
            )
        }
        val repository = createRepository(client)

        val result = repository.search(
            filters = MediaSearchFilters(
                mediaType = SearchMediaType.Manga,
                query = "manga",
                magazines = "1,2"
            ),
            page = 1
        )

        assertTrue(capturedUrl.contains("https://api.jikan.moe/v4/manga"))
        assertEquals("1,2", capturedMagazine)
        assertEquals(null, result.nextPage)
        assertEquals(1, result.items.size)
        assertEquals(SearchMediaType.Manga, result.items.first().mediaType)
        assertEquals("Manga Result", result.items.first().title)
    }

    private fun createRepository(client: HttpClient): SearchRepositoryImpl {
        val dispatcher = UnconfinedTestDispatcher()
        val dispatchers = AppDispatchers(
            default = dispatcher,
            io = dispatcher,
            main = dispatcher
        )
        val service = JikanService(client)
        val remoteDataSource = JikanSearchRemoteDataSource(
            service = service,
            dispatchers = dispatchers
        )

        return SearchRepositoryImpl(
            remoteDataSource = remoteDataSource,
            dispatchers = dispatchers
        )
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
}

package id.pusakakata.data.remote

import id.pusakakata.data.remote.dto.KbbiResponse
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

class ApiServiceTest {

    @Test
    fun testFetchDefinition_Success() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"status": true, "data": {"lema": "Sasmita", "arti": ["isyarat"]}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        val apiService = ApiService(client)
        val response = apiService.fetchDefinition("Sasmita")
        
        assertEquals("Sasmita", response.data?.lema)
    }
}

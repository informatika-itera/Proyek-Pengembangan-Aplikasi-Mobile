package id.pusakakata.data.remote

import id.pusakakata.data.remote.dto.KbbiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ApiService(private val client: HttpClient) {
    suspend fun fetchDefinition(word: String): KbbiResponse {
        // Menggunakan API yang lebih stabil dan menangani encoding
        return client.get("https://api-kbbi.vercel.app/api/kbbi") {
            parameter("text", word.trim())
        }.body()
    }
}

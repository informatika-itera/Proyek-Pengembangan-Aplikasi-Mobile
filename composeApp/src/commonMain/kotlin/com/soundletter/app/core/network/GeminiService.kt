package com.soundletter.app.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent
)

class GeminiService(private val httpClient: HttpClient) {
    private val apiKey = ApiConfig.geminiApiKey
    private val baseUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent"

    suspend fun getSongRecommendations(message: String): String {
        val prompt = "Berdasarkan curhatan ini: '$message', berikan 1 rekomendasi lagu (Format: Judul - Artis)."
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            )
        )

        return try {
            val response: GeminiResponse = httpClient.post(baseUrl) {
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()

            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Unknown Song"
        } catch (e: Exception) {
            "Unknown Song"
        }
    }
}

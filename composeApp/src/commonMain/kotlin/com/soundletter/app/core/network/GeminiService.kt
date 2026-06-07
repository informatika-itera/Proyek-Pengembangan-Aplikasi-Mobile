package com.soundletter.app.core.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(val contents: List<GeminiContent>)
@Serializable
data class GeminiContent(val parts: List<GeminiPart>)
@Serializable
data class GeminiPart(val text: String)
@Serializable
data class GeminiResponse(val candidates: List<GeminiCandidate>? = null)
@Serializable
data class GeminiCandidate(val content: GeminiContent)

class GeminiService(private val httpClient: HttpClient) {
    private val apiKey = ApiConfig.geminiApiKey.trim().replace("\"", "").replace("'", "")
    
    // Perbaikan URL: Menggunakan model Gemini 1.5 Flash yang stabil
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    suspend fun getSongRecommendations(message: String): String {
        if (apiKey.isBlank() || apiKey.startsWith("YOUR_")) return "Chill Acoustic"

        val prompt = """
            User message: "$message"
            Recommend ONE popular song that matches this mood.
            Format: Artist - Title
            Strictly ONLY output the Artist - Title.
        """.trimIndent()

        val request = GeminiRequest(contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))))

        return try {
            val response: HttpResponse = httpClient.post(baseUrl) {
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val body: GeminiResponse = response.body()
                val result = body.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                result?.trim() ?: "Acoustic Mood"
            } else {
                println("GEMINI_LOG: API Error ${response.status}")
                "Acoustic Mood"
            }
        } catch (e: Exception) {
            println("GEMINI_LOG: Exception ${e.message}")
            "Relaxing Music"
        }
    }
}

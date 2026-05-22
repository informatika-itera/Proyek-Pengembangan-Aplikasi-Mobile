package com.itera.news.data.remote.api

import com.itera.news.core.network.geminiApiKey
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GeminiRequest(val contents: List<Content>)
@Serializable
data class Content(val parts: List<Part>)
@Serializable
data class Part(val text: String)
@Serializable
data class GeminiResponse(val candidates: List<Candidate>)
@Serializable
data class Candidate(val content: Content)

class GeminiApi(private val httpClient: HttpClient) {
    suspend fun analyzeSentiment(title: String, description: String): String {
        val prompt = """
            Analisis sentimen berita berikut. Balas hanya dengan satu kata: "Pro", "Kontra", atau "Netral".
            Judul: $title
            Deskripsi: $description
        """.trimIndent()
        
        return try {
            val response = httpClient.post("https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=$geminiApiKey") {
                header("Content-Type", "application/json")
                setBody(GeminiRequest(listOf(Content(listOf(Part(prompt))))))
            }.body<GeminiResponse>()
            
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "Netral"
        } catch (e: Exception) {
            "Netral"
        }
    }
}
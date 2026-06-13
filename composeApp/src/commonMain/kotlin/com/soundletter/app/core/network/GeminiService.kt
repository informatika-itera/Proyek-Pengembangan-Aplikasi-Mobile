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

class GeminiService(
    private val httpClient: HttpClient,
    // Memungkinkan penyuntikkan fake_key saat pengujian agar tidak masuk mode fallback
    private val apiKeyOverride: String? = null
) {
    private val apiKey = apiKeyOverride ?: ApiConfig.geminiApiKey.trim().replace("\"", "").replace("'", "")
    
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    suspend fun getSongRecommendations(message: String): String {
        // Logika bypass jika key belum diatur atau masih default
        if (apiKey.isBlank() || apiKey.startsWith("YOUR_")) return "chill"

        val prompt = """
            Berdasarkan teks ini, berikan maksimal 2 kata kunci genre atau mood dalam bahasa Inggris yang dipisahkan oleh spasi (contoh: sad acoustic, happy pop, chill, dark rock). HANYA kembalikan kata kunci tersebut tanpa teks tambahan apa pun.
            
            Teks: "$message"
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
                // Menghasilkan output bersih seperti "sad lofi"
                body.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()?.lowercase() ?: "chill"
            } else {
                "chill"
            }
        } catch (e: Exception) {
            "ambient"
        }
    }
}

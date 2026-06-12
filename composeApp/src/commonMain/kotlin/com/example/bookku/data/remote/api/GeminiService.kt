package com.example.bookku.data.remote.api

import com.example.bookku.core.network.ApiConfig
import com.example.bookku.data.remote.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class GeminiService(private val client: HttpClient) {
    
    companion object {
        // Menggunakan v1 (Stable) secara eksplisit untuk model gemini-1.5-flash
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1"
        private const val MODEL = "gemini-1.5-flash" 
    }
    
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val apiKey = ApiConfig.geminiApiKey
        
        if (apiKey.isBlank() || apiKey == "null" || apiKey.contains("your_api_key")) {
            throw Exception("API Key tidak ditemukan. Pastikan sudah mengisi GEMINI_API_KEY di local.properties.")
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = if (systemPrompt != null) "$systemPrompt\n\n$prompt" else prompt))
                )
            ),
            generationConfig = GenerationConfig(temperature = 0.7, maxOutputTokens = 1000)
        )
        
        val endpoint = "$BASE_URL/models/$MODEL:generateContent"
        
        val httpResponse: HttpResponse = client.post(endpoint) {
            contentType(ContentType.Application.Json)
            parameter("key", apiKey)
            setBody(request)
        }

        when (httpResponse.status) {
            HttpStatusCode.OK -> {
                val responseBody = httpResponse.body<GeminiResponse>()
                responseBody.getTextContent() ?: throw Exception("AI memberikan respons kosong.")
            }
            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> {
                throw Exception("API Key tidak valid (401/403). Periksa kunci Anda.")
            }
            HttpStatusCode.NotFound -> {
                // Memberikan pesan spesifik jika model tidak ditemukan di v1
                throw Exception("Model '$MODEL' tidak ditemukan di $BASE_URL. Pastikan region Anda mendukung model ini.")
            }
            else -> {
                val errorBody = runCatching { httpResponse.body<GeminiResponse>() }.getOrNull()
                val serverMsg = errorBody?.getErrorMessage() ?: "Status: ${httpResponse.status}"
                throw Exception("Server AI: $serverMsg")
            }
        }
    }
}

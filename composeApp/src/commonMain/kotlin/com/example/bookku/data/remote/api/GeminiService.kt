package com.example.bookku.data.remote.api

import com.example.bookku.core.network.ApiConfig
import com.example.bookku.data.remote.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.serialization.json.Json

class GeminiService(private val client: HttpClient) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        // Menggunakan v1beta (Lebih stabil untuk Flash 1.5 dan streaming SSE)
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val MODEL = "gemini-1.5-flash" 
    }
    
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val apiKey = ApiConfig.geminiApiKey
        
        if (apiKey.isBlank() || apiKey == "null") {
            throw Exception("API Key kosong. Masukkan kunci valid di local.properties dan REBUILD project.")
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
                throw Exception("API Key tidak valid (401/403).")
            }
            else -> {
                val errorBody = runCatching { httpResponse.body<GeminiResponse>() }.getOrNull()
                val serverMsg = errorBody?.getErrorMessage() ?: "Status: ${httpResponse.status}"
                throw Exception("Server AI: $serverMsg")
            }
        }
    }.getOrElse { e ->
        val msg = e.message ?: ""
        if (msg.contains("Unable to resolve host") || msg.contains("No address associated")) {
            Result.failure(Exception("Koneksi Internet Error: Emulator Anda tidak terhubung ke internet. Mohon lakukan 'Cold Boot' pada Emulator Anda melaui Device Manager."))
        } else {
            Result.failure(e)
        }
    }

    fun generateContentStream(
        prompt: String,
        systemPrompt: String? = null
    ): Flow<String> = flow {
        val apiKey = ApiConfig.geminiApiKey
        
        if (apiKey.isBlank() || apiKey == "null") {
            throw Exception("API Key tidak ditemukan.")
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = if (systemPrompt != null) "$systemPrompt\n\n$prompt" else prompt))
                )
            ),
            generationConfig = GenerationConfig(temperature = 0.7, maxOutputTokens = 1000)
        )
        
        val endpoint = "$BASE_URL/models/$MODEL:streamGenerateContent?alt=sse&key=$apiKey"
        
        client.preparePost(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.execute { httpResponse ->
            if (httpResponse.status != HttpStatusCode.OK) {
                val errorText = httpResponse.bodyAsText()
                throw Exception("Gagal streaming: $errorText")
            }

            val channel = httpResponse.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data: ")) {
                    val jsonString = line.substring(6)
                    try {
                        val response = json.decodeFromString<GeminiResponse>(jsonString)
                        val text = response.getTextContent()
                        if (text != null) {
                            emit(text)
                        }
                    } catch (e: Exception) {
                        // Skip malformed JSON
                    }
                }
            }
        }
    }.catch { e ->
        val msg = e.message ?: ""
        if (msg.contains("Unable to resolve host") || msg.contains("No address associated")) {
            throw Exception("Koneksi Internet Error: Emulator Anda tidak terhubung ke internet. Mohon lakukan 'Cold Boot' pada Emulator.")
        } else {
            throw e
        }
    }
}

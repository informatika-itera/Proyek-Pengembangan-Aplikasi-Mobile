package com.dailybliss.app.data.remote.api

import com.dailybliss.app.core.network.ApiConfig
import com.dailybliss.app.data.remote.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class GeminiService(private val client: HttpClient) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val MAX_RETRIES = 5
        private const val INITIAL_DELAY_MS = 1000L
    }

    private suspend fun <T> retryWithBackoff(
        maxRetries: Int = MAX_RETRIES,
        initialDelay: Long = INITIAL_DELAY_MS,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(maxRetries) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) throw e
                delay(currentDelay)
                currentDelay *= 2
            }
        }
        throw Exception("Gagal setelah beberapa percobaan")
    }
    
    suspend fun streamContent(
        contents: List<GeminiContent>,
        systemInstruction: String? = null
    ): Flow<String> = flow {
        val modelName = ApiConfig.geminiModelName.ifBlank { "gemini-1.5-flash" }
        
        // Use streamGenerateContent for streaming
        val url = "$BASE_URL/models/$modelName:streamGenerateContent?alt=sse"
        
        val request = GeminiRequest(
            contents = contents,
            system_instruction = systemInstruction?.let { 
                GeminiSystemInstruction(parts = listOf(GeminiPart(text = it))) 
            },
            generationConfig = GenerationConfig(
                temperature = 0.8,
                maxOutputTokens = 2000
            )
        )
        
        retryWithBackoff {
            client.preparePost(url) {
                header("x-goog-api-key", ApiConfig.geminiApiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
                timeout {
                    requestTimeoutMillis = 60_000
                    socketTimeoutMillis = 60_000
                }
            }.execute { response ->
                if (response.status != HttpStatusCode.OK) {
                    val errorBody = response.bodyAsText()
                    throw Exception("API Error (${response.status.value}): $errorBody")
                }

                val channel = response.bodyAsChannel()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (line.startsWith("data: ")) {
                        val data = line.substring(6).trim()
                        if (data.isEmpty()) continue
                        
                        try {
                            val geminiResponse = json.decodeFromString<GeminiResponse>(data)
                            val text = geminiResponse.getTextContent()
                            if (text != null) {
                                emit(text)
                            }
                        } catch (e: Exception) { }
                    }
                }
            }
        }
    }

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        retryWithBackoff {
            val modelName = ApiConfig.geminiModelName.ifBlank { "gemini-1.5-flash" }
            
            val contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt)),
                    role = "user"
                )
            )
            
            val url = "$BASE_URL/models/$modelName:generateContent"
            
            val request = GeminiRequest(
                contents = contents,
                system_instruction = systemPrompt?.let { 
                    GeminiSystemInstruction(parts = listOf(GeminiPart(text = it))) 
                }
            )

            val response: HttpResponse = client.post(url) {
                header("x-goog-api-key", ApiConfig.geminiApiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            if (response.status != HttpStatusCode.OK) {
                val errorBody = response.bodyAsText()
                throw Exception("API Error (${response.status.value}): $errorBody")
            }
            
            val geminiResponse = response.body<GeminiResponse>()
            geminiResponse.getErrorMessage()?.let { throw Exception(it) }
            geminiResponse.getTextContent() ?: throw Exception("Respons kosong")
        }
    }

    suspend fun generateChat(
        contents: List<GeminiContent>,
        systemInstruction: String? = null
    ): Result<String> = runCatching {
        retryWithBackoff {
            val modelName = ApiConfig.geminiModelName.ifBlank { "gemini-1.5-flash" }
            val url = "$BASE_URL/models/$modelName:generateContent"
            
            val request = GeminiRequest(
                contents = contents,
                system_instruction = systemInstruction?.let { 
                    GeminiSystemInstruction(parts = listOf(GeminiPart(text = it))) 
                },
                generationConfig = GenerationConfig(
                    temperature = 0.85, // Slightly higher for more natural flow
                    maxOutputTokens = 2000
                )
            )

            val response: HttpResponse = client.post(url) {
                header("x-goog-api-key", ApiConfig.geminiApiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            if (response.status != HttpStatusCode.OK) {
                val errorBody = response.bodyAsText()
                throw Exception("API Error (${response.status.value}): $errorBody")
            }
            
            val geminiResponse = response.body<GeminiResponse>()
            geminiResponse.getErrorMessage()?.let { throw Exception(it) }
            geminiResponse.getTextContent() ?: throw Exception("Respons kosong")
        }
    }
}

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class GeminiService(private val client: HttpClient) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    }
    
    suspend fun streamContent(
        contents: List<GeminiContent>
    ): Flow<String> = flow {
        val model = ApiConfig.geminiModelName
        if (model.isBlank()) {
            throw Exception("GEMINI_MODEL_NAME belum diset di local.properties. AI tidak dapat digunakan.")
        }
        
        val url = "$BASE_URL/models/$model:streamGenerateContent?alt=sse"
        
        val request = GeminiRequest(
            contents = contents,
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 2000
            )
        )
        
        try {
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
        } catch (e: Exception) {
            throw Exception("Terjadi kesalahan: ${e.message}")
        }
    }

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val model = ApiConfig.geminiModelName
        if (model.isBlank()) {
            throw Exception("GEMINI_MODEL_NAME belum diset di local.properties.")
        }

        val contents = mutableListOf<GeminiContent>()
        
        if (systemPrompt != null) {
            contents.add(
                GeminiContent(
                    parts = listOf(GeminiPart(text = systemPrompt)),
                    role = "user"
                )
            )
            contents.add(
                GeminiContent(
                    parts = listOf(GeminiPart(text = "Baik, saya mengerti instruksinya.")),
                    role = "model"
                )
            )
        }
        
        contents.add(
            GeminiContent(
                parts = listOf(GeminiPart(text = prompt)),
                role = "user"
            )
        )
        
        val url = "$BASE_URL/models/$model:generateContent"
        
        val response: HttpResponse = client.post(url) {
            header("x-goog-api-key", ApiConfig.geminiApiKey)
            contentType(ContentType.Application.Json)
            setBody(GeminiRequest(contents = contents))
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

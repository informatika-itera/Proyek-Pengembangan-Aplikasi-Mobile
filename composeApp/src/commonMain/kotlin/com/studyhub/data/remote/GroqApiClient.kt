package com.studyhub.data.remote

import com.studyhub.core.network.ApiConfig
import com.studyhub.data.remote.dto.GroqMessage
import com.studyhub.data.remote.dto.GroqRequest
import com.studyhub.data.remote.dto.GroqResponse
import com.studyhub.data.remote.dto.getErrorMessage
import com.studyhub.data.remote.dto.getTextContent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class GroqApiClient(private val client: HttpClient) {
    
    companion object {
        private const val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"
        private const val MODEL = "llama-3.3-70b-versatile"
    }
    
    suspend fun getChatCompletion(prompt: String): String {
        val request = GroqRequest(
            model = MODEL,
            messages = listOf(
                GroqMessage(
                    role = "user",
                    content = prompt
                )
            ),
            temperature = 0.5,
            max_tokens = 1024
        )
        
        val httpResponse = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${ApiConfig.groqApiKey}")
            setBody(request)
        }

        if (!httpResponse.status.isSuccess()) {
            val errorBody = httpResponse.bodyAsText()
            try {
                val groqResponse = Json { ignoreUnknownKeys = true }.decodeFromString<GroqResponse>(errorBody)
                val msg = groqResponse.getErrorMessage() ?: "Error ${httpResponse.status.value}"
                throw Exception(msg)
            } catch (e: Exception) {
                throw Exception("HTTP ${httpResponse.status.value}: ${errorBody.take(100)}")
            }
        }

        val response: GroqResponse = httpResponse.body()
        
        response.getErrorMessage()?.let { errorMsg ->
            throw Exception(errorMsg)
        }
        
        return response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }
}

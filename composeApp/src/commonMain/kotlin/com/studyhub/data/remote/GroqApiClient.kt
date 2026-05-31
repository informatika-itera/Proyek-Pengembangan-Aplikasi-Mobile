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
import io.ktor.http.contentType

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
        
        val response: GroqResponse = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${ApiConfig.groqApiKey}")
            setBody(request)
        }.body()
        
        response.getErrorMessage()?.let { errorMsg ->
            throw Exception(errorMsg)
        }
        
        return response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }
}

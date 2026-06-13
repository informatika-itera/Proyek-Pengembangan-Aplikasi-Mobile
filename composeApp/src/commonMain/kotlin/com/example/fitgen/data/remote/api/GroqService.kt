package com.example.fitgen.data.remote.api

import com.example.fitgen.BuildKonfig
import com.example.fitgen.data.remote.dto.GroqChatRequest
import com.example.fitgen.data.remote.dto.GroqChatResponse
import com.example.fitgen.data.remote.dto.GroqContentPart
import com.example.fitgen.data.remote.dto.GroqImageUrl
import com.example.fitgen.data.remote.dto.GroqMessage
import com.example.fitgen.data.remote.dto.GroqVisionChatRequest
import com.example.fitgen.data.remote.dto.GroqVisionMessage
import com.example.fitgen.data.remote.dto.getTextContent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.encodeBase64

class GroqService(private val client: HttpClient) {
    
    companion object {
        private const val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"
        // Menggunakan model llama-3.3-70b-versatile karena llama3-70b-8192 sudah deprecated
        private const val MODEL = "llama-3.3-70b-versatile"
    }
    
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val messages = mutableListOf<GroqMessage>()
        
        if (systemPrompt != null) {
            messages.add(
                GroqMessage(
                    role = "system",
                    content = systemPrompt
                )
            )
        }
        
        messages.add(
            GroqMessage(
                role = "user",
                content = prompt
            )
        )
        
        val request = GroqChatRequest(
            model = MODEL,
            messages = messages,
            temperature = 0.7,
            maxTokens = 1000
        )
        
        try {
            val httpResponse: HttpResponse = client.post(BASE_URL) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${BuildKonfig.GROQ_API_KEY}")
                setBody(request)
            }
            
            if (!httpResponse.status.isSuccess()) {
                val errorBody = httpResponse.bodyAsText()
                throw Exception("Groq API Error (${httpResponse.status.value}): $errorBody")
            }
            
            val response = httpResponse.body<GroqChatResponse>()
            response.getTextContent() ?: throw Exception("Respons kosong dari Groq AI")
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("resolve host", ignoreCase = true) || 
                msg.contains("UnknownHostException", ignoreCase = true) || 
                msg.contains("ConnectException", ignoreCase = true) ||
                msg.contains("failed to connect", ignoreCase = true)) {
                throw Exception("Tidak ada koneksi internet. Silakan periksa jaringan Anda.")
            }
            throw e
        }
    }

    suspend fun analyzeImage(prompt: String, imageBytes: ByteArray): String {
        val base64Image = imageBytes.encodeBase64()
        
        val messages = listOf(
            GroqVisionMessage(
                role = "user",
                content = listOf(
                    GroqContentPart(type = "text", text = prompt),
                    GroqContentPart(
                        type = "image_url",
                        imageUrl = GroqImageUrl(url = "data:image/jpeg;base64,$base64Image")
                    )
                )
            )
        )
        
        val request = GroqVisionChatRequest(
            model = "llama-3.2-11b-vision-preview",
            messages = messages,
            temperature = 0.7,
            maxTokens = 1000
        )
        
        try {
            val httpResponse: HttpResponse = client.post(BASE_URL) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${BuildKonfig.GROQ_API_KEY}")
                setBody(request)
            }
            
            if (!httpResponse.status.isSuccess()) {
                val errorBody = httpResponse.bodyAsText()
                throw Exception("Groq API Error (${httpResponse.status.value}): $errorBody")
            }
            
            val response = httpResponse.body<GroqChatResponse>()
            return response.getTextContent() ?: throw Exception("Respons kosong dari Groq AI")
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("resolve host", ignoreCase = true) || 
                msg.contains("UnknownHostException", ignoreCase = true) || 
                msg.contains("ConnectException", ignoreCase = true) ||
                msg.contains("failed to connect", ignoreCase = true)) {
                throw Exception("Tidak ada koneksi internet. Silakan periksa jaringan Anda.")
            }
            throw e
        }
    }
}

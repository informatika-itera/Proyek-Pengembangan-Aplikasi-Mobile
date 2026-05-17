package com.studyhub.data.remote.api

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

class GroqService(private val client: HttpClient) {
    
    companion object {
        private const val BASE_URL = "https://api.groq.com/openai/v1/chat/completions"
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
        
        val request = GroqRequest(
            model = MODEL,
            messages = messages,
            temperature = 0.7,
            max_tokens = 2048
        )
        
        val response: GroqResponse = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${ApiConfig.groqApiKey}")
            setBody(request)
        }.body()
        
        response.getErrorMessage()?.let { errorMsg ->
            throw Exception(errorMsg)
        }
        
        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }
}

object SystemPrompts {
    
    val SUMMARIZER = """
        Kamu adalah asisten yang ahli dalam merangkum teks.
        Tugas: Rangkum teks yang diberikan menjadi poin-poin utama yang singkat dan jelas.
        Rules:
        - Gunakan Bahasa Indonesia
        - Maksimal 3-5 poin utama
        - Setiap poin maksimal 1-2 kalimat
        - Fokus pada informasi paling penting
        - Jangan menambahkan informasi yang tidak ada di teks asli
    """.trimIndent()
    
    val STUDY_INSIGHT = """
        Kamu adalah asisten produktivitas cerdas untuk aplikasi StudyHub.
        Tugas: Analisis data aktivitas belajar pengguna dan berikan satu insight singkat yang memotivasi dan informatif.
        Insight harus mencakup tren waktu produktif atau saran perbaikan jadwal.
        Rules:
        - Gunakan Bahasa Indonesia yang ramah dan profesional
        - Maksimal 2-3 kalimat
        - Jangan berikan angka statistik mentah, tapi berikan interpretasi yang berguna.
        - Fokus pada efisiensi belajar.
    """.trimIndent()
}

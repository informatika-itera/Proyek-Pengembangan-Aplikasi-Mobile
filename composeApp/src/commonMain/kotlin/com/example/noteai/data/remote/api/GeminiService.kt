package com.example.noteai.data.remote.api

import com.example.noteai.core.network.ApiConfig
import com.example.noteai.data.remote.dto.GeminiContent
import com.example.noteai.data.remote.dto.GeminiPart
import com.example.noteai.data.remote.dto.GeminiRequest
import com.example.noteai.data.remote.dto.GeminiResponse
import com.example.noteai.data.remote.dto.GenerationConfig
import com.example.noteai.data.remote.dto.getErrorMessage
import com.example.noteai.data.remote.dto.getTextContent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GeminiService(private val client: HttpClient) {
    
    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
        private const val MODEL = "gemini-2.0-flash"
    }
    
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
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
                    parts = listOf(GeminiPart(text = "Baik, saya akan menjadi asisten koki Anda.")),
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
        
        val request = GeminiRequest(
            contents = contents,
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 1500
            )
        )
        
        val response: GeminiResponse = client.post("$BASE_URL/models/$MODEL:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", ApiConfig.geminiApiKey)
            setBody(request)
        }.body()
        
        response.getErrorMessage()?.let { errorMsg ->
            throw Exception(errorMsg)
        }
        
        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }
}

object SystemPrompts {
    val CHEF_ASSISTANT = """
        Kamu adalah Cooknote AI, asisten koki pintar yang ahli dalam membantu pengguna mengelola pantry dan membuat resep.
        Tugas utama:
        1. Memberikan saran masakan berdasarkan bahan yang tersedia di pantry.
        2. Memberikan resep lengkap (bahan & instruksi) yang mudah diikuti.
        3. Membantu memberikan tips memasak dan substitusi bahan.
        
        Rules:
        - Gunakan Bahasa Indonesia yang ramah.
        - Jika pengguna bertanya "Mau masak apa hari ini?", berikan beberapa inspirasi masakan populer atau sehat.
        - Berikan instruksi memasak langkah demi langkah yang jelas.
        - Fokus pada efisiensi bahan untuk mengurangi limbah makanan.
    """.trimIndent()

    val SUMMARIZER = "Kamu adalah asisten yang ahli dalam merangkum teks resep."
    val IDEA_GENERATOR = "Kamu adalah asisten kreatif yang membantu memberikan ide masakan."
    val WRITING_IMPROVER = "Kamu adalah editor profesional yang membantu memperbaiki penulisan resep."
    val TITLE_SUGGESTER = "Kamu adalah asisten yang membantu membuat judul resep yang menarik."
    val TRANSLATOR = "Kamu adalah penerjemah resep profesional."
}

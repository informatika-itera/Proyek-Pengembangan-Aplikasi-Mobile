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
    
    val IDEA_GENERATOR = """
        Kamu adalah asisten kreatif yang membantu mengembangkan ide.
        Tugas: Berikan 5 ide kreatif berdasarkan topik yang diberikan.
        Rules:
        - Gunakan Bahasa Indonesia
        - Berikan tepat 5 ide
        - Setiap ide harus unik dan berbeda
        - Format: nomor diikuti ide (contoh: "1. Ide pertama")
        - Ide harus praktis dan bisa diimplementasikan
    """.trimIndent()
    
    val WRITING_IMPROVER = """
        Kamu adalah editor profesional yang membantu memperbaiki tulisan.
        Tugas: Perbaiki tulisan yang diberikan tanpa mengubah makna aslinya.
        Rules:
        - Gunakan Bahasa Indonesia yang baik dan benar
        - Perbaiki grammar, ejaan, dan struktur kalimat
        - Pertahankan gaya dan tone asli penulis
        - Jangan menambahkan informasi baru
        - Berikan HANYA hasil tulisan yang sudah diperbaiki, tanpa penjelasan
    """.trimIndent()
    
    val TITLE_SUGGESTER = """
        Kamu adalah asisten yang membantu membuat judul menarik.
        Tugas: Berikan 1 saran judul yang singkat dan menarik berdasarkan konten yang diberikan.
        Rules:
        - Gunakan Bahasa Indonesia
        - Judul maksimal 5-7 kata
        - Judul harus mencerminkan isi konten
        - Berikan HANYA judul, tanpa penjelasan atau tanda kutip
    """.trimIndent()
    
    val TRANSLATOR = """
        Kamu adalah penerjemah profesional.
        Tugas: Terjemahkan teks yang diberikan ke bahasa target.
        Rules:
        - Pertahankan makna dan nuansa asli
        - Gunakan bahasa yang natural, bukan literal
        - Berikan HANYA hasil terjemahan, tanpa penjelasan
    """.trimIndent()
}

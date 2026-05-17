package com.example.Feelia.data.remote.api

import com.example.Feelia.core.network.ApiConfig
import com.example.Feelia.data.remote.dto.GeminiContent
import com.example.Feelia.data.remote.dto.GeminiPart
import com.example.Feelia.data.remote.dto.GeminiRequest
import com.example.Feelia.data.remote.dto.GeminiResponse
import com.example.Feelia.data.remote.dto.GenerationConfig
import com.example.Feelia.data.remote.dto.getErrorMessage
import com.example.Feelia.data.remote.dto.getTextContent
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
                    parts = listOf(GeminiPart(text = "Baik, saya akan mengikuti instruksi tersebut.")),
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
                maxOutputTokens = 1000
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
// membuat prompt emotion detector, prompt emotional insight dan update AI context ke Feelia
// ====================
// System Prompts
// ====================

object SystemPrompts {

    val EMOTION_DETECTOR = """
        Kamu adalah AI yang menganalisis emosi dari teks jurnal harian.
        Tugas: Analisis teks dan tentukan SATU emosi dominan dari pilihan berikut:
        HAPPY, SAD, ANXIOUS, ANGRY, NEUTRAL
        
        Rules:
        - Jawab HANYA dengan satu kata dari pilihan di atas
        - Gunakan konteks keseluruhan kalimat, bukan hanya kata-kata tertentu
        - Jika tidak jelas, pilih NEUTRAL
        - Tidak ada penjelasan tambahan, hanya satu kata
    """.trimIndent()

    val EMOTION_INSIGHT = """
        Kamu adalah teman yang hangat dan empatik, membantu pengguna memahami perasaan mereka.
        Tugas: Berikan insight singkat dan supportif berdasarkan jurnal emosi yang diberikan.
        
        Rules:
        - Gunakan Bahasa Indonesia yang hangat dan personal
        - Maksimal 2-3 kalimat
        - Validasi perasaan pengguna, jangan menghakimi
        - Berikan satu saran kecil yang praktis jika relevan
        - Gunakan kata "kamu" bukan "Anda"
    """.trimIndent()

    val SUMMARIZER = """
        Kamu adalah asisten yang ahli dalam merangkum teks.
        Tugas: Rangkum teks yang diberikan menjadi poin-poin utama yang singkat dan jelas.
        Rules:
        - Gunakan Bahasa Indonesia
        - Maksimal 3-5 poin utama
        - Setiap poin maksimal 1-2 kalimat
        - Fokus pada informasi paling penting
    """.trimIndent()

    val WRITING_IMPROVER = """
        Kamu adalah editor profesional yang membantu memperbaiki tulisan.
        Tugas: Perbaiki tulisan yang diberikan tanpa mengubah makna aslinya.
        Rules:
        - Gunakan Bahasa Indonesia yang baik dan benar
        - Perbaiki grammar, ejaan, dan struktur kalimat
        - Pertahankan gaya dan tone asli penulis
        - Berikan HANYA hasil tulisan yang sudah diperbaiki, tanpa penjelasan
    """.trimIndent()

    val IDEA_GENERATOR = """
        Kamu adalah asisten kreatif yang membantu mengembangkan ide.
        Tugas: Berikan 5 ide kreatif berdasarkan topik yang diberikan.
        Rules:
        - Gunakan Bahasa Indonesia
        - Berikan tepat 5 ide
        - Format: nomor diikuti ide (contoh: "1. Ide pertama")
    """.trimIndent()

    val TITLE_SUGGESTER = """
        Kamu adalah asisten yang membantu membuat judul menarik.
        Tugas: Berikan 1 saran judul singkat berdasarkan konten yang diberikan.
        Rules:
        - Gunakan Bahasa Indonesia
        - Judul maksimal 5-7 kata
        - Berikan HANYA judul, tanpa penjelasan atau tanda kutip
    """.trimIndent()

    val TRANSLATOR = """
        Kamu adalah penerjemah profesional.
        Tugas: Terjemahkan teks yang diberikan ke bahasa target.
        Rules:
        - Pertahankan makna dan nuansa asli
        - Berikan HANYA hasil terjemahan, tanpa penjelasan
    """.trimIndent()
}

package com.example.foodsaver.data.remote.api

import com.example.foodsaver.core.network.ApiConfig
import com.example.foodsaver.data.remote.dto.GeminiContent
import com.example.foodsaver.data.remote.dto.GeminiPart
import com.example.foodsaver.data.remote.dto.GeminiRequest
import com.example.foodsaver.data.remote.dto.GeminiResponse
import com.example.foodsaver.data.remote.dto.GenerationConfig
import com.example.foodsaver.data.remote.dto.getErrorMessage
import com.example.foodsaver.data.remote.dto.getTextContent
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
        private const val MODEL = "gemini-1.5-flash"
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
                    parts = listOf(GeminiPart(text = "Baik, saya asisten FoodSaver siap membantu.")),
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
    
    val RECIPE_SUGGESTER = """
        Kamu adalah asisten dapur cerdas dari aplikasi FoodSaver.
        Tugas: Berikan rekomendasi resep masakan berdasarkan daftar bahan makanan yang hampir kedaluwarsa yang diberikan pengguna.
        Aturan:
        - Gunakan Bahasa Indonesia.
        - Prioritaskan bahan yang disebutkan oleh pengguna.
        - Berikan instruksi memasak yang singkat dan jelas.
        - Tambahkan estimasi waktu memasak.
        - Format: Judul Resep, Bahan, dan Langkah-langkah.
    """.trimIndent()
    
    val EXPIRY_ADVISOR = """
        Kamu adalah ahli pengawetan makanan.
        Tugas: Berikan saran cara menyimpan bahan makanan tertentu agar lebih tahan lama.
        Aturan:
        - Gunakan Bahasa Indonesia.
        - Berikan saran yang praktis dan mudah dilakukan di rumah.
        - Fokus pada pencegahan limbah makanan.
    """.trimIndent()

    val SUMMARIZER = """
        Kamu adalah asisten pengambil catatan yang ahli.
        Tugas: Buat ringkasan yang padat namun informatif dari teks yang diberikan.
        Aturan:
        - Gunakan Bahasa Indonesia.
        - Fokus pada poin-poin utama.
        - Jangan hilangkan informasi krusial.
    """.trimIndent()

    val IDEA_GENERATOR = """
        Kamu adalah asisten kreatif yang penuh ide.
        Tugas: Berikan ide-ide kreatif dan inspiratif berdasarkan topik yang diberikan.
        Aturan:
        - Berikan dalam bentuk daftar poin.
        - Gunakan Bahasa Indonesia.
    """.trimIndent()

    val WRITING_IMPROVER = """
        Kamu adalah editor tulisan profesional.
        Tugas: Perbaiki tata bahasa, struktur kalimat, dan pilihan kata agar tulisan menjadi lebih baik sesuai gaya yang diminta.
        Aturan:
        - Tetap pertahankan makna asli dari teks.
        - Gunakan Bahasa Indonesia.
    """.trimIndent()

    val TRANSLATOR = """
        Kamu adalah penerjemah multibahasa yang akurat.
        Tugas: Terjemahkan teks ke bahasa target yang diminta dengan tetap mempertahankan nuansa dan konteksnya.
    """.trimIndent()

    val TITLE_SUGGESTER = """
        Kamu adalah copywriter ahli.
        Tugas: Berikan saran judul yang menarik dan relevan untuk konten yang diberikan.
        Aturan:
        - Berikan 1 judul terbaik saja.
        - Gunakan Bahasa Indonesia.
    """.trimIndent()
}

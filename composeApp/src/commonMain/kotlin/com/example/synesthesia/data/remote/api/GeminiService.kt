package com.example.synesthesia.data.remote.api

import com.example.synesthesia.core.network.ApiConfig
import com.example.synesthesia.data.remote.dto.GeminiContent
import com.example.synesthesia.data.remote.dto.GeminiPart
import com.example.synesthesia.data.remote.dto.GeminiRequest
import com.example.synesthesia.data.remote.dto.GeminiResponse
import com.example.synesthesia.data.remote.dto.GenerationConfig
import com.example.synesthesia.data.remote.dto.getErrorMessage
import com.example.synesthesia.data.remote.dto.getTextContent
import com.example.synesthesia.data.remote.dto.EmotionAnalysisResponse
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
        private const val MODEL = "gemini-2.5-flash"
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
        
        val response: GeminiResponse = try {
            client.post("$BASE_URL/models/$MODEL:generateContent") {
                contentType(ContentType.Application.Json)
                parameter("key", ApiConfig.geminiApiKey)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            // Log for debugging if possible, or rethrow more specifically
            throw Exception("AI Connection Error: ${e.message}")
        }
        
        response.getErrorMessage()?.let { errorMsg ->
            throw Exception(errorMsg)
        }
        
        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }

    suspend fun analyzeEmotion(journalText: String): Result<EmotionAnalysisResponse> = runCatching {
        val contents = listOf(
            GeminiContent(
                parts = listOf(GeminiPart(text = SystemPrompts.EMOTION_ANALYZER)),
                role = "user"
            ),
            GeminiContent(
                parts = listOf(GeminiPart(text = "Tentu, berikan teks jurnalnya dan saya akan membalas HANYA dengan format JSON yang diminta.")),
                role = "model"
            ),
            GeminiContent(
                parts = listOf(GeminiPart(text = journalText)),
                role = "user"
            )
        )

        val request = GeminiRequest(
            contents = contents,
            generationConfig = GenerationConfig(
                temperature = 0.2,
                maxOutputTokens = 1000,
                responseMimeType = "application/json"
            )
        )

        val response: GeminiResponse = try {
            client.post("$BASE_URL/models/$MODEL:generateContent") {
                contentType(ContentType.Application.Json)
                parameter("key", ApiConfig.geminiApiKey)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            // Log for debugging if possible, or rethrow more specifically
            throw Exception("AI Connection Error: ${e.message}")
        }

        response.getErrorMessage()?.let { throw Exception(it) }

        val jsonString = response.getTextContent() ?: throw Exception("Respons kosong dari AI")

        val jsonParser = kotlinx.serialization.json.Json { 
            ignoreUnknownKeys = true 
            isLenient = true
        }
        jsonParser.decodeFromString<EmotionAnalysisResponse>(jsonString)
    }
}

// ====================
// System Prompts
// ====================

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

    val EMOTION_ANALYZER = """
        Kamu adalah AI penganalisis emosi untuk aplikasi jurnal "Synesthesia". 
        Tugasmu adalah menganalisis teks jurnal pengguna dan merangkumnya menjadi entitas memori yang indah.
        
        Sistem emosi kita memiliki 4 kuadran:
        1. HEP (High Energy, Pleasant): Lively, Enthusiastic, Exuberant, Elated, Ecstatic
        2. HEU (High Energy, Unpleasant): Agitated, Volatile, Frantic, Furious, Frenzied
        3. LEP (Low Energy, Pleasant): Relaxed, Mellow, Peaceful, Serene, Tranquil
        4. LEU (Low Energy, Unpleasant): Disappointed, Weary, Gloomy, Desolate, Lethargic
        
        Gunakan struktur JSON ini:
        {
            "autoTitle": "Judul singkat (3-5 kata) yang merangkum cerita",
            "paraphrasedContent": "Teks jurnal yang dirapikan tanpa mengubah makna asli (lebih puitis dan mengalir)",
            "emotionQuadrant": "ID Kuadran (HEP/HEU/LEP/LEU)",
            "subEmotion": "Satu kata emosi spesifik dari daftar di atas yang paling cocok",
            "artColorHex": "Kode warna HEX (HEP:#FFC107, HEU:#FF5722, LEP:#4CAF50, LEU:#3F51B5)",
            "summary": "Satu kalimat puitis singkat sebagai 'AI Resonance'."
        }
        
        Rules:
        - Jika teks jurnal terlalu pendek, tetap berikan judul dan parafrase yang relevan.
        - Balas HANYA dengan JSON.
    """.trimIndent()
}

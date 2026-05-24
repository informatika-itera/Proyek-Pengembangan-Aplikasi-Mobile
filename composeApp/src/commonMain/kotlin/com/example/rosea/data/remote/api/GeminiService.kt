package com.example.rosea.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText // <--- Import sudah dipindah ke atas
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import com.example.rosea.BuildConfig

class GeminiService(
    private val client: HttpClient
) {
    suspend fun generateContent(prompt: String, systemPrompt: String): Result<String> {
        return runCatching {
            // 1. Simpan respons mentah terlebih dahulu
            val httpResponse = client.post("https://api.groq.com/openai/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${BuildConfig.GEMINI_API_KEY}")

                setBody(
                    GroqRequestDto(
                        model = MODEL,
                        messages = listOf(
                            GroqMessageDto(role = "system", content = systemPrompt),
                            GroqMessageDto(role = "user", content = prompt)
                        )
                    )
                )
            }

            // 2. Cek apakah server membalas dengan status sukses (200-299)
            if (httpResponse.status.value !in 200..299) {
                // Jika gagal, baca pesan error aslinya dari server Groq
                val errorBody = httpResponse.bodyAsText()
                throw Exception("Groq Error (${httpResponse.status.value}): $errorBody")
            }

            // 3. Jika sukses, baru kita parsing JSON-nya dengan aman
            val response: GroqResponseDto = httpResponse.body()

            response.choices.firstOrNull()?.message?.content
                ?: throw Exception("Gagal mendapatkan balasan dari Groq AI")
        }
    } // <--- Kurung kurawal sudah diperbaiki jumlahnya

    companion object {
        // Menggunakan model generasi terbaru yang aktif di Groq
        private const val MODEL = "llama-3.3-70b-versatile"
    }
}

// ==================== DATA TRANSFER OBJECTS (DTO) UNTUK GROQ API ====================

@Serializable
data class GroqRequestDto(
    val model: String,
    val messages: List<GroqMessageDto>
)

@Serializable
data class GroqMessageDto(
    val role: String,
    val content: String
)

@Serializable
data class GroqResponseDto(
    val choices: List<GroqChoiceDto>
)

@Serializable
data class GroqChoiceDto(
    val message: GroqMessageResponseDto
)

@Serializable
data class GroqMessageResponseDto(
    val content: String
)

// ==================== SYSTEM PROMPTS (KEPRIBADIAN AI) ====================

object SystemPrompts {
    val BEAUTY_ADVISOR = """
        Kamu adalah ROSÉA AI, seorang Beauty Advisor profesional, ramah, dan ahli skincare/kosmetik.
        Rules:
        - JAWAB DENGAN SANGAT SINGKAT, PADAT, DAN JELAS (maksimal 1-2 paragraf).
        - Gunakan Bahasa Indonesia yang natural, santai, dan bersahabat.
        - Gunakan emoji secukupnya (seperti ✨, 🌸, 💖).
        - PENTING: JANGAN membahas atau menyebutkan produk yang ada di keranjang pengguna, KECUALI pengguna secara spesifik bertanya tentang produk tersebut atau meminta saran yang berhubungan dengannya. Fokus saja menjawab pertanyaan utama pengguna.
        - Jika pengguna menanyakan hal di luar kecantikan, tolak dengan halus.
    """.trimIndent()

    val SUMMARIZER = "Kamu adalah asisten pembantu rangkuman teks."
    val IDEA_GENERATOR = "Kamu adalah asisten pembuat ide kreatif."
    val WRITING_IMPROVER = "Kamu adalah asisten pembuat struktur tulisan yang baik."
    val TRANSLATOR = "Kamu adalah penerjemah bahasa yang akurat."
    val TITLE_SUGGESTER = "Kamu adalah pembuat saran judul konten."
}
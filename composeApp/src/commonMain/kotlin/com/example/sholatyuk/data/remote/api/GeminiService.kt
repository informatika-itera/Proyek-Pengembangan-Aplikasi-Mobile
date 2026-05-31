package com.example.sholatyuk.data.remote.api

import com.example.sholatyuk.core.network.ApiConfig
import com.example.sholatyuk.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class GeminiService(private val client: HttpClient) {

    suspend fun generateResponse(prompt: String): String {
        // Mendefinisikan versi model secara eksplisit
        val modelVersion = "gemini-2.5-flash-lite"

        // Membaca API Key dari konfigurasi yang aman
        val apiKey = ApiConfig.geminiApiKey

        // URL tujuan (Endpoint REST API Gemini)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelVersion:generateContent?key=$apiKey"

        // Instruksi sistem (System Prompt) untuk persona IslamAI
        val systemPrompt = """
            Anda adalah IslamAI, asisten virtual Islami yang dikembangkan untuk aplikasi SholatYuk. 
            Tugas Anda adalah membantu pengguna menjawab pertanyaan seputar agama Islam berdasarkan pemahaman Ahlussunnah wal Jama'ah, merujuk pada Al-Quran dan hadits shahih.
            Gunakan bahasa yang sopan, ramah, dan menyejukkan hati. 
            Jika pengguna menanyakan hal-hal di luar topik agama Islam (seperti otomotif, politik, coding, dll), tolaklah dengan halus dan ingatkan bahwa Anda hanya fokus berdiskusi seputar agama Islam.
        """.trimIndent()

        // Menyusun bentuk Request (JSON) yang akan dikirim
        val requestBody = GeminiRequest(
            systemInstruction = GeminiSystemInstruction(
                parts = listOf(GeminiPart(text = systemPrompt))
            ),
            contents = listOf(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )

        // Melakukan request HTTP POST menggunakan Ktor
        return try {
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()

            // Jika API merespons dengan pesan error (misal API Key salah/limit habis)
            if (response.error != null) {
                throw Exception(response.error.message)
            }

            // Membongkar JSON bertingkat untuk mengambil balasan teksnya saja
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Maaf, saya tidak dapat merangkai jawaban saat ini."

        } catch (e: Exception) {
            // Menangkap error jika koneksi internet terputus atau gagal routing
            throw Exception("Gagal terhubung ke server: ${e.message}")
        }
    }
}
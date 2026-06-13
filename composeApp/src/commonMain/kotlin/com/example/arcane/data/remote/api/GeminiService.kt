package com.example.arcane.data.remote.api

import com.example.arcane.core.network.ApiConfig
import com.example.arcane.data.remote.dto.GeminiContent
import com.example.arcane.data.remote.dto.GeminiPart
import com.example.arcane.data.remote.dto.GeminiRequest
import com.example.arcane.data.remote.dto.GeminiResponse
import com.example.arcane.data.remote.dto.GenerationConfig
import com.example.arcane.data.remote.dto.getErrorMessage
import com.example.arcane.data.remote.dto.getTextContent
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
        private const val MODEL = "gemini-3.1-flash-lite"
    }

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val contents = listOf(
            GeminiContent(parts = listOf(GeminiPart(text = prompt)), role = "user")
        )

        val systemInstruction = systemPrompt?.let {
            GeminiContent(parts = listOf(GeminiPart(text = it)), role = "user")
        }

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig(temperature = 0.7, maxOutputTokens = 2000)
        )

        println("GeminiDebug - URL: $BASE_URL/models/$MODEL:generateContent")

        val response: GeminiResponse = client.post("$BASE_URL/models/$MODEL:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", ApiConfig.geminiApiKey)
            setBody(request)
        }.body()

        response.getErrorMessage()?.let { throw Exception(it) }
        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }
}

// ====================
// System Prompts Analisis Literatur
// ====================

object SystemPrompts {

    val LITERATURE_SUMMARIZER = """
        Kamu adalah asisten riset akademis yang ahli merangkum literatur ilmiah.
        Tugas: Buat ringkasan komprehensif dari buku atau karya literatur yang diberikan.
        Aturan:
        - Gunakan Bahasa Indonesia yang baik dan formal
        - Identifikasi argumen utama, tesis, dan temuan kunci
        - Jelaskan kontribusi karya ini terhadap bidang pengetahuan
        - Maksimal 5-7 poin utama dalam format yang jelas
        - Setiap poin singkat namun informatif
    """.trimIndent()

    val METHODOLOGY_ANALYZER = """
        Kamu adalah pakar analisis metodologi penelitian dan karya literatur.
        Tugas: Analisis pendekatan, metodologi, dan kerangka teoritis yang digunakan dalam karya ini.
        Aturan:
        - Gunakan Bahasa Indonesia yang formal dan akademis
        - Identifikasi metode penelitian, kerangka konseptual, dan asumsi teoretis
        - Evaluasi kekuatan dan keterbatasan metodologi yang digunakan
        - Jelaskan bagaimana metodologi mendukung argumen penulis
    """.trimIndent()

    val CONCEPT_CONNECTOR = """
        Kamu adalah ilmuwan yang mampu mensintesis berbagai bidang pengetahuan.
        Tugas: Hubungkan konsep dalam karya ini dengan bidang riset dan teori yang lebih luas.
        Aturan:
        - Gunakan Bahasa Indonesia yang intelektual dan jelas
        - Identifikasi teori dan konsep yang berkaitan
        - Jelaskan bagaimana karya ini berdialog dengan literatur terkait
        - Tunjukkan relevansi lintas disiplin ilmu
    """.trimIndent()

    val DISCUSSION_QUESTIONS = """
        Kamu adalah dosen yang ahli membuat pertanyaan diskusi kritis dan mendalam.
        Tugas: Hasilkan 5-7 pertanyaan diskusi penting untuk menganalisis karya ini secara kritis.
        Aturan:
        - Gunakan Bahasa Indonesia
        - Buat pertanyaan yang mendorong pemikiran kritis dan analisis mendalam
        - Pertanyaan harus mencakup: argumen utama, implikasi, relevansi kontemporer
        - Format: nomor diikuti pertanyaan (contoh: "1. Pertanyaan pertama")
        - Hindari pertanyaan yang bisa dijawab hanya dengan ya/tidak
    """.trimIndent()

    val RESEARCH_ASSISTANT = """
        Kamu adalah asisten riset literatur yang cerdas dan berpengetahuan luas.
        Tugas: Jawab pertanyaan pengguna tentang buku atau topik riset yang diberikan secara akurat.
        Aturan:
        - Gunakan Bahasa Indonesia yang jelas dan informatif
        - Berikan jawaban berbasis bukti dan logis
        - Jika ada ketidakpastian, nyatakan dengan jujur
        - Fokus pada membantu pengguna memahami konten secara mendalam
    """.trimIndent()
}

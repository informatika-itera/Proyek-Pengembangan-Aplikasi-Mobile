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
                maxOutputTokens = 2000
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

// ====================
// System Prompts - VulnLog Cybersecurity Edition
// ====================

object SystemPrompts {

    val VDP_GENERATOR = """
        Kamu adalah seorang analis keamanan siber profesional dan Bug Bounty Hunter berpengalaman.
        Tugas: Ubah poin temuan kasar menjadi draft laporan Vulnerability Disclosure Program (VDP) standar industri yang formal, rapi, dan siap kirim.
        
        Format laporan WAJIB menggunakan struktur Markdown berikut:
        
        # [JUDUL TEMUAN]
        
        ## 1. Ringkasan Eksekutif
        [Jelaskan secara singkat celah keamanan dan dampaknya]
        
        ## 2. Detail Kerentanan
        - *Jenis*: [Jenis Kerentanan]
        - *Severity*: [Tingkat Keparahan]
        - *Target*: [URL/Endpoint]
        
        ## 3. Langkah Reproduksi
        [Langkah 1, 2, 3 secara detail dan sistematis]
        
        ## 4. Dampak
        [Potensi kerugian jika dieksploitasi]
        
        ## 5. Rekomendasi Perbaikan
        [Solusi teknis spesifik]
        
        Rules:
        - Gunakan Bahasa Indonesia yang profesional
        - Pertahankan detail teknis dari input user
        - Jangan mengarang informasi yang tidak ada di input
        - Format output dalam Markdown yang rapi
    """.trimIndent()

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

    val VULN_ADVISOR = """
        Kamu adalah konsultan keamanan siber senior.
        Tugas: Analisis temuan vulnerability yang diberikan dan berikan:
        1. Penilaian tingkat keparahan (severity) yang tepat berdasarkan dampak
        2. Saran langkah reproduksi yang lebih detail
        3. Rekomendasi perbaikan teknis
        Rules:
        - Gunakan Bahasa Indonesia yang profesional
        - Berikan penilaian yang objektif
        - Sertakan referensi OWASP atau CWE jika relevan
    """.trimIndent()
}
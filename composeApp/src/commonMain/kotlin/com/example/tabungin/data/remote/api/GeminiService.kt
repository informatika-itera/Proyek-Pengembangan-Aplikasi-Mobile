package com.example.tabungin.data.remote.api

import com.example.tabungin.core.network.ApiConfig
import com.example.tabungin.data.remote.dto.GeminiContent
import com.example.tabungin.data.remote.dto.GeminiPart
import com.example.tabungin.data.remote.dto.GeminiRequest
import com.example.tabungin.data.remote.dto.GeminiResponse
import com.example.tabungin.data.remote.dto.GenerationConfig
import com.example.tabungin.data.remote.dto.getErrorMessage
import com.example.tabungin.data.remote.dto.getTextContent
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
        private const val MAX_OUTPUT_TOKENS = 4096
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
                temperature = 0.9,
                maxOutputTokens = MAX_OUTPUT_TOKENS,
                topP = 0.95,
                topK = 40
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
// System Prompts
// ====================

object SystemPrompts {

    val SUMMARIZER = """
        Kamu adalah asisten keuangan yang ahli dalam merangkum riwayat transaksi dan tabungan.
        Tugas: Rangkum data transaksi atau tabungan menjadi poin-poin penting yang singkat.
        Rules:
        - Gunakan Bahasa Indonesia
        - Maksimal 3-5 poin utama
        - Setiap poin maksimal 1-2 kalimat
        - Fokus pada informasi paling penting (total, tren, dan insight)
        - Jangan menambahkan informasi yang tidak ada di data asli
    """.trimIndent()

    val IDEA_GENERATOR = """
        Kamu adalah asisten keuangan kreatif yang membantu menemukan ide tabungan dan target.
        Tugas: Berikan 5 ide target tabungan atau tips menabung yang unik dan bervariasi.
        Rules:
        - Gunakan Bahasa Indonesia
        - Berikan tepat 5 ide
        - Setiap ide harus unik dan berbeda
        - Format: nomor diikuti ide (contoh: "1. Ide pertama")
        - Ide harus realistis dan bisa dicapai
        - Variasikan antar ide: kombinasi target menabung, tips hemat, dan motivasi
    """.trimIndent()

    val WRITING_IMPROVER = """
        Kamu adalah editor catatan keuangan yang membantu memperbaiki penulisan.
        Tugas: Perbaiki catatan tabungan atau memo keuangan tanpa mengubah maknanya.
        Rules:
        - Gunakan Bahasa Indonesia yang baik dan benar
        - Perbaiki grammar, ejaan, dan struktur kalimat
        - Pertahankan gaya dan tone asli penulis
        - Jangan menambahkan informasi baru
        - Berikan HANYA hasil tulisan yang sudah diperbaiki, tanpa penjelasan
    """.trimIndent()

    val TITLE_SUGGESTER = """
        Kamu adalah asisten yang membantu membuat nama target tabungan.
        Tugas: Berikan 1 saran nama target tabungan yang singkat, jelas, dan menarik.
        Rules:
        - Gunakan Bahasa Indonesia
        - Nama target maksimal 3-5 kata
        - Nama harus mencerminkan isi target tabungan
        - Berikan HANYA nama, tanpa penjelasan atau tanda kutip
        - Nama harus mudah diingat dan motivatif
    """.trimIndent()

    val TRANSLATOR = """
        Kamu adalah penerjemah istilah keuangan profesional.
        Tugas: Terjemahkan istilah atau kalimat ke bahasa target dengan tepat.
        Rules:
        - Pertahankan makna dan nuansa asli
        - Gunakan istilah keuangan yang tepat jika ada
        - Berikan HANYA hasil terjemahan, tanpa penjelasan
    """.trimIndent()

    val TABUNGAN_ADVISOR = """
        Kamu adalah asisten keuangan yang ahli dalam perencanaan tabungan.
        Kamu akan menerima data tabungan yang sudah disusun dalam format terstruktur.

        DATA YANG DIBERIKAN:
        - Berisi informasi lengkap tentang target tabungan
        - Setiap target memiliki deadline dalam format Indonesia (contoh: "31 Desember 2025")
        - Jumlah hari tersisa sudah dihitung dari hari ini
        - Target diurutkan dari deadline terdekat

        TUGAS UTAMA: Jawab pertanyaan pengguna berdasarkan data yang diberikan.

        Rules:
        - SELALU gunakan Bahasa Indonesia yang baik dan mudah dipahami
        - Jawaban SINGKAT: maksimal 2-3 paragraf pendek (total max 100 kata)
        - Langsung ke inti, jangan bertele-tele
        - Gunakan bahasa sehari-hari yang mudah dipahami
        - Jika ada angka/data dari konteks, SEBUTKAN dengan jelas (nama target, jumlah uang, tanggal deadline, jumlah hari tersisa)
        - Berikan 1-3 saran actionable saja
        - JANGAN buat jawaban panjang dan detallada
        - Ringkas semaksimal mungkin
        - UNTUK PERTANYAAN TENTANG TARGET TERDEKAT: identifikasi target dengan deadline terdekat dari data yang diberikan, lalu berikan jawaban spesifik dengan nama target, deadline, dan sisa hari
    """.trimIndent()
}

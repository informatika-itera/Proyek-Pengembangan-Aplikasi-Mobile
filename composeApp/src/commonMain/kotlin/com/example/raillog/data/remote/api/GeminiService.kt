package com.example.raillog.data.remote.api

import com.example.raillog.core.network.ApiConfig
import com.example.raillog.data.remote.dto.GeminiContent
import com.example.raillog.data.remote.dto.GeminiPart
import com.example.raillog.data.remote.dto.GeminiRequest
import com.example.raillog.data.remote.dto.GeminiResponse
import com.example.raillog.data.remote.dto.GenerationConfig
import com.example.raillog.data.remote.dto.getErrorMessage
import com.example.raillog.data.remote.dto.getTextContent
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

// ====================
// System Prompts
// ====================

object SystemPrompts {
    val DOCUMENT_VERIFIER = """
        Kamu adalah inspektur QA teknis kereta api.
        Tugas: Verifikasi dokumen teknis (spesifikasi, sertifikat kepatuhan, laporan inspeksi).
        Rules:
        - Deteksi inkonsistensi atau ketidaksesuaian standar keselamatan kereta api.
        - Gunakan Bahasa Indonesia profesional dan jelas.
    """.trimIndent()

    val INSPECTION_SUMMARIZER = """
        Kamu adalah asisten logistik manufaktur kereta api.
        Tugas: Rangkum laporan inspeksi komponen panjang menjadi poin-poin kritis.
        Rules:
        - Fokus pada status komponen, kecacatan, dan rekomendasi perbaikan.
        - Output maksimal 5 poin.
    """.trimIndent()

    val SUPPLY_ADVISOR = """
        Kamu adalah analis supply chain.
        Tugas: Berikan rekomendasi prioritas pengadaan.
        Rules:
        - Analisis berdasarkan data urgensi, histori pemeliharaan, dan status stok.
        - Berikan output yang terstruktur.
    """.trimIndent()

    val ANOMALY_DETECTOR = """
        Kamu adalah auditor data teknis cerdas.
        Tugas: Temukan anomali dari metadata dan dokumen inventaris.
        Rules:
        - Tandai langsung parameter yang mencurigakan atau di luar rentang toleransi subsistem kereta.
    """.trimIndent()
}

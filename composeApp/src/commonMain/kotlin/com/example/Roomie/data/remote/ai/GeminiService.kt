package com.example.Roomie.data.remote.ai

import com.example.Roomie.core.util.AiConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

open class GeminiService(
    private val client: HttpClient,
    private val json: Json
) {
    private val systemInstruction = """
        Lo adalah asisten asik kampus ITERA yang bertugas membantu mahasiswa memproses permintaan booking ruangan.
        Tugas lo adalah mengekstrak data dari input user dan mengembalikannya HANYA DALAM FORMAT JSON.
        
        DATA YANG HARUS DIEKSTRAK:
        1. capacity: jumlah orang (Int)
        2. date: tanggal dalam format YYYY-MM-DD. Jika user hanya menyebutkan bulan, asumsikan tahun 2026.
        3. startTime: jam mulai dalam format HH:mm.
        4. endTime: jam selesai dalam format HH:mm.
        5. purpose: tujuan peminjaman (String).
        6. buildingName: nama gedung yang disebutkan (misal: GKU 2).
        
        ATURAN PENTING:
        - Jika ada data yang tidak disebutkan, isi dengan null.
        - Jangan memberikan teks penjelasan apa pun, cukup kembalikan JSON.
        - Jika user menggunakan bahasa gaul, tetap ekstrak intinya dengan sopan.
        - Konversi format tanggal apa pun (misal: 06/06, 6 Juni, 6-6) menjadi format standar YYYY-MM-DD.
    """.trimIndent()

    open suspend fun processUserCommand(input: String): ExtractionResult? {
        val url = "${AiConfig.BASE_URL}/${AiConfig.GEMINI_MODEL}:generateContent?key=${AiConfig.GEMINI_API_KEY}"
        
        val requestBody = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = "$systemInstruction\n\nInput User: $input")))
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json"
            )
        )

        return try {
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status != HttpStatusCode.OK) {
                val errorBody = response.bodyAsText()
                println("Gemini API Error (${response.status}): $errorBody")
                return null
            }

            val resultBody: GeminiResponse = response.body()
            val jsonString = resultBody.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonString != null) {
                json.decodeFromString<ExtractionResult>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Gemini Service Exception: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}

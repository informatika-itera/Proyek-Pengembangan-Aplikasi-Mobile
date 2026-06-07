package com.example.todomaster.data.remote.api

import com.example.todomaster.core.network.ApiConfig
import com.example.todomaster.data.remote.dto.GeminiContent
import com.example.todomaster.data.remote.dto.GeminiPart
import com.example.todomaster.data.remote.dto.GeminiRequest
import com.example.todomaster.data.remote.dto.GeminiResponse
import com.example.todomaster.data.remote.dto.GenerationConfig
import com.example.todomaster.data.remote.dto.getErrorMessage
import com.example.todomaster.data.remote.dto.getTextContent
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
        private const val MODEL = "gemini-3.5-flash"
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
                temperature = 0.2,
                maxOutputTokens = 3000,
                responseMimeType = "application/json"
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

    val TASK_BREAKDOWN_ASSISTANT = """
        Kamu adalah asisten produktivitas akademik luar biasa yang dikhususkan untuk mahasiswa teknik dan sains.
        Tugasmu: Evaluasi tugas yang diinput oleh pengguna.
        
        Rules Evaluasi & Pemecahan (WAJIB DIIKUTI):
        1. DETEKSI KERUMITAN: Jika tugas yang diberikan sangat sederhana, sepele, atau bisa diselesaikan dalam 1 langkah (misal: "Beli air", "Mandi", "Print tugas", "Nonton YouTube"), kamu TIDAK PERLU memecahnya. Langsung kembalikan respons berupa JSON array kosong: []
        2. FLEKSIBILITAS JUMLAH: Jika tugas tersebut rumit/kompleks, pecah menjadi beberapa sub-task. JUMLAH SUB-TASK BEBAS (bisa 2, 5, 8, dsb) murni menyesuaikan dengan seberapa besar dan kompleks tugas tersebut. Jangan terpaku pada jumlah tertentu!
        3. Jawab HANYA menggunakan Bahasa Indonesia yang santun dan profesional.
        4. Berikan estimasi waktu pengerjaan yang logis bagi mahasiswa dalam satuan menit (integer) untuk setiap sub-task.
        5. KEMBALIKAN RESPONS HANYA DALAM FORMAT JSON ARRAY SEPERTI CONTOH DI BAWAH INI. Jangan gunakan markdown (tanpa ```json).
        6. Tentukan "recommended_quadrant" untuk setiap sub-task dengan memilih HANYA SATU dari:
           - DO_FIRST (Krusial/mendesak)
           - SCHEDULE (Penting tapi bisa dijadwalkan)
           - DELEGATE (Operasional/bisa diotomatisasi)
           - DONT_DO (Opsional/tidak wajib)
        
        Format Contoh Output Jika Tugas Rumit:
        [
          {"title": "Membuat rancangan skema database lokal", "estimated_minutes": 45, "recommended_quadrant": "DO_FIRST"},
          {"title": "Menulis kode fungsi logika bisnis", "estimated_minutes": 60, "recommended_quadrant": "SCHEDULE"}
        ]
        
        Format Contoh Output Jika Tugas Sangat Sederhana:
        []
    """.trimIndent()

    val SUMMARIZER = "Kamu adalah asisten yang ahli dalam merangkum teks."
    val IDEA_GENERATOR = "Kamu adalah asisten kreatif yang membantu mengembangkan ide."
    val WRITING_IMPROVER = "Kamu adalah editor profesional yang membantu memperbaiki tulisan."
    val TITLE_SUGGESTER = "Kamu adalah asisten yang membantu membuat judul menarik."
    val TRANSLATOR = "Kamu adalah penerjemah profesional."
}
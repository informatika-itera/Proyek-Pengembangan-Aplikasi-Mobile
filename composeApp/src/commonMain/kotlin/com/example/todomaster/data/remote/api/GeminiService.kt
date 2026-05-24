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
                temperature = 0.2,
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


object SystemPrompts {

    val TASK_BREAKDOWN_ASSISTANT = """
        Kamu adalah asisten produktivitas akademik luar biasa yang dikhususkan untuk mahasiswa teknik dan sains.
        Tugasmu: Pecah tugas kuliah atau proyek yang besar, berat, dan abstrak yang diinput oleh pengguna menjadi 3 sampai 5 langkah kecil (sub-task) yang konkret, jelas, dan mudah dieksekusi mahasiswa.
        
        Rules yang WAJIB kamu ikuti:
        1. Jawab HANYA menggunakan Bahasa Indonesia yang santun dan profesional.
        2. Pecah tugas utama tersebut menjadi minimal 3 dan maksimal 5 sub-task.
        3. Berikan estimasi waktu pengerjaan yang logis bagi mahasiswa dalam satuan menit (integer) untuk setiap sub-task.
        4. KEMBALIKAN RESPONS HANYA DALAM FORMAT JSON ARRAY SEPERTI CONTOH DI BAWAH INI.
        5. JANGAN BERIKAN TEKS PEMBUKA, PENJELASAN, ATAU BUNGKUS MARKDOWN SAMA SEKALI (Jangan gunakan ```json atau ```). Respons harus berupa string JSON murni mentah agar tidak memicu kegagalan fungsi parsing pada aplikasi mobile.
        
        Format Contoh Output JSON yang Benar:
        [
          {"title": "Membaca modul panduan tugas besar dan jurnal referensi terkait", "estimated_minutes": 45},
          {"title": "Membuat rancangan skema database lokal dan mock-up antarmuka UI", "estimated_minutes": 60},
          {"title": "Menulis kode program inti dan melakukan pengujian fungsi bisnis", "estimated_minutes": 90}
        ]
    """.trimIndent()

    val SUMMARIZER = "Kamu adalah asisten yang ahli dalam merangkum teks."
    val IDEA_GENERATOR = "Kamu adalah asisten kreatif yang membantu mengembangkan ide."
    val WRITING_IMPROVER = "Kamu adalah editor profesional yang membantu memperbaiki tulisan."
    val TITLE_SUGGESTER = "Kamu adalah asisten yang membantu membuat judul menarik."
    val TRANSLATOR = "Kamu adalah penerjemah profesional."
}
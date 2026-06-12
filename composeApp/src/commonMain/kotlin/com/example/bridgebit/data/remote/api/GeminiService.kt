package com.example.bridgebit.data.remote.api

import com.example.bridgebit.core.network.ApiConfig
import com.example.bridgebit.data.remote.dto.GeminiContent
import com.example.bridgebit.data.remote.dto.GeminiPart
import com.example.bridgebit.data.remote.dto.GeminiRequest
import com.example.bridgebit.data.remote.dto.GeminiResponse
import com.example.bridgebit.data.remote.dto.GenerationConfig
import com.example.bridgebit.data.remote.dto.getErrorMessage
import com.example.bridgebit.data.remote.dto.getTextContent
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

    /**
     * General-purpose content generation. Used for summarization, translation,
     * writing improvement, idea generation, etc.
     *
     * @param prompt       The user-facing prompt.
     * @param systemPrompt Optional system instruction prepended as a fake user/model turn.
     */
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = runCatching {
        val contents = buildContents(prompt, systemPrompt)

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

        response.getErrorMessage()?.let { throw Exception(it) }
        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }

    /**
     * Structured JSON content generation. Used exclusively for quiz generation.
     *
     * Setting [responseMimeType] to "application/json" instructs the Gemini API
     * to return **only** valid JSON, preventing markdown wrappers, prose preambles,
     * or delimiter-based text that broke the old "|||" parsing approach.
     *
     * @param prompt           The user-facing prompt describing the JSON schema expected.
     * @param systemPrompt     System instruction enforcing JSON output rules.
     * @param responseMimeType Must be "application/json" for structured output.
     * @param maxOutputTokens  Higher limit for multi-question arrays (default 4096).
     */
    suspend fun generateStructuredContent(
        prompt: String,
        systemPrompt: String? = null,
        responseMimeType: String = "application/json",
        maxOutputTokens: Int = 4096
    ): Result<String> = runCatching {
        val contents = buildContents(prompt, systemPrompt)

        val request = GeminiRequest(
            contents = contents,
            generationConfig = GenerationConfig(
                temperature = 0.4, // Lower temperature = more deterministic JSON structure
                maxOutputTokens = maxOutputTokens,
                responseMimeType = responseMimeType
            )
        )

        val response: GeminiResponse = client.post("$BASE_URL/models/$MODEL:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", ApiConfig.geminiApiKey)
            setBody(request)
        }.body()

        response.getErrorMessage()?.let { throw Exception(it) }
        response.getTextContent() ?: throw Exception("Respons kosong dari AI")
    }

    /** Builds the content list, optionally prefixing with a system prompt fake-turn. */
    private fun buildContents(
        prompt: String,
        systemPrompt: String?
    ): MutableList<GeminiContent> {
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

        return contents
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

    /**
     * System prompt for the quiz generator.
     *
     * This prompt is paired with [generateStructuredContent] and
     * responseMimeType = "application/json". It enforces a strict JSON array
     * schema, preventing the model from adding prose, markdown, or extra fields.
     */
    val QUIZ_GENERATOR = """
        You are a strict JSON API endpoint for a vocabulary quiz application.
        Your ONLY job is to return a JSON array — nothing else.
        
        ABSOLUTE RULES:
        1. Your entire response MUST be a valid JSON array: [ {...}, {...}, ... ]
        2. Do NOT include markdown, code fences, explanations, or any text outside the JSON array.
        3. Each object in the array MUST have EXACTLY these four fields:
           - "question": string (the quiz question text)
           - "options": array of exactly 4 strings (the multiple-choice options)
           - "answer": string (must exactly match one of the strings in "options")
           - "explanation": string (a brief explanation of the correct answer)
        4. The number of objects in the array MUST exactly match the number requested.
        5. The "answer" field MUST be the full text of the correct option, not a letter like "A" or "B".
    """.trimIndent()
}
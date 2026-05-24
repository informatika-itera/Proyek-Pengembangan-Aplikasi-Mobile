package com.example.neurodeck.data.remote.api

import com.example.neurodeck.data.remote.dto.Content
import com.example.neurodeck.data.remote.dto.GeminiRequest
import com.example.neurodeck.data.remote.dto.GeminiResponse
import com.example.neurodeck.data.remote.dto.GeneratedFlashcardDto
import com.example.neurodeck.data.remote.dto.Part
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Service untuk berkomunikasi dengan Google Gemini API.
 *
 * Endpoint: POST https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent
 * Authentication: API key sebagai query parameter (?key=...)
 *
 * Reference:
 * https://ai.google.dev/api/generate-content
 *
 * Catatan: API key di-pass via constructor (di-inject dari BuildConfig di androidMain).
 */
class GeminiService(
    private val httpClient: HttpClient,
    private val apiKey: String,
) {
    /**
     * Generate flashcards dari teks materi user.
     *
     * @param material Teks materi dari user (paste dari slide, catatan, dll)
     * @return List flashcard yang berhasil di-parse dari response Gemini
     * @throws GeminiException dengan message user-friendly kalau gagal
     */
    suspend fun generateFlashcards(material: String): List<GeneratedFlashcardDto> {
        val prompt = buildPrompt(material)
        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt))),
            ),
        )

        val response: HttpResponse = try {
            httpClient.post(GEMINI_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(request)
                url {
                    parameters.append("key", apiKey)
                }
            }
        } catch (e: Exception) {
            throw GeminiException(
                "Koneksi gagal. Pastikan internet Anda menyala dan coba lagi.",
                cause = e,
            )
        }

        // Handle HTTP error
        if (!response.status.isSuccess()) {
            throw mapHttpErrorToException(response.status)
        }

        // Parse response body
        val geminiResponse: GeminiResponse = try {
            response.body()
        } catch (e: Exception) {
            throw GeminiException(
                "Response dari AI tidak bisa diparsing. Coba generate ulang.",
                cause = e,
            )
        }

        // Extract text content dari nested structure
        val rawText = geminiResponse.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: throw GeminiException("AI tidak menghasilkan jawaban. Coba dengan materi yang berbeda.")

        // Parse text sebagai JSON array of GeneratedFlashcardDto
        return parseFlashcardsJson(rawText)
    }

    // ════════════════════════════════════════════════════════════════════════
    // CHAT — Multi-turn conversation untuk AI Tutor (P3f)
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Multi-turn chat dengan AI Tutor.
     *
     * History pattern Gemini API: kirim semua turn previous dalam contents[]
     * dengan role alternating "user" dan "model". Pesan pertama (sistem prompt)
     * kita prepend sebagai user role karena Gemini API tidak support "system"
     * role di v1beta (akan support di v1 future).
     *
     * @param history  List pesan past turns (oldest first).
     *                 Pair<role, content>: role "user" atau "model".
     * @return Plain text reply dari AI (bisa markdown).
     * @throws GeminiException dengan message user-friendly.
     */
    suspend fun chatWithHistory(history: List<Pair<String, String>>): String {
        // Build contents[] dari history + system prompt prepended sebagai first user msg
        // (workaround karena Gemini v1beta belum support role=system).
        val contents = buildList {
            // System prompt + filler model reply supaya AI "tahu" persona-nya
            // tanpa polluting actual conversation.
            add(Content(role = "user", parts = listOf(Part(text = AI_TUTOR_SYSTEM_PROMPT))))
            add(Content(role = "model", parts = listOf(Part(text = AI_TUTOR_GREETING))))

            // Real history dari user
            history.forEach { (role, content) ->
                add(Content(role = role, parts = listOf(Part(text = content))))
            }
        }

        val request = GeminiRequest(
            contents = contents,
            generationConfig = com.example.neurodeck.data.remote.dto.GenerationConfig(
                temperature = 0.8,             // Chat lebih conversational, sedikit naikkan
                maxOutputTokens = 2048,        // Reply ~500-1000 words cukup
                responseMimeType = "text/plain",  // Plain text untuk chat (bukan JSON)
            ),
        )

        val response: HttpResponse = try {
            httpClient.post(GEMINI_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(request)
                url { parameters.append("key", apiKey) }
            }
        } catch (e: Exception) {
            throw GeminiException(
                "Koneksi gagal. Pastikan internet Anda menyala dan coba lagi.",
                cause = e,
            )
        }

        if (!response.status.isSuccess()) {
            throw mapHttpErrorToException(response.status)
        }

        val geminiResponse: GeminiResponse = try {
            response.body()
        } catch (e: Exception) {
            throw GeminiException(
                "Response dari AI tidak bisa diparsing. Coba lagi.",
                cause = e,
            )
        }

        return geminiResponse.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: throw GeminiException("AI tidak memberikan jawaban. Coba pertanyaan lain.")
    }

    // ==================== HELPERS ====================

    private fun buildPrompt(material: String): String = """
        Buat flashcard pembelajaran dari materi berikut. Aturan:
        1. Output HARUS JSON array murni, tanpa markdown wrapper (jangan pakai ```json)
        2. Setiap item harus punya field "front" (pertanyaan) dan "back" (jawaban)
        3. Buat 5-10 kartu, prioritaskan konsep paling penting
        4. Gunakan bahasa Indonesia
        5. Front: pertanyaan singkat (max 100 karakter)
        6. Back: jawaban lengkap tapi padat (max 300 karakter)
        7. JANGAN tambahkan komentar, penjelasan, atau teks lain di luar JSON
        
        Contoh format yang BENAR:
        [{"front":"Apa itu Kotlin?","back":"Bahasa pemrograman multiplatform modern..."}]
        
        Materi:
        $material
    """.trimIndent()

    /**
     * Parse JSON dari response Gemini.
     *
     * Defensive: walaupun prompt minta "jangan pakai markdown wrapper",
     * Gemini kadang tetap wrap dalam ```json ... ```. Kita strip dulu sebelum parse.
     */
    private fun parseFlashcardsJson(rawText: String): List<GeneratedFlashcardDto> {
        val cleaned = rawText
            .trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            permissiveJson.decodeFromString(cleaned)
        } catch (e: SerializationException) {
            throw GeminiException(
                "AI memberikan format tidak valid. Coba generate ulang.",
                cause = e,
            )
        }
    }

    private fun mapHttpErrorToException(status: HttpStatusCode): GeminiException = when (status.value) {
        400 -> GeminiException("Permintaan tidak valid. Mungkin materi terlalu panjang atau berisi konten yang tidak diizinkan.")
        401, 403 -> GeminiException("API key tidak valid atau sudah expired. Hubungi developer.")
        429 -> GeminiException("Rate limit tercapai. Tunggu beberapa menit lalu coba lagi.")
        500, 502, 503 -> GeminiException("Server AI sedang bermasalah. Coba lagi nanti.")
        else -> GeminiException("Error tidak diketahui (${status.value}). Coba lagi.")
    }

    companion object {
        /** Endpoint Gemini 2.0 Flash. Free tier ~15 RPM, ~1500 RPD. */
        private const val GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"

        /** JSON parser yang lebih permissive untuk handle quirky output dari LLM. */
        private val permissiveJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }

        /**
         * System prompt untuk persona AI Tutor NeuroDeck.
         * Di-prepend di setiap chat session (workaround karena Gemini v1beta
         * belum support role=system, jadi pakai role=user untuk system msg).
         */
        private const val AI_TUTOR_SYSTEM_PROMPT = """
            You are NeuroDeck Tutor, an AI study assistant for Indonesian university students.

            Your role:
            - Help students understand concepts clearly and patiently.
            - Use simple analogies when explaining abstract concepts.
            - Respond in the SAME language as the user's question (Indonesian or English).
            - Be encouraging and supportive — never condescending.
            - Use markdown sparingly for emphasis (bold for key terms, code blocks for code).
            - For coding concepts, provide short illustrative examples.
            - Keep responses focused: 2-5 paragraphs typically. Long enough to teach,
              short enough to read on mobile.
            - Encourage active recall: after explaining, suggest 1-2 follow-up questions
              the student could ponder.

            What NOT to do:
            - Don't write essays. Mobile-first means concise.
            - Don't pretend to know things you're not sure about — say "Saya tidak yakin" honestly.
            - Don't generate flashcards in chat (that's a separate feature in the app).
        """

        /** Filler initial AI response untuk warm-up conversation context. */
        private const val AI_TUTOR_GREETING =
            "Halo! Saya NeuroDeck Tutor. Ada konsep yang ingin kamu pahami? " +
                    "Tanyakan saja — saya bantu jelaskan dengan analogi sederhana. ✨"
    }
}

/**
 * Exception khusus untuk error dari Gemini service.
 * Message dijamin user-friendly (Bahasa Indonesia), siap ditampilkan di UI.
 */
class GeminiException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
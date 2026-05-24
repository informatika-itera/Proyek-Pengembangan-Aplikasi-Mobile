package com.example.neurodeck.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request payload untuk Gemini generateContent endpoint.
 *
 * Struktur minimal: 1 user message dengan prompt text.
 * Plus generationConfig untuk control output (temperature, etc).
 *
 * Endpoint reference:
 * https://ai.google.dev/api/generate-content#method:-models.generatecontent
 */
@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig = GenerationConfig(),
)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String = "user",
)

@Serializable
data class Part(
    val text: String,
)

@Serializable
data class GenerationConfig(
    /** Temperature 0.0-2.0, lower = lebih deterministic. 0.7 = balance creativity & consistency. */
    val temperature: Double = 0.7,

    /** Hard cap output length. 4096 tokens cukup untuk 10-15 cards atau jawaban chat panjang. */
    @SerialName("maxOutputTokens")
    val maxOutputTokens: Int = 4096,

    /**
     * Response format. Default "application/json" untuk flashcard generation
     * (structured output, lebih reliable parse). Untuk AI Chat, override jadi
     * "text/plain" supaya AI bebas markdown/prosa.
     */
    @SerialName("responseMimeType")
    val responseMimeType: String = "application/json",
)

// ==================== RESPONSE ====================

/**
 * Response dari Gemini API. Kita extract teks dari [candidates].first().content.parts.first().text.
 */
@Serializable
data class GeminiResponse(
    val candidates: List<Candidate> = emptyList(),
)

@Serializable
data class Candidate(
    val content: Content? = null,
    /** finishReason bisa: STOP, MAX_TOKENS, SAFETY, RECITATION. Kita treat selain STOP sebagai partial result. */
    val finishReason: String? = null,
)

/**
 * Error response dari Gemini API (HTTP error).
 * Diparsing terpisah, bukan masuk GeminiResponse.
 */
@Serializable
data class GeminiErrorResponse(
    val error: GeminiError,
)

@Serializable
data class GeminiError(
    val code: Int,
    val message: String,
    val status: String,
)

// ==================== FLASHCARD JSON SCHEMA ====================

/**
 * Schema JSON yang kita minta Gemini return.
 * Format: array of objects dengan field "front" dan "back".
 *
 * Contoh response yang diharapkan:
 * [
 *   {"front": "Apa itu Kotlin?", "back": "Bahasa pemrograman multi-paradigma..."},
 *   {"front": "...", "back": "..."}
 * ]
 *
 * Class ini dipakai untuk parse response text dari Gemini menjadi list flashcard.
 */
@Serializable
data class GeneratedFlashcardDto(
    val front: String,
    val back: String,
)
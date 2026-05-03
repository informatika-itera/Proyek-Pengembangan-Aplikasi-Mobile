package com.example.fitgen.data.remote.dto

import kotlinx.serialization.Serializable

// ==================== REQUEST ====================

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String = "user"
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 1000,
    val topP: Double = 0.95,
    val topK: Int = 40
)

@Serializable
data class SafetySetting(
    val category: String,
    val threshold: String
)

// ==================== RESPONSE ====================

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null,
    val promptFeedback: PromptFeedback? = null,
    val error: GeminiError? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String? = null,
    val index: Int = 0,
    val safetyRatings: List<SafetyRating>? = null
)

@Serializable
data class SafetyRating(
    val category: String,
    val probability: String
)

@Serializable
data class PromptFeedback(
    val safetyRatings: List<SafetyRating>? = null,
    val blockReason: String? = null
)

@Serializable
data class GeminiError(
    val code: Int,
    val message: String,
    val status: String
)

// ==================== HELPER EXTENSIONS ====================

fun GeminiResponse.getTextContent(): String? {
    return candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
}

fun GeminiResponse.isBlocked(): Boolean {
    return promptFeedback?.blockReason != null
}

fun GeminiResponse.getErrorMessage(): String? {
    return error?.message ?: if (isBlocked()) {
        "Konten diblokir: ${promptFeedback?.blockReason}"
    } else {
        null
    }
}

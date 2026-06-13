package com.example.foodsaver.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    @SerialName("system_instruction")
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GenerationConfig(
    val temperature: Double = 0.7,
    @SerialName("maxOutputTokens")
    val maxOutputTokens: Int = 1000,
    @SerialName("topP")
    val topP: Double = 0.95,
    @SerialName("topK")
    val topK: Int = 40
)

@Serializable
data class SafetySetting(
    val category: String,
    val threshold: String
)

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
    val code: Int? = null,
    val message: String,
    val status: String? = null
)

fun GeminiResponse.getTextContent(): String? {
    return candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
}

fun GeminiResponse.isBlocked(): Boolean {
    return promptFeedback?.blockReason != null
}

fun GeminiResponse.getErrorMessage(): String? {
    return error?.message ?: if (isBlocked()) {
        "Konten diblokir oleh AI: ${promptFeedback?.blockReason}"
    } else {
        null
    }
}

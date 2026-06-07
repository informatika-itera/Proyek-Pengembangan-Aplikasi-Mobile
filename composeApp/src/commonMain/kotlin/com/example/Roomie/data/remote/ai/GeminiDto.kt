package com.example.Roomie.data.remote.ai

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

@Serializable
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null,
    val maxOutputTokens: Int? = null
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>
)

@Serializable
data class Candidate(
    val content: Content,
    val finishReason: String? = null
)

/**
 * Hasil ekstraksi data dari AI untuk pencarian ruangan
 */
@Serializable
data class ExtractionResult(
    val capacity: Int? = null,
    val date: String? = null, // Format YYYY-MM-DD
    val startTime: String? = null, // Format HH:mm
    val endTime: String? = null, // Format HH:mm
    val purpose: String? = null,
    val buildingName: String? = null
)

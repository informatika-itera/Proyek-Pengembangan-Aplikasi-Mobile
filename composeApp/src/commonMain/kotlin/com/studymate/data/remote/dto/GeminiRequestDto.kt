package com.studymate.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequestDto(
    val contents: List<ContentDto>,
    val generationConfig: GenerationConfigDto = GenerationConfigDto()
)

@Serializable
data class ContentDto(
    val parts: List<PartDto>,
    val role: String = "user"
)

@Serializable
data class PartDto(
    val text: String
)

@Serializable
data class GenerationConfigDto(
    val temperature: Float = 0.7f,
    @SerialName("maxOutputTokens")
    val maxOutputTokens: Int = 2048
)

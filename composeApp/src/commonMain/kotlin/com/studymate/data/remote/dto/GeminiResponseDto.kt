package com.studymate.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiResponseDto(
    val candidates: List<CandidateDto>? = null,
    val error: GeminiErrorDto? = null
)

@Serializable
data class CandidateDto(
    val content: ContentDto,
    @SerialName("finishReason")
    val finishReason: String
)

@Serializable
data class GeminiErrorDto(
    val code: Int,
    val message: String,
    val status: String
)

package com.movein.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeminiResponseDto(
    val candidates: List<Candidate>
)

@Serializable
data class Candidate(
    val content: Content
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)
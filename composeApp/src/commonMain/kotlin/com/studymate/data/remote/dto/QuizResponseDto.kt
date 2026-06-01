package com.studymate.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuizResponseDto(
    val questions: List<QuizItemDto>
)

@Serializable
data class QuizItemDto(
    val question: String,
    val options: List<String>,
    val correct: Int,
    val explanation: String
)

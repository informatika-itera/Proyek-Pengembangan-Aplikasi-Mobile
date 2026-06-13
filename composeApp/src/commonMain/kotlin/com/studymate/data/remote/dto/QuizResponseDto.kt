package com.studymate.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizItemDto(
    @SerialName("question")
    val question: String,
    @SerialName("options")
    val options: List<String>,
    @SerialName("correct")
    val correct: Int,
    @SerialName("explanation")
    val explanation: String
)

@Serializable
data class QuizResponseDto(
    @SerialName("questions")
    val questions: List<QuizItemDto>
)

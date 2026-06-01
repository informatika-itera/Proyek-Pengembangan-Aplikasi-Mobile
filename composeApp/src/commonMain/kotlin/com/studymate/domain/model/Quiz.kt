package com.studymate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

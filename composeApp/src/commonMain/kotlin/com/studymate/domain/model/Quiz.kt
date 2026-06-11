package com.studymate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizHistory(
    val id: Long = 0,
    val noteId: Long,
    val noteTitle: String,
    val subject: String,
    val score: Int,
    val totalQuestions: Int,
    val createdAt: Long
)

@Serializable
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

@Serializable
data class QuizSession(
    val noteId: Long,
    val questions: List<QuizQuestion>
)

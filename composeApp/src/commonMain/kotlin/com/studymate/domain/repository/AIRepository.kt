package com.studymate.domain.repository

interface AIRepository {
    suspend fun refineNote(subject: String, title: String, content: String): Result<String>
    suspend fun generateQuiz(subject: String, title: String, noteContent: String, questionCount: Int = 5): Result<String>
    suspend fun generateMantra(): Result<String>
}

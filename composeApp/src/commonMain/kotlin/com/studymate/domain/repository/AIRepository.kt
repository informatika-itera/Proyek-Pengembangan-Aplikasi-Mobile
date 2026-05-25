package com.studymate.domain.repository

interface AIRepository {
    suspend fun refineNote(rawNote: String): Result<String>
    suspend fun generateQuiz(noteContent: String): Result<String>
}

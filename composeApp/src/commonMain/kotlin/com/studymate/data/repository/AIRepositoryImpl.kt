package com.studymate.data.repository

import com.studymate.data.remote.api.GeminiService
import com.studymate.domain.repository.AIRepository

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun refineNote(rawNote: String): Result<String> {
        if (rawNote.isBlank()) return Result.failure(Exception("Catatan tidak boleh kosong"))
        return geminiService.refineNote(rawNote)
    }

    override suspend fun generateQuiz(noteContent: String): Result<String> {
        if (noteContent.isBlank()) return Result.failure(Exception("Konten catatan kosong"))
        return geminiService.generateQuiz(noteContent)
    }
}

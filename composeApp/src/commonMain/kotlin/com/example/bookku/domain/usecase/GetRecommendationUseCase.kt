package com.example.bookku.domain.usecase

import com.example.bookku.domain.repository.AIRepository
import com.example.bookku.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class GetRecommendationUseCase(
    private val aiRepository: AIRepository,
    private val noteRepository: NoteRepository
) {
    operator fun invoke(bookId: Long, content: String): Flow<Result<String>> = flow {
        // 1. Ambil dari cache
        val cached = noteRepository.getCachedRecommendation(bookId).firstOrNull()
        if (cached != null) {
            emit(Result.success(cached))
        }

        // 2. Fetch dari Remote
        val remoteResult = aiRepository.chat("Berikan rekomendasi untuk konten ini: $content")
        
        remoteResult.onSuccess { newRecommendation ->
            noteRepository.saveRecommendation(bookId, newRecommendation)
            emit(Result.success(newRecommendation))
        }.onFailure { error ->
            if (cached == null) {
                emit(Result.failure(error))
            }
        }
    }
}

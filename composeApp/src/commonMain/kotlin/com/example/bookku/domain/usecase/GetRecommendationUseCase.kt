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
    /**
     * Cache-First Strategy:
     * 1. Ambil data dari cache lokal (SQLDelight)
     * 2. Jika ada, tampilkan segera
     * 3. Secara paralel, coba fetch data baru dari Remote (Gemini)
     * 4. Jika fetch remote berhasil, update cache dan tampilkan data baru
     */
    operator fun invoke(bookId: Long, content: String): Flow<Result<String>> = flow {
        // 1. Cek Cache
        val cached = noteRepository.getCachedRecommendation(bookId).firstOrNull()
        if (cached != null) {
            emit(Result.success(cached))
        }

        // 2. Fetch Remote (Gemini)
        val result = aiRepository.chat("Berikan rekomendasi buku atau saran pengembangan untuk tulisan berikut: $content")
        
        result.onSuccess { newRecommendation ->
            // 3. Update Cache
            noteRepository.saveRecommendation(bookId, newRecommendation)
            emit(Result.success(newRecommendation))
        }.onFailure { error ->
            // Jika gagal remote dan tidak ada cache, baru return error
            if (cached == null) {
                emit(Result.failure(error))
            }
        }
    }
}

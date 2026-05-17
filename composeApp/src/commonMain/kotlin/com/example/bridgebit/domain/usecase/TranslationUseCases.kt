package com.example.bridgebit.domain.usecase

import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow

class GetAllHistoryUseCase(private val repository: TranslationRepository) {
    operator fun invoke(): Flow<List<Translation>> {
        return repository.getAllHistory()
    }
}

class GetVaultPhrasesUseCase(private val repository: TranslationRepository) {
    operator fun invoke(): Flow<List<Translation>> {
        return repository.getVaultPhrases()
    }
}

class SearchHistoryUseCase(private val repository: TranslationRepository) {
    operator fun invoke(query: String): Flow<List<Translation>> {
        return if (query.isBlank()) {
            repository.getAllHistory()
        } else {
            repository.searchHistory(query)
        }
    }
}

class SaveTranslationUseCase(private val repository: TranslationRepository) {
    suspend operator fun invoke(translation: Translation): Result<Long> {
        return try {
            if (translation.sourceText.isBlank()) {
                return Result.failure(IllegalArgumentException("Teks asli tidak boleh kosong"))
            }

            val id = if (translation.id == 0L) {
                repository.insertTranslation(translation)
            } else {
                repository.updateTranslation(translation)
                translation.id
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteTranslationUseCase(private val repository: TranslationRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteTranslationById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class ToggleVaultStatusUseCase(private val repository: TranslationRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.toggleVaultStatus(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ==========================================
// AI USE CASES (Dinonaktifkan Sementara untuk Sprint 2)
// ==========================================
/*
class SummarizeTextUseCase(private val aiRepository: AIRepository) { ... }
class ImproveWritingUseCase(private val aiRepository: AIRepository) { ... }
class GenerateIdeasUseCase(private val aiRepository: AIRepository) { ... }
*/
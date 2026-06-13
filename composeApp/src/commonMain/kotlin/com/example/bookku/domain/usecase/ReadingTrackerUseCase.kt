package com.example.bookku.domain.usecase

import com.example.bookku.domain.model.ReadingProgress
import com.example.bookku.domain.model.ReadingStatus
import com.example.bookku.domain.repository.ReadingProgressRepository

class UpdateReadingProgressUseCase(private val readingProgressRepository: ReadingProgressRepository) {
    suspend operator fun invoke(userId: String, bookId: Long, currentPage: Int, status: ReadingStatus): Result<Unit> {
        if (currentPage < 0) return Result.failure(IllegalArgumentException("Halaman tidak boleh negatif"))
        return readingProgressRepository.updateProgress(userId, bookId, currentPage, status)
    }
}

class CompleteReadingUseCase(private val readingProgressRepository: ReadingProgressRepository) {
    suspend operator fun invoke(userId: String, bookId: Long): Result<Unit> = readingProgressRepository.completeReading(userId, bookId)
}

class AddReadingProgressUseCase(private val readingProgressRepository: ReadingProgressRepository) {
    suspend operator fun invoke(userId: String, progress: ReadingProgress): Result<Long> {
        if (progress.totalPages <= 0) return Result.failure(IllegalArgumentException("Total halaman harus lebih dari 0"))
        return readingProgressRepository.addReadingProgress(userId, progress)
    }
}

package com.example.travelplanner.domain.usecase

import com.example.travelplanner.domain.repository.AIRepository

class ExtractExpenseUseCase(
    private val aiRepository: AIRepository
) {
    /**
     * Mengubah teks bahasa sehari-hari menjadi format data pengeluaran terstruktur.
     */
    suspend fun execute(conversationalText: String): String {
        if (conversationalText.isBlank()) {
            return "[]"
        }

        return aiRepository.extractExpenseFromText(conversationalText)
    }
}
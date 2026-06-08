package com.example.travelplanner.domain.usecase

import com.example.travelplanner.domain.repository.AIRepository

class GenerateItineraryUseCase(
    private val aiRepository: AIRepository
) {
    /**
     * Mengeksekusi pembuatan itinerary liburan secara cerdas dengan validasi dasar.
     */
    suspend fun execute(
        destination: String,
        duration: String,
        vibe: String,
        language: String,
        errDestEmpty: String,
        errVibeEmpty: String
    ): String {
        // Validasi aturan bisnis dasar untuk mencegah payload kosong dikirim ke Gemini
        if (destination.isBlank()) {
            return "{\"error\": \"$errDestEmpty\"}"
        }
        if (vibe.isBlank()) {
            return "{\"error\": \"$errVibeEmpty\"}"
        }

        return aiRepository.generateItinerary(
            destination = destination.trim(),
            duration = duration.ifBlank { "2 Hari" },
            vibe = vibe.trim(),
            language = language
        )
    }
}
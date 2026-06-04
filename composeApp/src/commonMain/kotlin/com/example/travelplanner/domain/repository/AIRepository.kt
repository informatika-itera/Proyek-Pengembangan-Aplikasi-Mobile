package com.example.travelplanner.domain.repository

interface AIRepository {
    /**
     * Menghasilkan itinerary mentah berbentuk JSON dari Gemini API berdasarkan parameter perjalanan.
     */
    suspend fun generateItinerary(destination: String, duration: String, vibe: String, language: String): String

    /**
     * Mengekstrak percakapan natural pengeluaran menjadi struktur data JSON terstruktur.
     */
    suspend fun extractExpenseFromText(conversationalText: String): String
    
    /**
     * Translates an old itinerary (missing English text) into a bilingual itinerary.
     */
    suspend fun translateItinerary(jsonItinerary: String): String
}
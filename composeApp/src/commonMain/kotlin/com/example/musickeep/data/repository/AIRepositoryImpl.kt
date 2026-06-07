package com.example.musickeep.data.repository

import com.example.musickeep.data.remote.api.GeminiService
import com.example.musickeep.domain.repository.AIRepository
import com.example.musickeep.domain.repository.SongSuggestion

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun getSongSuggestion(title: String): SongSuggestion? {
        // Prompt paling simpel di dunia
        val prompt = "Song: $title. Artist and Genre? Format: Artist|Genre"
        
        val result = geminiService.generateContent(prompt)
        
        if (result.isFailure) {
            throw Exception(result.exceptionOrNull()?.message)
        }
        
        val text = result.getOrNull()
        return text?.let { 
            val cleanText = it.trim().lines().firstOrNull { l -> l.contains("|") } ?: it
            val parts = cleanText.split("|")
            if (parts.size >= 2) {
                SongSuggestion(parts[0].trim(), parts[1].trim())
            } else throw Exception("Format AI Salah: $it")
        }
    }
}

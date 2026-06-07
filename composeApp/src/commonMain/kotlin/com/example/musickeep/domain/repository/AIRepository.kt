package com.example.musickeep.domain.repository

data class SongSuggestion(
    val artist: String,
    val genre: String
)

interface AIRepository {
    suspend fun getSongSuggestion(title: String): SongSuggestion?
}

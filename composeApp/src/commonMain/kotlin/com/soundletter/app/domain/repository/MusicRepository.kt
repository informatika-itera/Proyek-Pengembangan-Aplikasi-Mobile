package com.soundletter.app.domain.repository

interface MusicRepository {
    // Spotify API placeholder
    suspend fun searchSongs(query: String): List<String>
}

package com.soundletter.app.domain.repository

import com.soundletter.app.domain.model.MusicTrack

interface MusicRepository {
    /**
     * Mencari lagu berdasarkan mood atau genre tags (fuzzy search).
     */
    suspend fun searchSongs(mood: String): List<MusicTrack>
}

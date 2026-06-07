package com.soundletter.app.domain.repository

import com.soundletter.app.domain.model.MusicTrack

interface MusicRepository {
    suspend fun searchSongs(query: String): List<MusicTrack>
}

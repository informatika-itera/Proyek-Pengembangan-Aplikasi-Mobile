package com.example.gamenews.domain.repository

import com.example.gamenews.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    /**
     * Mengambil daftar game terbaru dari API/Database.
     */
    fun getLatestGames(): Flow<List<Game>>
}
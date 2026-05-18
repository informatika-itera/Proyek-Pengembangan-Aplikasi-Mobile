package com.example.gamenews.domain.repository

import com.example.gamenews.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun searchGames(query: String, genre: String? = null): Flow<List<Game>>
    fun getLatestGames(): Flow<List<Game>>
}
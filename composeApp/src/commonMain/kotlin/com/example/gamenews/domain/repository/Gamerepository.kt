package com.example.gamenews.domain.repository

import com.example.gamenews.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun searchGames(query: String, genre: String? = null): Flow<List<Game>>
    fun getLatestGames(): Flow<List<Game>>
    fun getGameById(id: Long): Flow<Game?>

    fun getWishlistGames(): Flow<List<Game>>
    suspend fun toggleWishlist(game: Game)
    fun isGameWishlisted(id: Long): Flow<Boolean>
}
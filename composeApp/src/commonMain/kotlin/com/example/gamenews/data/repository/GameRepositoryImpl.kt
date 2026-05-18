package com.example.gamenews.data.repository

import com.example.gamenews.data.remote.api.GameBrainService
import com.example.gamenews.data.mapper.toDomain
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GameRepositoryImpl(
    private val apiService: GameBrainService
) : GameRepository {

    override fun getLatestGames(): Flow<List<Game>> = flow {
        val result = apiService.searchGames(
            query = "*",
            releaseDate = "last_month",
            sortBy = "release_date"
        )

        result.onSuccess { response ->
            val domainList = response.results.map { it.toDomain() }
            emit(domainList)
        }.onFailure {
            emit(emptyList())
        }
    }
    override fun searchGames(query: String, genre: String?): Flow<List<Game>> = flow {
        val searchQuery = query.ifBlank { "*" }

        val result = apiService.searchGames(
            query = searchQuery,
            genre = genre,
            sortBy = "release_date"
        )

        result.onSuccess { response ->
            emit(response.results.map { it.toDomain() })
        }.onFailure {
            emit(emptyList())
        }
    }
}
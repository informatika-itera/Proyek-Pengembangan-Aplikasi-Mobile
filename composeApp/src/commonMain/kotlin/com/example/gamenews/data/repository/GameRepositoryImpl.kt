package com.example.gamenews.data.repository

import com.example.gamenews.GameDatabase
import com.example.gamenews.data.remote.api.GameBrainService
import com.example.gamenews.data.mapper.toDomain
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GameRepositoryImpl(
    private val apiService: GameBrainService,
    private val gameDatabase: GameDatabase
) : GameRepository {

    private companion object {
        private val cachedGames = mutableListOf<Game>()
        private val searchedQueries = mutableSetOf<String>()
        private val mutex = Mutex()
    }

    override fun getLatestGames(): Flow<List<Game>> = flow {
        mutex.withLock {
            if (cachedGames.isNotEmpty()) {
                emit(cachedGames.toList())
                return@flow
            }
        }

        val result = apiService.searchGames(
            query = "*",
            sortBy = "release_date"
        )
        result.onSuccess { response ->
            val domainList = response.results.map { it.toDomain() }
            mutex.withLock {
                cachedGames.clear()
                cachedGames.addAll(domainList)
                searchedQueries.add("*")
            }
            emit(domainList)
        }.onFailure {
            mutex.withLock {
                if (cachedGames.isNotEmpty()) {
                    emit(cachedGames.toList())
                } else {
                    emit(emptyList())
                }
            }
        }
    }

    override fun searchGames(query: String, genre: String?): Flow<List<Game>> = flow {
        val searchQuery = query.ifBlank { "*" }

        val localFiltered = mutex.withLock {
            cachedGames.filter { game ->
                game.title.contains(searchQuery, ignoreCase = true) ||
                        game.genre.contains(searchQuery, ignoreCase = true) ||
                        game.description.contains(searchQuery, ignoreCase = true)
            }
        }

        val isAlreadySearched = mutex.withLock { searchedQueries.contains(searchQuery) }
        if (isAlreadySearched && localFiltered.isNotEmpty()) {
            emit(localFiltered)
            return@flow
        }

        if (localFiltered.isNotEmpty() && searchQuery != "*") {
            emit(localFiltered)
        }

        val result = apiService.searchGames(
            query = searchQuery,
            sortBy = "release_date"
        )
        result.onSuccess { response ->
            val remoteList = response.results.map { it.toDomain() }

            mutex.withLock {
                searchedQueries.add(searchQuery)
                if (searchQuery == "*") {
                    cachedGames.clear()
                    cachedGames.addAll(remoteList)
                } else {
                    remoteList.forEach { remoteGame ->
                        val existingIndex = cachedGames.indexOfFirst { it.id.toLong() == remoteGame.id.toLong() }
                        if (existingIndex != -1) {
                            cachedGames[existingIndex] = remoteGame
                        } else {
                            cachedGames.add(remoteGame)
                        }
                    }
                }
            }
            emit(remoteList)
        }.onFailure {
            if (localFiltered.isNotEmpty()) {
                emit(localFiltered)
            } else {
                emit(emptyList())
            }
        }
    }

    override fun getGameById(id: Long): Flow<Game?> = flow {
        val localGame = mutex.withLock { cachedGames.find { it.id.toLong() == id } }

        if (localGame != null && localGame.description.isNotBlank()) {
            emit(localGame)
            return@flow
        }

        if (localGame != null) {
            emit(localGame)
        }

        val result = apiService.getGameDetails(id)
        result.onSuccess { entity ->
            val domainGame = entity.toDomain()

            mutex.withLock {
                val index = cachedGames.indexOfFirst { it.id.toLong() == id }
                if (index != -1) {
                    cachedGames[index] = domainGame
                } else {
                    cachedGames.add(domainGame)
                }
            }
            emit(domainGame)
        }.onFailure {
            if (localGame != null) {
                emit(localGame)
            } else {
                val dbGame = gameDatabase.gameQueries.searchGames("").executeAsList().find { it.id == id }
                if (dbGame != null) {
                    emit(dbGame.toDomain())
                } else {
                    emit(null)
                }
            }
        }
    }

    override fun getWishlistGames(): Flow<List<Game>> = flow {
        val localDbList = gameDatabase.gameQueries.searchGames("").executeAsList()
        emit(localDbList.map { it.toDomain() })
    }

    override suspend fun toggleWishlist(game: Game) {
        val existingGame = gameDatabase.gameQueries.searchGames("").executeAsList().find { it.id == game.id.toLong() }
        if (existingGame != null) {
            gameDatabase.gameQueries.insertGame(
                game.id.toLong(),
                game.title,
                game.description,
                game.genre,
                game.rating,
                game.imageUrl
            )
        } else {
            gameDatabase.gameQueries.insertGame(
                game.id.toLong(),
                game.title,
                game.description,
                game.genre,
                game.rating,
                game.imageUrl
            )
        }
    }

    override fun isGameWishlisted(id: Long): Flow<Boolean> = flow {
        val exists = gameDatabase.gameQueries.searchGames("").executeAsList().any { it.id == id }
        emit(exists)
    }
}
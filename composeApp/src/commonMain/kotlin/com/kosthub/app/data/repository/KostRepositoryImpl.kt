package com.kosthub.app.data.repository

import com.kosthub.app.data.local.KostDatabase
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.domain.repository.KostRepository
import com.kosthub.app.data.remote.api.ApiService
import com.kosthub.app.data.remote.NetworkResult
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KostRepositoryImpl(
    private val database: KostDatabase,
    private val apiService: ApiService? = null
) : KostRepository {
    private val queries = database.kostQueries
    private val _remoteKosts = MutableStateFlow<List<Kost>>(emptyList())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                syncRemote()
            } catch (e: Exception) {
                // Ignore initial sync error
            }
        }
    }

    override suspend fun getAll(): List<Kost> = withContext(Dispatchers.Default) {
        val favSet = queries.getFavorites().executeAsList().toSet()
        _remoteKosts.value.map { kost ->
            kost.copy(isFavorite = favSet.contains(kost.id))
        }
    }

    override fun getAllFlow(): Flow<List<Kost>> {
        val favoritesFlow = queries.getFavorites()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.toSet() }

        return combine(_remoteKosts, favoritesFlow) { remoteList, favSet ->
            remoteList.map { kost ->
                kost.copy(isFavorite = favSet.contains(kost.id))
            }
        }
    }

    override suspend fun syncRemote(): Unit = withContext(Dispatchers.Default) {
        if (apiService == null) return@withContext
        when (val result = apiService.getAllKosts()) {
            is NetworkResult.Success -> {
                val remoteList = result.data.data.map { it.toDomain() }
                _remoteKosts.value = remoteList
            }
            else -> {}
        }
    }

    override suspend fun getById(id: Long): Kost? = withContext(Dispatchers.Default) {
        val kost = _remoteKosts.value.find { it.id == id } ?: run {
            if (apiService != null) {
                when (val result = apiService.getKostById(id)) {
                    is NetworkResult.Success -> {
                        result.data.data?.toDomain()
                    }
                    else -> null
                }
            } else {
                null
            }
        }

        kost?.let {
            val isFav = queries.isFavorite(it.id).executeAsOne() > 0
            it.copy(isFavorite = isFav)
        }
    }

    override suspend fun add(kost: Kost): Long {
        throw UnsupportedOperationException("Fitur kontribusi dinonaktifkan")
    }

    override suspend fun update(kost: Kost) = withContext(Dispatchers.Default) {
        if (kost.isFavorite) {
            queries.insertFavorite(kost.id)
        } else {
            queries.deleteFavorite(kost.id)
        }
    }

    override suspend fun delete(id: Long) {
        throw UnsupportedOperationException("Fitur kontribusi dinonaktifkan")
    }

    override suspend fun seedIfEmpty(items: List<Kost>) {
        // No-op
    }
}

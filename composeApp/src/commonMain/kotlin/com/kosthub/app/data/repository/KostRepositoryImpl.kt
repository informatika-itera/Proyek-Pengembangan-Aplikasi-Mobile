package com.kosthub.app.data.repository

import com.kosthub.app.data.local.KostDatabase
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.domain.repository.KostRepository
import com.kosthub.app.data.remote.api.ApiService
import com.kosthub.app.data.remote.NetworkResult
import com.kosthub.app.data.remote.dto.KostDto
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KostRepositoryImpl(
    private val database: KostDatabase,
    private val apiService: ApiService? = null
) : KostRepository {
    private val queries = database.kostQueries

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
        queries.selectAll().executeAsList().map { row ->
            row.toDomain()
        }
    }

    override fun getAllFlow(): Flow<List<Kost>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncRemote(): Unit = withContext(Dispatchers.Default) {
        if (apiService == null) return@withContext
        when (val result = apiService.getAllKosts()) {
            is NetworkResult.Success -> {
                val remoteList = result.data.data
                database.transaction {
                    queries.deleteAllKosts()
                    remoteList.forEach { dto ->
                        insertFromDto(dto)
                    }
                }
            }
            is NetworkResult.Error -> {
                throw Exception(result.message ?: "Gagal mengambil data dari server")
            }
            else -> {
                throw Exception("Koneksi bermasalah")
            }
        }
    }

    override suspend fun getById(id: Long): Kost? = withContext(Dispatchers.Default) {
        val cached = queries.selectById(id).executeAsOneOrNull()
        if (cached != null) {
            return@withContext cached.toDomain()
        }
        if (apiService == null) return@withContext null
        when (val result = apiService.getKostById(id)) {
            is NetworkResult.Success -> {
                val dto = result.data.data
                if (dto != null) {
                    insertFromDto(dto)
                    dto.toDomain()
                } else {
                    null
                }
            }
            else -> null
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

    private fun insertFromDto(dto: KostDto) {
        queries.insertKost(
            id = dto.id,
            namaKos = dto.namaKos,
            nomorTelepon = dto.nomorTelepon,
            jarakKm = dto.jarakKm,
            hargaTahunan = dto.hargaTahunan,
            tipeKos = dto.tipeKos,
            kamarMandi = dto.kamarMandi,
            wifi = dto.wifi,
            furniturKasur = dto.furniturKasur,
            furniturLemari = dto.furniturLemari,
            furniturMejaBelajar = dto.furniturMejaBelajar,
            fasilitasPendingin = dto.fasilitasPendingin,
            areaLaundry = dto.areaLaundry,
            areaDapur = dto.areaDapur,
            keamananCctv = dto.keamananCctv
        )
    }

    private fun com.kosthub.app.data.local.SelectAll.toDomain(): Kost {
        return Kost(
            id = id,
            namaKos = namaKos,
            nomorTelepon = nomorTelepon,
            jarakKm = jarakKm,
            hargaTahunan = hargaTahunan,
            tipeKos = tipeKos,
            kamarMandi = kamarMandi,
            wifi = wifi,
            furniturKasur = furniturKasur,
            furniturLemari = furniturLemari,
            furniturMejaBelajar = furniturMejaBelajar,
            fasilitasPendingin = fasilitasPendingin,
            areaLaundry = areaLaundry,
            areaDapur = areaDapur,
            keamananCctv = keamananCctv,
            isFavorite = isFavorite != 0L
        )
    }

    private fun com.kosthub.app.data.local.SelectById.toDomain(): Kost {
        return Kost(
            id = id,
            namaKos = namaKos,
            nomorTelepon = nomorTelepon,
            jarakKm = jarakKm,
            hargaTahunan = hargaTahunan,
            tipeKos = tipeKos,
            kamarMandi = kamarMandi,
            wifi = wifi,
            furniturKasur = furniturKasur,
            furniturLemari = furniturLemari,
            furniturMejaBelajar = furniturMejaBelajar,
            fasilitasPendingin = fasilitasPendingin,
            areaLaundry = areaLaundry,
            areaDapur = areaDapur,
            keamananCctv = keamananCctv,
            isFavorite = isFavorite != 0L
        )
    }
}

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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class KostRepositoryImpl(
    private val database: KostDatabase,
    private val apiService: ApiService? = null
) : KostRepository {
    private val queries = database.kostQueries

    override suspend fun getAll(): List<Kost> = withContext(Dispatchers.Default) {
        queries.getAllKost().executeAsList().map { it.toDomain() }
    }

    override fun getAllFlow(): Flow<List<Kost>> {
        // Trigger background sync when flow is collected
        CoroutineScope(Dispatchers.Default).launch {
            try {
                syncRemote()
            } catch (e: Exception) {
                // Ignore sync errors for offline first
            }
        }

        return queries.getAllKost()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncRemote(): Unit = withContext(Dispatchers.Default) {
        if (apiService == null) return@withContext
        when (val result = apiService.getAllKosts()) {
            is NetworkResult.Success -> {
                val remoteKosts = result.data.data
                database.transaction {
                    remoteKosts.forEach { dto ->
                        val localKost = queries.getKostById(dto.id).executeAsOneOrNull()
                        if (localKost != null) {
                            queries.updateKost(
                                contributor_id = dto.contributorId,
                                nama_kos = dto.namaKos,
                                nomor_telepon = dto.nomorTelepon,
                                daerah = dto.daerah,
                                jarak_km = dto.jarakKm,
                                harga_tahunan = dto.hargaTahunan,
                                tipe_kos = dto.tipeKos,
                                kamar_mandi = dto.kamarMandi,
                                wifi = dto.wifi,
                                furnitur_kasur = dto.furniturKasur,
                                furnitur_lemari = dto.furniturLemari,
                                furnitur_meja_belajar = dto.furniturMejaBelajar,
                                fasilitas_pendingin = dto.fasilitasPendingin,
                                area_laundry = dto.areaLaundry,
                                area_dapur = dto.areaDapur,
                                keamanan_cctv = dto.keamananCctv,
                                is_favorite = if (dto.isFavorite) 1 else 0,
                                updated_at = currentTimeMillis(),
                                id = dto.id
                            )
                        } else {
                            // Insert since it's not present locally
                            queries.insertKost(
                                contributor_id = dto.contributorId,
                                nama_kos = dto.namaKos,
                                nomor_telepon = dto.nomorTelepon,
                                daerah = dto.daerah,
                                jarak_km = dto.jarakKm,
                                harga_tahunan = dto.hargaTahunan,
                                tipe_kos = dto.tipeKos,
                                kamar_mandi = dto.kamarMandi,
                                wifi = dto.wifi,
                                furnitur_kasur = dto.furniturKasur,
                                furnitur_lemari = dto.furniturLemari,
                                furnitur_meja_belajar = dto.furniturMejaBelajar,
                                fasilitas_pendingin = dto.fasilitasPendingin,
                                area_laundry = dto.areaLaundry,
                                area_dapur = dto.areaDapur,
                                keamanan_cctv = dto.keamananCctv,
                                is_favorite = if (dto.isFavorite) 1 else 0,
                                created_at = currentTimeMillis(),
                                updated_at = currentTimeMillis()
                            )
                        }
                    }
                }
            }
            is NetworkResult.Error -> {
                // Log or ignore network error for cache-first behavior
            }
            NetworkResult.Loading -> {
                // No-op
            }
        }
    }

    override suspend fun getById(id: Long): Kost? = withContext(Dispatchers.Default) {
        queries.getKostById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun add(kost: Kost): Long = withContext(Dispatchers.Default) {
        val now = currentTimeMillis()
        queries.insertKost(
            contributor_id = kost.contributorId,
            nama_kos = kost.namaKos,
            nomor_telepon = kost.nomorTelepon,
            daerah = kost.daerah,
            jarak_km = kost.jarakKm,
            harga_tahunan = kost.hargaTahunan,
            tipe_kos = kost.tipeKos,
            kamar_mandi = kost.kamarMandi,
            wifi = kost.wifi,
            furnitur_kasur = kost.furniturKasur,
            furnitur_lemari = kost.furniturLemari,
            furnitur_meja_belajar = kost.furniturMejaBelajar,
            fasilitas_pendingin = kost.fasilitasPendingin,
            area_laundry = kost.areaLaundry,
            area_dapur = kost.areaDapur,
            keamanan_cctv = kost.keamananCctv,
            is_favorite = if (kost.isFavorite) 1 else 0,
            created_at = now,
            updated_at = now
        )
        queries.lastInsertId().executeAsOne()
    }

    override suspend fun update(kost: Kost) = withContext(Dispatchers.Default) {
        queries.updateKost(
            contributor_id = kost.contributorId,
            nama_kos = kost.namaKos,
            nomor_telepon = kost.nomorTelepon,
            daerah = kost.daerah,
            jarak_km = kost.jarakKm,
            harga_tahunan = kost.hargaTahunan,
            tipe_kos = kost.tipeKos,
            kamar_mandi = kost.kamarMandi,
            wifi = kost.wifi,
            furnitur_kasur = kost.furniturKasur,
            furnitur_lemari = kost.furniturLemari,
            furnitur_meja_belajar = kost.furniturMejaBelajar,
            fasilitas_pendingin = kost.fasilitasPendingin,
            area_laundry = kost.areaLaundry,
            area_dapur = kost.areaDapur,
            keamanan_cctv = kost.keamananCctv,
            is_favorite = if (kost.isFavorite) 1 else 0,
            updated_at = currentTimeMillis(),
            id = kost.id
        )
    }

    override suspend fun delete(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteKost(id)
    }

    override suspend fun seedIfEmpty(items: List<Kost>) = withContext(Dispatchers.Default) {
        val count = queries.countKost().executeAsOne()
        if (count == 0L) {
            items.forEach { add(it) }
        }
    }

    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}

private fun com.kosthub.app.data.local.KostEntity.toDomain(): Kost {
    return Kost(
        id = id,
        contributorId = contributor_id,
        namaKos = nama_kos,
        nomorTelepon = nomor_telepon,
        daerah = daerah,
        jarakKm = jarak_km,
        hargaTahunan = harga_tahunan,
        tipeKos = tipe_kos,
        kamarMandi = kamar_mandi,
        wifi = wifi,
        furniturKasur = furnitur_kasur,
        furniturLemari = furnitur_lemari,
        furniturMejaBelajar = furnitur_meja_belajar,
        fasilitasPendingin = fasilitas_pendingin,
        areaLaundry = area_laundry,
        areaDapur = area_dapur,
        keamananCctv = keamanan_cctv,
        isFavorite = is_favorite == 1L
    )
}


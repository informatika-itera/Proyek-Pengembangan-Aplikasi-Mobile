package com.kosthub.app.data.repository

import com.kosthub.app.data.local.KostDatabase
import com.kosthub.app.domain.model.Kost
import com.kosthub.app.domain.repository.KostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class KostRepositoryImpl(private val database: KostDatabase) : KostRepository {
    private val queries = database.kostQueries

    override suspend fun getAll(): List<Kost> = withContext(Dispatchers.Default) {
        queries.getAllKost().executeAsList().map { it.toDomain() }
    }

    override suspend fun getById(id: Long): Kost? = withContext(Dispatchers.Default) {
        queries.getKostById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun add(kost: Kost): Long = withContext(Dispatchers.Default) {
        val now = currentTimeMillis()
        queries.insertKost(
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

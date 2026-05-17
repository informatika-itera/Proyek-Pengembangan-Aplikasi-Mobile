package com.kelazzz.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.kelazzz.app.data.local.KelazZzDatabase
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.JenisJadwal
import com.kelazzz.app.domain.repository.JadwalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

/**
 * Implementasi JadwalRepository — offline-first via SQLDelight
 */
class JadwalRepositoryImpl(
    private val database: KelazZzDatabase
) : JadwalRepository {

    private val queries = database.jadwalQueries

    override fun getAllJadwal(): Flow<List<Jadwal>> =
        queries.getAllJadwal()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getJadwalByTanggal(tanggal: String): Flow<List<Jadwal>> =
        queries.getJadwalByTanggal(tanggal)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getUpcomingJadwal(fromDate: String, limit: Int): Flow<List<Jadwal>> =
        queries.getUpcomingJadwal(fromDate, limit.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getJadwalById(id: Long): Flow<Jadwal?> {
        return queries.getJadwalById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity: com.kelazzz.app.data.local.JadwalEntity? -> entity?.toDomain() }
    }

    override suspend fun insertJadwal(jadwal: Jadwal): Long {
        return withContext(Dispatchers.IO) {
            queries.insertJadwal(
                judul = jadwal.judul,
                deskripsi = jadwal.deskripsi,
                tanggal = jadwal.tanggal,
                waktu = jadwal.waktu,
                jenis = jadwal.jenis.name.lowercase(),
                createdAt = jadwal.createdAt.toString(),
                updatedAt = jadwal.updatedAt.toString()
            )
            queries.lastInsertId().executeAsOne()
        }
    }

    override suspend fun updateJadwal(jadwal: Jadwal) {
        withContext(Dispatchers.IO) {
            queries.updateJadwal(
                judul = jadwal.judul,
                deskripsi = jadwal.deskripsi,
                tanggal = jadwal.tanggal,
                waktu = jadwal.waktu,
                jenis = jadwal.jenis.name.lowercase(),
                updatedAt = jadwal.updatedAt.toString(),
                id = jadwal.id
            )
        }
    }

    override suspend fun deleteJadwal(id: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteJadwalById(id)
        }
    }
}

// ==================== MAPPER ====================

private fun com.kelazzz.app.data.local.JadwalEntity.toDomain(): Jadwal {
    val now = kotlinx.datetime.Clock.System.now()
    return Jadwal(
        id = id,
        judul = judul,
        deskripsi = deskripsi,
        tanggal = tanggal,
        waktu = waktu,
        jenis = JenisJadwal.fromString(jenis.uppercase()),
        createdAt = runCatching { Instant.parse(createdAt) }.getOrElse { now },
        updatedAt = runCatching { Instant.parse(updatedAt) }.getOrElse { now }
    )
}

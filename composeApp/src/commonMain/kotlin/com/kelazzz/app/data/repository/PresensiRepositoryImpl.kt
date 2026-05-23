package com.kelazzz.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kelazzz.app.data.local.KelazZzDatabase
import com.kelazzz.app.data.local.KelasEntity
import com.kelazzz.app.data.local.PresensiEntity
import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.data.remote.pocket.PocketApiService
import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Kelas
import com.kelazzz.app.domain.model.Presensi
import com.kelazzz.app.domain.model.StatusPresensi
import com.kelazzz.app.domain.repository.PresensiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Implementasi PresensiRepository — offline-first via SQLDelight & Ktor
 */
class PresensiRepositoryImpl(
    private val database: KelazZzDatabase,
    private val apiService: PocketApiService,
    private val userPreferences: UserPreferences
) : PresensiRepository {

    private val queries = database.presensiQueries

    override fun getKelasList(): Flow<List<Kelas>> =
        queries.getAllKelas()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun syncKelas(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.authToken.first()
                    ?: return@withContext Result.failure(Exception("Sesi login tidak ditemukan. Harap login kembali."))
                val deviceId = userPreferences.deviceId.first() ?: ""
                val nim = userPreferences.userNim.first()
                    ?: return@withContext Result.failure(Exception("NIM mahasiswa tidak ditemukan."))
                val email = userPreferences.userEmail.first() ?: "$nim@student.itera.ac.id"

                // Lakukan registrasi token/sesi terlebih dahulu
                val registerResult = apiService.registerToken(token = token, deviceId = deviceId, email = email)
                if (registerResult.isFailure) {
                    return@withContext Result.failure(
                        registerResult.exceptionOrNull() ?: Exception("Gagal otorisasi token sesi.")
                    )
                }

                // Jika registrasi token berhasil, ambil daftar kelas
                val result = apiService.getKelas(token = token, deviceId = deviceId, nim = nim)
                if (result.isSuccess) {
                    val response = result.getOrThrow()
                    if (response.meta.status) {
                        // Simpan ke local database cache
                        queries.transaction {
                            queries.deleteAllKelas()
                            response.data.forEach { data ->
                                queries.insertKelas(
                                    kodeKelas = data.kodeKelas,
                                    nomorMk = data.nomorMk,
                                    kodeMk = data.kodeMk,
                                    namaKelas = data.namaKelas,
                                    mode = data.mode,
                                    namaMk = data.namaMk,
                                    sksMk = data.sksMk,
                                    namaDosenList = data.namaDosenList,
                                    jadwalHari = data.jadwalHari
                                )
                            }
                        }
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception(response.meta.message))
                    }
                } else {
                    Result.failure(result.exceptionOrNull() ?: Exception("Gagal sinkronisasi data kelas."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun getAllPresensi(): Flow<List<Presensi>> =
        queries.getAllPresensi()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getPresensiByMataKuliah(mataKuliahId: String): Flow<List<Presensi>> =
        queries.getPresensiByMataKuliah(mataKuliahId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getAttendanceSummary(): Flow<List<AttendanceSummary>> =
        queries.getAttendanceSummary()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    AttendanceSummary(
                        mataKuliahId = entity.mataKuliahId,
                        mataKuliahNama = entity.mataKuliahNama,
                        totalPertemuan = entity.totalPertemuan?.toInt() ?: 0,
                        totalHadir = entity.totalHadir?.toInt() ?: 0,
                        totalAlpha = entity.totalAlpha?.toInt() ?: 0,
                        totalIzin = entity.totalIzin?.toInt() ?: 0,
                        totalSakit = entity.totalSakit?.toInt() ?: 0
                    )
                }
            }

    override suspend fun syncPresensiForKelas(kelasId: String, mataKuliahNama: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.authToken.first()
                    ?: return@withContext Result.failure(Exception("Sesi login tidak ditemukan. Harap login kembali."))
                val deviceId = userPreferences.deviceId.first() ?: ""
                val nim = userPreferences.userNim.first()
                    ?: return@withContext Result.failure(Exception("NIM mahasiswa tidak ditemukan."))
                val email = userPreferences.userEmail.first() ?: "$nim@student.itera.ac.id"

                // Lakukan registrasi token/sesi terlebih dahulu
                val registerResult = apiService.registerToken(token = token, deviceId = deviceId, email = email)
                if (registerResult.isFailure) {
                    return@withContext Result.failure(
                        registerResult.exceptionOrNull() ?: Exception("Gagal otorisasi token sesi.")
                    )
                }

                // Ambil data presensi
                val result = apiService.getPresensiDetail(
                    token = token,
                    deviceId = deviceId,
                    nim = nim,
                    kelasKode = kelasId
                )

                if (result.isSuccess) {
                    val response = result.getOrThrow()
                    if (response.meta.status) {
                        val lastSyncStr = Clock.System.now().toString()
                        
                        queries.transaction {
                            queries.deletePresensiByMataKuliah(kelasId)
                            response.data.forEach { data ->
                                val status = when {
                                    data.pertemuan.isNullOrBlank() || data.waktuMulai.isNullOrBlank() -> "BELUM_MULAI"
                                    data.absenMahasiswa == "1" -> "HADIR"
                                    else -> "ALPHA"
                                }
                                queries.insertPresensi(
                                    mataKuliahId = kelasId,
                                    mataKuliahNama = mataKuliahNama,
                                    pertemuan = data.noPertemuan.toLong(),
                                    status = status,
                                    tanggal = data.waktuMulai ?: "",
                                    lastSync = lastSyncStr
                                )
                            }
                        }
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception(response.meta.message))
                    }
                } else {
                    Result.failure(result.exceptionOrNull() ?: Exception("Gagal sinkronisasi data presensi."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun submitPresensi(token: String): Result<Unit> {
        // Stub untuk pengiriman token presensi
        return Result.success(Unit)
    }

    override suspend fun syncPresensi(): Result<Unit> {
        // Stub untuk sinkronisasi data presensi
        return Result.success(Unit)
    }
}

// ==================== MAPPER ====================

private fun KelasEntity.toDomain(): Kelas {
    return Kelas(
        nomorMk = nomorMk,
        kodeMk = kodeMk,
        kodeKelas = kodeKelas,
        namaKelas = namaKelas,
        mode = mode,
        namaMk = namaMk,
        sksMk = sksMk,
        namaDosenList = namaDosenList,
        jadwalHari = jadwalHari
    )
}

private fun PresensiEntity.toDomain(): Presensi {
    return Presensi(
        id = id,
        mataKuliahId = mataKuliahId,
        mataKuliahNama = mataKuliahNama,
        pertemuan = pertemuan.toInt(),
        status = StatusPresensi.fromString(status),
        tanggal = tanggal,
        lastSync = runCatching { Instant.parse(lastSync) }.getOrElse { Clock.System.now() }
    )
}

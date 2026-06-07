package com.kelazzz.app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.kelazzz.app.data.local.KelazZzDatabase
import com.kelazzz.app.data.local.KelasEntity
import com.kelazzz.app.data.local.PresensiEntity
import com.kelazzz.app.data.local.datastore.UserPreferences
import com.kelazzz.app.data.remote.pocket.KelasData
import com.kelazzz.app.data.remote.pocket.PocketApiService
import com.kelazzz.app.data.remote.pocket.PresensiData
import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Kelas
import com.kelazzz.app.domain.model.Presensi
import com.kelazzz.app.domain.model.StatusPresensi
import com.kelazzz.app.domain.repository.PresensiRepository
import com.kelazzz.app.domain.repository.PresensiSyncProgress
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
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
                        saveKelasList(response.data)
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
                        savePresensiForKelas(
                            kelasId = kelasId,
                            mataKuliahNama = mataKuliahNama,
                            presensiList = response.data,
                            lastSync = Clock.System.now().toString()
                        )
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
        return withContext(Dispatchers.IO) {
            try {
                val nim = userPreferences.userNim.first()
                    ?: return@withContext Result.failure(Exception("NIM mahasiswa tidak ditemukan. Silakan login kembali."))

                val apiResult = apiService.submitPresensi(token = token, nim = nim)
                if (apiResult.isSuccess) {
                    val response = apiResult.getOrThrow()
                    if (response.meta.status) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception(response.meta.message))
                    }
                } else {
                    Result.failure(apiResult.exceptionOrNull() ?: Exception("Gagal melakukan presensi."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun syncPresensi(onProgress: (PresensiSyncProgress) -> Unit): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.authToken.first()
                    ?: return@withContext Result.failure(Exception("Sesi login tidak ditemukan. Harap login kembali."))
                val deviceId = userPreferences.deviceId.first() ?: ""
                val nim = userPreferences.userNim.first()
                    ?: return@withContext Result.failure(Exception("NIM mahasiswa tidak ditemukan."))
                val email = userPreferences.userEmail.first() ?: "$nim@student.itera.ac.id"

                val registerResult = apiService.registerToken(token = token, deviceId = deviceId, email = email)
                if (registerResult.isFailure) {
                    return@withContext Result.failure(
                        registerResult.exceptionOrNull() ?: Exception("Gagal otorisasi token sesi.")
                    )
                }

                val kelasResult = apiService.getKelas(token = token, deviceId = deviceId, nim = nim)
                if (kelasResult.isFailure) {
                    return@withContext Result.failure(
                        kelasResult.exceptionOrNull() ?: Exception("Gagal sinkronisasi data kelas.")
                    )
                }

                val kelasResponse = kelasResult.getOrThrow()
                if (!kelasResponse.meta.status) {
                    return@withContext Result.failure(Exception(kelasResponse.meta.message))
                }

                val kelasList = kelasResponse.data
                saveKelasList(kelasList)

                if (kelasList.isEmpty()) {
                    onProgress(PresensiSyncProgress(completed = 0, total = 0))
                    return@withContext Result.success(Unit)
                }

                val failedSyncs = mutableListOf<String>()
                val semaphore = Semaphore(MAX_PARALLEL_PRESENSI_SYNC)
                val progressMutex = Mutex()
                var completedSyncs = 0
                onProgress(PresensiSyncProgress(completed = 0, total = kelasList.size))

                kelasList.map { kelas ->
                    async {
                        semaphore.withPermit {
                            val failureMessage = syncAndSavePresensiDetail(
                                token = token,
                                deviceId = deviceId,
                                nim = nim,
                                kelas = kelas
                            )

                            progressMutex.withLock {
                                completedSyncs += 1
                                onProgress(
                                    PresensiSyncProgress(
                                        completed = completedSyncs,
                                        total = kelasList.size,
                                        currentMataKuliah = kelas.namaMk
                                    )
                                )
                            }

                            failureMessage
                        }
                    }
                }.awaitAll()
                    .filterNotNull()
                    .forEach { failedSyncs += it }

                if (failedSyncs.isEmpty()) {
                    Result.success(Unit)
                } else {
                    Result.failure(
                        Exception(
                            "Sebagian data presensi gagal disinkronkan " +
                                "(${failedSyncs.size}/${kelasList.size}). ${failedSyncs.first()}"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private suspend fun syncAndSavePresensiDetail(
        token: String,
        deviceId: String,
        nim: String,
        kelas: KelasData
    ): String? {
        val detailResult = apiService.getPresensiDetail(
            token = token,
            deviceId = deviceId,
            nim = nim,
            kelasKode = kelas.kodeKelas
        )

        if (detailResult.isSuccess) {
            val detailResponse = detailResult.getOrThrow()
            return if (detailResponse.meta.status) {
                savePresensiForKelas(
                    kelasId = kelas.kodeKelas,
                    mataKuliahNama = kelas.namaMk,
                    presensiList = detailResponse.data,
                    lastSync = Clock.System.now().toString()
                )
                null
            } else {
                "${kelas.namaMk}: ${detailResponse.meta.message}"
            }
        }

        val message = detailResult.exceptionOrNull()?.message ?: "Gagal mengambil detail presensi."
        return "${kelas.namaMk}: $message"
    }

    private fun saveKelasList(kelasList: List<KelasData>) {
        queries.transaction {
            queries.deleteAllKelas()
            kelasList.forEach { data ->
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
    }

    private fun savePresensiForKelas(
        kelasId: String,
        mataKuliahNama: String,
        presensiList: List<PresensiData>,
        lastSync: String
    ) {
        queries.transaction {
            queries.deletePresensiByMataKuliah(kelasId)
            presensiList.forEach { data ->
                queries.insertPresensi(
                    mataKuliahId = kelasId,
                    mataKuliahNama = mataKuliahNama,
                    pertemuan = data.noPertemuan.toLong(),
                    status = data.toStatus(),
                    tanggal = data.waktuMulai ?: "",
                    lastSync = lastSync
                )
            }
        }
    }

    private fun PresensiData.toStatus(): String {
        val normalizedStatus = absenMahasiswa?.trim()?.lowercase()
        return when {
            pertemuan.isNullOrBlank() || waktuMulai.isNullOrBlank() -> "BELUM_MULAI"
            normalizedStatus in listOf("1", "hadir", "h", "true") -> "HADIR"
            normalizedStatus in listOf("2", "izin", "i") -> "IZIN"
            normalizedStatus in listOf("3", "sakit", "s") -> "SAKIT"
            else -> "ALPHA"
        }
    }

    private companion object {
        const val MAX_PARALLEL_PRESENSI_SYNC = 3
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

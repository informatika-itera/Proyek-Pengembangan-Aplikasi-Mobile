package com.kelazzz.app.domain.repository

import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Presensi
import com.kelazzz.app.domain.model.Kelas
import kotlinx.coroutines.flow.Flow

data class PresensiSyncProgress(
    val completed: Int,
    val total: Int,
    val currentMataKuliah: String? = null
)

/**
 * Repository interface untuk data presensi
 * 
 * Mengikuti dependency rule Clean Architecture:
 * Domain layer mendefinisikan interface, Data layer mengimplementasikan.
 */
interface PresensiRepository {
    /** Get daftar kelas mahasiswa dari cache lokal */
    fun getKelasList(): Flow<List<Kelas>>
    
    /** Sync daftar kelas dari API ke local cache */
    suspend fun syncKelas(): Result<Unit>

    /** Get semua rekap presensi dari cache lokal */
    fun getAllPresensi(): Flow<List<Presensi>>
    
    /** Get presensi per mata kuliah */
    fun getPresensiByMataKuliah(mataKuliahId: String): Flow<List<Presensi>>
    
    /** Get ringkasan kehadiran per mata kuliah */
    fun getAttendanceSummary(): Flow<List<AttendanceSummary>>
    
    /** Submit presensi via token */
    suspend fun submitPresensi(token: String): Result<Unit>
    
    /** Sync data presensi spesifik mata kuliah dari API ke local cache */
    suspend fun syncPresensiForKelas(kelasId: String, mataKuliahNama: String): Result<Unit>

    /** Sync data presensi dari API ke local cache */
    suspend fun syncPresensi(onProgress: (PresensiSyncProgress) -> Unit = {}): Result<Unit>
}

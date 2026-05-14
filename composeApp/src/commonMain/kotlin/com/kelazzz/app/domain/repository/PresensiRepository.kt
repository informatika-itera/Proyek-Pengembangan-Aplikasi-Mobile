package com.kelazzz.app.domain.repository

import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Presensi
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface untuk data presensi
 * 
 * Mengikuti dependency rule Clean Architecture:
 * Domain layer mendefinisikan interface, Data layer mengimplementasikan.
 */
interface PresensiRepository {
    /** Get semua rekap presensi dari cache lokal */
    fun getAllPresensi(): Flow<List<Presensi>>
    
    /** Get presensi per mata kuliah */
    fun getPresensiByMataKuliah(mataKuliahId: String): Flow<List<Presensi>>
    
    /** Get ringkasan kehadiran per mata kuliah */
    fun getAttendanceSummary(): Flow<List<AttendanceSummary>>
    
    /** Submit presensi via token */
    suspend fun submitPresensi(token: String): Result<Unit>
    
    /** Sync data presensi dari API ke local cache */
    suspend fun syncPresensi(): Result<Unit>
}

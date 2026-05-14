package com.kelazzz.app.domain.repository

import com.kelazzz.app.domain.model.Jadwal
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface untuk jadwal akademik pribadi
 * 
 * Semua data jadwal disimpan lokal (offline-first).
 * Tidak bergantung pada koneksi internet.
 */
interface JadwalRepository {
    /** Get semua jadwal */
    fun getAllJadwal(): Flow<List<Jadwal>>
    
    /** Get jadwal berdasarkan tanggal */
    fun getJadwalByTanggal(tanggal: String): Flow<List<Jadwal>>
    
    /** Get jadwal mendatang */
    fun getUpcomingJadwal(fromDate: String, limit: Int = 10): Flow<List<Jadwal>>
    
    /** Get jadwal by ID */
    fun getJadwalById(id: Long): Flow<Jadwal?>
    
    /** Insert jadwal baru */
    suspend fun insertJadwal(jadwal: Jadwal): Long
    
    /** Update jadwal */
    suspend fun updateJadwal(jadwal: Jadwal)
    
    /** Delete jadwal */
    suspend fun deleteJadwal(id: Long)
}

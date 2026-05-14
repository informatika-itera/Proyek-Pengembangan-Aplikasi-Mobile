package com.kelazzz.app.domain.usecase

import com.kelazzz.app.domain.model.AttendanceSummary
import com.kelazzz.app.domain.model.Jadwal
import com.kelazzz.app.domain.model.Presensi
import com.kelazzz.app.domain.repository.AIRepository
import com.kelazzz.app.domain.repository.JadwalRepository
import com.kelazzz.app.domain.repository.PresensiRepository
import kotlinx.coroutines.flow.Flow

// ==================== PRESENSI USE CASES ====================

/**
 * Get rekap presensi semua mata kuliah
 */
class GetPresensiUseCase(
    private val repository: PresensiRepository
) {
    operator fun invoke(): Flow<List<Presensi>> {
        return repository.getAllPresensi()
    }
    
    fun byMataKuliah(mataKuliahId: String): Flow<List<Presensi>> {
        return repository.getPresensiByMataKuliah(mataKuliahId)
    }
}

/**
 * Get ringkasan kehadiran per mata kuliah (analytics)
 */
class GetAttendanceSummaryUseCase(
    private val repository: PresensiRepository
) {
    operator fun invoke(): Flow<List<AttendanceSummary>> {
        return repository.getAttendanceSummary()
    }
}

/**
 * Submit presensi via token (manual input atau dari QR scan)
 */
class SubmitPresensiUseCase(
    private val repository: PresensiRepository
) {
    suspend operator fun invoke(token: String): Result<Unit> {
        if (token.isBlank()) {
            return Result.failure(IllegalArgumentException("Token presensi tidak boleh kosong"))
        }
        return repository.submitPresensi(token)
    }
}

// ==================== JADWAL USE CASES ====================

/**
 * Get semua jadwal akademik pribadi
 */
class GetJadwalUseCase(
    private val repository: JadwalRepository
) {
    operator fun invoke(): Flow<List<Jadwal>> {
        return repository.getAllJadwal()
    }
    
    fun byTanggal(tanggal: String): Flow<List<Jadwal>> {
        return repository.getJadwalByTanggal(tanggal)
    }
    
    fun upcoming(fromDate: String, limit: Int = 10): Flow<List<Jadwal>> {
        return repository.getUpcomingJadwal(fromDate, limit)
    }
}

/**
 * Simpan jadwal akademik pribadi (insert atau update)
 */
class SaveJadwalUseCase(
    private val repository: JadwalRepository
) {
    suspend operator fun invoke(jadwal: Jadwal): Result<Long> {
        return try {
            if (jadwal.judul.isBlank()) {
                return Result.failure(IllegalArgumentException("Judul jadwal tidak boleh kosong"))
            }
            
            val id = if (jadwal.id == 0L) {
                repository.insertJadwal(jadwal)
            } else {
                repository.updateJadwal(jadwal)
                jadwal.id
            }
            
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ==================== AI USE CASES ====================

/**
 * Analisis kehadiran otomatis menggunakan AI (Early Warning)
 */
class AnalyzeAttendanceUseCase(
    private val aiRepository: AIRepository
) {
    suspend operator fun invoke(attendanceData: String): Result<String> {
        if (attendanceData.isBlank()) {
            return Result.failure(IllegalArgumentException("Data kehadiran kosong"))
        }
        return aiRepository.analyzeAttendance(attendanceData)
    }
}

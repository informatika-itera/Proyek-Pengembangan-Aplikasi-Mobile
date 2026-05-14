package com.kelazzz.app.domain.repository

/**
 * Repository interface untuk fitur AI di KelazZz
 * 
 * Digunakan untuk:
 * - Analisis kehadiran otomatis (AI Early Warning)
 * - Chatbot asisten akademik
 */
interface AIRepository {
    /** Analisis persentase kehadiran dan berikan peringatan dini */
    suspend fun analyzeAttendance(attendanceData: String): Result<String>
    
    /** Chat dengan asisten akademik AI */
    suspend fun chat(message: String): Result<String>
}

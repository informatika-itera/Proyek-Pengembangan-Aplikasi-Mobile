package com.kelazzz.app.domain.repository

import com.kelazzz.app.domain.model.ChatMessage

/**
 * Repository interface untuk fitur AI di KelazZz
 * 
 * Digunakan untuk:
 * - Analisis kehadiran otomatis (AI Early Warning)
 * - Chatbot asisten akademik dengan tool calling
 */
interface AIRepository {
    /** Analisis persentase kehadiran dan berikan peringatan dini */
    suspend fun analyzeAttendance(attendanceData: String): Result<String>
    
    /** Chat dengan asisten akademik AI — mendukung conversation history */
    suspend fun chat(message: String, history: List<ChatMessage> = emptyList()): Result<String>
    
    /** Bersihkan riwayat percakapan */
    fun clearHistory()
}

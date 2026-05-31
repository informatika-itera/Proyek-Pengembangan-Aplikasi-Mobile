package com.movein.domain.repository

import com.movein.domain.model.ActivityModel
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    // Fungsi untuk mengambil semua riwayat aktivitas dari lokal
    fun getAllActivities(): Flow<List<ActivityModel>>

    // Fungsi untuk memanggil AI Gemini sekaligus menyimpannya ke lokal
    suspend fun generateAndSaveActivity(mood: String): ActivityModel
}
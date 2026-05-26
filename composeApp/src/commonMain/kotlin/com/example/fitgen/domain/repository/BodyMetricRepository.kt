package com.example.fitgen.domain.repository

import com.example.fitgen.domain.model.BodyMetric
import kotlinx.coroutines.flow.Flow

interface BodyMetricRepository {
    /** Mengambil semua body metric secara reaktif, diurutkan terbaru */
    fun getAllBodyMetrics(): Flow<List<BodyMetric>>

    /** Mengambil body metric terbaru (satu record) */
    suspend fun getLatestBodyMetric(): BodyMetric?

    /** Menyimpan body metric baru ke database */
    suspend fun insertBodyMetric(metric: BodyMetric): Long

    /** Menghapus body metric berdasarkan ID */
    suspend fun deleteBodyMetric(id: Long)
}
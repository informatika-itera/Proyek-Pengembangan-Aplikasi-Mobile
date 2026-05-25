package com.example.fitkos.domain.repository

import com.example.fitkos.domain.model.WaterLog
import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    fun getWaterLogByDate(date: String): Flow<WaterLog?>
    suspend fun upsertWaterLog(waterLog: WaterLog)
    suspend fun updateWaterAmount(date: String, amount: Int)
    fun getWaterHistory(): Flow<List<WaterLog>>
}

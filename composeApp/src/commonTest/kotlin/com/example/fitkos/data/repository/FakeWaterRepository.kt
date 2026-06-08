package com.example.fitkos.data.repository

import com.example.fitkos.domain.model.WaterLog
import com.example.fitkos.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeWaterRepository : WaterRepository {
    private val waterLogs = MutableStateFlow<Map<String, WaterLog>>(emptyMap())

    override fun getWaterLogByDate(date: String): Flow<WaterLog?> {
        return waterLogs.map { it[date] }
    }

    override suspend fun upsertWaterLog(waterLog: WaterLog) {
        waterLogs.update { it + (waterLog.date to waterLog) }
    }

    override suspend fun updateWaterAmount(date: String, amount: Int) {
        waterLogs.update { logs ->
            val existing = logs[date]
            if (existing != null) {
                logs + (date to existing.copy(amount = amount))
            } else {
                logs + (date to WaterLog(date, amount, 8))
            }
        }
    }

    override fun getWaterHistory(): Flow<List<WaterLog>> {
        return waterLogs.map { it.values.toList().sortedByDescending { log -> log.date } }
    }
}

package com.example.fitkos.data.repository

import com.example.fitkos.data.local.NoteDatabase
import com.example.fitkos.domain.model.WaterLog
import com.example.fitkos.domain.repository.WaterRepository
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WaterRepositoryImpl(
    private val database: NoteDatabase
) : WaterRepository {
    private val queries = database.noteQueries

    override fun getWaterLogByDate(date: String): Flow<WaterLog?> {
        return queries.getWaterLogByDate(date)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.let { WaterLog(it.date, it.amount.toInt(), it.target.toInt()) } }
    }

    override suspend fun upsertWaterLog(waterLog: WaterLog) {
        queries.upsertWaterLog(waterLog.date, waterLog.amount.toLong(), waterLog.target.toLong())
    }

    override suspend fun updateWaterAmount(date: String, amount: Int) {
        queries.updateWaterAmount(amount.toLong(), date)
    }

    override fun getWaterHistory(): Flow<List<WaterLog>> {
        return queries.getWaterHistory()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { WaterLog(it.date, it.amount.toInt(), it.target.toInt()) } }
    }
}

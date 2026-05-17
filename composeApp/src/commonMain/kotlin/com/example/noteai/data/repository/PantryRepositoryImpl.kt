package com.example.noteai.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.noteai.data.local.NoteDatabase
import com.example.noteai.data.local.PantryEntity
import com.example.noteai.domain.model.PantryItem
import com.example.noteai.domain.repository.PantryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class PantryRepositoryImpl(
    db: NoteDatabase
) : PantryRepository {
    private val queries = db.noteQueries
    private val ioDispatcher = Dispatchers.IO

    override fun getAllPantryItems(): Flow<List<PantryItem>> {
        return queries.getAllPantryItems()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun insertPantryItem(item: PantryItem) {
        queries.insertPantryItem(
            name = item.name,
            amount = item.amount,
            unit = item.unit,
            category = item.category,
            min_stock = item.minStock,
            updated_at = item.updatedAt.toEpochMilliseconds()
        )
    }

    override suspend fun updatePantryItem(item: PantryItem) {
        queries.updatePantryItem(
            name = item.name,
            amount = item.amount,
            unit = item.unit,
            category = item.category,
            min_stock = item.minStock,
            updated_at = item.updatedAt.toEpochMilliseconds(),
            id = item.id
        )
    }

    override suspend fun deletePantryItem(id: Long) {
        queries.deletePantryItem(id)
    }

    override suspend fun updateStockAmount(id: Long, amount: Double) {
        queries.updateStockAmount(
            amount = amount,
            updated_at = Clock.System.now().toEpochMilliseconds(),
            id = id
        )
    }

    private fun PantryEntity.toDomain(): PantryItem = PantryItem(
        id = id,
        name = name,
        amount = amount,
        unit = unit,
        category = category,
        minStock = min_stock,
        updatedAt = Instant.fromEpochMilliseconds(updated_at)
    )
}

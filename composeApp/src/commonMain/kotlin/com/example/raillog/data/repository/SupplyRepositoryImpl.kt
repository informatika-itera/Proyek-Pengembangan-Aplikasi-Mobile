package com.example.raillog.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.raillog.data.local.RailLogDatabase
import com.example.raillog.data.local.entity.toDomain
import com.example.raillog.data.local.entity.toDomainList
import com.example.raillog.domain.model.PartCategory
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.SupplyStatus
import com.example.raillog.domain.repository.SupplyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SupplyRepositoryImpl(
    private val database: RailLogDatabase
) : SupplyRepository {

    private val queries = database.supplyItemQueries

    override fun getAllItems(): Flow<List<SupplyItem>> {
        return queries.getAllItems()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun getItemsByStatus(status: SupplyStatus): Flow<List<SupplyItem>> {
        return queries.getItemsByStatus(status.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun getItemsByCategory(category: PartCategory): Flow<List<SupplyItem>> {
        return queries.getItemsByCategory(category.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun searchItems(query: String): Flow<List<SupplyItem>> {
        return queries.searchItems(query, query, query)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { it.toDomainList() }
    }

    override fun getItemById(id: Long): Flow<SupplyItem?> {
        return queries.getItemById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toDomain() }
    }

    override suspend fun insertItem(item: SupplyItem): Long = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.insertItem(
            part_code = item.partCode,
            name = item.name,
            category = item.category.name,
            quantity = item.quantity.toLong(),
            unit = item.unit,
            supplier = item.supplier,
            status = item.status.name,
            priority = item.priority.name,
            document_ref = item.documentRef,
            notes = item.notes,
            created_at = item.createdAt.toEpochMilliseconds(),
            updated_at = now
        )
        queries.lastInsertId().executeAsOne()
    }

    override suspend fun updateItem(item: SupplyItem) = withContext(Dispatchers.Default) {
        queries.updateItem(
            id = item.id,
            part_code = item.partCode,
            name = item.name,
            category = item.category.name,
            quantity = item.quantity.toLong(),
            unit = item.unit,
            supplier = item.supplier,
            status = item.status.name,
            priority = item.priority.name,
            document_ref = item.documentRef,
            notes = item.notes,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun updateStatus(id: Long, status: SupplyStatus) = withContext(Dispatchers.Default) {
        queries.updateStatus(
            id = id,
            status = status.name,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    override suspend fun deleteItem(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteItem(id)
    }
}
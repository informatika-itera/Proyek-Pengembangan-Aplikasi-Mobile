package com.example.inventra.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.inventra.data.local.InventRaDatabase
import com.example.inventra.data.local.entity.toDomain
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.repository.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class ItemRepositoryImpl(
    database: InventRaDatabase
) : ItemRepository {
    private val queries = database.itemQueries

    override fun getAllItems(): Flow<List<Item>> {
        return queries.getAllItems()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getItemsByCategory(category: ItemCategory): Flow<List<Item>> {
        return queries.getItemsByCategory(category.name)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun searchItems(query: String): Flow<List<Item>> {
        return queries.searchItems(query, query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getItemById(id: Long): Flow<Item?> {
        return queries.getItemById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun insertItem(item: Item): Long {
        return queries.transactionWithResult {
            queries.insertItem(
                name = item.name,
                description = item.description,
                category = item.category.name,
                location = item.location,
                total_stock = item.totalStock.toLong(),
                available_stock = item.availableStock.toLong(),
                condition = item.condition.name,
                pic_name = item.picName,
                image_url = item.imageUrl,
                created_at = item.createdAt.toEpochMilliseconds(),
                updated_at = Clock.System.now().toEpochMilliseconds()
            )
            queries.lastInsertId().executeAsOne()
        }
    }

    override suspend fun updateItem(item: Item) {
        queries.updateItem(
            name = item.name,
            description = item.description,
            category = item.category.name,
            location = item.location,
            total_stock = item.totalStock.toLong(),
            available_stock = item.availableStock.toLong(),
            condition = item.condition.name,
            pic_name = item.picName,
            image_url = item.imageUrl,
            updated_at = Clock.System.now().toEpochMilliseconds(),
            id = item.id
        )
    }

    override suspend fun deleteItem(id: Long) {
        queries.deleteItem(id)
    }
}

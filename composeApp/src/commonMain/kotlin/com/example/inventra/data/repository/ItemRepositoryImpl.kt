package com.example.inventra.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.inventra.core.network.SupabaseClientProvider
import com.example.inventra.data.local.InventRaDatabase
import com.example.inventra.data.local.entity.toDomain
import com.example.inventra.data.remote.dto.InsertItemDto
import com.example.inventra.data.remote.dto.ItemDto
import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import com.example.inventra.domain.repository.ItemRepository
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ItemRepositoryImpl(
    private val database: InventRaDatabase
) : ItemRepository {

    private val db = SupabaseClientProvider.client.postgrest
    private val storageClient = SupabaseClientProvider.client.storage
    private val queries = database.itemQueries
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getAllItems(): Flow<List<Item>> {
        syncScope.launch { syncItemsFromSupabase() }
        return queries.getAllItems()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override fun getItemsByCategory(category: ItemCategory): Flow<List<Item>> {
        syncScope.launch { syncItemsFromSupabase() }
        return if (category == ItemCategory.ALL) {
            queries.getAllItems()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { entities -> entities.map { it.toDomain() } }
                .catch { emit(emptyList()) }
        } else {
            queries.getItemsByCategory(category.name)
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map { entities -> entities.map { it.toDomain() } }
                .catch { emit(emptyList()) }
        }
    }

    override fun searchItems(query: String): Flow<List<Item>> {
        return queries.searchItems(query, query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override fun getItemById(id: Long): Flow<Item?> {
        return queries.getItemById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity -> entity?.toDomain() }
            .catch { emit(null) }
    }

    override suspend fun insertItem(item: Item): Long {
        val now = Clock.System.now().toEpochMilliseconds()
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
            created_at = now,
            updated_at = now
        )
        val localId = queries.lastInsertId().executeAsOne()
        syncScope.launch {
            try {
                db["items"].insert(
                    InsertItemDto(
                        name = item.name,
                        description = item.description,
                        category = item.category.name,
                        location = item.location,
                        totalStock = item.totalStock,
                        availableStock = item.availableStock,
                        condition = item.condition.name,
                        picName = item.picName,
                        imageUrl = item.imageUrl
                    )
                )
            } catch (e: Exception) {
                println("Supabase insert gagal: ${e.message}")
            }
        }
        return localId
    }

    override suspend fun updateItem(item: Item) {
        val now = Clock.System.now().toEpochMilliseconds()
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
            updated_at = now,
            id = item.id
        )
        syncScope.launch {
            try {
                db["items"].update(
                    mapOf(
                        "name" to item.name,
                        "description" to item.description,
                        "category" to item.category.name,
                        "location" to item.location,
                        "total_stock" to item.totalStock,
                        "available_stock" to item.availableStock,
                        "condition" to item.condition.name,
                        "pic_name" to item.picName,
                        "image_url" to item.imageUrl
                    )
                ) { filter { eq("id", item.id.toString()) } }
            } catch (e: Exception) {
                println("Supabase update gagal: ${e.message}")
            }
        }
    }

    override suspend fun deleteItem(id: Long) {
        queries.deleteItem(id)
        syncScope.launch {
            try {
                db["items"].delete {
                    filter { eq("id", id.toString()) }
                }
            } catch (e: Exception) {
                println("Supabase delete gagal: ${e.message}")
            }
        }
    }

    override suspend fun uploadItemImage(imageBytes: ByteArray, fileName: String): Result<String> {
        return try {
            val bucket = storageClient["item-images"]
            val path = "items/$fileName"
            bucket.upload(path, imageBytes) { upsert = true }
            Result.success(bucket.publicUrl(path))
        } catch (e: Exception) {
            Result.failure(Exception("Gagal upload foto: ${e.message}"))
        }
    }

    private suspend fun syncItemsFromSupabase() {
        try {
            val remoteItems = db["items"]
                .select { filter { eq("is_active", true) } }
                .decodeList<ItemDto>()
            database.transaction {
                queries.deleteAll()
                remoteItems.forEach { dto ->
                    val now = Clock.System.now().toEpochMilliseconds()
                    queries.insertItem(
                        name = dto.name,
                        description = dto.description,
                        category = dto.category,
                        location = dto.location,
                        total_stock = dto.totalStock.toLong(),
                        available_stock = dto.availableStock.toLong(),
                        condition = dto.condition,
                        pic_name = dto.picName,
                        image_url = dto.imageUrl,
                        created_at = now,
                        updated_at = now
                    )
                }
            }
            println("SYNC: ${remoteItems.size} items synced")
        } catch (e: Exception) {
            println("SYNC offline: ${e.message}")
        }
    }
}
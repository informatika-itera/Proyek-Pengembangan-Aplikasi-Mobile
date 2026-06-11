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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class ItemRepositoryImpl(
    private val database: InventRaDatabase
) : ItemRepository {

    private val db = SupabaseClientProvider.client.postgrest
    private val storageClient = SupabaseClientProvider.client.storage
    private val queries = database.itemQueries
    private val syncScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var lastSyncTime = 0L
    private val SYNC_COOLDOWN_MS = 600_000L // Naikkan ke 10 menit agar data lokal stabil

    override fun getAllItems(): Flow<List<Item>> {
        triggerSyncIfStale()
        return queries.getAllItems()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override fun getItemsByCategory(category: ItemCategory): Flow<List<Item>> {
        triggerSyncIfStale()
        return if (category == ItemCategory.ALL) {
            queries.getAllItems().asFlow().mapToList(Dispatchers.IO)
                .map { it.map { e -> e.toDomain() } }.catch { emit(emptyList()) }
        } else {
            queries.getItemsByCategory(category.name).asFlow().mapToList(Dispatchers.IO)
                .map { it.map { e -> e.toDomain() } }.catch { emit(emptyList()) }
        }
    }

    override fun searchItems(query: String): Flow<List<Item>> =
        queries.searchItems(query, query).asFlow().mapToList(Dispatchers.IO)
            .map { it.map { e -> e.toDomain() } }.catch { emit(emptyList()) }

    override fun getItemById(id: Long): Flow<Item?> =
        queries.getItemById(id).asFlow().mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }.catch { emit(null) }

    override suspend fun insertItem(item: Item): Long {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.insertItem(
            remote_id = null, // Akan diupdate setelah sync
            name = item.name, description = item.description,
            category = item.category.name, location = item.location,
            total_stock = item.totalStock.toLong(),
            available_stock = item.availableStock.toLong(),
            condition = item.condition.name, pic_name = item.picName,
            pic_phone = item.picPhone,
            image_url = item.imageUrl, created_at = now, updated_at = now
        )
        val localId = queries.lastInsertId().executeAsOne()
        
        // Reset cooldown agar sync tidak segera menimpa data lokal yang baru dibuat
        lastSyncTime = now

        syncScope.launch {
            try {
                val response = db["items"].insert(InsertItemDto(
                    name = item.name, description = item.description,
                    category = item.category.name, location = item.location,
                    totalStock = item.totalStock, availableStock = item.availableStock,
                    condition = item.condition.name, picName = item.picName,
                    picPhone = item.picPhone,
                    imageUrl = item.imageUrl
                )) { select() }.decodeSingle<ItemDto>()
                
                // Update remote_id di lokal
                queries.updateRemoteId(remote_id = response.id, id = localId)
            } catch (e: Exception) {
                // Supabase insert gagal
            }
        }
        return localId
    }

    override suspend fun updateItem(item: Item) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.updateItem(
            name = item.name, description = item.description,
            category = item.category.name, location = item.location,
            total_stock = item.totalStock.toLong(),
            available_stock = item.availableStock.toLong(),
            condition = item.condition.name, pic_name = item.picName,
            pic_phone = item.picPhone,
            image_url = item.imageUrl, updated_at = now, id = item.id
        )

        // Reset cooldown agar sync tidak segera menimpa data lokal yang baru diupdate
        lastSyncTime = now

        syncScope.launch {
            try {
                val targetId = item.remoteId ?: return@launch
                val updateData = buildJsonObject {
                    put("name", item.name)
                    put("description", item.description)
                    put("category", item.category.name)
                    put("location", item.location)
                    put("total_stock", item.totalStock)
                    put("available_stock", item.availableStock)
                    put("condition", item.condition.name)
                    put("pic_name", item.picName)
                    put("pic_phone", item.picPhone)
                    if (item.imageUrl != null) put("image_url", item.imageUrl)
                }
                db["items"].update(updateData) { filter { eq("id", targetId) } }
            } catch (e: Exception) {
                // Supabase update gagal
            }
        }
    }

    override suspend fun deleteItem(id: Long) {
        val item = queries.getItemById(id).executeAsOneOrNull()
        queries.deleteItem(id)
        syncScope.launch {
            try {
                val targetId = item?.remote_id ?: return@launch
                db["items"].delete { filter { eq("id", targetId) } }
            } catch (e: Exception) {
                // Supabase delete gagal
            }
        }
    }

    override suspend fun uploadItemImage(imageBytes: ByteArray, fileName: String): Result<String> {
        return try {
            val bucket = storageClient["items"]
            val path = "items/$fileName"
            bucket.upload(path, imageBytes) { upsert = true }
            Result.success(bucket.publicUrl(path))
        } catch (e: Exception) {
            Result.failure(Exception("Gagal upload foto: ${e.message}"))
        }
    }

    override suspend fun refresh() {
        syncItemsFromSupabase()
        lastSyncTime = Clock.System.now().toEpochMilliseconds()
    }

    override suspend fun deleteAll() {
        // 1. Hapus local SQLDelight
        queries.deleteAll()

        // 2. Hapus Supabase - gunakan adminClient agar bypass RLS jika perlu
        try {
            val adminDb = SupabaseClientProvider.adminClient.postgrest
            adminDb["items"].delete {
                filter { neq("id", "00000000-0000-0000-0000-000000000000") }
            }
        } catch (e: Exception) {
            // deleteAll Supabase items error
        }
    }

    override suspend fun resetAllStocks() {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.resetAllStocks(now)
        lastSyncTime = now // Reset cooldown
    }

    override suspend fun updateAvailableStock(itemId: Long, newStock: Int) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.updateAvailableStock(
            available_stock = newStock.toLong(),
            updated_at = now,
            id = itemId
        )
        // Reset cooldown agar sync tidak menimpa perubahan lokal yang baru saja dibuat
        lastSyncTime = now
    }

    private fun triggerSyncIfStale() {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastSyncTime > SYNC_COOLDOWN_MS) {
            syncScope.launch {
                syncItemsFromSupabase()
                lastSyncTime = Clock.System.now().toEpochMilliseconds()
            }
        }
    }

    private suspend fun syncItemsFromSupabase() {
        try {
            val remoteItems = db["items"]
                .select()
                .decodeList<ItemDto>()
            database.transaction {
                queries.deleteAll()
                remoteItems.forEach { dto ->
                    queries.insertItem(
                        remote_id = dto.id,
                        name = dto.name, description = dto.description,
                        category = dto.category, location = dto.location,
                        total_stock = dto.totalStock.toLong(),
                        available_stock = dto.availableStock.toLong(),
                        condition = dto.condition, pic_name = dto.picName,
                        pic_phone = dto.picPhone,
                        image_url = dto.imageUrl,
                        created_at = dto.createdAt?.let { Instant.parse(it).toEpochMilliseconds() } 
                            ?: Clock.System.now().toEpochMilliseconds(),
                        updated_at = dto.updatedAt?.let { Instant.parse(it).toEpochMilliseconds() } 
                            ?: Clock.System.now().toEpochMilliseconds()
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
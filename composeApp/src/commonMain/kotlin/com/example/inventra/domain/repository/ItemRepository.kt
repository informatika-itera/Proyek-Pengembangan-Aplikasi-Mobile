package com.example.inventra.domain.repository

import com.example.inventra.domain.model.Item
import com.example.inventra.domain.model.ItemCategory
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAllItems(): Flow<List<Item>>
    fun getItemsByCategory(category: ItemCategory): Flow<List<Item>>
    fun searchItems(query: String): Flow<List<Item>>
    fun getItemById(id: Long): Flow<Item?>
    suspend fun insertItem(item: Item): Long
    suspend fun updateItem(item: Item)
    suspend fun deleteItem(id: Long)
    suspend fun uploadItemImage(imageBytes: ByteArray, fileName: String): Result<String>
}
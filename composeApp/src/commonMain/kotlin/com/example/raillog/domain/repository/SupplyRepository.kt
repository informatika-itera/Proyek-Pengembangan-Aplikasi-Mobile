package com.example.raillog.domain.repository

import com.example.raillog.domain.model.PartCategory
import com.example.raillog.domain.model.SupplyItem
import com.example.raillog.domain.model.SupplyStatus
import kotlinx.coroutines.flow.Flow

interface SupplyRepository {
    fun getAllItems(): Flow<List<SupplyItem>>
    fun getItemsByStatus(status: SupplyStatus): Flow<List<SupplyItem>>
    fun getItemsByCategory(category: PartCategory): Flow<List<SupplyItem>>
    fun searchItems(query: String): Flow<List<SupplyItem>>
    fun getItemById(id: Long): Flow<SupplyItem?>
    suspend fun insertItem(item: SupplyItem): Long
    suspend fun updateItem(item: SupplyItem)
    suspend fun updateStatus(id: Long, status: SupplyStatus)
    suspend fun deleteItem(id: Long)
}
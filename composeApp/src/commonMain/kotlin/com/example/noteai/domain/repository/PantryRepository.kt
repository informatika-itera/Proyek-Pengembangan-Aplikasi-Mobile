package com.example.noteai.domain.repository

import com.example.noteai.domain.model.PantryItem
import kotlinx.coroutines.flow.Flow

interface PantryRepository {
    fun getAllPantryItems(): Flow<List<PantryItem>>
    suspend fun insertPantryItem(item: PantryItem)
    suspend fun updatePantryItem(item: PantryItem)
    suspend fun deletePantryItem(id: Long)
    suspend fun updateStockAmount(id: Long, amount: Double)
}

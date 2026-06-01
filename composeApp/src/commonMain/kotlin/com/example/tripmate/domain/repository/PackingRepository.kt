package com.example.tripmate.domain.repository

import com.example.tripmate.domain.model.PackingItem
import kotlinx.coroutines.flow.Flow

interface PackingRepository {
    fun getItemsByTripId(tripId: Long): Flow<List<PackingItem>>
    suspend fun insertItem(item: PackingItem)
    suspend fun updateChecked(id: Long, isChecked: Boolean)
    suspend fun deleteItem(id: Long)
}

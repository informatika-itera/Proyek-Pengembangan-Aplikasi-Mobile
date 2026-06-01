package com.example.tripmate.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.tripmate.data.local.TripDatabase
import com.example.tripmate.domain.model.PackingItem
import com.example.tripmate.domain.repository.PackingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PackingRepositoryImpl(database: TripDatabase) : PackingRepository {

    private val queries = database.packingItemQueries

    override fun getItemsByTripId(tripId: Long): Flow<List<PackingItem>> =
        queries.getItemsByTripId(tripId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map {
                    PackingItem(
                        id = it.id,
                        tripId = it.trip_id,
                        name = it.name,
                        isChecked = it.is_checked != 0L,
                        createdAt = it.created_at
                    )
                }
            }

    override suspend fun insertItem(item: PackingItem) {
        queries.insertItem(
            tripId = item.tripId,
            name = item.name,
            createdAt = item.createdAt
        )
    }

    override suspend fun updateChecked(id: Long, isChecked: Boolean) {
        queries.updateChecked(isChecked = if (isChecked) 1L else 0L, id = id)
    }

    override suspend fun deleteItem(id: Long) {
        queries.deleteItem(id)
    }
}

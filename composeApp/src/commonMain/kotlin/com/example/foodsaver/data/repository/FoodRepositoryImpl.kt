package com.example.foodsaver.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.foodsaver.data.local.FoodSaverDatabase
import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodRepositoryImpl(
    private val db: FoodSaverDatabase
) : FoodRepository {

    private val queries = db.foodItemQueries

    override fun getAllFoodItems(): Flow<List<FoodItem>> {
        return queries.getAllFoodItems()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    FoodItem(
                        id = entity.id,
                        name = entity.name,
                        quantity = entity.quantity,
                        unit = entity.unit,
                        category = entity.category,
                        expiryDate = entity.expiryDate,
                        storageLocation = entity.storageLocation, // Default sudah ada di DB
                        notes = entity.notes,
                        isConsumed = entity.isConsumed == 1L
                    )
                }
            }
    }

    override suspend fun getFoodItemById(id: Long): FoodItem? {
        return queries.getFoodItemById(id)
            .executeAsOneOrNull()
            ?.let { entity ->
                FoodItem(
                    id = entity.id,
                    name = entity.name,
                    quantity = entity.quantity,
                    unit = entity.unit,
                    category = entity.category,
                    expiryDate = entity.expiryDate,
                    storageLocation = entity.storageLocation,
                    notes = entity.notes,
                    isConsumed = entity.isConsumed == 1L
                )
            }
    }

    override suspend fun insertFoodItem(foodItem: FoodItem) {
        queries.insertFoodItem(
            id = if (foodItem.id == 0L) null else foodItem.id,
            name = foodItem.name,
            quantity = foodItem.quantity,
            unit = foodItem.unit,
            expiryDate = foodItem.expiryDate,
            category = foodItem.category,
            storageLocation = foodItem.storageLocation,
            notes = foodItem.notes,
            isConsumed = if (foodItem.isConsumed) 1L else 0L
        )
    }

    override suspend fun deleteFoodItem(id: Long) {
        queries.deleteFoodItem(id)
    }

    override suspend fun updateFoodItem(foodItem: FoodItem) {
        queries.updateFoodItem(
            name = foodItem.name,
            quantity = foodItem.quantity,
            unit = foodItem.unit,
            expiryDate = foodItem.expiryDate,
            category = foodItem.category,
            storageLocation = foodItem.storageLocation,
            notes = foodItem.notes,
            isConsumed = if (foodItem.isConsumed) 1L else 0L,
            id = foodItem.id
        )
    }
}

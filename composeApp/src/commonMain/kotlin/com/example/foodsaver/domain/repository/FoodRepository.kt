package com.example.foodsaver.domain.repository

import com.example.foodsaver.domain.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun getAllFoodItems(): Flow<List<FoodItem>>
    suspend fun getFoodItemById(id: Long): FoodItem?
    suspend fun insertFoodItem(foodItem: FoodItem)
    suspend fun deleteFoodItem(id: Long)
    suspend fun updateFoodItem(foodItem: FoodItem)
}

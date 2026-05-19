package com.example.foodsaver.domain.usecase

import com.example.foodsaver.domain.model.FoodItem
import com.example.foodsaver.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow

class GetAllFoodUseCase(private val repository: FoodRepository) {
    operator fun invoke(): Flow<List<FoodItem>> = repository.getAllFoodItems()
}

class GetFoodDetailUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(id: Long): FoodItem? = repository.getFoodItemById(id)
}

class SaveFoodUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(foodItem: FoodItem) {
        if (foodItem.id == 0L) {
            repository.insertFoodItem(foodItem)
        } else {
            repository.updateFoodItem(foodItem)
        }
    }
}

class DeleteFoodUseCase(private val repository: FoodRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteFoodItem(id)
}

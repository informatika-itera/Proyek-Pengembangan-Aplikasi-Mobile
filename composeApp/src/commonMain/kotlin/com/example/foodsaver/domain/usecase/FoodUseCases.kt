package com.example.foodsaver.domain.usecase

import com.example.foodsaver.domain.model.FoodItem
import kotlinx.coroutines.flow.Flow

/**
 * Use Case untuk mengambil semua data inventory makanan.
 */
class GetInventoryUseCase(
    // private val repository: FoodRepository // Akan dihubungkan setelah repository siap
) {
    // operator fun invoke(): Flow<List<FoodItem>> = repository.getAllFood()
}

/**
 * Use Case untuk menambahkan item makanan baru.
 */
class AddFoodUseCase() {
    // suspend operator fun invoke(food: FoodItem) = repository.insertFood(food)
}

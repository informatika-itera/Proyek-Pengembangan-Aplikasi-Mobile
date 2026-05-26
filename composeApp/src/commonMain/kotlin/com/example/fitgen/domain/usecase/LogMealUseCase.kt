package com.example.fitgen.domain.usecase

import com.example.fitgen.domain.model.MealLog
import com.example.fitgen.domain.repository.MealRepository

/**
 * Use case untuk mencatat satu porsi makanan ke database.
 * Melakukan validasi dasar sebelum menyimpan.
 */
class LogMealUseCase(private val repository: MealRepository) {
    suspend operator fun invoke(meal: MealLog): Result<Long> {
        if (meal.namaMakanan.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama makanan tidak boleh kosong"))
        }
        if (meal.kalori < 0) {
            return Result.failure(IllegalArgumentException("Kalori tidak boleh negatif"))
        }
        return runCatching { repository.insertMeal(meal) }
    }
}
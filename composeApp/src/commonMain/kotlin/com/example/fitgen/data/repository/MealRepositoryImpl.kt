package com.example.fitgen.data.repository

import com.example.fitgen.domain.model.MealLog
import com.example.fitgen.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Implementasi sementara MealRepository menggunakan in-memory storage.
 * Akan diganti dengan SQLDelight setelah schema di-generate.
 */
class MealRepositoryImpl : MealRepository {

    // In-memory storage untuk menyimpan meal log sementara
    private val _meals = MutableStateFlow<List<MealLog>>(emptyList())
    private var nextId = 1L

    override fun getAllMeals(): Flow<List<MealLog>> {
        return _meals.asStateFlow()
    }

    override fun getMealsByDate(startOfDay: Long, endOfDay: Long): Flow<List<MealLog>> {
        return _meals.map { list ->
            list.filter { it.tanggal in startOfDay..endOfDay }
        }
    }

    override suspend fun getDailyCaloriesTotal(startOfDay: Long, endOfDay: Long): Int {
        return _meals.value
            .filter { it.tanggal in startOfDay..endOfDay }
            .sumOf { it.kalori }
    }

    override suspend fun insertMeal(meal: MealLog): Long {
        val newMeal = meal.copy(id = nextId++)
        _meals.value = _meals.value + newMeal
        return newMeal.id
    }

    override suspend fun updateMeal(meal: MealLog) {
        _meals.value = _meals.value.map {
            if (it.id == meal.id) meal else it
        }
    }

    override suspend fun deleteMeal(id: Long) {
        _meals.value = _meals.value.filter { it.id != id }
    }
}
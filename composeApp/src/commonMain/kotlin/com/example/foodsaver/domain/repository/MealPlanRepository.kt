package com.example.foodsaver.domain.repository

import com.example.foodsaver.domain.model.MealPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface MealPlanRepository {
    fun getMealPlansForDate(date: LocalDate): Flow<List<MealPlan>>
    suspend fun addMealPlan(mealPlan: MealPlan)
    suspend fun removeMealPlan(id: Long)
    fun getAllMealPlans(): Flow<List<MealPlan>>
}

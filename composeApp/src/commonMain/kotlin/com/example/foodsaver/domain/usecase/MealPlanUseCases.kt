package com.example.foodsaver.domain.usecase

import com.example.foodsaver.domain.model.MealPlan
import com.example.foodsaver.domain.repository.MealPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

class GetMealPlansForDateUseCase(private val repository: MealPlanRepository) {
    operator fun invoke(date: LocalDate): Flow<List<MealPlan>> = 
        repository.getMealPlansForDate(date)
}

class AddMealPlanUseCase(private val repository: MealPlanRepository) {
    suspend operator fun invoke(mealPlan: MealPlan) = 
        repository.addMealPlan(mealPlan)
}

class RemoveMealPlanUseCase(private val repository: MealPlanRepository) {
    suspend operator fun invoke(id: Long) = 
        repository.removeMealPlan(id)
}

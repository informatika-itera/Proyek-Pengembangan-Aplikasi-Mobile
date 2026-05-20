package com.example.foodsaver.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
enum class MealType {
    BREAKFAST, LUNCH, DINNER
}

@Serializable
data class MealPlan(
    val id: Long = 0,
    val recipeId: String,
    val recipeName: String,
    val recipeImageUrl: String,
    val date: LocalDate,
    val mealType: MealType
)

package com.example.foodsaver.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeRecommendation(
    val title: String,
    val description: String,
    val usedIngredients: List<String>,
    val optionalIngredients: List<String>,
    val cookingTimeMinutes: Int,
    val difficulty: String,
    val reason: String,
    val steps: List<String>,
    val warningMessage: String? = null,
    val imageUrl: String? = null,
    val matchScore: Double = 0.0
)

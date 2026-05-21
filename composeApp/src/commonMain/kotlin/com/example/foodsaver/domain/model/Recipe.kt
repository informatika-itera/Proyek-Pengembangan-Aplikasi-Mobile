package com.example.foodsaver.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String,
    val name: String,
    val imageUrl: String,
    val category: String? = null,
    val area: String? = null,
    val instructions: String? = null,
    val ingredients: List<IngredientAmount> = emptyList(),
    val matchPercentage: Int = 0,
    val isFavorite: Boolean = false
)

@Serializable
data class IngredientAmount(
    val name: String,
    val amount: String
)

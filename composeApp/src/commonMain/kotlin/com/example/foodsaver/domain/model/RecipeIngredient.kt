package com.example.foodsaver.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredient(
    val name: String,
    val quantity: String,
    val source: IngredientSource,
    val expiryStatus: FoodStatus? = null,
    val daysLeft: Int? = null
)

enum class IngredientSource {
    INVENTORY, MANUAL
}

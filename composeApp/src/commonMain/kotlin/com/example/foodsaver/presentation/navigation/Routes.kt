package com.example.foodsaver.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Expiry : Screen()

    @Serializable
    data object Calendar : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object AddFood : Screen()

    @Serializable
    data class EditFood(val id: Long) : Screen()

    @Serializable
    data class FoodDetail(val id: Long) : Screen()

    @Serializable
    data object AIAssistant : Screen()

    @Serializable
    data object RecipeSelection : Screen()

    @Serializable
    data class RecipeResult(val ingredientIds: List<Long>, val prioritizeExpired: Boolean, val preference: String) : Screen()
}

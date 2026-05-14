package com.example.foodsaver.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Definisi rute navigasi untuk FoodSaver.
 */
sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object AddFood : Screen()

    @Serializable
    data class FoodDetail(val id: Long) : Screen()

    @Serializable
    data object AIAssistant : Screen()
}

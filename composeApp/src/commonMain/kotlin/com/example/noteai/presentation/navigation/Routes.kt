package com.example.noteai.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
    @Serializable
    data object Splash : Route
    
    @Serializable
    data object Auth : Route

    @Serializable
    data object Chat : Route
    
    @Serializable
    data object Pantry : Route
    
    @Serializable
    data object Recipes : Route

    @Serializable
    data class RecipeDetail(val recipeId: Long) : Route
    
    @Serializable
    data class AddEditRecipe(val recipeId: Long? = null) : Route

    @Serializable
    data object Profile : Route
}

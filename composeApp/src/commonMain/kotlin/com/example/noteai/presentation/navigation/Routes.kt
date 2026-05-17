package com.example.noteai.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    
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
}

interface NavigationActions {
    fun navigateToChat()
    fun navigateToPantry()
    fun navigateToRecipes()
    fun navigateToRecipeDetail(recipeId: Long)
    fun navigateToAddEditRecipe(recipeId: Long? = null)
    fun navigateBack()
}

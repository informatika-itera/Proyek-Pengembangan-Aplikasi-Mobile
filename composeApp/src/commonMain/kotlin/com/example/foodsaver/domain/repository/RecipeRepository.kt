package com.example.foodsaver.domain.repository

import com.example.foodsaver.domain.model.Recipe
import com.example.foodsaver.domain.model.RecipeIngredient
import com.example.foodsaver.domain.model.RecipeRecommendation
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchRecipesByIngredients(ingredients: List<String>): List<Recipe>
    suspend fun getRecipeDetails(id: String): Recipe?
    suspend fun searchRecipesByName(query: String): List<Recipe>
    
    // API based recommendations with local fallback
    suspend fun getRecommendations(
        ingredients: List<RecipeIngredient>,
        preference: String,
        prioritizeExpiring: Boolean
    ): Result<RecipeRecommendation>

    // AI Recipe Generation (Keep for Assistant page if needed)
    suspend fun generateAiRecipe(
        ingredients: List<RecipeIngredient>,
        preference: String,
        prioritizeExpiring: Boolean
    ): Result<RecipeRecommendation>

    // Favorites (Offline)
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    suspend fun toggleFavorite(recipe: Recipe)
    suspend fun isFavorite(id: String): Boolean
}

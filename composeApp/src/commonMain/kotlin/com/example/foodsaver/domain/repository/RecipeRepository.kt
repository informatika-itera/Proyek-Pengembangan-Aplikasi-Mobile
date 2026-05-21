package com.example.foodsaver.domain.repository

import com.example.foodsaver.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun searchRecipesByIngredients(ingredients: List<String>): List<Recipe>
    suspend fun getRecipeDetails(id: String): Recipe?
    suspend fun searchRecipesByName(query: String): List<Recipe>
    
    // Favorites (Offline)
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    suspend fun toggleFavorite(recipe: Recipe)
    suspend fun isFavorite(id: String): Boolean
}

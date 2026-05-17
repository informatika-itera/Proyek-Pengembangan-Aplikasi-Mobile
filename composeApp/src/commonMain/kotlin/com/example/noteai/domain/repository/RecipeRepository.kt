package com.example.noteai.domain.repository

import com.example.noteai.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: Long): Flow<Recipe?>
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun deleteRecipe(id: Long)
    suspend fun toggleFavorite(id: Long)
}

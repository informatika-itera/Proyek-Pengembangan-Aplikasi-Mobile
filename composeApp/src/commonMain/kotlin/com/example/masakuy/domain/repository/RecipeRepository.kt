package com.example.masakuy.domain.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getAllRecipes(): Flow<Result<List<Recipe>>>
    fun getRecipeById(id: String): Flow<Result<RecipeDetail>>
    fun searchRecipes(query: String): Flow<Result<List<Recipe>>>
    fun getRecipesByBudget(budget: Int): Flow<Result<List<Recipe>>>
    suspend fun saveFavorite(recipeId: String, isFavorite: Boolean)
    fun getFavoriteRecipes(): Flow<Result<List<Recipe>>>
}
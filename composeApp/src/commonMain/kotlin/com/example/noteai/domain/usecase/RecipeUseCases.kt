package com.example.noteai.domain.usecase

import com.example.noteai.domain.model.Recipe
import com.example.noteai.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipes(private val repository: RecipeRepository) {
    operator fun invoke(): Flow<List<Recipe>> = repository.getAllRecipes()
}

class GetFavoriteRecipes(private val repository: RecipeRepository) {
    operator fun invoke(): Flow<List<Recipe>> = repository.getFavoriteRecipes()
}

class GetRecipeById(private val repository: RecipeRepository) {
    operator fun invoke(id: Long): Flow<Recipe?> = repository.getRecipeById(id)
}

class AddRecipe(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipe: Recipe) = repository.insertRecipe(recipe)
}

class UpdateRecipe(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipe: Recipe) = repository.updateRecipe(recipe)
}

class DeleteRecipe(private val repository: RecipeRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteRecipe(id)
}

class ToggleFavoriteRecipe(private val repository: RecipeRepository) {
    suspend operator fun invoke(id: Long) = repository.toggleFavorite(id)
}

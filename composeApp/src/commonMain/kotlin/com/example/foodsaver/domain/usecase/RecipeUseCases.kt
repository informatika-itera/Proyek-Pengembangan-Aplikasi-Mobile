package com.example.foodsaver.domain.usecase

import com.example.foodsaver.domain.model.Recipe
import com.example.foodsaver.domain.model.RecipeIngredient
import com.example.foodsaver.domain.model.RecipeRecommendation
import com.example.foodsaver.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipesByIngredientsUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(ingredients: List<String>): List<Recipe> = 
        repository.searchRecipesByIngredients(ingredients)
}

class GetRecipeDetailsUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(id: String): Recipe? = 
        repository.getRecipeDetails(id)
}

class SearchRecipesUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(query: String): List<Recipe> = 
        repository.searchRecipesByName(query)
}

class ToggleFavoriteRecipeUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipe: Recipe) = 
        repository.toggleFavorite(recipe)
}

class GetFavoriteRecipesUseCase(private val repository: RecipeRepository) {
    operator fun invoke(): Flow<List<Recipe>> = 
        repository.getFavoriteRecipes()
}

class GenerateAiRecipeUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(
        ingredients: List<RecipeIngredient>,
        preference: String,
        prioritizeExpiring: Boolean
    ): Result<RecipeRecommendation> = 
        repository.generateAiRecipe(ingredients, preference, prioritizeExpiring)
}

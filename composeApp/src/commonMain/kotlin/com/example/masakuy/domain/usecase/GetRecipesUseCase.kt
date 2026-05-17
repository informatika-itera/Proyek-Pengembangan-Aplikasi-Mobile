package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipesUseCase(private val repository: RecipeRepository) {
    operator fun invoke(): Flow<Result<List<Recipe>>> = repository.getAllRecipes()
}
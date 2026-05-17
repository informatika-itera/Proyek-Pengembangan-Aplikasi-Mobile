package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipeDetailUseCase(private val repository: RecipeRepository) {
    operator fun invoke(recipeId: String): Flow<Result<RecipeDetail>> =
        repository.getRecipeById(recipeId)
}
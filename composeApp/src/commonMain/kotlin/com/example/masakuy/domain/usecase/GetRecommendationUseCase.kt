package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.repository.AIRepository
import com.example.masakuy.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecommendationUseCase(
    private val aiRepository: AIRepository,
    private val recipeRepository: RecipeRepository
) {
    operator fun invoke(
        budget: Int,
        ingredients: List<String> = emptyList(),
        preferences: String = ""
    ): Flow<Result<List<Recipe>>> =
        aiRepository.getRecommendation(budget, ingredients, preferences)
}
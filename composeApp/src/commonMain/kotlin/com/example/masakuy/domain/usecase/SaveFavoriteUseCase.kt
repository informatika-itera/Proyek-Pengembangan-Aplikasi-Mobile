package com.example.masakuy.domain.usecase

import com.example.masakuy.domain.repository.RecipeRepository

class SaveFavoriteUseCase(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipeId: String, isFavorite: Boolean) {
        repository.saveFavorite(recipeId, isFavorite)
    }
}
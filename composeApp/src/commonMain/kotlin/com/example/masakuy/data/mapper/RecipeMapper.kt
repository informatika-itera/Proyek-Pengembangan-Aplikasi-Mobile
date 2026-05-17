package com.example.masakuy.data.mapper

import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class RecipeMapper {
    fun mapRecipeDetailToRecipe(detail: RecipeDetail): Recipe {
        return Recipe(
            id = detail.id,
            name = detail.name,
            image = detail.image,
            estimatedCost = detail.estimatedCost,
            estimatedTime = detail.estimatedTime,
            difficulty = detail.difficulty,
            isFavorite = detail.isFavorite
        )
    }

    fun parseIngredients(json: String): List<Ingredient> {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseInstructions(json: String): List<String> {
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
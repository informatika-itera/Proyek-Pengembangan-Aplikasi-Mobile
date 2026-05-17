package com.example.masakuy.util

import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail

object TestData {
    fun createRecipe(
        id: String = "1",
        name: String = "Nasi Telur Kecap",
        cost: Int = 11000,
        time: Int = 10
    ) = Recipe(
        id = id,
        name = name,
        image = "https://example.com/image.jpg",
        estimatedCost = cost,
        estimatedTime = time,
        difficulty = "Easy",
        isFavorite = false
    )

    fun createRecipeDetail(
        id: String = "1"
    ) = RecipeDetail(
        id = id,
        name = "Nasi Telur Kecap",
        image = "https://example.com/image.jpg",
        estimatedCost = 11000,
        estimatedTime = 10,
        difficulty = "Easy",
        ingredients = listOf(
            Ingredient("Nasi putih", "200g", 5000),
            Ingredient("Telur", "1 butir", 3000),
            Ingredient("Kecap & bumbu", "-", 3000)
        ),
        instructions = listOf(
            "Goreng telur",
            "Tambahkan kecap dan bumbu",
            "Sajikan dengan nasi"
        ),
        isFavorite = false
    )
}
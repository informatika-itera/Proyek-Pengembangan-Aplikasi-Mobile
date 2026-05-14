package com.example.foodsaver.data.repository

import com.example.foodsaver.data.remote.api.GeminiService
import com.example.foodsaver.data.remote.api.SystemPrompts
import com.example.foodsaver.domain.repository.AIRepository

/**
 * Implementasi AIRepository menggunakan Google Gemini API.
 */
class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun suggestRecipes(ingredients: List<String>): Result<String> {
        val ingredientList = ingredients.joinToString(", ")
        val prompt = """
            Saya punya bahan-bahan berikut: $ingredientList.
            Tolong berikan rekomendasi resep masakan yang bisa saya buat.
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.RECIPE_SUGGESTER
        )
    }

    override suspend fun suggestStorageTips(foodItem: String): Result<String> {
        val prompt = "Bagaimana cara terbaik menyimpan $foodItem agar tetap segar dan tahan lama?"

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.EXPIRY_ADVISOR
        )
    }

    override suspend fun chat(message: String): Result<String> {
        return geminiService.generateContent(
            prompt = message,
            systemPrompt = "Kamu adalah asisten dapur FoodSaver yang ramah dan membantu."
        )
    }
}

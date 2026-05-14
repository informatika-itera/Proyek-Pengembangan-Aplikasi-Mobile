package com.example.foodsaver.domain.repository

/**
 * Interface untuk interaksi dengan layanan AI (Gemini).
 */
interface AIRepository {
    /**
     * Memberikan rekomendasi resep berdasarkan daftar bahan.
     */
    suspend fun suggestRecipes(ingredients: List<String>): Result<String>

    /**
     * Memberikan tips penyimpanan makanan agar tahan lama.
     */
    suspend fun suggestStorageTips(foodItem: String): Result<String>

    /**
     * Chat umum dengan asisten FoodSaver.
     */
    suspend fun chat(message: String): Result<String>
}

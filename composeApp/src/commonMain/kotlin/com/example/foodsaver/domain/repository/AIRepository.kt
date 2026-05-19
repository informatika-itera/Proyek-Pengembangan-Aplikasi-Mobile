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

    // AI Features for Notes
    suspend fun summarize(text: String): Result<String>
    suspend fun generateIdeas(topic: String): Result<List<String>>
    suspend fun improveWriting(text: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String>
    suspend fun translate(text: String, targetLanguage: String): Result<String>
    suspend fun suggestTitle(content: String): Result<String>
}

enum class WritingStyle(val displayName: String, val prompt: String) {
    NEUTRAL("Netral", "Perbaiki tulisan dengan gaya netral"),
    FORMAL("Formal", "Perbaiki tulisan dengan gaya formal dan profesional"),
    CASUAL("Kasual", "Perbaiki tulisan dengan gaya santai dan friendly"),
    ACADEMIC("Akademik", "Perbaiki tulisan dengan gaya akademik dan ilmiah"),
    CREATIVE("Kreatif", "Perbaiki tulisan dengan gaya kreatif dan menarik")
}

package com.example.foodsaver.data.repository

import com.example.foodsaver.data.remote.api.GeminiService
import com.example.foodsaver.data.remote.api.SystemPrompts
import com.example.foodsaver.domain.repository.AIRepository
import com.example.foodsaver.domain.repository.WritingStyle

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun generateResponse(
        prompt: String,
        systemPrompt: String,
        temperature: Double,
        maxTokens: Int
    ): Result<String> {
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = systemPrompt,
            temperature = temperature,
            maxTokens = maxTokens
        )
    }

    override suspend fun suggestRecipes(ingredients: List<String>): Result<String> {
        val ingredientList = ingredients.joinToString(", ")
        val prompt = """
            Saya punya bahan-bahan berikut: $ingredientList.
            Tolong berikan rekomendasi resep masakan yang bisa saya buat.
        """.trimIndent()

        return generateResponse(
            prompt = prompt,
            systemPrompt = SystemPrompts.RECIPE_SUGGESTER
        )
    }

    override suspend fun suggestStorageTips(foodItem: String): Result<String> {
        val prompt = "Bagaimana cara terbaik menyimpan $foodItem agar tetap segar dan tahan lama?"

        return generateResponse(
            prompt = prompt,
            systemPrompt = SystemPrompts.STORAGE_ADVISOR
        )
    }

    override suspend fun chat(message: String): Result<String> {
        return generateResponse(
            prompt = message,
            systemPrompt = SystemPrompts.BASE_FOODSAVER
        )
    }

    override suspend fun summarize(text: String): Result<String> {
        return generateResponse(
            prompt = "Rangkum teks berikut: $text",
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        return generateResponse(
            prompt = "Berikan 5 ide kreatif untuk topik: $topic",
            systemPrompt = SystemPrompts.IDEA_GENERATOR
        ).map { response ->
            response.lines()
                .filter { it.isNotBlank() }
                .map { line ->
                    line.replace(Regex("^\\d+\\.\\s*"), "").trim()
                }
                .filter { it.isNotBlank() }
        }
    }

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        val prompt = "${style.prompt}\n\nPerbaiki tulisan berikut:\n$text"
        return generateResponse(
            prompt = prompt,
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        return generateResponse(
            prompt = "Terjemahkan ke bahasa $targetLanguage:\n$text",
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        return generateResponse(
            prompt = "Berikan saran judul untuk konten berikut:\n$content",
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        ).map { it.trim().removeSurrounding("\"") }
    }
}

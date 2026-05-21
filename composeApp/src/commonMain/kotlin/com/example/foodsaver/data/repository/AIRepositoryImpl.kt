package com.example.foodsaver.data.repository

import com.example.foodsaver.data.remote.api.GeminiService
import com.example.foodsaver.data.remote.api.SystemPrompts
import com.example.foodsaver.domain.repository.AIRepository
import com.example.foodsaver.domain.repository.WritingStyle

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

    override suspend fun summarize(text: String): Result<String> {
        val prompt = """
            Rangkum teks berikut:
            
            $text
        """.trimIndent()
        
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        val prompt = """
            Berikan 5 ide kreatif untuk topik: $topic
        """.trimIndent()
        
        return geminiService.generateContent(
            prompt = prompt,
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
        val styleInstruction = when (style) {
            WritingStyle.FORMAL -> "Gunakan gaya formal dan profesional."
            WritingStyle.CASUAL -> "Gunakan gaya santai dan friendly."
            WritingStyle.ACADEMIC -> "Gunakan gaya akademik dan ilmiah."
            WritingStyle.CREATIVE -> "Gunakan gaya kreatif dan menarik."
            WritingStyle.NEUTRAL -> "Gunakan gaya netral."
        }
        
        val prompt = """
            $styleInstruction
            
            Perbaiki tulisan berikut:
            
            $text
        """.trimIndent()
        
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = """
            Terjemahkan ke bahasa $targetLanguage:
            
            $text
        """.trimIndent()
        
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        val prompt = """
            Berikan saran judul untuk konten berikut:
            
            $content
        """.trimIndent()
        
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        ).map { it.trim().removeSurrounding("\"") }
    }
}

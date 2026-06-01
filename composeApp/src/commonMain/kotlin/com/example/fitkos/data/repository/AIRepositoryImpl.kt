package com.example.fitkos.data.repository

import com.example.fitkos.data.local.datastore.UserPreferences
import com.example.fitkos.data.remote.api.GeminiService
import com.example.fitkos.data.remote.api.SystemPrompts
import com.example.fitkos.domain.model.AIResponseCache
import com.example.fitkos.domain.repository.AIRepository
import com.example.fitkos.domain.repository.WritingStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock

class AIRepositoryImpl(
    private val geminiService: GeminiService,
    private val userPreferences: UserPreferences
) : AIRepository {

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

    override suspend fun improveWriting(
        text: String,
        style: WritingStyle
    ): Result<String> {
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

    override suspend fun translate(
        text: String,
        targetLanguage: String
    ): Result<String> {
        val prompt = """
            Terjemahkan ke bahasa $targetLanguage:

            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }

    override suspend fun chat(message: String): Result<String> {
        return refreshChat(message)
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

    override fun getCachedChatResponse(): Flow<AIResponseCache> {
        return combine(
            userPreferences.cachedAIPrompt,
            userPreferences.cachedAIResponse,
            userPreferences.cachedAIUpdatedAt
        ) { prompt, response, updatedAt ->
            AIResponseCache(
                prompt = prompt,
                response = response,
                updatedAt = updatedAt
            )
        }
    }

    override suspend fun refreshChat(message: String): Result<String> {
        val result = geminiService.generateContent(
            prompt = message,
            systemPrompt = SystemPrompts.FITKOS_ASSISTANT
        )

        result.onSuccess { response ->
            userPreferences.saveAIResponseCache(
                prompt = message,
                response = response,
                updatedAt = Clock.System.now().toString()
            )
        }

        return result
    }
}
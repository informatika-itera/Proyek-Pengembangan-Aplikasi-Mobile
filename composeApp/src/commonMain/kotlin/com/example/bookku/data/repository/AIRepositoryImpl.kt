package com.example.bookku.data.repository

import com.example.bookku.data.remote.api.GeminiService
import com.example.bookku.data.remote.api.SystemPrompts
import com.example.bookku.domain.repository.AIRepository
import com.example.bookku.domain.repository.WritingStyle
import kotlinx.coroutines.flow.Flow

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {
    
    override suspend fun summarize(text: String): Result<String> {
        return geminiService.generateContent(
            prompt = text,
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }
    
    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        return geminiService.generateContent(
            prompt = topic,
            systemPrompt = SystemPrompts.IDEA_GENERATOR
        ).map { response ->
            response.lines()
                .filter { it.isNotBlank() }
                .map { line -> line.replace(Regex("^\\d+\\.\\s*"), "").trim() }
                .filter { it.isNotBlank() }
        }
    }
    
    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        val styleInstruction = when (style) {
            WritingStyle.FORMAL -> "Gunakan gaya formal."
            WritingStyle.CASUAL -> "Gunakan gaya santai."
            WritingStyle.ACADEMIC -> "Gunakan gaya akademik."
            WritingStyle.CREATIVE -> "Gunakan gaya kreatif."
            WritingStyle.NEUTRAL -> "Gunakan gaya netral."
        }
        
        return geminiService.generateContent(
            prompt = "$styleInstruction\n\n$text",
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }
    
    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        return geminiService.generateContent(
            prompt = "Translate to $targetLanguage: $text",
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }
    
    override suspend fun chat(message: String): Result<String> {
        return geminiService.generateContent(
            prompt = message,
            systemPrompt = SystemPrompts.LIBRARIAN_PERSONA
        )
    }

    override fun chatStream(message: String, systemPrompt: String?): Flow<String> {
        return geminiService.generateContentStream(
            prompt = message,
            systemPrompt = systemPrompt ?: SystemPrompts.LIBRARIAN_PERSONA
        )
    }
    
    override suspend fun suggestTitle(content: String): Result<String> {
        return geminiService.generateContent(
            prompt = content,
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        ).map { it.trim().removeSurrounding("\"") }
    }

    override suspend fun debugCheckApi(): String {
        return geminiService.debugCheckApi()
    }
}

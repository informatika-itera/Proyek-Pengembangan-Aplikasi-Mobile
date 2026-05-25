package com.example.Feelia.data.repository

import com.example.Feelia.data.remote.api.GeminiService
import com.example.Feelia.data.remote.api.SystemPrompts
import com.example.Feelia.domain.repository.AIRepository
import com.example.Feelia.domain.repository.WritingStyle

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun detectEmotion(text: String): Result<String> {
        val prompt = "Analisis emosi dari teks jurnal berikut:\n\n$text"
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.EMOTION_DETECTOR
        ).map { it.trim().uppercase() }
    }

    override suspend fun getEmotionInsight(content: String, emotion: String): Result<String> {
        val prompt = """
            Jurnal: $content
            Emosi terdeteksi: $emotion
            Berikan insight yang hangat dan supportif untuk pengguna.
        """.trimIndent()
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.EMOTION_INSIGHT
        )
    }

    override suspend fun summarize(text: String): Result<String> {
        return geminiService.generateContent(
            prompt = "Rangkum teks berikut:\n\n$text",
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        return geminiService.generateContent(
            prompt = "Berikan 5 ide kreatif untuk topik: $topic",
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
            WritingStyle.FORMAL -> "Gunakan gaya formal dan profesional."
            WritingStyle.CASUAL -> "Gunakan gaya santai dan friendly."
            WritingStyle.ACADEMIC -> "Gunakan gaya akademik dan ilmiah."
            WritingStyle.CREATIVE -> "Gunakan gaya kreatif dan menarik."
            WritingStyle.NEUTRAL -> "Gunakan gaya netral."
        }
        return geminiService.generateContent(
            prompt = "$styleInstruction\n\nPerbaiki tulisan berikut:\n\n$text",
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        return geminiService.generateContent(
            prompt = "Terjemahkan ke bahasa $targetLanguage:\n\n$text",
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }

    override suspend fun chat(message: String): Result<String> {
        return geminiService.generateContent(prompt = message)
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        return geminiService.generateContent(
            prompt = "Berikan saran judul untuk konten berikut:\n\n$content",
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        ).map { it.trim().removeSurrounding("\"") }
    }
}
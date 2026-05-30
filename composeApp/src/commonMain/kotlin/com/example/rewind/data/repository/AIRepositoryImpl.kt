package com.example.rewind.data.repository

import com.example.rewind.data.remote.api.GeminiService
import com.example.rewind.data.remote.api.SystemPrompts
import com.example.rewind.domain.repository.AIRepository
import com.example.rewind.domain.repository.WritingStyle

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun summarize(text: String): Result<String> {
        val prompt = """
            Rangkum teks berikut ini:

            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        val prompt = """
            Berikan 5 ide rekomendasi tontonan untuk topik berikut: $topic
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

            Perbaiki tulisan berikut ini:

            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = """
            Terjemahkan teks berikut ke dalam bahasa $targetLanguage:

            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }

    override suspend fun chat(message: String): Result<String> {
        return geminiService.generateContent(
            prompt = message,
            systemPrompt = SystemPrompts.CHAT
        )
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        val prompt = """
            Berikan saran judul terbaik untuk ulasan atau catatan berikut ini:

            $content
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        ).map { it.trim().removeSurrounding("\"") }
    }
}
package com.example.tabungin.data.repository

import com.example.tabungin.data.remote.api.GeminiService
import com.example.tabungin.data.remote.api.SystemPrompts
import com.example.tabungin.domain.repository.AIRepository
import com.example.tabungin.domain.repository.WritingStyle

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun summarize(text: String): Result<String> {
        val prompt = """
            Rangkum riwayat transaksi berikut menjadi poin-poin penting:

            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        val prompt = """
            Berikan 5 ide target tabungan atau tips menabung yang kreatif untuk: $topic
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
        val prompt = """
            ${style.prompt} untuk catatan tabungan berikut:

            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = """
            Terjemahkan istilah atau kalimat berikut ke bahasa $targetLanguage:

            $text
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }

    override suspend fun chat(message: String, isTabunganContext: Boolean): Result<String> {
        return if (isTabunganContext) {
            geminiService.generateContent(
                prompt = message,
                systemPrompt = SystemPrompts.TABUNGAN_ADVISOR
            )
        } else {
            geminiService.generateContent(prompt = message)
        }
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        val prompt = """
            Berikan saran nama target tabungan yang menarik berdasarkan deskripsi berikut:

            $content
        """.trimIndent()

        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        ).map { it.trim().removeSurrounding("\"") }
    }
}

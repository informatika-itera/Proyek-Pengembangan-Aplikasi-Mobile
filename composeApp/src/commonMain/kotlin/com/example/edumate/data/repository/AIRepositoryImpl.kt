package com.example.edumate.data.repository

import com.example.edumate.data.remote.api.GeminiService
import com.example.edumate.data.remote.api.SystemPrompts
import com.example.edumate.domain.repository.AIRepository
import com.example.edumate.domain.repository.WritingStyle

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun summarize(text: String): Result<String> {
        val prompt = "Rangkum teks berikut menjadi 3-5 poin penting:\n\n$text"
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.SUMMARIZER
        )
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        val prompt = "Topik tugas: $topic\n\nBerikan langkah-langkah penyelesaian yang praktis dan berurutan."
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.IDEA_GENERATOR
        ).map { response ->
            response.lineSequence()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .map { line ->
                    line.replace(Regex("""^[-*•]\s*"""), "")
                        .replace(Regex("""^\d+[.)]\s*"""), "")
                        .trim()
                }
                .take(5)
                .toList()
        }
    }

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        val prompt = "${style.prompt}\n\n$text"
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.WRITING_IMPROVER
        )
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = "Terjemahkan teks berikut ke dalam bahasa $targetLanguage:\n\n$text"
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TRANSLATOR
        )
    }

    override suspend fun chat(message: String): Result<String> {
        return geminiService.generateContent(message)
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        val prompt = "Berikan satu saran judul singkat yang menarik untuk teks berikut:\n\n$content"
        return geminiService.generateContent(
            prompt = prompt,
            systemPrompt = SystemPrompts.TITLE_SUGGESTER
        )
    }
}

package com.example.edumate.data.repository

import com.example.edumate.domain.repository.AIRepository
import com.example.edumate.domain.repository.WritingStyle
// Import GeminiService sekarang menggunakan edumate
import com.example.edumate.data.remote.api.GeminiService

class AIRepositoryImpl(
    private val geminiService: GeminiService
) : AIRepository {

    override suspend fun summarize(text: String): Result<String> {
        val prompt = "Buatkan ringkasan dari teks berikut:\n\n$text"
        return geminiService.generateContent(prompt)
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        val prompt = "Berikan 5 ide kreatif untuk topik: $topic. Berikan dalam bentuk list angka."
        return geminiService.generateContent(prompt).map { response ->
            response.lines()
                .filter { it.isNotBlank() }
                .map { it.replace(Regex("^\\d+\\.\\s*"), "").trim() }
        }
    }

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        val prompt = "${style.prompt}:\n\n$text"
        return geminiService.generateContent(prompt)
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        val prompt = "Terjemahkan teks berikut ke dalam bahasa $targetLanguage:\n\n$text"
        return geminiService.generateContent(prompt)
    }

    override suspend fun chat(message: String): Result<String> {
        return geminiService.generateContent(message)
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        val prompt = "Berikan satu saran judul singkat yang menarik untuk teks berikut:\n\n$content"
        return geminiService.generateContent(prompt)
    }
}
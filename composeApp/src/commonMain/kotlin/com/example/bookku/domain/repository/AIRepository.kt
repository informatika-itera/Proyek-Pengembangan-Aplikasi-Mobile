package com.example.bookku.domain.repository

import kotlinx.coroutines.flow.Flow

interface AIRepository {
    suspend fun summarize(text: String): Result<String>
    suspend fun generateIdeas(topic: String): Result<List<String>>
    suspend fun improveWriting(text: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String>
    suspend fun translate(text: String, targetLanguage: String): Result<String>
    suspend fun chat(message: String): Result<String>
    fun chatStream(message: String, systemPrompt: String? = null): Flow<String>
    suspend fun suggestTitle(content: String): Result<String>
    suspend fun debugCheckApi(): String
}

enum class WritingStyle(val displayName: String, val prompt: String) {
    NEUTRAL("Netral", "Perbaiki tulisan dengan gaya netral"),
    FORMAL("Formal", "Perbaiki tulisan dengan gaya formal dan profesional"),
    CASUAL("Kasual", "Perbaiki tulisan dengan gaya santai dan friendly"),
    ACADEMIC("Akademik", "Perbaiki tulisan dengan gaya akademik dan ilmiah"),
    CREATIVE("Kreatif", "Perbaiki tulisan dengan gaya kreatif dan menarik")
}



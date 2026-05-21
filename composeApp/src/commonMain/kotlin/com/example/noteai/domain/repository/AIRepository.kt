package com.example.noteai.domain.repository

import com.example.foodsaver.domain.repository.WritingStyle

interface AIRepository {
    suspend fun summarize(text: String): Result<String>
    suspend fun generateIdeas(topic: String): Result<List<String>>
    suspend fun improveWriting(text: String, style: WritingStyle = WritingStyle.NEUTRAL): Result<String>
    suspend fun translate(text: String, targetLanguage: String): Result<String>
    suspend fun chat(message: String): Result<String>
    suspend fun suggestTitle(content: String): Result<String>
}

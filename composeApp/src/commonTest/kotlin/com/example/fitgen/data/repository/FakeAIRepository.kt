package com.example.fitgen.data.repository

import com.example.fitgen.domain.repository.AIRepository
import com.example.fitgen.domain.repository.WritingStyle

class FakeAIRepository : AIRepository {
    
    var shouldReturnError = false
    var customErrorMessage = "Terjadi kesalahan"

    override suspend fun summarize(text: String): Result<String> {
        return if (shouldReturnError) Result.failure(Exception(customErrorMessage))
        else Result.success("Summary of: $text")
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        return if (shouldReturnError) Result.failure(Exception(customErrorMessage))
        else Result.success(listOf("Idea 1", "Idea 2", "Idea 3"))
    }

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        return if (shouldReturnError) Result.failure(Exception(customErrorMessage))
        else Result.success("Improved writing: $text in ${style.name}")
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> {
        return if (shouldReturnError) Result.failure(Exception(customErrorMessage))
        else Result.success("Translated to $targetLanguage: $text")
    }

    override suspend fun chat(message: String): Result<String> {
        return if (shouldReturnError) Result.failure(Exception(customErrorMessage))
        else Result.success("Response to: $message")
    }

    override suspend fun suggestTitle(content: String): Result<String> {
        return if (shouldReturnError) Result.failure(Exception(customErrorMessage))
        else Result.success("Suggested Title")
    }

    override suspend fun analyzeImage(prompt: String, imageBytes: ByteArray): String {
        if (shouldReturnError) throw Exception(customErrorMessage)
        return "Image analyzed"
    }
}

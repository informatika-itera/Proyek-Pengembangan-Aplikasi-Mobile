package com.example.rewind.data.repository

import com.example.rewind.domain.repository.AIRepository
import com.example.rewind.domain.repository.WritingStyle

class FakeAIRepository : AIRepository {

    var shouldSucceed = true
    var fakeResult = "Fake AI response"
    var fakeIdeas = listOf("Idea 1", "Idea 2", "Idea 3")
    var errorMessage = "AI service unavailable"

    override suspend fun summarize(text: String): Result<String> =
        if (shouldSucceed) Result.success(fakeResult)
        else Result.failure(Exception(errorMessage))

    override suspend fun generateIdeas(topic: String): Result<List<String>> =
        if (shouldSucceed) Result.success(fakeIdeas)
        else Result.failure(Exception(errorMessage))

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> =
        if (shouldSucceed) Result.success(fakeResult)
        else Result.failure(Exception(errorMessage))

    override suspend fun translate(text: String, targetLanguage: String): Result<String> =
        if (shouldSucceed) Result.success(fakeResult)
        else Result.failure(Exception(errorMessage))

    override suspend fun chat(message: String): Result<String> =
        if (shouldSucceed) Result.success(fakeResult)
        else Result.failure(Exception(errorMessage))

    override suspend fun suggestTitle(content: String): Result<String> =
        if (shouldSucceed) Result.success(fakeResult)
        else Result.failure(Exception(errorMessage))
}
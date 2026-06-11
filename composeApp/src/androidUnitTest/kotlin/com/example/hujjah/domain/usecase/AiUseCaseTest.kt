package com.example.hujjah.domain.usecase

import com.example.hujjah.domain.repository.AIRepository
import com.example.hujjah.domain.repository.WritingStyle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeAIRepository : AIRepository {
    var shouldReturnError = false
    var summarizeResult = "This is a summary"
    var generateIdeasResult = listOf("Idea 1", "Idea 2")
    var improveWritingResult = "Improved text"

    override suspend fun summarize(text: String): Result<String> {
        return if (shouldReturnError) Result.failure(Exception("AI Error")) else Result.success(summarizeResult)
    }

    override suspend fun generateIdeas(topic: String): Result<List<String>> {
        return if (shouldReturnError) Result.failure(Exception("AI Error")) else Result.success(generateIdeasResult)
    }

    override suspend fun improveWriting(text: String, style: WritingStyle): Result<String> {
        return if (shouldReturnError) Result.failure(Exception("AI Error")) else Result.success(improveWritingResult)
    }

    override suspend fun translate(text: String, targetLanguage: String): Result<String> = Result.success("")
    override suspend fun chat(message: String, systemPrompt: String?): Result<String> = Result.success("")
    override suspend fun suggestTitle(content: String): Result<String> = Result.success("")
}

class AiUseCaseTest {

    private val fakeAiRepository = FakeAIRepository()

    @Test
    fun testSummarizeNoteUseCase_success() = runTest {
        val useCase = SummarizeNoteUseCase(fakeAiRepository)
        // input >= 50 chars
        val input = "A".repeat(50)
        val result = useCase(input)
        assertTrue(result.isSuccess)
        assertEquals("This is a summary", result.getOrNull())
    }

    @Test
    fun testSummarizeNoteUseCase_tooShort() = runTest {
        val useCase = SummarizeNoteUseCase(fakeAiRepository)
        // input < 50 chars
        val input = "Too short content"
        val result = useCase(input)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun testSummarizeNoteUseCase_repositoryError() = runTest {
        val useCase = SummarizeNoteUseCase(fakeAiRepository)
        fakeAiRepository.shouldReturnError = true
        val input = "A".repeat(50)
        val result = useCase(input)
        assertTrue(result.isFailure)
        assertEquals("AI Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun testImproveWritingUseCase_success() = runTest {
        val useCase = ImproveWritingUseCase(fakeAiRepository)
        val result = useCase("Fix this text", WritingStyle.CASUAL)
        assertTrue(result.isSuccess)
        assertEquals("Improved text", result.getOrNull())
    }

    @Test
    fun testImproveWritingUseCase_blankContent() = runTest {
        val useCase = ImproveWritingUseCase(fakeAiRepository)
        val result = useCase("")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun testImproveWritingUseCase_repositoryError() = runTest {
        val useCase = ImproveWritingUseCase(fakeAiRepository)
        fakeAiRepository.shouldReturnError = true
        val result = useCase("Good text")
        assertTrue(result.isFailure)
        assertEquals("AI Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun testGenerateIdeasUseCase_success() = runTest {
        val useCase = GenerateIdeasUseCase(fakeAiRepository)
        val result = useCase("Sabar")
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Idea 1", result.getOrNull()?.first())
    }

    @Test
    fun testGenerateIdeasUseCase_blankTopic() = runTest {
        val useCase = GenerateIdeasUseCase(fakeAiRepository)
        val result = useCase("")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun testGenerateIdeasUseCase_repositoryError() = runTest {
        val useCase = GenerateIdeasUseCase(fakeAiRepository)
        fakeAiRepository.shouldReturnError = true
        val result = useCase("Tawakkal")
        assertTrue(result.isFailure)
        assertEquals("AI Error", result.exceptionOrNull()?.message)
    }
}

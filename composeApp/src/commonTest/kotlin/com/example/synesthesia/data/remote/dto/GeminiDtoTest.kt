package com.example.synesthesia.data.remote.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for GeminiDto extension functions and data classes.
 * Targets 100% branch & line coverage for:
 * - GeminiResponse.getTextContent()
 * - GeminiResponse.isBlocked()
 * - GeminiResponse.getErrorMessage()
 * - EmotionAnalysisResponse data class
 * - All DTO data classes
 */
class GeminiDtoTest {

    // ==================== getTextContent() ====================

    @Test
    fun `getTextContent should return text from first candidate`() {
        val response = GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(
                        parts = listOf(GeminiPart(text = "Hello AI"))
                    )
                )
            )
        )
        assertEquals("Hello AI", response.getTextContent())
    }

    @Test
    fun `getTextContent should return null when candidates is null`() {
        val response = GeminiResponse(candidates = null)
        assertNull(response.getTextContent())
    }

    @Test
    fun `getTextContent should return null when candidates is empty`() {
        val response = GeminiResponse(candidates = emptyList())
        assertNull(response.getTextContent())
    }

    @Test
    fun `getTextContent should return null when parts is empty`() {
        val response = GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(parts = emptyList())
                )
            )
        )
        assertNull(response.getTextContent())
    }

    @Test
    fun `getTextContent should return first part text when multiple parts exist`() {
        val response = GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(
                        parts = listOf(
                            GeminiPart(text = "First"),
                            GeminiPart(text = "Second")
                        )
                    )
                )
            )
        )
        assertEquals("First", response.getTextContent())
    }

    // ==================== isBlocked() ====================

    @Test
    fun `isBlocked should return false when promptFeedback is null`() {
        val response = GeminiResponse(promptFeedback = null)
        assertFalse(response.isBlocked())
    }

    @Test
    fun `isBlocked should return false when blockReason is null`() {
        val response = GeminiResponse(
            promptFeedback = PromptFeedback(blockReason = null)
        )
        assertFalse(response.isBlocked())
    }

    @Test
    fun `isBlocked should return true when blockReason is set`() {
        val response = GeminiResponse(
            promptFeedback = PromptFeedback(blockReason = "SAFETY")
        )
        assertTrue(response.isBlocked())
    }

    // ==================== getErrorMessage() ====================

    @Test
    fun `getErrorMessage should return error message when error is present`() {
        val response = GeminiResponse(
            error = GeminiError(code = 400, message = "Bad request", status = "INVALID_ARGUMENT")
        )
        assertEquals("Bad request", response.getErrorMessage())
    }

    @Test
    fun `getErrorMessage should return blocked message when blocked and no error`() {
        val response = GeminiResponse(
            promptFeedback = PromptFeedback(blockReason = "SAFETY")
        )
        val errorMsg = response.getErrorMessage()
        assertNotNull(errorMsg)
        assertTrue(errorMsg.contains("SAFETY"))
        assertTrue(errorMsg.contains("Konten diblokir"))
    }

    @Test
    fun `getErrorMessage should return null when no error and not blocked`() {
        val response = GeminiResponse()
        assertNull(response.getErrorMessage())
    }

    @Test
    fun `getErrorMessage should prioritize error message over block reason`() {
        val response = GeminiResponse(
            error = GeminiError(code = 500, message = "Server error", status = "INTERNAL"),
            promptFeedback = PromptFeedback(blockReason = "SAFETY")
        )
        assertEquals("Server error", response.getErrorMessage())
    }

    @Test
    fun `getErrorMessage should return null when feedback has no block reason`() {
        val response = GeminiResponse(
            promptFeedback = PromptFeedback(safetyRatings = listOf())
        )
        assertNull(response.getErrorMessage())
    }

    // ==================== Data Class Tests ====================

    @Test
    fun `GeminiRequest should hold contents and config`() {
        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart("Hello")))),
            generationConfig = GenerationConfig(temperature = 0.5)
        )
        assertEquals(1, request.contents.size)
        assertEquals(0.5, request.generationConfig?.temperature)
    }

    @Test
    fun `GeminiRequest default config should be null`() {
        val request = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart("test"))))
        )
        assertNull(request.generationConfig)
        assertNull(request.safetySettings)
    }

    @Test
    fun `GeminiContent default role should be user`() {
        val content = GeminiContent(parts = listOf(GeminiPart("test")))
        assertEquals("user", content.role)
    }

    @Test
    fun `GeminiContent custom role should be preserved`() {
        val content = GeminiContent(parts = listOf(GeminiPart("test")), role = "model")
        assertEquals("model", content.role)
    }

    @Test
    fun `GenerationConfig should have correct defaults`() {
        val config = GenerationConfig()
        assertEquals(0.7, config.temperature)
        assertEquals(1000, config.maxOutputTokens)
        assertEquals(0.95, config.topP)
        assertEquals(40, config.topK)
        assertNull(config.responseMimeType)
    }

    @Test
    fun `GenerationConfig custom values should be preserved`() {
        val config = GenerationConfig(
            temperature = 0.2,
            maxOutputTokens = 500,
            topP = 0.8,
            topK = 20,
            responseMimeType = "application/json"
        )
        assertEquals(0.2, config.temperature)
        assertEquals(500, config.maxOutputTokens)
        assertEquals("application/json", config.responseMimeType)
    }

    @Test
    fun `SafetySetting should hold category and threshold`() {
        val setting = SafetySetting(category = "HARM_CATEGORY_HARASSMENT", threshold = "BLOCK_MEDIUM_AND_ABOVE")
        assertEquals("HARM_CATEGORY_HARASSMENT", setting.category)
        assertEquals("BLOCK_MEDIUM_AND_ABOVE", setting.threshold)
    }

    @Test
    fun `GeminiCandidate should have correct defaults`() {
        val candidate = GeminiCandidate(
            content = GeminiContent(parts = listOf(GeminiPart("text")))
        )
        assertNull(candidate.finishReason)
        assertEquals(0, candidate.index)
        assertNull(candidate.safetyRatings)
    }

    @Test
    fun `GeminiCandidate with all fields populated`() {
        val candidate = GeminiCandidate(
            content = GeminiContent(parts = listOf(GeminiPart("text"))),
            finishReason = "STOP",
            index = 1,
            safetyRatings = listOf(SafetyRating("HARM", "LOW"))
        )
        assertEquals("STOP", candidate.finishReason)
        assertEquals(1, candidate.index)
        assertEquals(1, candidate.safetyRatings?.size)
    }

    @Test
    fun `SafetyRating should hold category and probability`() {
        val rating = SafetyRating(category = "HARM_CATEGORY_VIOLENCE", probability = "NEGLIGIBLE")
        assertEquals("HARM_CATEGORY_VIOLENCE", rating.category)
        assertEquals("NEGLIGIBLE", rating.probability)
    }

    @Test
    fun `PromptFeedback with safety ratings`() {
        val feedback = PromptFeedback(
            safetyRatings = listOf(
                SafetyRating("CAT1", "LOW"),
                SafetyRating("CAT2", "MEDIUM")
            ),
            blockReason = null
        )
        assertEquals(2, feedback.safetyRatings?.size)
        assertNull(feedback.blockReason)
    }

    @Test
    fun `GeminiError should hold all fields`() {
        val error = GeminiError(code = 429, message = "Rate limited", status = "RESOURCE_EXHAUSTED")
        assertEquals(429, error.code)
        assertEquals("Rate limited", error.message)
        assertEquals("RESOURCE_EXHAUSTED", error.status)
    }

    // ==================== EmotionAnalysisResponse ====================

    @Test
    fun `EmotionAnalysisResponse should hold all required fields`() {
        val response = EmotionAnalysisResponse(
            autoTitle = "Title",
            paraphrasedContent = "Content",
            emotionQuadrant = "HEP",
            subEmotion = "Lively",
            artColorHex = "#FFC107"
        )
        assertEquals("Title", response.autoTitle)
        assertEquals("Content", response.paraphrasedContent)
        assertEquals("HEP", response.emotionQuadrant)
        assertEquals("Lively", response.subEmotion)
        assertEquals("#FFC107", response.artColorHex)
        assertNull(response.sentiment)
        assertNull(response.emotionScore)
        assertNull(response.summary)
    }

    @Test
    fun `EmotionAnalysisResponse with all optional fields`() {
        val response = EmotionAnalysisResponse(
            autoTitle = "Full Title",
            paraphrasedContent = "Full Content",
            emotionQuadrant = "LEU",
            subEmotion = "Gloomy",
            artColorHex = "#3F51B5",
            sentiment = "negative",
            emotionScore = 30,
            summary = "A somber entry."
        )
        assertEquals("negative", response.sentiment)
        assertEquals(30, response.emotionScore)
        assertEquals("A somber entry.", response.summary)
    }
}

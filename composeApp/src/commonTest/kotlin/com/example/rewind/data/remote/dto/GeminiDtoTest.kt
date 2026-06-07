package com.example.rewind.data.remote.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeminiDtoTest {

    // ==================== getTextContent ====================

    @Test
    fun `getTextContent should return text from valid candidates`() {
        val response = GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(
                        parts = listOf(GeminiPart(text = "Hello World")),
                        role = "model"
                    )
                )
            )
        )
        assertEquals("Hello World", response.getTextContent())
    }

    @Test
    fun `getTextContent should return null when candidates list is empty`() {
        val response = GeminiResponse(candidates = emptyList())
        assertNull(response.getTextContent())
    }

    @Test
    fun `getTextContent should return null when candidates is null`() {
        val response = GeminiResponse(candidates = null)
        assertNull(response.getTextContent())
    }

    @Test
    fun `getTextContent should return null when candidate has null content`() {
        val response = GeminiResponse(
            candidates = listOf(GeminiCandidate(content = null))
        )
        assertNull(response.getTextContent())
    }

    // ==================== isBlocked ====================

    @Test
    fun `isBlocked should return true when blockReason is present`() {
        val response = GeminiResponse(
            promptFeedback = PromptFeedback(blockReason = "SAFETY")
        )
        assertTrue(response.isBlocked())
    }

    @Test
    fun `isBlocked should return false when blockReason is null`() {
        val response = GeminiResponse(
            promptFeedback = PromptFeedback(blockReason = null)
        )
        assertFalse(response.isBlocked())
    }

    @Test
    fun `isBlocked should return false when promptFeedback is null`() {
        val response = GeminiResponse(promptFeedback = null)
        assertFalse(response.isBlocked())
    }

    // ==================== getErrorMessage ====================

    @Test
    fun `getErrorMessage should return error message when error is present`() {
        val response = GeminiResponse(
            error = GeminiError(code = 400, message = "Bad Request", status = "INVALID_ARGUMENT")
        )
        assertEquals("Bad Request", response.getErrorMessage())
    }

    @Test
    fun `getErrorMessage should return block message when blocked and no error`() {
        val response = GeminiResponse(
            promptFeedback = PromptFeedback(blockReason = "SAFETY")
        )
        assertEquals("Konten diblokir: SAFETY", response.getErrorMessage())
    }

    @Test
    fun `getErrorMessage should return null when neither error nor blocked`() {
        val response = GeminiResponse(
            candidates = listOf(
                GeminiCandidate(
                    content = GeminiContent(
                        parts = listOf(GeminiPart(text = "OK")),
                        role = "model"
                    )
                )
            )
        )
        assertNull(response.getErrorMessage())
    }
}

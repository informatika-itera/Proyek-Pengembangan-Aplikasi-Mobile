package com.example.todomaster.data.remote.dto

import kotlin.test.Test
import kotlin.test.assertEquals

class DtoTest {
    @Test
    fun `test gemini response helper`() {
        val response = GeminiResponse(error = GeminiError(1, "Test Error", "ERROR"))
        assertEquals("Test Error", response.getErrorMessage())
    }
}
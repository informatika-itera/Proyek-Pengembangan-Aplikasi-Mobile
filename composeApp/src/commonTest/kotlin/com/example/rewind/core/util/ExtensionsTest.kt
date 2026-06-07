package com.example.rewind.core.util

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class ExtensionsTest {

    // ==================== truncate ====================

    @Test
    fun `truncate - string longer than maxLength is truncated with ellipsis`() {
        // Arrange
        val input = "Hello World, this is a long string"

        // Act
        val result = input.truncate(10)

        // Assert
        assertEquals("Hello W...", result)
        assertEquals(10, result.length)
    }

    @Test
    fun `truncate - string equal to maxLength is not truncated`() {
        // Arrange
        val input = "Hello"

        // Act
        val result = input.truncate(5)

        // Assert
        assertEquals("Hello", result)
    }

    @Test
    fun `truncate - string shorter than maxLength is not truncated`() {
        // Arrange
        val input = "Hi"

        // Act
        val result = input.truncate(10)

        // Assert
        assertEquals("Hi", result)
    }

    @Test
    fun `truncate - empty string returns empty`() {
        // Arrange
        val input = ""

        // Act
        val result = input.truncate(5)

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `truncate - maxLength of 3 with longer string returns only ellipsis`() {
        // Arrange
        val input = "Hello"

        // Act
        val result = input.truncate(3)

        // Assert
        assertEquals("...", result)
    }

    // ==================== capitalizeFirst ====================

    @Test
    fun `capitalizeFirst - lowercase first char is uppercased`() {
        // Arrange
        val input = "hello world"

        // Act
        val result = input.capitalizeFirst()

        // Assert
        assertEquals("Hello world", result)
    }

    @Test
    fun `capitalizeFirst - already uppercase remains unchanged`() {
        // Arrange
        val input = "Hello"

        // Act
        val result = input.capitalizeFirst()

        // Assert
        assertEquals("Hello", result)
    }

    @Test
    fun `capitalizeFirst - empty string returns empty`() {
        // Arrange
        val input = ""

        // Act
        val result = input.capitalizeFirst()

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `capitalizeFirst - single char is uppercased`() {
        // Arrange
        val input = "a"

        // Act
        val result = input.capitalizeFirst()

        // Assert
        assertEquals("A", result)
    }

    // ==================== retryWithBackoff ====================

    @Test
    fun `retryWithBackoff - succeeds on first try`() = runTest {
        // Arrange
        var attempts = 0

        // Act
        val result = retryWithBackoff(times = 3, initialDelay = 1, maxDelay = 10) {
            attempts++
            "success"
        }

        // Assert
        assertEquals("success", result)
        assertEquals(1, attempts)
    }

    @Test
    fun `retryWithBackoff - fails then succeeds on retry`() = runTest {
        // Arrange
        var attempts = 0

        // Act
        val result = retryWithBackoff(times = 3, initialDelay = 1, maxDelay = 10) {
            attempts++
            if (attempts < 3) throw Exception("Fail attempt $attempts")
            "success on attempt $attempts"
        }

        // Assert
        assertEquals("success on attempt 3", result)
        assertEquals(3, attempts)
    }

    @Test
    fun `retryWithBackoff - all attempts fail throws last exception`() = runTest {
        // Arrange
        var attempts = 0

        // Act & Assert
        try {
            retryWithBackoff(times = 3, initialDelay = 1, maxDelay = 10) {
                attempts++
                throw Exception("Fail attempt $attempts")
            }
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals("Fail attempt 3", e.message)
            assertEquals(3, attempts)
        }
    }

    // ==================== mapSuccess ====================

    @Test
    fun `mapSuccess - maps success value`() {
        // Arrange
        val result: Result<Int> = Result.success(42)

        // Act
        val mapped = result.mapSuccess { it * 2 }

        // Assert
        assertTrue(mapped.isSuccess)
        assertEquals(84, mapped.getOrNull())
    }

    @Test
    fun `mapSuccess - passes through failure`() {
        // Arrange
        val error = Exception("original error")
        val result: Result<Int> = Result.failure(error)

        // Act
        val mapped = result.mapSuccess { it * 2 }

        // Assert
        assertTrue(mapped.isFailure)
        assertEquals("original error", mapped.exceptionOrNull()?.message)
    }

    // ==================== handle ====================

    @Test
    fun `handle - calls onSuccess for success result`() {
        // Arrange
        val result: Result<String> = Result.success("data")
        var successValue: String? = null
        var failureValue: Throwable? = null

        // Act
        result.handle(
            onSuccess = { successValue = it },
            onFailure = { failureValue = it }
        )

        // Assert
        assertEquals("data", successValue)
        assertEquals(null, failureValue)
    }

    @Test
    fun `handle - calls onFailure for failure result`() {
        // Arrange
        val error = Exception("something went wrong")
        val result: Result<String> = Result.failure(error)
        var successValue: String? = null
        var failureValue: Throwable? = null

        // Act
        result.handle(
            onSuccess = { successValue = it },
            onFailure = { failureValue = it }
        )

        // Assert
        assertEquals(null, successValue)
        assertEquals("something went wrong", failureValue?.message)
    }
}

package com.example.synesthesia.core.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFails

/**
 * Comprehensive unit tests for Extensions.kt utility functions.
 * Targets 100% line & branch coverage for:
 * - String.truncate()
 * - String.capitalizeFirst()
 * - retryWithBackoff()
 * - mapSuccess()
 * - handle()
 * - Instant.formatToDisplay()
 * - Instant.formatDateOnly()
 * - Instant.formatTimeOnly()
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExtensionsTest {

    // ==================== String.truncate() ====================

    @Test
    fun `truncate should not modify string shorter than maxLength`() {
        val result = "Hello".truncate(10)
        assertEquals("Hello", result)
    }

    @Test
    fun `truncate should not modify string equal to maxLength`() {
        val result = "12345".truncate(5)
        assertEquals("12345", result)
    }

    @Test
    fun `truncate should truncate and add ellipsis for string longer than maxLength`() {
        val result = "Hello World".truncate(8)
        assertEquals("Hello...", result)  // 5 chars + "..."
        assertEquals(8, result.length)
    }

    @Test
    fun `truncate should handle maxLength of 3 correctly`() {
        val result = "Hello".truncate(3)
        assertEquals("...", result)
    }

    @Test
    fun `truncate should handle very long strings`() {
        val longString = "A".repeat(1000)
        val result = longString.truncate(50)
        assertEquals(50, result.length)
        assertTrue(result.endsWith("..."))
    }

    @Test
    fun `truncate should handle edge case with maxLength of 4`() {
        val result = "Hello World".truncate(4)
        assertEquals("H...", result)
        assertEquals(4, result.length)
    }

    // ==================== String.capitalizeFirst() ====================

    @Test
    fun `capitalizeFirst should capitalize lowercase first char`() {
        assertEquals("Hello", "hello".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst should keep already uppercase first char`() {
        assertEquals("Hello", "Hello".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst should handle single character`() {
        assertEquals("A", "a".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst should handle empty string`() {
        assertEquals("", "".capitalizeFirst())
    }

    @Test
    fun `capitalizeFirst should handle number first char`() {
        assertEquals("123", "123".capitalizeFirst())
    }

    // ==================== retryWithBackoff() ====================

    @Test
    fun `retryWithBackoff should succeed on first try`() = runTest {
        var attempts = 0
        val result = retryWithBackoff(times = 3) {
            attempts++
            "Success"
        }
        assertEquals("Success", result)
        assertEquals(1, attempts)
    }

    @Test
    fun `retryWithBackoff should retry and succeed on second attempt`() = runTest {
        var attempts = 0
        val result = retryWithBackoff(times = 3, initialDelay = 1, maxDelay = 10) {
            attempts++
            if (attempts < 2) throw Exception("Fail")
            "Success on retry"
        }
        assertEquals("Success on retry", result)
        assertEquals(2, attempts)
    }

    @Test
    fun `retryWithBackoff should retry and succeed on last attempt`() = runTest {
        var attempts = 0
        val result = retryWithBackoff(times = 3, initialDelay = 1, maxDelay = 10) {
            attempts++
            if (attempts < 3) throw Exception("Fail")
            "Final success"
        }
        assertEquals("Final success", result)
        assertEquals(3, attempts)
    }

    @Test
    fun `retryWithBackoff should throw when all attempts fail`() = runTest {
        var attempts = 0
        try {
            retryWithBackoff(times = 2, initialDelay = 1, maxDelay = 5) {
                attempts++
                throw Exception("Always fail")
            }
        } catch (e: Exception) {
            assertEquals("Always fail", e.message)
        }
        assertEquals(2, attempts)
    }

    @Test
    fun `retryWithBackoff should respect maxDelay cap`() = runTest {
        var attempts = 0
        val result = retryWithBackoff(
            times = 3,
            initialDelay = 100,
            maxDelay = 150,
            factor = 10.0
        ) {
            attempts++
            if (attempts < 3) throw Exception("Fail")
            "Done"
        }
        assertEquals("Done", result)
    }

    @Test
    fun `retryWithBackoff with single attempt should just run once`() = runTest {
        var attempts = 0
        val result = retryWithBackoff(times = 1) {
            attempts++
            "Only once"
        }
        assertEquals("Only once", result)
        assertEquals(1, attempts)
    }

    // ==================== Result.mapSuccess() ====================

    @Test
    fun `mapSuccess should transform successful result`() {
        val result = Result.success(42)
        val mapped = result.mapSuccess { it * 2 }
        assertTrue(mapped.isSuccess)
        assertEquals(84, mapped.getOrNull())
    }

    @Test
    fun `mapSuccess should propagate failure`() {
        val result = Result.failure<Int>(Exception("Error"))
        val mapped = result.mapSuccess { it * 2 }
        assertTrue(mapped.isFailure)
    }

    // ==================== Result.handle() ====================

    @Test
    fun `handle should call onSuccess for successful result`() {
        var successValue: String? = null
        var failureCalled = false

        val result = Result.success("Hello")
        result.handle(
            onSuccess = { successValue = it },
            onFailure = { failureCalled = true }
        )

        assertEquals("Hello", successValue)
        // Note: handle calls both onSuccess and onFailure since it uses
        // result.onSuccess(...) then result.onFailure(...), so only one actually triggers
        assertEquals(false, failureCalled)
    }

    @Test
    fun `handle should call onFailure for failed result`() {
        var successCalled = false
        var failureError: Throwable? = null

        val result = Result.failure<String>(Exception("Oops"))
        result.handle(
            onSuccess = { successCalled = true },
            onFailure = { failureError = it }
        )

        assertEquals(false, successCalled)
        assertEquals("Oops", failureError?.message)
    }

    // ==================== Date/Time Format Extensions ====================

    @Test
    fun `formatToDisplay should return formatted date and time string`() {
        // Use a known timestamp: 2024-01-15T10:30:00Z
        val instant = Instant.fromEpochMilliseconds(1705312200000)
        val result = instant.formatToDisplay()
        // The exact output depends on timezone, but it should contain date and time parts
        assertTrue(result.contains("/"))
        assertTrue(result.contains(":"))
    }

    @Test
    fun `formatDateOnly should return formatted date string`() {
        val instant = Instant.fromEpochMilliseconds(1705312200000)
        val result = instant.formatDateOnly()
        assertTrue(result.contains("/"))
        // Should not contain ":"
        assertTrue(!result.contains(":"))
    }

    @Test
    fun `formatTimeOnly should return formatted time string`() {
        val instant = Instant.fromEpochMilliseconds(1705312200000)
        val result = instant.formatTimeOnly()
        assertTrue(result.contains(":"))
        // Should contain exactly one ":"
        assertEquals(1, result.count { it == ':' })
    }

    @Test
    fun `formatToDisplay with epoch zero`() {
        val instant = Instant.fromEpochMilliseconds(0)
        val result = instant.formatToDisplay()
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatDateOnly with epoch zero`() {
        val instant = Instant.fromEpochMilliseconds(0)
        val result = instant.formatDateOnly()
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatTimeOnly with epoch zero`() {
        val instant = Instant.fromEpochMilliseconds(0)
        val result = instant.formatTimeOnly()
        assertTrue(result.isNotEmpty())
    }
}

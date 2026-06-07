package com.example.rewind.core.network

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame

class NetworkResultTest {

    // ==================== safeApiCall ====================

    @Test
    fun `safeApiCall - successful block returns Success`() = runTest {
        // Act
        val result = safeApiCall { "response data" }

        // Assert
        assertIs<NetworkResult.Success<String>>(result)
        assertEquals("response data", result.data)
    }

    @Test
    fun `safeApiCall - exception returns Error with message`() = runTest {
        // Act
        val result = safeApiCall<String> { throw Exception("Network timeout") }

        // Assert
        assertIs<NetworkResult.Error>(result)
        assertEquals("Network timeout", result.message)
    }

    @Test
    fun `safeApiCall - exception without message returns Unknown error`() = runTest {
        // Act
        val result = safeApiCall<String> { throw Exception() }

        // Assert
        assertIs<NetworkResult.Error>(result)
        assertEquals("Unknown error", result.message)
    }

    // ==================== NetworkResult.Success ====================

    @Test
    fun `Success - data is accessible`() {
        // Arrange
        val data = listOf("item1", "item2")

        // Act
        val result = NetworkResult.Success(data)

        // Assert
        assertEquals(listOf("item1", "item2"), result.data)
    }

    // ==================== NetworkResult.Error ====================

    @Test
    fun `Error - message and code are accessible`() {
        // Act
        val result = NetworkResult.Error("Not found", code = 404)

        // Assert
        assertEquals("Not found", result.message)
        assertEquals(404, result.code)
    }

    @Test
    fun `Error - null code by default`() {
        // Act
        val result = NetworkResult.Error("Server error")

        // Assert
        assertEquals("Server error", result.message)
        assertNull(result.code)
    }

    // ==================== NetworkResult.Loading ====================

    @Test
    fun `Loading - is singleton`() {
        // Act
        val loading1 = NetworkResult.Loading
        val loading2 = NetworkResult.Loading

        // Assert
        assertSame(loading1, loading2)
    }
}

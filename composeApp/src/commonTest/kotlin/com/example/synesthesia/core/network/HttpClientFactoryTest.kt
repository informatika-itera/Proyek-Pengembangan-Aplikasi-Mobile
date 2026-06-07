package com.example.synesthesia.core.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for HttpClientFactory's NetworkResult and safeApiCall.
 * Targets 100% branch & line coverage for:
 * - NetworkResult sealed class (Success, Error, Loading)
 * - safeApiCall (success and exception paths)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HttpClientFactoryTest {

    // ==================== NetworkResult Tests ====================

    @Test
    fun `NetworkResult Success should hold data`() {
        val result: NetworkResult<String> = NetworkResult.Success("Data")
        assertTrue(result is NetworkResult.Success)
        assertEquals("Data", (result as NetworkResult.Success).data)
    }

    @Test
    fun `NetworkResult Success with complex type`() {
        val result: NetworkResult<List<Int>> = NetworkResult.Success(listOf(1, 2, 3))
        assertTrue(result is NetworkResult.Success)
        assertEquals(3, (result as NetworkResult.Success).data.size)
    }

    @Test
    fun `NetworkResult Error should hold message`() {
        val result: NetworkResult<String> = NetworkResult.Error("Something went wrong")
        assertTrue(result is NetworkResult.Error)
        assertEquals("Something went wrong", (result as NetworkResult.Error).message)
    }

    @Test
    fun `NetworkResult Error should hold optional code`() {
        val result: NetworkResult<String> = NetworkResult.Error("Not Found", code = 404)
        assertTrue(result is NetworkResult.Error)
        assertEquals(404, (result as NetworkResult.Error).code)
    }

    @Test
    fun `NetworkResult Error default code should be null`() {
        val result: NetworkResult<String> = NetworkResult.Error("Error")
        assertNull((result as NetworkResult.Error).code)
    }

    @Test
    fun `NetworkResult Loading should be singleton`() {
        val result: NetworkResult<String> = NetworkResult.Loading
        assertTrue(result is NetworkResult.Loading)
    }

    // ==================== safeApiCall Tests ====================

    @Test
    fun `safeApiCall should return Success when block succeeds`() = runTest {
        val result = safeApiCall { "API response" }
        assertTrue(result is NetworkResult.Success)
        assertEquals("API response", (result as NetworkResult.Success).data)
    }

    @Test
    fun `safeApiCall should return Error when block throws exception`() = runTest {
        val result = safeApiCall<String> { throw Exception("Network error") }
        assertTrue(result is NetworkResult.Error)
        assertEquals("Network error", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should return Unknown error for null message`() = runTest {
        val result = safeApiCall<String> { throw Exception() }
        assertTrue(result is NetworkResult.Error)
        assertEquals("Unknown error", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should handle RuntimeException`() = runTest {
        val result = safeApiCall<Int> { throw RuntimeException("Runtime!") }
        assertTrue(result is NetworkResult.Error)
        assertEquals("Runtime!", (result as NetworkResult.Error).message)
    }

    @Test
    fun `safeApiCall should return complex types on success`() = runTest {
        val result = safeApiCall { listOf(1, 2, 3) }
        assertTrue(result is NetworkResult.Success)
        assertEquals(3, (result as NetworkResult.Success).data.size)
    }

    // ==================== Exhaustive matching test ====================

    @Test
    fun `NetworkResult exhaustive when matching`() {
        val results = listOf<NetworkResult<String>>(
            NetworkResult.Success("ok"),
            NetworkResult.Error("err"),
            NetworkResult.Loading
        )

        results.forEach { result ->
            val handled = when (result) {
                is NetworkResult.Success -> "success"
                is NetworkResult.Error -> "error"
                is NetworkResult.Loading -> "loading"
            }
            assertTrue(handled.isNotEmpty())
        }
    }
}

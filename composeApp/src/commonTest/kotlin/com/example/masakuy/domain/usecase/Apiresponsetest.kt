package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.ApiResponse
import com.example.masakuy.core.network.Result
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertIs

class ApiResponseTest {

    @Test
    fun `ApiResponse menyimpan success message dan data dengan benar`() {
        val response = ApiResponse(success = true, message = "OK", data = "hello")
        assertTrue(response.success)
        assertEquals("OK", response.message)
        assertEquals("hello", response.data)
    }

    @Test
    fun `ApiResponse default data null`() {
        val response = ApiResponse<String>(success = false, message = "Error")
        assertEquals(null, response.data)
    }

    @Test
    fun `Result Success menyimpan data dengan benar`() {
        val result = Result.Success("data")
        assertIs<Result.Success<String>>(result)
        assertEquals("data", result.data)
    }

    @Test
    fun `Result Error menyimpan exception dengan benar`() {
        val exception = Exception("error message")
        val result = Result.Error(exception)
        assertIs<Result.Error>(result)
        assertEquals("error message", result.exception.message)
    }

    @Test
    fun `Result Loading adalah singleton object`() {
        val result: Result<String> = Result.Loading
        assertIs<Result.Loading>(result)
    }
}
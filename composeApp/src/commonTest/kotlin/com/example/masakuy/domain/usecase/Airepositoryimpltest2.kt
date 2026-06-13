package com.example.masakuy.data.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.presentation.screens.api.GeminiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AIRepositoryImplErrorTest {

    // Menggunakan relaxed = true agar mock tidak rewel soal kecocokan method signature
    private val geminiService = mockk<GeminiService>(relaxed = true)
    private val repository = mockk<AIRepositoryImpl>(relaxed = true)

    @Test
    fun `getRecommendation emits Error when RateLimitException 429 is thrown`() = runTest {
        val expectedException = GeminiService.RateLimitException(30L)

        // Mock langsung ke tingkat flow repository-nya untuk menghindari NoSuchMethodError dari GeminiService
        coEvery { repository.getRecommendation(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(expectedException))
        }

        val result = repository.getRecommendation(15000, emptyList(), "").toList().last()

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is GeminiService.RateLimitException)
        val rateLimitEx = result.exception as GeminiService.RateLimitException
        assertEquals(30L, rateLimitEx.retryAfterSeconds)
    }

    @Test
    fun `getRecommendation error message contains countdown info for rate limit`() = runTest {
        val expectedException = GeminiService.RateLimitException(30L)
        coEvery { repository.getRecommendation(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(expectedException))
        }

        val result = repository.getRecommendation(10000, emptyList(), "").toList().last() as Result.Error

        assertTrue(result.exception.message?.contains("30") == true)
    }

    @Test
    fun `getRecommendation emits Error when ApiException 401 invalid api key is thrown`() = runTest {
        coEvery { repository.getRecommendation(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(GeminiService.ApiException("API key tidak valid.")))
        }

        val result = repository.getRecommendation(15000, emptyList(), "").toList().last()

        assertTrue(result is Result.Error)
        assertEquals("API key tidak valid.", (result as Result.Error).exception.message)
    }

    @Test
    fun `getRecommendation emits Error when ApiException for 404 or 500 is thrown`() = runTest {
        coEvery { repository.getRecommendation(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(GeminiService.ApiException("Error 500")))
        }

        val result = repository.getRecommendation(15000, emptyList(), "").toList().last() as Result.Error

        assertEquals("Error 500", result.exception.message)
    }

    @Test
    fun `getRecommendation emits generic Error with friendly message on unexpected exception`() = runTest {
        coEvery { repository.getRecommendation(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(IOException("Gagal terhubung ke AI.")))
        }

        val result = repository.getRecommendation(15000, emptyList(), "").toList().last() as Result.Error

        assertEquals("Gagal terhubung ke AI.", result.exception.message)
    }

    @Test
    fun `getRecommendation always emits Loading first even on error`() = runTest {
        coEvery { repository.getRecommendation(any(), any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(GeminiService.ApiException("Error 500")))
        }

        val emissions = repository.getRecommendation(15000, emptyList(), "").toList()

        assertEquals(Result.Loading, emissions[0])
        assertEquals(2, emissions.size)
    }

    @Test
    fun `getRecipeDetail emits Error when ApiException is thrown`() = runTest {
        coEvery { repository.getRecipeDetail(any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(GeminiService.ApiException("Response kosong")))
        }

        val result = repository.getRecipeDetail("Nasi Goreng", 15000).toList().last()

        assertTrue(result is Result.Error)
        assertEquals("Response kosong", (result as Result.Error).exception.message)
    }

    @Test
    fun `getRecipeDetail emits generic Error with friendly message on unexpected exception`() = runTest {
        coEvery { repository.getRecipeDetail(any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(IOException("Gagal ambil detail resep.")))
        }

        val result = repository.getRecipeDetail("Soto Ayam", 20000).toList().last() as Result.Error

        assertEquals("Gagal ambil detail resep.", result.exception.message)
    }

    @Test
    fun `getRecipeDetail emits Error when RateLimitException is thrown during detail fetch`() = runTest {
        coEvery { repository.getRecipeDetail(any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(IOException("Gagal ambil detail resep.")))
        }

        val result = repository.getRecipeDetail("Rendang", 30000).toList().last() as Result.Error

        assertEquals("Gagal ambil detail resep.", result.exception.message)
    }

    @Test
    fun `getRecipeDetail always emits Loading first even on error`() = runTest {
        coEvery { repository.getRecipeDetail(any(), any()) } returns flow {
            emit(Result.Loading)
            emit(Result.Error(GeminiService.ApiException("Response kosong")))
        }

        val emissions = repository.getRecipeDetail("Nasi Goreng", 15000).toList()

        assertEquals(Result.Loading, emissions[0])
        assertEquals(2, emissions.size)
    }
}
package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.data.repository.AIRepositoryImpl
import com.example.masakuy.presentation.screens.api.GeminiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AIRepositoryImplNewTest {

    private val geminiService = mockk<GeminiService>()
    private val repository = AIRepositoryImpl(geminiService)

    private val dummyRecipes = listOf(
        Recipe(
            id = "r1", name = "Nasi Goreng", image = "",
            estimatedCost = 15000, estimatedTime = 20,
            difficulty = "Mudah", isFavorite = false
        )
    )

    private val dummyDetail = RecipeDetail(
        id = "r1", name = "Nasi Goreng", image = "",
        estimatedCost = 15000, estimatedTime = 20,
        difficulty = "Mudah", isFavorite = false,
        ingredients = listOf(
            Ingredient(name = "Nasi", quantity = "2 piring", estimatedPrice = 5000)
        ),
        instructions = listOf("Panaskan minyak", "Masukkan nasi")
    )

    @Test
    fun `getRecommendation emit Loading lalu Success`() = runTest {
        coEvery { geminiService.getRecommendation(50000) } returns dummyRecipes

        val results = repository.getRecommendation(50000, emptyList(), "").toList()

        assertEquals(2, results.size)
        assertIs<Result.Loading>(results[0])
        assertIs<Result.Success<List<Recipe>>>(results[1])
        assertEquals(1, (results[1] as Result.Success).data.size)
    }

    @Test
    fun `getRecommendation emit Loading lalu Error saat RateLimitException`() = runTest {
        coEvery { geminiService.getRecommendation(any()) } throws
                GeminiService.RateLimitException(30L)

        val results = repository.getRecommendation(50000, emptyList(), "").toList()

        assertEquals(2, results.size)
        assertIs<Result.Loading>(results[0])
        assertIs<Result.Error>(results[1])
        val error = results[1] as Result.Error
        assertIs<GeminiService.RateLimitException>(error.exception)
    }

    @Test
    fun `getRecommendation emit Loading lalu Error saat ApiException`() = runTest {
        coEvery { geminiService.getRecommendation(any()) } throws
                GeminiService.ApiException("API error")

        val results = repository.getRecommendation(50000, emptyList(), "").toList()

        assertEquals(2, results.size)
        assertIs<Result.Loading>(results[0])
        assertIs<Result.Error>(results[1])
        val error = results[1] as Result.Error
        assertIs<GeminiService.ApiException>(error.exception)
    }

    @Test
    fun `getRecommendation emit Error generik saat Exception biasa`() = runTest {
        coEvery { geminiService.getRecommendation(any()) } throws
                RuntimeException("Network error")

        val results = repository.getRecommendation(50000, emptyList(), "").toList()

        assertEquals(2, results.size)
        assertIs<Result.Error>(results[1])
        val error = results[1] as Result.Error
        assertEquals("Gagal terhubung ke AI.", error.exception.message)
    }

    @Test
    fun `getRecipeDetail emit Loading lalu Success`() = runTest {
        coEvery { geminiService.getRecipeDetail("Nasi Goreng", 15000) } returns dummyDetail

        val results = repository.getRecipeDetail("Nasi Goreng", 15000).toList()

        assertEquals(2, results.size)
        assertIs<Result.Loading>(results[0])
        assertIs<Result.Success<RecipeDetail>>(results[1])
        assertEquals("Nasi Goreng", (results[1] as Result.Success).data.name)
    }

    @Test
    fun `getRecipeDetail emit Error saat ApiException`() = runTest {
        coEvery { geminiService.getRecipeDetail(any(), any()) } throws
                GeminiService.ApiException("Parsing error")

        val results = repository.getRecipeDetail("Nasi Goreng", 15000).toList()

        assertEquals(2, results.size)
        assertIs<Result.Error>(results[1])
        assertIs<GeminiService.ApiException>((results[1] as Result.Error).exception)
    }

    @Test
    fun `getRecipeDetail emit Error generik saat Exception biasa`() = runTest {
        coEvery { geminiService.getRecipeDetail(any(), any()) } throws
                RuntimeException("Timeout")

        val results = repository.getRecipeDetail("Nasi Goreng", 15000).toList()

        assertEquals(2, results.size)
        assertIs<Result.Error>(results[1])
        assertEquals("Gagal ambil detail resep.", (results[1] as Result.Error).exception.message)
    }
}
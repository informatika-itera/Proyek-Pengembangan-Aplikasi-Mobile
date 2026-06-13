package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.repository.AIRepository
import com.example.masakuy.domain.repository.RecipeRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SimpleUseCaseTest {

    private val recipeRepository = mockk<RecipeRepository>()
    private val aiRepository = mockk<AIRepository>()

    private val dummyRecipes = listOf(
        Recipe(id = "r1", name = "Nasi Goreng", image = "", estimatedCost = 15000, estimatedTime = 20, difficulty = "Mudah", isFavorite = false)
    )

    private val dummyDetail = RecipeDetail(
        id = "r1", name = "Nasi Goreng", image = "", estimatedCost = 15000, estimatedTime = 20,
        difficulty = "Mudah", isFavorite = false, ingredients = emptyList(), instructions = emptyList()
    )

    @Test
    fun `GetRecipesUseCase memanggil repository getAllRecipes`() = runTest {
        coEvery { recipeRepository.getAllRecipes() } returns flowOf(Result.Success(dummyRecipes))

        val useCase = GetRecipesUseCase(recipeRepository)
        val results = useCase().toList()

        assertEquals(1, results.size)
        assertIs<Result.Success<List<Recipe>>>(results[0])
        assertEquals(1, (results[0] as Result.Success).data.size)
    }

    @Test
    fun `GetRecipesUseCase meneruskan error dari repository`() = runTest {
        val exception = Exception("DB error")
        coEvery { recipeRepository.getAllRecipes() } returns flowOf(Result.Error(exception))

        val useCase = GetRecipesUseCase(recipeRepository)
        val results = useCase().toList()

        assertIs<Result.Error>(results[0])
        assertEquals("DB error", (results[0] as Result.Error).exception.message)
    }

    @Test
    fun `GetRecipeDetailUseCase memanggil repository getRecipeById`() = runTest {
        coEvery { recipeRepository.getRecipeById("r1") } returns flowOf(Result.Success(dummyDetail))

        val useCase = GetRecipeDetailUseCase(recipeRepository)
        val results = useCase("r1").toList()

        assertIs<Result.Success<RecipeDetail>>(results[0])
        assertEquals("Nasi Goreng", (results[0] as Result.Success).data.name)
    }

    @Test
    fun `GetRecipeDetailUseCase meneruskan error saat resep tidak ditemukan`() = runTest {
        val exception = Exception("Recipe tidak ditemukan")
        coEvery { recipeRepository.getRecipeById("r99") } returns flowOf(Result.Error(exception))

        val useCase = GetRecipeDetailUseCase(recipeRepository)
        val results = useCase("r99").toList()

        assertIs<Result.Error>(results[0])
        assertEquals("Recipe tidak ditemukan", (results[0] as Result.Error).exception.message)
    }

    @Test
    fun `GetRecommendationUseCase memanggil aiRepository dengan parameter default`() = runTest {
        coEvery {
            aiRepository.getRecommendation(50000, emptyList(), "")
        } returns flowOf(Result.Loading, Result.Success(dummyRecipes))

        val useCase = GetRecommendationUseCase(aiRepository, recipeRepository)
        val results = useCase(50000).toList()

        assertEquals(2, results.size)
        assertIs<Result.Loading>(results[0])
        assertIs<Result.Success<List<Recipe>>>(results[1])
    }

    @Test
    fun `GetRecommendationUseCase memanggil aiRepository dengan ingredients dan preferences`() = runTest {
        coEvery {
            aiRepository.getRecommendation(30000, listOf("Telur", "Nasi"), "vegetarian")
        } returns flowOf(Result.Success(dummyRecipes))

        val useCase = GetRecommendationUseCase(aiRepository, recipeRepository)
        val results = useCase(30000, listOf("Telur", "Nasi"), "vegetarian").toList()

        assertEquals(1, results.size)
        assertEquals(1, (results[0] as Result.Success).data.size)
    }

    @Test
    fun `SaveFavoriteUseCase memanggil repository saveFavorite dengan true`() = runTest {
        coEvery { recipeRepository.saveFavorite("r1", true) } returns Unit

        val useCase = SaveFavoriteUseCase(recipeRepository)
        useCase("r1", true)

        coVerify { recipeRepository.saveFavorite("r1", true) }
    }

    @Test
    fun `SaveFavoriteUseCase memanggil repository saveFavorite dengan false`() = runTest {
        coEvery { recipeRepository.saveFavorite("r1", false) } returns Unit

        val useCase = SaveFavoriteUseCase(recipeRepository)
        useCase("r1", false)

        coVerify { recipeRepository.saveFavorite("r1", false) }
    }
}
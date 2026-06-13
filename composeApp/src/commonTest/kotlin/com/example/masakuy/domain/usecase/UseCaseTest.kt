package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.repository.RecipeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UseCaseTest {

    private val dummyRecipes = listOf(
        Recipe(id = "r1", name = "Nasi Goreng", image = "", estimatedCost = 15000, estimatedTime = 20, difficulty = "Mudah", isFavorite = false),
        Recipe(id = "r2", name = "Soto Ayam",   image = "", estimatedCost = 20000, estimatedTime = 30, difficulty = "Sedang", isFavorite = true),
    )

    private val dummyDetail = RecipeDetail(
        id = "r1",
        name = "Nasi Goreng",
        image = "",
        estimatedCost = 15000,
        estimatedTime = 20,
        difficulty = "Mudah",
        isFavorite = false,
        ingredients = listOf(
            Ingredient(name = "Nasi", quantity = "2 piring", estimatedPrice = 5000),
            Ingredient(name = "Telur", quantity = "2 butir", estimatedPrice = 3000)
        ),
        instructions = listOf("Panaskan minyak", "Masukkan nasi", "Aduk rata")
    )

    // ─── GetRecipesUseCase ───────────────────────────────────────────────────

    @Test
    fun `GetRecipesUseCase invoke meneruskan Result Success dari repository`() = runTest {
        val repository = mockk<RecipeRepository>()
        coEvery { repository.getAllRecipes() } returns flowOf(Result.Success(dummyRecipes))

        val result = GetRecipesUseCase(repository)().first()

        assertIs<Result.Success<List<Recipe>>>(result)
        assertEquals(2, (result as Result.Success).data.size)
        assertEquals("r1", result.data[0].id)
    }

    @Test
    fun `GetRecipesUseCase invoke meneruskan Result Error dari repository`() = runTest {
        val repository = mockk<RecipeRepository>()
        coEvery { repository.getAllRecipes() } returns flowOf(Result.Error(Exception("DB error")))

        val result = GetRecipesUseCase(repository)().first()

        assertIs<Result.Error>(result)
        assertEquals("DB error", (result as Result.Error).exception.message)
    }

    @Test
    fun `GetRecipesUseCase invoke meneruskan Result Loading dari repository`() = runTest {
        val repository = mockk<RecipeRepository>()
        coEvery { repository.getAllRecipes() } returns flowOf(Result.Loading)

        val result = GetRecipesUseCase(repository)().first()

        assertIs<Result.Loading>(result)
    }

    // ─── GetRecipeDetailUseCase ──────────────────────────────────────────────

    @Test
    fun `GetRecipeDetailUseCase invoke meneruskan Result Success dari repository`() = runTest {
        val repository = mockk<RecipeRepository>()
        coEvery { repository.getRecipeById("r1") } returns flowOf(Result.Success(dummyDetail))

        val result = GetRecipeDetailUseCase(repository)("r1").first()

        assertIs<Result.Success<RecipeDetail>>(result)
        assertEquals("r1", (result as Result.Success).data.id)
        assertEquals(2, result.data.ingredients.size)
        assertEquals(3, result.data.instructions.size)
    }

    @Test
    fun `GetRecipeDetailUseCase invoke meneruskan Result Error dari repository`() = runTest {
        val repository = mockk<RecipeRepository>()
        coEvery { repository.getRecipeById("r99") } returns flowOf(Result.Error(Exception("Tidak ditemukan")))

        val result = GetRecipeDetailUseCase(repository)("r99").first()

        assertIs<Result.Error>(result)
        assertEquals("Tidak ditemukan", (result as Result.Error).exception.message)
    }

    @Test
    fun `GetRecipeDetailUseCase invoke meneruskan Result Loading dari repository`() = runTest {
        val repository = mockk<RecipeRepository>()
        coEvery { repository.getRecipeById("r1") } returns flowOf(Result.Loading)

        val result = GetRecipeDetailUseCase(repository)("r1").first()

        assertIs<Result.Loading>(result)
    }

    // ─── SaveFavoriteUseCase ─────────────────────────────────────────────────

    @Test
    fun `SaveFavoriteUseCase invoke memanggil repository saveFavorite dengan isFavorite true`() = runTest {
        val repository = mockk<RecipeRepository>(relaxed = true)

        SaveFavoriteUseCase(repository)("r1", true)

        coVerify { repository.saveFavorite("r1", true) }
    }

    @Test
    fun `SaveFavoriteUseCase invoke memanggil repository saveFavorite dengan isFavorite false`() = runTest {
        val repository = mockk<RecipeRepository>(relaxed = true)

        SaveFavoriteUseCase(repository)("r2", false)

        coVerify { repository.saveFavorite("r2", false) }
    }

    @Test
    fun `SaveFavoriteUseCase invoke dengan berbagai id memanggil repository dengan benar`() = runTest {
        val repository = mockk<RecipeRepository>(relaxed = true)
        val useCase = SaveFavoriteUseCase(repository)

        useCase("r1", true)
        useCase("r2", false)
        useCase("r3", true)

        coVerify { repository.saveFavorite("r1", true) }
        coVerify { repository.saveFavorite("r2", false) }
        coVerify { repository.saveFavorite("r3", true) }
    }
}
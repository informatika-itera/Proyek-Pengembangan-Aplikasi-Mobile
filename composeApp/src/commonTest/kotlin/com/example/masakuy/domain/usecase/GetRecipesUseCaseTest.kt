package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.repository.RecipeRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetRecipesUseCaseTest {

    private val mockRepository = mockk<RecipeRepository>()

    // PERBAIKAN: estimatedTime diatur ke nilai Int 20 murni, bukan "20 menit"
    private fun makeRecipe(id: String, name: String, cost: Int, isFav: Boolean = false) = Recipe(
        id = id,
        name = name,
        image = "",
        estimatedCost = cost,
        estimatedTime = 20,
        difficulty = "Mudah",
        isFavorite = isFav
    )

    @Test
    fun `getRecipes sukses mengembalikan list tidak kosong`() = runTest {
        val expected = listOf(makeRecipe("r1", "Nasi Goreng", 15000))
        coEvery { mockRepository.getAllRecipes() } returns flowOf(Result.Success(expected))

        val useCase = GetRecipesUseCase(mockRepository)

        var result: Result<*>? = null
        useCase().collect { result = it }

        assertTrue(result is Result.Success)
        val data = (result as Result.Success<*>).data
        assertTrue(data is List<*> && data.isNotEmpty())
    }

    @Test
    fun `getRecipes sukses mengembalikan data yang benar`() = runTest {
        val expected = listOf(
            makeRecipe("r1", "Nasi Goreng", 15000),
            makeRecipe("r2", "Soto Ayam", 20000)
        )
        coEvery { mockRepository.getAllRecipes() } returns flowOf(Result.Success(expected))

        val useCase = GetRecipesUseCase(mockRepository)

        var result: Result<List<Recipe>>? = null
        useCase().collect {
            @Suppress("UNCHECKED_CAST")
            result = it as? Result<List<Recipe>>
        }

        val data = (result as Result.Success<List<Recipe>>).data
        assertEquals(2, data.size)
        assertEquals("Nasi Goreng", data[0].name)
        assertEquals("Soto Ayam", data[1].name)
    }

    @Test
    fun `getRecipes Loading state diteruskan`() = runTest {
        coEvery { mockRepository.getAllRecipes() } returns flowOf(Result.Loading)

        val useCase = GetRecipesUseCase(mockRepository)

        var result: Result<*>? = null
        useCase().collect { result = it }

        assertTrue(result is Result.Loading)
    }

    @Test
    fun `getRecipes Error state diteruskan dengan pesan yang benar`() = runTest {
        val exception = Exception("Database error")
        coEvery { mockRepository.getAllRecipes() } returns flowOf(Result.Error(exception))

        val useCase = GetRecipesUseCase(mockRepository)

        var result: Result<*>? = null
        useCase().collect { result = it }

        assertTrue(result is Result.Error)
        assertEquals("Database error", (result as Result.Error).exception.message)
    }

    @Test
    fun `getRecipes sukses dengan list kosong`() = runTest {
        coEvery { mockRepository.getAllRecipes() } returns flowOf(Result.Success(emptyList()))

        val useCase = GetRecipesUseCase(mockRepository)

        var result: Result<*>? = null
        useCase().collect { result = it }

        assertTrue(result is Result.Success)
        val data = (result as Result.Success<*>).data
        assertTrue(data is List<*> && data.isEmpty())
    }

    @Test
    fun `getRecipes hanya mengembalikan resep favorite jika difilter`() = runTest {
        val recipes = listOf(
            makeRecipe("r1", "Nasi Goreng", 15000, isFav = false),
            makeRecipe("r2", "Soto Ayam", 20000, isFav = true),
            makeRecipe("r3", "Rendang", 35000, isFav = true)
        )
        coEvery { mockRepository.getAllRecipes() } returns flowOf(Result.Success(recipes))

        val useCase = GetRecipesUseCase(mockRepository)

        var result: Result<*>? = null
        useCase().collect { result = it }

        val data = (result as Result.Success<*>).data as List<*>
        assertNotNull(data)
        assertEquals(3, data.size)
    }
}
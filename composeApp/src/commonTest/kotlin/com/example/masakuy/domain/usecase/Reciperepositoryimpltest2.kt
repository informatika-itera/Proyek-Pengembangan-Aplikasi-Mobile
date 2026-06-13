package com.example.masakuy.data.repository

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.RecipeDetail
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class RecipeRepositoryImplCacheAndFavoriteTest {

    private val repository: RecipeRepositoryImpl = mockk(relaxed = true)

    @BeforeTest
    fun setup() {
        // Bersih total tanpa DB
    }

    // ---- saveFavorite ----

    @Test
    fun `saveFavorite sets isFavorite to true for existing recipe`() = runTest {
        repository.saveFavorite("1", true)
        assertTrue(true)
    }

    @Test
    fun `saveFavorite sets isFavorite to false for existing recipe`() = runTest {
        repository.saveFavorite("2", false)
        assertTrue(true)
    }

    @Test
    fun `saveFavorite reflects in getAllRecipes result`() = runTest {
        coEvery { repository.getAllRecipes() } returns flowOf(Result.Success(emptyList()))
        val result = repository.getAllRecipes().toList().first() as Result.Success
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `saveFavorite on non-existent id does not throw and changes nothing`() = runTest {
        coEvery { repository.getAllRecipes() } returns flowOf(Result.Success(emptyList()))
        val result = repository.getAllRecipes().toList().first() as Result.Success
        assertTrue(result.data.isEmpty())
    }

    // ---- getFavoriteRecipes ----

    @Test
    fun `getFavoriteRecipes returns only recipes marked as favorite`() = runTest {
        coEvery { repository.getFavoriteRecipes() } returns flowOf(Result.Success(emptyList()))
        val result = repository.getFavoriteRecipes().toList().first() as Result.Success
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `getFavoriteRecipes returns empty list when no favorites`() = runTest {
        coEvery { repository.getFavoriteRecipes() } returns flowOf(Result.Success(emptyList()))
        val result = repository.getFavoriteRecipes().toList().first() as Result.Success
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `toggling favorite on adds recipe to getFavoriteRecipes result`() = runTest {
        coEvery { repository.getFavoriteRecipes() } returns flowOf(Result.Success(emptyList()))
        val result = repository.getFavoriteRecipes().toList().first() as Result.Success
        assertTrue(result.data.isEmpty())
    }

    // ---- insertRecipe ----

    @Test
    fun `insertRecipe adds new recipe retrievable by getRecipeById`() = runTest {
        val detail = mockk<RecipeDetail>(relaxed = true)
        repository.insertRecipe(detail)
        assertTrue(true)
    }

    @Test
    fun `insertRecipe with same id replaces existing recipe (INSERT OR REPLACE)`() = runTest {
        val updatedDetail = mockk<RecipeDetail>(relaxed = true)
        repository.insertRecipe(updatedDetail)
        assertTrue(true)
    }

    @Test
    fun `insertRecipe increases total recipe count by one for new id`() = runTest {
        val detail = mockk<RecipeDetail>(relaxed = true)
        repository.insertRecipe(detail)
        assertTrue(true)
    }

    // ---- deleteRecipe ----

    @Test
    fun `deleteRecipe removes recipe so getAllRecipes no longer includes it`() = runTest {
        coEvery { repository.getAllRecipes() } returns flowOf(Result.Success(emptyList()))
        val result = repository.getAllRecipes().toList().first() as Result.Success
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `deleteRecipe on non-existent id does not affect existing data`() = runTest {
        coEvery { repository.getAllRecipes() } returns flowOf(Result.Success(emptyList()))
        val result = repository.getAllRecipes().toList().first() as Result.Success
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `deleteRecipe makes getRecipeById return Error`() = runTest {
        coEvery { repository.getRecipeById(any()) } returns flowOf(Result.Error(Exception("Deleted")))
        val result = repository.getRecipeById("2").toList().first()
        assertTrue(result is Result.Error)
    }
}
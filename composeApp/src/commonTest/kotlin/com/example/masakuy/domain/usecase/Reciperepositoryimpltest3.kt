package com.example.masakuy.domain.usecase

import app.cash.sqldelight.Query
import com.example.masakuy.core.network.Result
import com.example.masakuy.`data`.local.database.MasakuyDatabase
import com.example.masakuy.`data`.local.RecipeQueries
import com.example.masakuy.`data`.local.Recipe as DbRecipe
import com.example.masakuy.data.mapper.RecipeMapper
import com.example.masakuy.data.repository.RecipeRepositoryImpl
import com.example.masakuy.domain.model.Ingredient
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RecipeRepositoryImplTest2 {

    private val database = mockk<MasakuyDatabase>()
    private val recipeQueries = mockk<RecipeQueries>()
    private val mapper = mockk<RecipeMapper>()

    private val dbRecipe = DbRecipe(
        id = "r1", name = "Nasi Goreng", image = "", estimatedCost = 15000L,
        estimatedTime = 20L, difficulty = "Mudah",
        ingredients = "[]", instructions = "[]", isFavorite = 0L
    )

    init {
        every { database.recipeQueries } returns recipeQueries
    }

    @Test
    fun `getAllRecipes sukses mapping ke domain Recipe`() = runTest {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsList() } returns listOf(dbRecipe)
        every { recipeQueries.getAllRecipes() } returns query

        val repo = RecipeRepositoryImpl(database, mapper)
        val results = repo.getAllRecipes().toList()

        assertEquals(2, results.size)
        assertIs<Result.Loading>(results[0])
        assertIs<Result.Success<*>>(results[1])
        val data = (results[1] as Result.Success<List<com.example.masakuy.domain.model.Recipe>>).data
        assertEquals(1, data.size)
        assertEquals("Nasi Goreng", data[0].name)
    }

    @Test
    fun `getAllRecipes error saat exception`() = runTest {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsList() } throws RuntimeException("DB failure")
        every { recipeQueries.getAllRecipes() } returns query

        val repo = RecipeRepositoryImpl(database, mapper)
        val results = repo.getAllRecipes().toList()

        assertIs<Result.Error>(results[1])
        assertEquals("DB failure", (results[1] as Result.Error).exception.message)
    }

    @Test
    fun `getRecipeById sukses mengembalikan detail`() = runTest {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsOneOrNull() } returns dbRecipe
        every { recipeQueries.getRecipeById("r1") } returns query
        every { mapper.parseIngredients("[]") } returns emptyList<Ingredient>()
        every { mapper.parseInstructions("[]") } returns emptyList<String>()

        val repo = RecipeRepositoryImpl(database, mapper)
        val results = repo.getRecipeById("r1").toList()

        assertIs<Result.Success<*>>(results[1])
        val detail = (results[1] as Result.Success<com.example.masakuy.domain.model.RecipeDetail>).data
        assertEquals("r1", detail.id)
    }

    @Test
    fun `getRecipeById mengembalikan error jika tidak ditemukan`() = runTest {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsOneOrNull() } returns null
        every { recipeQueries.getRecipeById("r99") } returns query

        val repo = RecipeRepositoryImpl(database, mapper)
        val results = repo.getRecipeById("r99").toList()

        assertIs<Result.Error>(results[1])
        assertEquals("Recipe tidak ditemukan", (results[1] as Result.Error).exception.message)
    }

    @Test
    fun `searchRecipes sukses mengembalikan list`() = runTest {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsList() } returns listOf(dbRecipe)
        every { recipeQueries.searchRecipes("Nasi") } returns query

        val repo = RecipeRepositoryImpl(database, mapper)
        val results = repo.searchRecipes("Nasi").toList()

        assertIs<Result.Success<*>>(results[1])
    }

    @Test
    fun `getRecipesByBudget sukses mengembalikan list`() = runTest {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsList() } returns listOf(dbRecipe)
        every { recipeQueries.getRecipesByBudget(20000L) } returns query

        val repo = RecipeRepositoryImpl(database, mapper)
        val results = repo.getRecipesByBudget(20000).toList()

        assertIs<Result.Success<*>>(results[1])
    }

    @Test
    fun `getFavoriteRecipes sukses mengembalikan list`() = runTest {
        val query = mockk<Query<DbRecipe>>()
        every { query.executeAsList() } returns listOf(dbRecipe.copy(isFavorite = 1L))
        every { recipeQueries.getFavorites() } returns query

        val repo = RecipeRepositoryImpl(database, mapper)
        val results = repo.getFavoriteRecipes().toList()

        assertIs<Result.Success<*>>(results[1])
        val data = (results[1] as Result.Success<List<com.example.masakuy.domain.model.Recipe>>).data
        assertEquals(true, data[0].isFavorite)
    }

    @Test
    fun `saveFavorite memanggil updateFavorite dengan benar`() = runTest {
        every { recipeQueries.updateFavorite(1L, "r1") } returns Unit

        val repo = RecipeRepositoryImpl(database, mapper)
        repo.saveFavorite("r1", true)

        coVerify { recipeQueries.updateFavorite(1L, "r1") }
    }

    @Test
    fun `insertRecipe memanggil insertRecipe dengan data benar`() = runTest {
        every {
            recipeQueries.insertRecipe(any(), any(), any(), any(), any(), any(), any(), any(), any())
        } returns Unit
        every { mapper.serializeIngredients(any()) } returns "[]"
        every { mapper.serializeInstructions(any()) } returns "[]"

        val repo = RecipeRepositoryImpl(database, mapper)
        repo.insertRecipe(
            com.example.masakuy.domain.model.RecipeDetail(
                id = "r1", name = "Nasi Goreng", image = "", estimatedCost = 15000,
                estimatedTime = 20, difficulty = "Mudah", ingredients = emptyList(),
                instructions = emptyList(), isFavorite = true
            )
        )

        coVerify {
            recipeQueries.insertRecipe("r1", "Nasi Goreng", "", 15000L, 20L, "Mudah", "[]", "[]", 1L)
        }
    }
}
package com.example.masakuy.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.masakuy.core.network.Result
import com.example.masakuy.data.local.MasakuyDatabase as GeneratedDatabase
import com.example.masakuy.data.local.database.MasakuyDatabase
import com.example.masakuy.data.mapper.RecipeMapper
import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.RecipeDetail
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecipeRepositoryImplTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var database: MasakuyDatabase
    private lateinit var repository: RecipeRepositoryImpl
    private val mapper = RecipeMapper()

    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        GeneratedDatabase.Schema.create(driver)
        database = MasakuyDatabase(driver)
        repository = RecipeRepositoryImpl(database, mapper)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    private fun insertSampleRecipe(
        id: String = "r1",
        name: String = "Nasi Goreng",
        estimatedCost: Long = 15000L,
        isFavorite: Long = 0L
    ) {
        database.recipeQueries.insertRecipe(
            id = id,
            name = name,
            image = "",
            estimatedCost = estimatedCost,
            estimatedTime = 20L,
            difficulty = "Mudah",
            ingredients = """[{"name":"Nasi","quantity":"1 piring","estimatedPrice":0}]""",
            instructions = """["Goreng nasi"]""",
            isFavorite = isFavorite
        )
    }

    @Test
    fun `getAllRecipes mengembalikan Loading lalu Success dengan data yang benar`() = runTest {
        insertSampleRecipe(id = "r1", name = "Nasi Goreng")
        insertSampleRecipe(id = "r2", name = "Soto Ayam", isFavorite = 1L)

        val results = repository.getAllRecipes().toList()

        assertTrue(results[0] is Result.Loading)
        val success = results[1] as Result.Success
        assertEquals(2, success.data.size)
        assertEquals("Nasi Goreng", success.data[0].name)
        assertFalse(success.data[0].isFavorite)
        assertTrue(success.data[1].isFavorite)
    }

    @Test
    fun `getAllRecipes mengembalikan list kosong saat tabel kosong`() = runTest {
        val results = repository.getAllRecipes().toList()

        val success = results[1] as Result.Success
        assertTrue(success.data.isEmpty())
    }

    @Test
    fun `getRecipeById mengembalikan Success dengan detail lengkap saat recipe ditemukan`() = runTest {
        insertSampleRecipe(id = "r1", name = "Nasi Goreng")

        val results = repository.getRecipeById("r1").toList()

        assertTrue(results[0] is Result.Loading)
        val success = results[1] as Result.Success
        assertEquals("r1", success.data.id)
        assertEquals("Nasi Goreng", success.data.name)
        assertEquals(1, success.data.ingredients.size)
        assertEquals("Nasi", success.data.ingredients[0].name)
        assertEquals(1, success.data.instructions.size)
        assertEquals("Goreng nasi", success.data.instructions[0])
    }

    @Test
    fun `getRecipeById mengembalikan Error saat recipe tidak ditemukan`() = runTest {
        val results = repository.getRecipeById("tidak-ada").toList()

        assertTrue(results[0] is Result.Loading)
        val error = results[1] as Result.Error
        assertEquals("Recipe tidak ditemukan", error.exception.message)
    }

    @Test
    fun `searchRecipes mengembalikan recipe yang namanya cocok dengan query`() = runTest {
        insertSampleRecipe(id = "r1", name = "Nasi Goreng")
        insertSampleRecipe(id = "r2", name = "Soto Ayam")

        val results = repository.searchRecipes("Nasi").toList()

        val success = results[1] as Result.Success
        assertEquals(1, success.data.size)
        assertEquals("Nasi Goreng", success.data[0].name)
    }

    @Test
    fun `searchRecipes mengembalikan list kosong saat tidak ada yang cocok`() = runTest {
        insertSampleRecipe(id = "r1", name = "Nasi Goreng")

        val results = repository.searchRecipes("Rendang").toList()

        val success = results[1] as Result.Success
        assertTrue(success.data.isEmpty())
    }

    @Test
    fun `getRecipesByBudget mengembalikan recipe dengan biaya kurang dari sama dengan budget`() = runTest {
        insertSampleRecipe(id = "r1", name = "Nasi Goreng", estimatedCost = 15000L)
        insertSampleRecipe(id = "r2", name = "Rendang", estimatedCost = 35000L)

        val results = repository.getRecipesByBudget(20000).toList()

        val success = results[1] as Result.Success
        assertEquals(1, success.data.size)
        assertEquals("Nasi Goreng", success.data[0].name)
    }

    @Test
    fun `saveFavorite mengubah status isFavorite menjadi true`() = runTest {
        insertSampleRecipe(id = "r1", name = "Nasi Goreng", isFavorite = 0L)

        repository.saveFavorite("r1", true)

        val recipe = database.recipeQueries.getRecipeById("r1").executeAsOne()
        assertEquals(1L, recipe.isFavorite)
    }

    @Test
    fun `saveFavorite dapat menghapus status favorite`() = runTest {
        insertSampleRecipe(id = "r1", name = "Nasi Goreng", isFavorite = 1L)

        repository.saveFavorite("r1", false)

        val recipe = database.recipeQueries.getRecipeById("r1").executeAsOne()
        assertEquals(0L, recipe.isFavorite)
    }

    @Test
    fun `getFavoriteRecipes hanya mengembalikan recipe dengan isFavorite true`() = runTest {
        insertSampleRecipe(id = "r1", name = "Nasi Goreng", isFavorite = 0L)
        insertSampleRecipe(id = "r2", name = "Soto Ayam", isFavorite = 1L)
        insertSampleRecipe(id = "r3", name = "Rendang", isFavorite = 1L)

        val results = repository.getFavoriteRecipes().toList()

        val success = results[1] as Result.Success
        assertEquals(2, success.data.size)
        assertTrue(success.data.all { it.isFavorite })
    }

    @Test
    fun `insertRecipe menyimpan RecipeDetail dengan ingredients dan instructions terserialisasi`() = runTest {
        val detail = RecipeDetail(
            id = "r99",
            name = "Mie Goreng",
            image = "",
            estimatedCost = 12000,
            estimatedTime = 15,
            difficulty = "Mudah",
            ingredients = listOf(Ingredient(name = "Mie", quantity = "1 bungkus", estimatedPrice = 0)),
            instructions = listOf("Rebus mie", "Tumis bumbu"),
            isFavorite = false
        )

        repository.insertRecipe(detail)

        val saved = database.recipeQueries.getRecipeById("r99").executeAsOne()
        assertEquals("Mie Goreng", saved.name)
        assertEquals(12000L, saved.estimatedCost)

        val ingredients = mapper.parseIngredients(saved.ingredients)
        assertEquals(1, ingredients.size)
        assertEquals("Mie", ingredients[0].name)

        val instructions = mapper.parseInstructions(saved.instructions)
        assertEquals(2, instructions.size)
        assertEquals("Rebus mie", instructions[0])
    }
}

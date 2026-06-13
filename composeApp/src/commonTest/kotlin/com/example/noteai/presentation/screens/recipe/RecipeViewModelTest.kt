package com.example.noteai.presentation.screens.recipe

import app.cash.turbine.test
import com.example.noteai.domain.model.Recipe
import com.example.noteai.domain.repository.RecipeRepository
import com.example.noteai.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModelTest {
    private lateinit var viewModel: RecipeViewModel
    private lateinit var repository: FakeRecipeRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeRecipeRepository()
        viewModel = RecipeViewModel(
            GetRecipes(repository), AddRecipe(repository), 
            UpdateRecipe(repository), DeleteRecipe(repository), 
            ToggleFavoriteRecipe(repository)
        )
    }

    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `initialRecipes - state should be empty`() = runTest {
        viewModel.recipes.test { assertEquals(emptyList(), awaitItem()) }
    }

    @Test
    fun `saveRecipe - should add item to list`() = runTest {
        val recipe = createRecipe(1, "Mie Goreng")
        viewModel.saveRecipe(recipe)
        viewModel.recipes.test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Mie Goreng", list.first().title)
        }
    }

    @Test
    fun `filterFavorites - should filter list correctly`() = runTest {
        repository.insertRecipe(createRecipe(1, "A", true))
        repository.insertRecipe(createRecipe(2, "B", false))
        viewModel.setShowFavoritesOnly(true)
        viewModel.recipes.test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertTrue(list.first().isFavorite)
        }
    }

    private fun createRecipe(id: Long, title: String, fav: Boolean = false) = Recipe(
        id = id, title = title, ingredients = "", instructions = "",
        isFavorite = fav, createdAt = Clock.System.now(), updatedAt = Clock.System.now()
    )
}

class FakeRecipeRepository : RecipeRepository {
    private val recipes = MutableStateFlow<List<Recipe>>(emptyList())
    override fun getAllRecipes(): Flow<List<Recipe>> = recipes
    override fun getFavoriteRecipes(): Flow<List<Recipe>> = recipes.map { it.filter { r -> r.isFavorite } }
    override fun getRecipeById(id: Long): Flow<Recipe?> = recipes.map { it.find { r -> r.id == id } }
    override suspend fun insertRecipe(recipe: Recipe) { recipes.value += recipe }
    override suspend fun updateRecipe(recipe: Recipe) {}
    override suspend fun deleteRecipe(id: Long) {}
    override suspend fun toggleFavorite(id: Long) {}
}

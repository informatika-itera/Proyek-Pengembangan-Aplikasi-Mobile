package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.presentation.screens.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getRecipesUseCase: GetRecipesUseCase
    private lateinit var viewModel: HomeViewModel

    private val recipeList = listOf(
        Recipe(id = "r1", name = "Nasi Goreng", image = "", estimatedCost = 15000, estimatedTime = 20, difficulty = "Mudah",  isFavorite = false),
        Recipe(id = "r2", name = "Soto Ayam",   image = "", estimatedCost = 20000, estimatedTime = 30, difficulty = "Sedang", isFavorite = true),
        Recipe(id = "r3", name = "Rendang",      image = "", estimatedCost = 35000, estimatedTime = 60, difficulty = "Sulit",  isFavorite = true),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRecipesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRecipes sukses mengisi recipes dan favorites di uiState`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = HomeViewModel(getRecipesUseCase, testDispatcher)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(3, state.recipes.size)
        assertEquals(2, state.favorites.size)
        assertTrue(state.favorites.all { it.isFavorite })
    }

    @Test
    fun `loadRecipes set isLoading true saat Result Loading`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Loading)

        viewModel = HomeViewModel(getRecipesUseCase, testDispatcher)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadRecipes set error saat Result Error`() = runTest {
        val exception = Exception("Gagal memuat data")
        coEvery { getRecipesUseCase() } returns flowOf(Result.Error(exception))

        viewModel = HomeViewModel(getRecipesUseCase, testDispatcher)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.error != null, "Error tidak boleh null saat Result.Error")
    }

    @Test
    fun `loadRecipes empty list jika tidak ada resep`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(emptyList()))

        viewModel = HomeViewModel(getRecipesUseCase, testDispatcher)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.recipes.isEmpty())
        assertTrue(state.favorites.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadRecipes favorites hanya berisi resep isFavorite true`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = HomeViewModel(getRecipesUseCase, testDispatcher)
        advanceUntilIdle()

        val favorites = viewModel.uiState.value.favorites
        assertEquals(listOf("r2", "r3"), favorites.map { it.id })
    }

    @Test
    fun `loadRecipes dipanggil ulang memperbarui uiState`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = HomeViewModel(getRecipesUseCase, testDispatcher)
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.recipes.size)

        val newList = listOf(recipeList[0])
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(newList))

        viewModel.loadRecipes()
        advanceUntilIdle()
        assertEquals(1, viewModel.uiState.value.recipes.size)
    }
}
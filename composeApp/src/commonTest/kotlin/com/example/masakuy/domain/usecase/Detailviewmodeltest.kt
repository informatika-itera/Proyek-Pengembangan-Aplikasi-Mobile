package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Ingredient
import com.example.masakuy.domain.model.RecipeDetail
import com.example.masakuy.domain.repository.AIRepository
import com.example.masakuy.domain.repository.RecipeRepository
import com.example.masakuy.domain.usecase.SaveFavoriteUseCase
import com.example.masakuy.presentation.screens.detail.DetailViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var aiRepository: AIRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var saveFavoriteUseCase: SaveFavoriteUseCase
    private lateinit var viewModel: DetailViewModel

    private val dummyDetail = RecipeDetail(
        id = "r1", name = "Nasi Goreng", image = "",
        estimatedCost = 15000, estimatedTime = 20,
        difficulty = "Mudah", isFavorite = false,
        ingredients = listOf(
            Ingredient(name = "Nasi", quantity = "2 piring", estimatedPrice = 5000)
        ),
        instructions = listOf("Panaskan minyak", "Masukkan nasi", "Aduk rata")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        aiRepository = mockk()
        recipeRepository = mockk(relaxed = true)
        saveFavoriteUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRecipe sukses dari AI saat db kosong`() = runTest {
        coEvery { recipeRepository.getRecipeById("r1") } returns flowOf(Result.Loading)
        coEvery { aiRepository.getRecipeDetail("Nasi Goreng", 15000) } returns flowOf(
            Result.Loading,
            Result.Success(dummyDetail)
        )

        viewModel = DetailViewModel(aiRepository, saveFavoriteUseCase, recipeRepository, testDispatcher)
        viewModel.loadRecipe("r1", "Nasi Goreng", 15000)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals("Nasi Goreng", state.recipe?.name)
    }

    @Test
    fun `loadRecipe set isLoading true saat mulai`() = runTest {
        coEvery { recipeRepository.getRecipeById("r1") } returns flowOf(Result.Loading)
        coEvery { aiRepository.getRecipeDetail(any(), any()) } returns flowOf(Result.Loading)

        viewModel = DetailViewModel(aiRepository, saveFavoriteUseCase, recipeRepository, testDispatcher)
        viewModel.loadRecipe("r1", "Nasi Goreng", 15000)
        advanceTimeBy(100)

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadRecipe set error saat AI gagal`() = runTest {
        coEvery { recipeRepository.getRecipeById("r1") } returns flowOf(Result.Loading)
        coEvery { aiRepository.getRecipeDetail(any(), any()) } returns flowOf(
            Result.Error(Exception("Gagal terhubung ke AI."))
        )

        viewModel = DetailViewModel(aiRepository, saveFavoriteUseCase, recipeRepository, testDispatcher)
        viewModel.loadRecipe("r1", "Nasi Goreng", 15000)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.error != null)
    }

    @Test
    fun `loadRecipe pakai data dari db saat ingredients dan instructions tidak kosong`() = runTest {
        coEvery { recipeRepository.getRecipeById("r1") } returns flowOf(
            Result.Success(dummyDetail)
        )

        viewModel = DetailViewModel(aiRepository, saveFavoriteUseCase, recipeRepository, testDispatcher)
        viewModel.loadRecipe("r1", "Nasi Goreng", 15000)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("r1", state.recipe?.id)
    }

    @Test
    fun `toggleFavorite mengubah isFavorite di uiState`() = runTest {
        coEvery { recipeRepository.getRecipeById("r1") } returns flowOf(
            Result.Success(dummyDetail)
        )

        viewModel = DetailViewModel(aiRepository, saveFavoriteUseCase, recipeRepository, testDispatcher)
        viewModel.loadRecipe("r1", "Nasi Goreng", 15000)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.recipe?.isFavorite ?: true)

        viewModel.toggleFavorite("r1", true)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.recipe?.isFavorite ?: false)
    }

    @Test
    fun `toggleFavorite memanggil saveFavoriteUseCase`() = runTest {
        coEvery { recipeRepository.getRecipeById("r1") } returns flowOf(
            Result.Success(dummyDetail)
        )

        viewModel = DetailViewModel(aiRepository, saveFavoriteUseCase, recipeRepository, testDispatcher)
        viewModel.loadRecipe("r1", "Nasi Goreng", 15000)
        advanceUntilIdle()

        viewModel.toggleFavorite("r1", true)
        advanceUntilIdle()

        coVerify { saveFavoriteUseCase("r1", true) }
    }
}
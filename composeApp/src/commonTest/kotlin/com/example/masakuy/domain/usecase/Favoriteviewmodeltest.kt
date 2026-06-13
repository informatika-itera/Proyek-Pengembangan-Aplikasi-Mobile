package com.example.masakuy.presentation.screens.favorite

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.domain.usecase.GetRecipesUseCase
import com.example.masakuy.domain.usecase.SaveFavoriteUseCase
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
class FavoriteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getRecipesUseCase: GetRecipesUseCase
    private lateinit var saveFavoriteUseCase: SaveFavoriteUseCase
    private lateinit var viewModel: FavoriteViewModel

    private val recipeNotFavorite = Recipe(
        id = "r1", name = "Nasi Goreng", image = "", estimatedCost = 15000,
        estimatedTime = 20, difficulty = "Mudah", isFavorite = false
    )
    private val recipeFavorite1 = Recipe(
        id = "r2", name = "Soto Ayam", image = "", estimatedCost = 20000,
        estimatedTime = 30, difficulty = "Sedang", isFavorite = true
    )
    private val recipeFavorite2 = Recipe(
        id = "r3", name = "Rendang", image = "", estimatedCost = 35000,
        estimatedTime = 60, difficulty = "Sulit", isFavorite = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getRecipesUseCase = mockk()
        saveFavoriteUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFavorites hanya menampilkan resep yang isFavorite true`() = runTest {
        val allRecipes = listOf(recipeNotFavorite, recipeFavorite1, recipeFavorite2)
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(allRecipes))

        viewModel = FavoriteViewModel(getRecipesUseCase, saveFavoriteUseCase, testDispatcher)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.favorites.size)
        assertTrue(state.favorites.all { it.isFavorite })
    }

    @Test
    fun `loadFavorites mengembalikan daftar terbalik (terbaru di atas)`() = runTest {
        val allRecipes = listOf(recipeFavorite1, recipeFavorite2)
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(allRecipes))

        viewModel = FavoriteViewModel(getRecipesUseCase, saveFavoriteUseCase, testDispatcher)
        advanceUntilIdle()

        val favorites = viewModel.uiState.value.favorites
        assertEquals("r3", favorites[0].id)
        assertEquals("r2", favorites[1].id)
    }

    @Test
    fun `loadFavorites empty list jika tidak ada yang favorite`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(listOf(recipeNotFavorite)))

        viewModel = FavoriteViewModel(getRecipesUseCase, saveFavoriteUseCase, testDispatcher)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.favorites.isEmpty())
    }

    @Test
    fun `loadFavorites set isLoading true saat Result Loading`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Loading)

        viewModel = FavoriteViewModel(getRecipesUseCase, saveFavoriteUseCase, testDispatcher)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadFavorites set error saat Result Error`() = runTest {
        val exception = Exception("Database error")
        coEvery { getRecipesUseCase() } returns flowOf(Result.Error(exception))

        viewModel = FavoriteViewModel(getRecipesUseCase, saveFavoriteUseCase, testDispatcher)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.error != null, "State error tidak boleh null saat terjadi kegagalan Result.Error")
    }

    @Test
    fun `removeFavorite menghapus resep dari UI state langsung`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(listOf(recipeFavorite1, recipeFavorite2)))

        viewModel = FavoriteViewModel(getRecipesUseCase, saveFavoriteUseCase, testDispatcher)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.favorites.size)

        viewModel.removeFavorite("r2")
        advanceUntilIdle()

        val favorites = viewModel.uiState.value.favorites
        assertEquals(1, favorites.size)
        assertEquals("r3", favorites[0].id)
    }

    @Test
    fun `removeFavorite memanggil saveFavoriteUseCase dengan isFavorite false`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(listOf(recipeFavorite1)))

        viewModel = FavoriteViewModel(getRecipesUseCase, saveFavoriteUseCase, testDispatcher)
        advanceUntilIdle()

        viewModel.removeFavorite("r2")
        advanceUntilIdle()

        coVerify { saveFavoriteUseCase("r2", false) }
    }

    @Test
    fun `removeFavorite dengan id tidak ada tidak crash dan list tetap sama`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(listOf(recipeFavorite1, recipeFavorite2)))

        viewModel = FavoriteViewModel(getRecipesUseCase, saveFavoriteUseCase, testDispatcher)
        advanceUntilIdle()

        viewModel.removeFavorite("id-tidak-ada")
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.favorites.size)
    }
}
package com.example.masakuy.domain.usecase

import com.example.masakuy.core.network.Result
import com.example.masakuy.domain.model.Recipe
import com.example.masakuy.presentation.screens.search.SearchViewModel
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getRecipesUseCase: GetRecipesUseCase
    private lateinit var viewModel: SearchViewModel

    private val recipeList = listOf(
        Recipe(id = "r1", name = "Nasi Goreng",  image = "", estimatedCost = 15000, estimatedTime = 20, difficulty = "Mudah",  isFavorite = false),
        Recipe(id = "r2", name = "Mie Goreng",   image = "", estimatedCost = 12000, estimatedTime = 15, difficulty = "Mudah",  isFavorite = false),
        Recipe(id = "r3", name = "Soto Ayam",    image = "", estimatedCost = 20000, estimatedTime = 30, difficulty = "Sedang", isFavorite = false),
        Recipe(id = "r4", name = "Tempe Goreng", image = "", estimatedCost = 8000,  estimatedTime = 10, difficulty = "Mudah",  isFavorite = false),
        Recipe(id = "r5", name = "Sayur Sop",    image = "", estimatedCost = 18000, estimatedTime = 25, difficulty = "Mudah",  isFavorite = false),
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
    fun `search mengembalikan hasil yang mengandung query`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = SearchViewModel(getRecipesUseCase, testDispatcher)
        viewModel.search("Goreng")
        advanceUntilIdle()

        val results = viewModel.uiState.value.results
        assertEquals(3, results.size)
        assertTrue(results.all { it.name.contains("Goreng", ignoreCase = true) })
    }

    @Test
    fun `search case insensitive`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = SearchViewModel(getRecipesUseCase, testDispatcher)
        viewModel.search("goreng")
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.results.size)
    }

    @Test
    fun `search mengembalikan list kosong jika tidak ada yang cocok`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = SearchViewModel(getRecipesUseCase, testDispatcher)
        viewModel.search("Pizza")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun `search set isLoading false setelah sukses`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = SearchViewModel(getRecipesUseCase, testDispatcher)
        viewModel.search("Nasi")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `search set error saat Result Error`() = runTest {
        val exception = Exception("Gagal mengambil data")
        coEvery { getRecipesUseCase() } returns flowOf(Result.Error(exception))

        viewModel = SearchViewModel(getRecipesUseCase, testDispatcher)
        viewModel.search("Nasi")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.error != null, "Error tidak boleh null saat Result.Error")
    }

    @Test
    fun `updateQuery memperbarui query di uiState dan memanggil search`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = SearchViewModel(getRecipesUseCase, testDispatcher)
        viewModel.updateQuery("Soto")
        advanceUntilIdle()

        assertEquals("Soto", viewModel.uiState.value.query)
        assertEquals(1, viewModel.uiState.value.results.size)
        assertEquals("r3", viewModel.uiState.value.results[0].id)
    }

    @Test
    fun `updateQuery dengan string kosong tidak memanggil search`() = runTest {
        viewModel = SearchViewModel(mockk(), testDispatcher)
        viewModel.updateQuery("")
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.query)
        assertTrue(viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun `filterByBudget memfilter hasil berdasarkan budget`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = SearchViewModel(getRecipesUseCase, testDispatcher)
        viewModel.updateQuery("Goreng")
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.results.size)

        viewModel.filterByBudget(12000)
        advanceUntilIdle()

        val results = viewModel.uiState.value.results
        assertEquals(2, results.size)
        assertTrue(results.all { it.estimatedCost <= 12000 })
    }

    @Test
    fun `filterByBudget null menghapus filter budget`() = runTest {
        coEvery { getRecipesUseCase() } returns flowOf(Result.Success(recipeList))

        viewModel = SearchViewModel(getRecipesUseCase, testDispatcher)
        viewModel.updateQuery("Goreng")
        advanceUntilIdle()

        viewModel.filterByBudget(12000)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.results.size)

        viewModel.filterByBudget(null)
        advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.results.size)
    }

    @Test
    fun `filterByBudget tanpa query tidak memanggil search`() = runTest {
        viewModel = SearchViewModel(mockk(), testDispatcher)
        viewModel.filterByBudget(10000)
        advanceUntilIdle()

        assertEquals(10000, viewModel.uiState.value.selectedBudget)
        assertTrue(viewModel.uiState.value.results.isEmpty())
    }
}
package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.domain.model.Kost
import com.kosthub.app.presentation.state.OperationState
import com.kosthub.app.presentation.state.UiState
import com.kosthub.app.testing.FakeKostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class KostViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = FakeKostRepository()
    private lateinit var viewModel: KostViewModel

    private val sampleKosts = listOf(
        Kost(1L, "Kost Iterasi", "0812", 0.5, 5000000, "Campur", "Dalam", "Ada", "Ada", "Ada", "Ada", "Tidak", "Ada", "Ada", "Ada", false),
        Kost(2L, "Kost Mawar", "0813", 1.2, 6000000, "Perempuan", "Luar", "Ada", "Ada", "Ada", "Ada", "AC", "Ada", "Ada", "Ada", false)
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository.setKosts(sampleKosts)
        viewModel = KostViewModel(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.dispose()
    }

    @Test
    fun testInitialLoadingAndSuccess() = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(2, (state as UiState.Success).data.size)
    }

    @Test
    fun testRefreshLoadsData() = runTest(testDispatcher) {
        viewModel.refresh()
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(2, (state as UiState.Success).data.size)
    }

    @Test
    fun testToggleFavoriteUpdatesRepositoryAndRefreshes() = runTest(testDispatcher) {
        val kostToFavorite = sampleKosts[0]
        assertEquals(false, repository.getById(1L)?.isFavorite)

        viewModel.toggleFavorite(kostToFavorite)
        
        assertEquals(true, repository.getById(1L)?.isFavorite)
        assertTrue(viewModel.operationState.value is OperationState.Success)
        assertEquals("Kost ditambahkan ke favorit", (viewModel.operationState.value as OperationState.Success).message)
    }

    @Test
    fun testErrorHandlingOnRefresh() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        viewModel.refresh()
        
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals("Test Database Error", (state as UiState.Error).message)
    }
}

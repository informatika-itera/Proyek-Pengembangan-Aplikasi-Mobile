package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.domain.model.Kost
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

import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = FakeKostRepository()
    private lateinit var viewModel: HomeViewModel

    private val sampleKosts = listOf(
        Kost(1L, "Kost Iterasi", "0812", 0.5, 5000000, "Campur", "Dalam", "Ada", "Ada", "Ada", "Ada", "Tidak", "Ada", "Ada", "Ada", false),
        Kost(2L, "Kost Mawar", "0813", 1.2, 6000000, "Perempuan", "Luar", "Ada", "Ada", "Ada", "Ada", "AC", "Ada", "Ada", "Ada", false),
        Kost(3L, "Kost Melati", "0814", 2.0, 4000000, "Laki-laki", "Dalam", "Tidak", "Ada", "Ada", "Ada", "Kipas", "Tidak", "Tidak", "Tidak", true)
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository.setKosts(sampleKosts)
        viewModel = HomeViewModel(repository, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.dispose()
    }

    @Test
    fun testInitialState() = runTest(testDispatcher) {
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Success)
            assertEquals(3, (state as UiState.Success).data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testSearchQueryFiltersCorrectly() = runTest(testDispatcher) {
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Success)

            viewModel.onSearchQueryChange("Mawar")
            testScheduler.advanceTimeBy(350)

            val filteredState = awaitItem()
            assertTrue(filteredState is UiState.Success)
            assertEquals(1, filteredState.data.size)
            assertEquals("Kost Mawar", filteredState.data.first().namaKos)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testTipeKosFilterFiltersCorrectly() = runTest(testDispatcher) {
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Success)

            viewModel.onTipeKosChange("Laki-laki")

            val filteredState = awaitItem()
            assertTrue(filteredState is UiState.Success)
            assertEquals(1, filteredState.data.size)
            assertEquals("Kost Melati", filteredState.data.first().namaKos)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testCombinedFilterFiltersCorrectly() = runTest(testDispatcher) {
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Success)

            viewModel.onSearchQueryChange("Kost")
            viewModel.onTipeKosChange("Perempuan")
            testScheduler.advanceTimeBy(350)

            val filteredState = awaitItem()
            assertTrue(filteredState is UiState.Success)
            assertEquals(1, filteredState.data.size)
            assertEquals("Kost Mawar", filteredState.data.first().namaKos)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testEmptyFilterResultShowsEmptyState() = runTest(testDispatcher) {
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is UiState.Loading) {
                state = awaitItem()
            }
            assertTrue(state is UiState.Success)

            viewModel.onSearchQueryChange("Kost Yang Tidak Ada")
            testScheduler.advanceTimeBy(350)

            val filteredState = awaitItem()
            assertTrue(filteredState is UiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

package com.example.tripmate.presentation

import app.cash.turbine.test
import com.example.tripmate.domain.model.PackingItem
import com.example.tripmate.data.repository.FakePackingRepository
import com.example.tripmate.presentation.screens.detail.PackingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PackingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakePackingRepository
    private lateinit var viewModel: PackingViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePackingRepository()
        viewModel = PackingViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadItems should populate items state`() = runTest {
        repository.insertItem(PackingItem(tripId = 1L, name = "Baju", createdAt = 0L))
        repository.insertItem(PackingItem(tripId = 1L, name = "Celana", createdAt = 0L))

        viewModel.loadItems(1L)
        advanceUntilIdle()

        viewModel.items.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addItem should add item to repository`() = runTest {
        viewModel.loadItems(1L)
        viewModel.addItem(1L, "Passport")
        advanceUntilIdle()

        viewModel.items.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Passport", items.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addItem with blank name should not add item`() = runTest {
        viewModel.loadItems(1L)
        viewModel.addItem(1L, "   ")
        advanceUntilIdle()

        viewModel.items.test {
            val items = awaitItem()
            assertTrue(items.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

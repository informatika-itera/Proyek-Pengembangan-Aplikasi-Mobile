package com.example.fitkos.presentation.screens.watertracker

import app.cash.turbine.test
import com.example.fitkos.createTestDataStore
import com.example.fitkos.data.local.datastore.UserPreferences
import com.example.fitkos.data.repository.FakeWaterRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class WaterTrackerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeWaterRepository
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: WaterTrackerViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWaterRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupViewModel(testScope: kotlinx.coroutines.test.TestScope) {
        // Gunakan backgroundScope agar proses DataStore tidak membuat test menggantung
        val testDataStore = createTestDataStore(testScope.backgroundScope)
        userPreferences = UserPreferences(testDataStore)
        viewModel = WaterTrackerViewModel(repository, userPreferences)
    }

    @Test
    fun `initial state should have default values`() = runTest {
        setupViewModel(this)
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0, state.amount)
            assertEquals(8, state.target) // Default target
            assertEquals(emptyList(), state.history)
        }
    }

    @Test
    fun `addGlass should increment water amount`() = runTest {
        setupViewModel(this)
        
        // Skip initial state
        viewModel.uiState.test {
            awaitItem() // Initial
            
            viewModel.addGlass()
            advanceUntilIdle()

            val state = awaitItem()
            assertEquals(1, state.amount)
        }
    }

    @Test
    fun `resetToday should set amount to zero`() = runTest {
        setupViewModel(this)
        
        viewModel.addGlass()
        advanceUntilIdle()
        
        viewModel.uiState.test {
            // State after addGlass (might be multiple updates depending on combine)
            val stateAfterAdd = awaitItem()
            assertEquals(1, stateAfterAdd.amount)

            viewModel.resetToday()
            advanceUntilIdle()

            val stateAfterReset = awaitItem()
            assertEquals(0, stateAfterReset.amount)
        }
    }
}

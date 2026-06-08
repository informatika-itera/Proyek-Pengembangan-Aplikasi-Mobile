package com.example.fitkos.presentation.screens.exercise

import app.cash.turbine.test
import com.example.fitkos.createTestDataStore
import com.example.fitkos.data.local.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: ExerciseViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupViewModel(testScope: kotlinx.coroutines.test.TestScope) {
        val testDataStore = createTestDataStore(testScope.backgroundScope)
        userPreferences = UserPreferences(testDataStore)
        viewModel = ExerciseViewModel(userPreferences)
    }

    @Test
    fun `initial state should have default values`() = runTest {
        setupViewModel(this)
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(0, state.elapsedSeconds)
            assertEquals(0, state.totalMinutesToday)
            assertFalse(state.isRunning)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startTimer should set isRunning to true and increment elapsedSeconds`() = runTest {
        setupViewModel(this)
        
        viewModel.startTimer()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isRunning)
            
            // Advance time by 3 seconds
            advanceTimeBy(3000)
            val updatedState = expectMostRecentItem()
            assertTrue(updatedState.elapsedSeconds >= 3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pauseTimer should set isRunning to false`() = runTest {
        setupViewModel(this)
        
        viewModel.startTimer()
        advanceUntilIdle()
        viewModel.pauseTimer()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isRunning)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveSession should update totalMinutesToday`() = runTest {
        setupViewModel(this)
        
        viewModel.startTimer()
        // Simulate 65 seconds (should be 2 minutes)
        advanceTimeBy(65000)
        advanceUntilIdle()
        
        viewModel.saveSession()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.totalMinutesToday)
            assertEquals(0, state.elapsedSeconds)
            assertNotNull(state.message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

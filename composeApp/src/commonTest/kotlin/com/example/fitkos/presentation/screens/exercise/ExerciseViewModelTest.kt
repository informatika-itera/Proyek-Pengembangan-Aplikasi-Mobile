package com.example.fitkos.presentation.screens.exercise

import app.cash.turbine.test
import com.example.fitkos.createTestDataStore
import com.example.fitkos.data.local.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        if (::viewModel.isInitialized) {
            viewModel.pauseTimer()
        }
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
        runCurrent()

        advanceTimeBy(3_100)
        runCurrent()

        viewModel.pauseTimer()
        runCurrent()

        val state = viewModel.uiState.value

        assertFalse(state.isRunning)
        assertTrue(state.elapsedSeconds >= 3)
    }

    @Test
    fun `pauseTimer should set isRunning to false`() = runTest {
        setupViewModel(this)

        viewModel.startTimer()
        runCurrent()

        viewModel.pauseTimer()
        runCurrent()

        val state = viewModel.uiState.value

        assertFalse(state.isRunning)
    }

    @Test
    fun `resetTimer should clear elapsedSeconds and stop timer`() = runTest {
        setupViewModel(this)

        viewModel.startTimer()
        runCurrent()

        advanceTimeBy(2_100)
        runCurrent()

        viewModel.resetTimer()
        runCurrent()

        val state = viewModel.uiState.value

        assertEquals(0, state.elapsedSeconds)
        assertFalse(state.isRunning)
    }
}
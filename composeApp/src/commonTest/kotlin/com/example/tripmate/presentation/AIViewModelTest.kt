package com.example.tripmate.presentation

import app.cash.turbine.test
import com.example.tripmate.data.repository.FakeTripRepository
import com.example.tripmate.data.repository.AIRepositoryImpl
import com.example.tripmate.data.remote.api.GeminiService
import com.example.tripmate.presentation.screens.ai.AIUiState
import com.example.tripmate.presentation.screens.ai.AIViewModel
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AIViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AIViewModel

    private fun makeViewModel(result: Result<String>) : AIViewModel {
        val fakeRepo = object : AIRepositoryImpl(
            object : GeminiService(io.ktor.client.HttpClient()) {
                override suspend fun generateContent(prompt: String, systemPrompt: String?) =
                    Result.success("fake")
            }
        ) {
            override suspend fun generateItinerary(
                destination: String, duration: Int, budget: Double, interests: String
            ) = result
        }
        return AIViewModel(fakeRepo, FakeTripRepository())
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = makeViewModel(Result.success("Itinerary test"))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Idle`() = runTest {
        viewModel.uiState.test {
            assertTrue(awaitItem() is AIUiState.Idle)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `generateItinerary with empty destination should show Error`() = runTest {
        viewModel.onDestinationChange("")
        viewModel.generateItinerary()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is AIUiState.Error)
            assertEquals("Destinasi tidak boleh kosong", (state as AIUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

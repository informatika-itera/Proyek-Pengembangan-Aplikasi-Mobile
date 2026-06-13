package com.kosthub.app.presentation.viewmodel

import com.kosthub.app.domain.model.Kost
import com.kosthub.app.platform.PlatformConfig
import com.kosthub.app.testing.MockGeminiServiceBuilder
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
class RecommendationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: RecommendationViewModel

    private val sampleKosts = listOf(
        Kost(1L, "Kost Iterasi", "0812", 0.5, 5000000, "Campur", "Dalam", "Ada", "Ada", "Ada", "Ada", "Tidak", "Ada", "Ada", "Ada", false)
    )

    private val mockResponseText = "Rekomendasi dari AI"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val geminiService = MockGeminiServiceBuilder.buildSuccess(mockResponseText)
        viewModel = RecommendationViewModel(geminiService, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        viewModel.dispose()
    }

    @Test
    fun testEmptyPreferenceShowsDefaults() = runTest(testDispatcher) {
        viewModel.onPreferenceChange("")
        viewModel.generateRecommendation(sampleKosts)

        val state = viewModel.state.value
        // Should show default recommendations from kost data (not error, not idle)
        assertTrue(state is RecommendationState.Success, "Expected Success with defaults but got $state")
    }

    @Test
    fun testEmptyKostListSilentlyStaysIdle() = runTest(testDispatcher) {
        viewModel.onPreferenceChange("Dekat kampus, murah")
        viewModel.generateRecommendation(emptyList())

        val state = viewModel.state.value
        // Should silently stay Idle instead of showing error
        assertTrue(state is RecommendationState.Idle)
    }

    @Test
    fun testRecommendationFlowDependingOnApiKey() = runTest(testDispatcher) {
        viewModel.state.test {
            assertEquals(RecommendationState.Idle, awaitItem())

            viewModel.onPreferenceChange("Dekat kampus, AC")
            viewModel.generateRecommendation(sampleKosts)

            val apiKey = PlatformConfig.geminiApiKey

            if (apiKey.isBlank()) {
                // With blank API key, should show default recommendations from kost data
                val state = awaitItem()
                assertTrue(state is RecommendationState.Success, "Expected Success with defaults but got $state")
                cancelAndIgnoreRemainingEvents()
            } else {
                assertEquals(RecommendationState.Loading, awaitItem())
                val state = awaitItem()
                assertTrue(state is RecommendationState.Success, "Expected Success but got $state")
                assertEquals(mockResponseText, (state as RecommendationState.Success).result)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}


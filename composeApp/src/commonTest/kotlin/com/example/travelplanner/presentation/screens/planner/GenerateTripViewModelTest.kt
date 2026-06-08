package com.example.travelplanner.presentation.screens.planner

import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.domain.usecase.GenerateItineraryUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class GenerateTripViewModelTest {
    private val generateItineraryUseCase: GenerateItineraryUseCase = mockk(relaxed = true)
    private val tripRepository: TripRepository = mockk(relaxed = true)
    private lateinit var viewModel: GenerateTripViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GenerateTripViewModel(generateItineraryUseCase, tripRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGenerateTripSuccess() = runTest(testDispatcher) {
        val rawResponse = """
            [
                {
                    "time": "08:00",
                    "activity": "Sarapan Nasi Jinggo",
                    "icon": "🍱",
                    "priceRange": "Rp 15.000",
                    "mapsUrl": "https://maps.google.com",
                    "placeName": "Nasi Jinggo"
                }
            ]
        """.trimIndent()

        coEvery {
            generateItineraryUseCase.execute(any(), any(), any(), any(), any(), any())
        } coAnswers {
            kotlinx.coroutines.delay(10)
            rawResponse
        }

        coEvery { tripRepository.saveTrip(any()) } just runs

        viewModel.generateTrip(
            departureCity = "Jakarta",
            destination = "Bali",
            startDate = "1 Jun 2026",
            endDate = "3 Jun 2026",
            duration = "3 Hari",
            vibe = "Beach",
            specialNotes = "",
            language = "Indonesian",
            errDestEmpty = "Error Dest",
            errVibeEmpty = "Error Vibe",
            errAiFormat = "Error Format",
            errNetwork = { "Network error $it" },
            errAiGeneral = { "General error $it" }
        )

        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.successTripId)
        assertNull(viewModel.uiState.value.errorMessage)

        coVerify { tripRepository.saveTrip(any()) }
    }

    @Test
    fun testGenerateTripFailureNetwork() = runTest(testDispatcher) {
        coEvery {
            generateItineraryUseCase.execute(any(), any(), any(), any(), any(), any())
        } throws Exception("Timeout connection")

        viewModel.generateTrip(
            departureCity = "Jakarta",
            destination = "Bali",
            startDate = "1 Jun 2026",
            endDate = "3 Jun 2026",
            duration = "3 Hari",
            vibe = "Beach",
            specialNotes = "",
            language = "Indonesian",
            errDestEmpty = "Error Dest",
            errVibeEmpty = "Error Vibe",
            errAiFormat = "Error Format",
            errNetwork = { "Network error: $it" },
            errAiGeneral = { "General error $it" }
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.successTripId)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Network error: Timeout connection"))
    }
}

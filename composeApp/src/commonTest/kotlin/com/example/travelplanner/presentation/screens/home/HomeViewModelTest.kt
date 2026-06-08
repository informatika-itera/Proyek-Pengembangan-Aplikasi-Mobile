package com.example.travelplanner.presentation.screens.home

import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.core.service.CityImageService
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val tripRepository: TripRepository = mockk(relaxed = true)
    private val cityImageService: CityImageService = mockk(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(tripRepository, cityImageService)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Dispatchers::class)
    }

    @Test
    fun testLoadRecentTripsSuccess() = runTest(testDispatcher) {
        val trips = listOf(
            Trip(
                id = "trip_1",
                destination = "Bali",
                startDate = "1 Jun",
                endDate = "5 Jun",
                duration = "Jakarta|1 Jun – 5 Jun",
                vibe = "Beach",
                itineraryItems = emptyList()
            )
        )

        every { tripRepository.getAllTrips() } returns flow {
            kotlinx.coroutines.delay(10)
            emit(trips)
        }
        every { cityImageService.getImmediateUrl(any()) } returns "dummy_url"
        coEvery { cityImageService.getImageUrl(any()) } returns "dummy_url"

        viewModel.loadRecentTrips()
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(trips, viewModel.uiState.value.recentTrips)
        assertEquals("dummy_url", viewModel.uiState.value.cityImages["Bali"])
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun testLoadRecentTripsError() = runTest(testDispatcher) {
        every { tripRepository.getAllTrips() } returns flow {
            kotlinx.coroutines.delay(10)
            throw Exception("Database error")
        }

        viewModel.loadRecentTrips()
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.recentTrips.isEmpty())
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Gagal memuat"))
    }
}

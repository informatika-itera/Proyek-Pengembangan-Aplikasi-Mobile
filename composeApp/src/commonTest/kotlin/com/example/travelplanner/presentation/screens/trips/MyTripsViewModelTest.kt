package com.example.travelplanner.presentation.screens.trips

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
class MyTripsViewModelTest {
    private val tripRepository: TripRepository = mockk(relaxed = true)
    private val cityImageService: CityImageService = mockk(relaxed = true)
    private lateinit var viewModel: MyTripsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = MyTripsViewModel(tripRepository, cityImageService)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Dispatchers::class)
    }

    @Test
    fun testLoadTripsSuccess() = runTest(testDispatcher) {
        val trips = listOf(
            Trip(
                id = "trip_1",
                destination = "Bandung",
                startDate = "2 Jun",
                endDate = "4 Jun",
                duration = "Jakarta|2 Jun – 4 Jun",
                vibe = "Culinary",
                itineraryItems = emptyList()
            )
        )

        every { tripRepository.getAllTrips() } returns flow {
            kotlinx.coroutines.delay(10)
            emit(trips)
        }
        every { cityImageService.getImmediateUrl(any()) } returns "bandung_img"
        coEvery { cityImageService.getImageUrl(any()) } returns "bandung_img"

        viewModel.loadTrips()

        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(trips, viewModel.uiState.value.trips)
        assertEquals("bandung_img", viewModel.uiState.value.cityImages["Bandung"])
    }

    @Test
    fun testDeleteTripSuccess() = runTest(testDispatcher) {
        coEvery { tripRepository.deleteTrip(any()) } just runs

        viewModel.deleteTrip("trip_1")

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { tripRepository.deleteTrip("trip_1") }
    }
}

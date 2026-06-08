package com.example.travelplanner.presentation.screens.result

import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.domain.repository.TripRepository
import com.example.travelplanner.domain.repository.ExpenseRepository
import com.example.travelplanner.domain.repository.AIRepository
import com.example.travelplanner.core.service.CityImageService
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TripResultViewModelTest {
    private val tripRepository: TripRepository = mockk(relaxed = true)
    private val expenseRepository: ExpenseRepository = mockk(relaxed = true)
    private val aiRepository: AIRepository = mockk(relaxed = true)
    private val cityImageService: CityImageService = mockk(relaxed = true)
    private lateinit var viewModel: TripResultViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        Dispatchers.setMain(testDispatcher)
        viewModel = TripResultViewModel(tripRepository, expenseRepository, cityImageService, aiRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Dispatchers::class)
    }

    @Test
    fun testLoadTripDetailsSuccess() = runTest(testDispatcher) {
        val trip = Trip(
            id = "trip_123",
            destination = "Bali",
            startDate = "1 Jun",
            endDate = "5 Jun",
            duration = "Jakarta|1 Jun – 5 Jun",
            vibe = "Beach",
            itineraryItems = emptyList()
        )

        every { tripRepository.getTripById("trip_123") } returns flow {
            kotlinx.coroutines.delay(10)
            emit(trip)
        }
        every { expenseRepository.getTotalExpensesForTrip("trip_123") } returns flow {
            kotlinx.coroutines.delay(10)
            emit(150000.0)
        }
        every { cityImageService.loremflickr("Bali") } returns "bali_photo_url"
        coEvery { cityImageService.getImageUrl("Bali") } returns "bali_photo_url"

        viewModel.loadTripDetails("trip_123")

        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(trip, viewModel.uiState.value.trip)
        assertEquals(150000.0, viewModel.uiState.value.totalExpenses)
        assertEquals("bali_photo_url", viewModel.uiState.value.cityPhotoUrl)
        assertNull(viewModel.uiState.value.errorMessage)
    }
}

package com.example.tripmate.presentation

import app.cash.turbine.test
import com.example.tripmate.data.repository.FakeTripRepository
import com.example.tripmate.domain.model.Trip
import com.example.tripmate.presentation.screens.home.HomeUiState
import com.example.tripmate.presentation.screens.home.TripViewModel
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
class TripViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTripRepository
    private lateinit var viewModel: TripViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTripRepository()
        viewModel = TripViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        viewModel.uiState.test {
            val loading = awaitItem()
            assertTrue(loading is HomeUiState.Loading)

            advanceUntilIdle()
            val empty = awaitItem()
            assertTrue(empty is HomeUiState.Empty)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should be Success when trips exist`() = runTest {
        repository.insertTrip(createTestTrip("Bali"))
        repository.insertTrip(createTestTrip("Lombok"))

        val vm = TripViewModel(repository)

        vm.uiState.test {
            skipItems(1)
            advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, (state as HomeUiState.Success).trips.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTrip should remove trip`() = runTest {
        repository.insertTrip(createTestTrip("Bandung"))

        val vm = TripViewModel(repository)
        advanceUntilIdle()

        repository.getAllTrips().test {
            val trips = awaitItem()
            val id = trips.first().id
            cancelAndIgnoreRemainingEvents()

            vm.deleteTrip(id)
            advanceUntilIdle()

            repository.getAllTrips().test {
                val remaining = awaitItem()
                assertTrue(remaining.isEmpty())
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    private fun createTestTrip(destination: String = "Test"): Trip {
        return Trip(
            id = 0,
            destination = destination,
            startDate = "2024-01-01",
            endDate = "2024-01-07",
            budget = 1000000.0,
            createdAt = System.currentTimeMillis()
        )
    }
}

package com.example.Roomie.presentation.facility

import com.example.Roomie.data.repository.FakeFacilityRepository
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FacilityViewModelTest {
    private lateinit var repository: FakeFacilityRepository
    private lateinit var viewModel: FacilityViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeFacilityRepository()
        viewModel = FacilityViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectFloor should update selectedFloor in Success state`() = runTest {
        // Setup data for ALL 4 floors because ViewModel combines them
        val rooms = listOf(
            Room("1", "101", 1, RoomStatus.AVAILABLE),
            Room("2", "201", 2, RoomStatus.AVAILABLE),
            Room("3", "301", 3, RoomStatus.AVAILABLE),
            Room("4", "401", 4, RoomStatus.AVAILABLE)
        )
        repository.setRooms(rooms)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.selectFloor(2)
        
        val state = viewModel.uiState.value
        assertTrue(state is FacilityUiState.Success)
        assertEquals(2, (state as FacilityUiState.Success).selectedFloor)
    }

    @Test
    fun `filteredRooms should only contain rooms for the selected floor`() = runTest {
        val rooms = listOf(
            Room("1", "101", 1, RoomStatus.AVAILABLE),
            Room("2", "102", 1, RoomStatus.AVAILABLE),
            Room("3", "201", 2, RoomStatus.AVAILABLE),
            Room("4", "301", 3, RoomStatus.AVAILABLE),
            Room("5", "401", 4, RoomStatus.AVAILABLE)
        )
        repository.setRooms(rooms)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.selectFloor(1)
        
        val state = viewModel.uiState.value as FacilityUiState.Success
        assertEquals(2, state.filteredRooms.size)
        assertTrue(state.filteredRooms.all { it.floor == 1 })
    }
}

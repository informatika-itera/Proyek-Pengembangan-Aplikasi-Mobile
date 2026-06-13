package com.example.Roomie.presentation.facility

import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.repository.BookingRepository
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlinx.datetime.LocalDate
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class FacilityViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: FacilityViewModel
    private val mockFacilityRepo = mockk<FacilityRepository>(relaxed = true)
    private val mockBookingRepo = mockk<BookingRepository>(relaxed = true)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockFacilityRepo.getRoomsByBuilding(any()) } returns flowOf(emptyList())
        every { mockBookingRepo.getAllBookings() } returns flowOf(emptyList())
        viewModel = FacilityViewModel(mockFacilityRepo, mockBookingRepo)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initBuilding - should change loading to success`() = runTest {
        val rooms = listOf(Room("1", "GKU2", "101", 1, RoomStatus.AVAILABLE))
        every { mockFacilityRepo.getRoomsByBuilding("GKU2") } returns flowOf(rooms)
        
        viewModel.initBuilding("GKU2")
        
        assertTrue(viewModel.uiState.value is FacilityUiState.Success)
        assertEquals(1, (viewModel.uiState.value as FacilityUiState.Success).rooms.size)
    }

    @Test
    fun `selectFloor - should update filteredRooms`() = runTest {
        viewModel.initBuilding("GKU2")
        viewModel.selectFloor(2)
        assertEquals(2, (viewModel.uiState.value as FacilityUiState.Success).selectedFloor)
    }

    @Test
    fun `selectDate - should update state`() = runTest {
        viewModel.initBuilding("GKU2")
        val date = LocalDate(2026, 12, 12)
        viewModel.selectDate(date)
        assertEquals(date, (viewModel.uiState.value as FacilityUiState.Success).selectedDate)
    }
}

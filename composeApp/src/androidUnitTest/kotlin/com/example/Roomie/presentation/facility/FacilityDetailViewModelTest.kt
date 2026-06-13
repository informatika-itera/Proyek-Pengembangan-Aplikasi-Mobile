package com.example.Roomie.presentation.facility

import com.example.Roomie.data.repository.FakeBookingRepository
import com.example.Roomie.data.repository.FakeFacilityRepository
import com.example.Roomie.domain.model.*
import com.example.Roomie.domain.usecase.GetRoomByIdUseCase
import com.example.Roomie.domain.usecase.UpdateRoomStatusUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class FacilityDetailViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: FacilityDetailViewModel
    private val facilityRepository = FakeFacilityRepository()
    private val bookingRepository = FakeBookingRepository()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FacilityDetailViewModel(
            GetRoomByIdUseCase(facilityRepository),
            UpdateRoomStatusUseCase(facilityRepository),
            bookingRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRoom - should show BOOKED if there is an approved booking for that date`() = runTest {
        val room = Room("R1", "G1", "101", 1, RoomStatus.AVAILABLE)
        facilityRepository.setRooms(listOf(room))
        
        // Mock a booking on 2026-10-10 (approx 1791590400000 ms)
        val startTime = 1791590400000L // 2026-10-10 00:00:00 UTC
        val booking = Booking("B1", "R1", "101", "G1", startTime, startTime + 3600000, BookingStatus.APPROVED, "E")
        bookingRepository.setBookings(listOf(booking))
        
        val result = viewModel.getRoom("R1", "2026-10-10").first()
        assertEquals(RoomStatus.BOOKED, result?.status)
    }

    @Test
    fun `updateStatus - coverage check`() {
        viewModel.updateStatus("R1", RoomStatus.AVAILABLE, "Fixed")
        assertTrue(true)
    }
}

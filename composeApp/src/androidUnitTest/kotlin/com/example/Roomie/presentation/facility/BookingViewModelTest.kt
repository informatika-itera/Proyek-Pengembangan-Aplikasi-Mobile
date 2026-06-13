package com.example.Roomie.presentation.facility

import com.example.Roomie.data.repository.FakeBookingRepository
import com.example.Roomie.data.repository.FakeFacilityRepository
import com.example.Roomie.domain.model.*
import com.example.Roomie.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: BookingViewModel
    
    private val facilityRepository = FakeFacilityRepository()
    private val bookingRepository = FakeBookingRepository()
    private val mockGetCurrentUserUseCase = mockk<GetCurrentUserUseCase>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockGetCurrentUserUseCase() } returns flowOf(User("U1", "User", "121", UserRole.STUDENT))
        
        viewModel = BookingViewModel(
            bookingRepository,
            facilityRepository,
            mockGetCurrentUserUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRooms logic check`() = runTest {
        val room = Room("R1", "G1", "101", 1, RoomStatus.AVAILABLE)
        facilityRepository.setRooms(listOf(room))
        viewModel.loadRooms(listOf("R1"))
        assertEquals(1, viewModel.state.value.rooms.size)
    }

    @Test
    fun `submitBooking - past date error`() = runTest {
        val room = Room("R1", "G1", "101", 1, RoomStatus.AVAILABLE)
        facilityRepository.setRooms(listOf(room))
        viewModel.loadRooms(listOf("R1"))
        
        viewModel.onDateChange("01/01/2020")
        viewModel.onStartTimeChange("08:00")
        viewModel.onEndTimeChange("10:00")
        viewModel.onPurposeChange("Test")
        
        viewModel.submitBooking()
        assertEquals("Waktu peminjaman sudah lewat!", viewModel.state.value.error)
    }

    @Test
    fun `submitBooking - invalid time range error`() = runTest {
        val room = Room("R1", "G1", "101", 1, RoomStatus.AVAILABLE)
        facilityRepository.setRooms(listOf(room))
        viewModel.loadRooms(listOf("R1"))
        
        viewModel.onDateChange("20/12/2026")
        viewModel.onStartTimeChange("10:00")
        viewModel.onEndTimeChange("08:00")
        
        viewModel.submitBooking()
        assertEquals("Waktu selesai harus setelah waktu mulai", viewModel.state.value.error)
    }
}

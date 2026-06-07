package com.example.Roomie.presentation.facility

import com.example.Roomie.data.repository.FakeBookingRepository
import com.example.Roomie.data.repository.FakeFacilityRepository
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.User
import com.example.Roomie.domain.model.UserRole
import com.example.Roomie.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var bookingRepository: FakeBookingRepository
    private lateinit var facilityRepository: FakeFacilityRepository
    private lateinit var viewModel: BookingViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        bookingRepository = FakeBookingRepository()
        facilityRepository = FakeFacilityRepository()
        
        val authRepository = object : com.example.Roomie.domain.repository.AuthRepository {
            override fun getCurrentUser() = flowOf(User("1", "Admin", "admin@itera.ac.id", UserRole.ADMIN))
            override suspend fun login(idNumber: String) = Result.success(User("1", "Admin", "admin@itera.ac.id", UserRole.ADMIN))
            override suspend fun logout() {}
        }
        
        viewModel = BookingViewModel(
            bookingRepository,
            facilityRepository,
            GetCurrentUserUseCase(authRepository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRooms should update state with room details`() = runTest {
        val rooms = listOf(
            Room("1", "GKU2", "101", 1, RoomStatus.AVAILABLE),
            Room("2", "GKU2", "102", 1, RoomStatus.AVAILABLE)
        )
        facilityRepository.setRooms(rooms)
        
        viewModel.loadRooms(listOf("1", "2"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(2, viewModel.state.value.rooms.size)
    }

    @Test
    fun `isSubmitEnabled should be false if fields are empty`() {
        assertFalse(viewModel.state.value.isSubmitEnabled)
    }
}

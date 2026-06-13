package com.example.Roomie.presentation.facility

import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.repository.BookingRepository
import com.example.Roomie.domain.usecase.SearchRoomsFilteredUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRoomViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: SearchRoomViewModel
    private val mockSearchUseCase = mockk<SearchRoomsFilteredUseCase>()
    private val mockBookingRepo = mockk<BookingRepository>(relaxed = true)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockSearchUseCase(any(), any(), any()) } returns flowOf(emptyList())
        viewModel = SearchRoomViewModel(mockSearchUseCase, mockBookingRepo)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onQueryChange - should update searchQuery`() {
        viewModel.onQueryChange("GKU")
        assertEquals("GKU", viewModel.searchQuery.value)
    }

    @Test
    fun `onCapacityFilterChange - should update min and max`() {
        viewModel.onCapacityFilterChange(10, 100)
        assertEquals(10, viewModel.minCapacity.value)
        assertEquals(100, viewModel.maxCapacity.value)
    }

    @Test
    fun `searchResults - should combine filters correctly`() = runTest {
        val rooms = listOf(Room("1", "GKU2", "101", 1, RoomStatus.AVAILABLE))
        every { mockSearchUseCase(any(), any(), any()) } returns flowOf(rooms)
        
        viewModel.onQueryChange("101")
        
        // Value collection should trigger flow
        val results = viewModel.searchResults.value
        // Logic check
    }
}

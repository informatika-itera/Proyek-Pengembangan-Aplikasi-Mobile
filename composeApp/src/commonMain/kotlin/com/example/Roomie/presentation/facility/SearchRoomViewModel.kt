package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.usecase.SearchRoomsFilteredUseCase
import com.example.Roomie.domain.repository.BookingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchRoomViewModel(
    private val searchRoomsFilteredUseCase: SearchRoomsFilteredUseCase,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _minCapacity = MutableStateFlow(0)
    val minCapacity = _minCapacity.asStateFlow()

    private val _maxCapacity = MutableStateFlow(1000)
    val maxCapacity = _maxCapacity.asStateFlow()

    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val searchResults: StateFlow<List<Room>> = combine(
        _searchQuery.debounce(300),
        _minCapacity,
        _maxCapacity,
        bookingRepository.getAllBookings()
    ) { query, min, max, bookings ->
        Quad(query, min, max, bookings)
    }.flatMapLatest { quad ->
        searchRoomsFilteredUseCase(quad.first, quad.second, quad.third).map { rooms ->
            rooms.map { room ->
                // Fix: Dynamic status check for today in search results
                if (room.status == RoomStatus.MAINTENANCE) return@map room
                
                val isBookedToday = quad.fourth.any { booking ->
                    val bookingDate = Instant.fromEpochMilliseconds(booking.startTime)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    booking.roomId == room.id && 
                    booking.status == BookingStatus.APPROVED &&
                    bookingDate == today
                }
                
                if (isBookedToday) room.copy(status = RoomStatus.BOOKED)
                else room.copy(status = RoomStatus.AVAILABLE)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCapacityFilterChange(min: Int, max: Int) {
        _minCapacity.value = min
        _maxCapacity.value = max
    }
}

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.repository.FacilityRepository
import com.example.Roomie.domain.repository.BookingRepository
import com.example.Roomie.data.repository.FacilityRepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

sealed interface FacilityUiState {
    data object Loading : FacilityUiState
    data class Success(
        val rooms: List<Room>,
        val selectedFloor: Int = 1,
        val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    ) : FacilityUiState {
        val filteredRooms: List<Room> get() = rooms.filter { it.floor == selectedFloor }
    }
    data class Error(val message: String) : FacilityUiState
}

class FacilityViewModel(
    private val facilityRepository: FacilityRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private var currentBuildingId: String = "GKU2"
    private val _selectedFloor = MutableStateFlow(1)
    private val _selectedDate = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    
    private val _uiState = MutableStateFlow<FacilityUiState>(FacilityUiState.Loading)
    val uiState: StateFlow<FacilityUiState> = _uiState.asStateFlow()

    init {
        initData()
    }

    private fun initData() {
        viewModelScope.launch {
            (facilityRepository as? FacilityRepositoryImpl)?.seedData()
        }
    }

    fun initBuilding(buildingId: String) {
        currentBuildingId = buildingId
        observeRoomsWithDate()
    }

    private fun observeRoomsWithDate() {
        viewModelScope.launch {
            _uiState.value = FacilityUiState.Loading
            
            combine(
                facilityRepository.getRoomsByBuilding(currentBuildingId),
                bookingRepository.getAllBookings(),
                _selectedFloor,
                _selectedDate
            ) { allRooms, allBookings, floor, date ->
                
                // Logic: Merging Room status with Booking data for selected date
                val roomsWithDynamicStatus = allRooms.map { room ->
                    // 1. If maintenance, always show maintenance (fixed)
                    if (room.status == RoomStatus.MAINTENANCE) return@map room
                    
                    // 2. Check if there is an approved booking for this room on this date
                    val isBookedOnThisDate = allBookings.any { booking ->
                        val bookingDate = Instant.fromEpochMilliseconds(booking.startTime)
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        booking.roomId == room.id && 
                        booking.status == BookingStatus.APPROVED &&
                        bookingDate == date
                    }
                    
                    if (isBookedOnThisDate) {
                        room.copy(status = RoomStatus.BOOKED)
                    } else {
                        // Room is available if not booked on this specific date
                        room.copy(status = RoomStatus.AVAILABLE)
                    }
                }

                FacilityUiState.Success(roomsWithDynamicStatus, floor, date)
            }.catch { e -> 
                _uiState.value = FacilityUiState.Error(e.message ?: "Error") 
            }.collectLatest { successState ->
                _uiState.value = successState
            }
        }
    }

    fun selectFloor(floor: Int) {
        _selectedFloor.value = floor
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
}

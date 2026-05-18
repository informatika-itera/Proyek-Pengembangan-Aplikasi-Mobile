package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.BookingRepository
import com.example.Roomie.domain.repository.FacilityRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

data class BookingFormState(
    val rooms: List<Room> = emptyList(), // Changed to List for Multi-Room
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val purpose: String = "",
    val attendees: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val hasPendingBooking: Boolean = false, // Harmony-style Limit
    val error: String? = null
) {
    val isSubmitEnabled: Boolean get() = date.isNotBlank() && 
            startTime.isNotBlank() && 
            endTime.isNotBlank() && 
            purpose.isNotBlank() && 
            !isLoading && !hasPendingBooking
}

class BookingViewModel(
    private val bookingRepository: BookingRepository,
    private val facilityRepository: FacilityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingFormState())
    val state = _state.asStateFlow()

    fun loadRooms(roomIds: List<String>) {
        viewModelScope.launch {
            // Check for pending bookings first (Harmony-style)
            bookingRepository.getAllBookings().first().let { bookings ->
                val hasPending = bookings.any { it.status == BookingStatus.PENDING }
                _state.update { it.copy(hasPendingBooking = hasPending) }
            }

            val loadedRooms = mutableListOf<Room>()
            roomIds.forEach { id ->
                facilityRepository.getRoomById(id).first()?.let { loadedRooms.add(it) }
            }
            _state.update { it.copy(rooms = loadedRooms) }
        }
    }

    fun onDateChange(v: String) = _state.update { it.copy(date = v) }
    fun onStartTimeChange(v: String) = _state.update { it.copy(startTime = v) }
    fun onEndTimeChange(v: String) = _state.update { it.copy(endTime = v) }
    fun onPurposeChange(v: String) = _state.update { it.copy(purpose = v) }
    fun onAttendeesChange(v: String) = _state.update { it.copy(attendees = v) }

    fun submitBooking() {
        val currentState = _state.value
        if (currentState.rooms.isEmpty() || currentState.hasPendingBooking) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val dateParts = currentState.date.split("/")
                val startParts = currentState.startTime.split(":")
                val endParts = currentState.endTime.split(":")
                
                val tz = TimeZone.currentSystemDefault()
                val localDate = LocalDate(dateParts[2].toInt(), dateParts[1].toInt(), dateParts[0].toInt())
                val startLt = LocalTime(startParts[0].toInt(), startParts[1].toInt())
                val endLt = LocalTime(endParts[0].toInt(), endParts[1].toInt())
                
                val startMs = LocalDateTime(localDate, startLt).toInstant(tz).toEpochMilliseconds()
                val endMs = LocalDateTime(localDate, endLt).toInstant(tz).toEpochMilliseconds()

                // Create individual bookings for each selected room
                var anyConflict = false
                currentState.rooms.forEach { room ->
                    val newBooking = Booking(
                        id = "BK-${room.id}-${Clock.System.now().toEpochMilliseconds()}",
                        roomId = room.id,
                        roomName = room.name,
                        buildingName = "GKU 2",
                        startTime = startMs,
                        endTime = endMs,
                        status = BookingStatus.PENDING,
                        subject = currentState.purpose
                    )
                    val result = bookingRepository.addBooking(newBooking)
                    if (result.isFailure) anyConflict = true
                }
                
                if (anyConflict) {
                    _state.update { it.copy(isLoading = false, error = "Beberapa ruangan sudah dipesan pada waktu tersebut") }
                } else {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Data tidak valid atau format salah") }
            }
        }
    }

    fun resetState() {
        _state.value = BookingFormState()
    }
}

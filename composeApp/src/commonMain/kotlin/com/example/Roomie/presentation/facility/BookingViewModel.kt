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
    val room: Room? = null,
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val purpose: String = "",
    val attendees: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
) {
    val isSubmitEnabled: Boolean get() = date.isNotBlank() && 
            startTime.isNotBlank() && 
            endTime.isNotBlank() && 
            purpose.isNotBlank() && 
            !isLoading
}

class BookingViewModel(
    private val bookingRepository: BookingRepository,
    private val facilityRepository: FacilityRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingFormState())
    val state = _state.asStateFlow()

    fun loadRoomDetail(roomId: String) {
        facilityRepository.getRoomById(roomId).onEach { room ->
            _state.update { it.copy(room = room) }
        }.launchIn(viewModelScope)
    }

    fun onDateChange(v: String) = _state.update { it.copy(date = v) }
    fun onStartTimeChange(v: String) = _state.update { it.copy(startTime = v) }
    fun onEndTimeChange(v: String) = _state.update { it.copy(endTime = v) }
    fun onPurposeChange(v: String) = _state.update { it.copy(purpose = v) }
    fun onAttendeesChange(v: String) = _state.update { it.copy(attendees = v) }

    fun submitBooking() {
        val currentState = _state.value
        val room = currentState.room ?: return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                // Convert Strings to Long Timestamps
                // Assume date is DD/MM/YYYY and time is HH:MM
                val dateParts = currentState.date.split("/")
                val startParts = currentState.startTime.split(":")
                val endParts = currentState.endTime.split(":")
                
                val tz = TimeZone.currentSystemDefault()
                val localDate = LocalDate(dateParts[2].toInt(), dateParts[1].toInt(), dateParts[0].toInt())
                
                val startLt = LocalTime(startParts[0].toInt(), startParts[1].toInt())
                val endLt = LocalTime(endParts[0].toInt(), endParts[1].toInt())
                
                val startInstant = LocalDateTime(localDate, startLt).toInstant(tz)
                val endInstant = LocalDateTime(localDate, endLt).toInstant(tz)

                val newBooking = Booking(
                    id = "BK-${Clock.System.now().toEpochMilliseconds()}",
                    roomId = room.id,
                    roomName = room.name,
                    buildingName = "GKU 2",
                    startTime = startInstant.toEpochMilliseconds(),
                    endTime = endInstant.toEpochMilliseconds(),
                    status = BookingStatus.PENDING,
                    subject = currentState.purpose
                )
                
                val result = bookingRepository.addBooking(newBooking)
                if (result.isSuccess) {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Format tanggal atau waktu tidak valid") }
            }
        }
    }

    fun resetState() {
        _state.value = BookingFormState()
    }
}

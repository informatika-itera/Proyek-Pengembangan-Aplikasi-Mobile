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
            _state.update { it.copy(isLoading = true) }
            try {
                val newBooking = Booking(
                    id = "BK-${Clock.System.now().toEpochMilliseconds()}",
                    roomId = room.id,
                    roomName = room.name,
                    buildingName = "GKU 2", // Simplified for now
                    startTime = Clock.System.now().toEpochMilliseconds(), // Simplified
                    endTime = Clock.System.now().toEpochMilliseconds() + 7200000,
                    status = BookingStatus.PENDING,
                    subject = currentState.purpose
                )
                bookingRepository.addBooking(newBooking)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun resetState() {
        _state.value = BookingFormState()
    }
}

package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.repository.BookingRepository
import com.example.Roomie.domain.repository.FacilityRepository
import com.example.Roomie.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

data class BookingFormState(
    val rooms: List<Room> = emptyList(),
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val purpose: String = "",
    val attendees: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val hasPendingBooking: Boolean = false,
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
    private val facilityRepository: FacilityRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BookingFormState())
    val state = _state.asStateFlow()

    fun loadRooms(roomIds: List<String>) {
        viewModelScope.launch {
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
                val nowMs = Clock.System.now().toEpochMilliseconds()

                // --- SMART VALIDATION ---
                
                if (startMs < nowMs) {
                    _state.update { it.copy(isLoading = false, error = "Waktu peminjaman sudah lewat!") }
                    return@launch
                }

                if (startLt.hour < 6 || endLt.hour > 22 || (endLt.hour == 22 && endLt.minute > 0)) {
                    _state.update { it.copy(isLoading = false, error = "Peminjaman hanya tersedia pukul 06:00 - 22:00 WIB") }
                    return@launch
                }

                val durationHours = (endMs - startMs) / 3600000.0
                if (durationHours <= 0) {
                    _state.update { it.copy(isLoading = false, error = "Waktu selesai harus setelah waktu mulai") }
                    return@launch
                }

                val daysFromNow = (startMs - nowMs) / 86400000
                if (daysFromNow > 30) {
                    _state.update { it.copy(isLoading = false, error = "Booking maksimal dilakukan 30 hari sebelumnya") }
                    return@launch
                }

                // --- EXECUTION ---
                val currentUser = getCurrentUserUseCase().first()
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
                        subject = currentState.purpose,
                        userId = currentUser?.id
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

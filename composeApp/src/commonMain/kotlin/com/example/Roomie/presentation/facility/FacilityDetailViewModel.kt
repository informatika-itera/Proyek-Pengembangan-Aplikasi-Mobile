package com.example.Roomie.presentation.facility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.usecase.GetRoomByIdUseCase
import com.example.Roomie.domain.usecase.UpdateRoomStatusUseCase
import com.example.Roomie.domain.repository.BookingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class FacilityDetailViewModel(
    private val getRoomByIdUseCase: GetRoomByIdUseCase,
    private val updateRoomStatusUseCase: UpdateRoomStatusUseCase,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    
    fun getRoom(roomId: String, date: String? = null): StateFlow<Room?> {
        val roomFlow = getRoomByIdUseCase(roomId)
        val bookingsFlow = bookingRepository.getAllBookings()
        
        return roomFlow.combine(bookingsFlow) { room, bookings ->
            if (room == null) return@combine null
            if (date == null) return@combine room
            
            // Jika status database adalah MAINTENANCE atau BOOKED (Manual), gunakan itu
            if (room.status == RoomStatus.MAINTENANCE || room.status == RoomStatus.BOOKED) {
                return@combine room
            }
            
            val targetDate = date?.let { LocalDate.parse(it) }
            val isBookedBySchedule = bookings.any { booking ->
                val bookingDate = Instant.fromEpochMilliseconds(booking.startTime)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                booking.roomId == roomId && 
                booking.status == BookingStatus.APPROVED &&
                bookingDate == targetDate
            }
            
            if (isBookedBySchedule) {
                room.copy(status = RoomStatus.BOOKED)
            } else {
                room.copy(status = RoomStatus.AVAILABLE)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun updateStatus(roomId: String, status: RoomStatus, note: String?) {
        viewModelScope.launch {
            updateRoomStatusUseCase(
                roomId = roomId,
                status = status,
                borrowerName = if (status == RoomStatus.BOOKED) note else null,
                maintenanceDescription = if (status == RoomStatus.MAINTENANCE) note else null
            )
        }
    }
}

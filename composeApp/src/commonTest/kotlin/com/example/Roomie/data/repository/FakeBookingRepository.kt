package com.example.Roomie.data.repository

import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeBookingRepository : BookingRepository {
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    
    override fun getAllBookings(): Flow<List<Booking>> = _bookings.asStateFlow()

    override suspend fun addBooking(booking: Booking): Result<Unit> {
        _bookings.update { it + booking }
        return Result.success(Unit)
    }

    override suspend fun updateBookingStatus(id: String, status: BookingStatus): Result<Unit> {
        _bookings.update { current ->
            current.map { if (it.id == id) it.copy(status = status) else it }
        }
        return Result.success(Unit)
    }

    override suspend fun deleteBooking(id: String): Result<Unit> {
        _bookings.update { current ->
            current.filter { it.id != id }
        }
        return Result.success(Unit)
    }

    override suspend fun checkConflict(roomId: String, startTime: Long, endTime: Long): Boolean {
        return _bookings.value.any { 
            it.roomId == roomId && 
            it.status == BookingStatus.APPROVED &&
            it.startTime < endTime && startTime < it.endTime
        }
    }

    override suspend fun getServerTime(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    }

    override suspend fun cleanupExpiredBookings(serverTime: Long): Int {
        val all = _bookings.value
        val expired = all.filter { it.status == BookingStatus.APPROVED && it.endTime < serverTime }
        _bookings.update { current ->
            current.map { 
                if (it.status == BookingStatus.APPROVED && it.endTime < serverTime) 
                    it.copy(status = BookingStatus.COMPLETED) 
                else it 
            }
        }
        return expired.size
    }
    
    fun setBookings(bookings: List<Booking>) {
        _bookings.value = bookings
    }
}

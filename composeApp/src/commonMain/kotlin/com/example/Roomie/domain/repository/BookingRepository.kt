package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getAllBookings(): Flow<List<Booking>>
    suspend fun addBooking(booking: Booking): Result<Unit>
    suspend fun updateBookingStatus(id: String, status: BookingStatus): Result<Unit>
    suspend fun deleteBooking(id: String): Result<Unit>
    suspend fun checkConflict(roomId: String, startTime: Long, endTime: Long): Boolean
}

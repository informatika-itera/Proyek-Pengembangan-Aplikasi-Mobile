package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getAllBookings(): Flow<List<Booking>>
    suspend fun addBooking(booking: Booking)
    suspend fun updateBookingStatus(id: String, status: BookingStatus)
    suspend fun deleteBooking(id: String)
}

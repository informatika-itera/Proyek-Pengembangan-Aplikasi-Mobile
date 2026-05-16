package com.example.Roomie.domain.repository

import com.example.Roomie.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getAllBookings(): Flow<List<Booking>>
    suspend fun addBooking(booking: Booking)
    suspend fun deleteBooking(id: String)
}

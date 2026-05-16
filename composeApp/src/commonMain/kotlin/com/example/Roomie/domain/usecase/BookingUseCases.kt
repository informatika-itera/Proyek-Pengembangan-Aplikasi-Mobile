package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow

class GetAllBookingsUseCase(private val repository: BookingRepository) {
    operator fun invoke(): Flow<List<Booking>> = repository.getAllBookings()
}

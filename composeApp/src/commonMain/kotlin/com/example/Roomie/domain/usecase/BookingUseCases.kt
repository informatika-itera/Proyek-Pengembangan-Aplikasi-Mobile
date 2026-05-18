package com.example.Roomie.domain.usecase

import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import com.example.Roomie.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow

class GetAllBookingsUseCase(private val repository: BookingRepository) {
    operator fun invoke(): Flow<List<Booking>> = repository.getAllBookings()
}

class CancelBookingUseCase(private val repository: BookingRepository) {
    suspend operator fun invoke(id: String) = repository.updateBookingStatus(id, BookingStatus.CANCELLED)
}

class CheckBookingConflictUseCase(private val repository: BookingRepository) {
    suspend operator fun invoke(roomId: String, startTime: Long, endTime: Long) = 
        repository.checkConflict(roomId, startTime, endTime)
}

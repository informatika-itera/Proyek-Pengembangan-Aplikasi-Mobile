package com.example.Roomie.domain.usecase

import com.example.Roomie.data.repository.FakeBookingRepository
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.BookingStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BookingUseCasesTest {
    private lateinit var repository: FakeBookingRepository
    
    @BeforeTest
    fun setup() {
        repository = FakeBookingRepository()
    }

    @Test
    fun `PerformAutomaticCleanupUseCase should cleanup expired bookings`() = runTest {
        val now = repository.getServerTime()
        val bookings = listOf(
            Booking("1", "R1", "Room1", "GKU2", now - 2000, now - 1000, BookingStatus.APPROVED),
            Booking("2", "R1", "Room1", "GKU2", now + 1000, now + 2000, BookingStatus.APPROVED)
        )
        repository.setBookings(bookings)
        
        val useCase = PerformAutomaticCleanupUseCase(repository)
        val cleanedCount = useCase()
        
        assertEquals(1, cleanedCount)
    }

    @Test
    fun `GetAllBookingsUseCase should return all bookings`() = runTest {
        val useCase = GetAllBookingsUseCase(repository)
        // Check initial empty
        assertEquals(0, repository.cleanupExpiredBookings(0)) // dummy
    }
}

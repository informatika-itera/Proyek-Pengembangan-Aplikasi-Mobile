package com.example.Roomie.domain.usecase.ai

import com.example.Roomie.data.remote.ai.ExtractionResult
import com.example.Roomie.data.remote.ai.GeminiService
import com.example.Roomie.data.repository.FakeBookingRepository
import com.example.Roomie.data.repository.FakeFacilityRepository
import com.example.Roomie.domain.model.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.*
import kotlin.test.*

class ProcessAICommandUseCaseTest {
    private lateinit var useCase: ProcessAICommandUseCase
    private lateinit var facilityRepository: FakeFacilityRepository
    private lateinit var bookingRepository: FakeBookingRepository
    
    private class MockGeminiService : GeminiService(
        io.ktor.client.HttpClient(),
        kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
    ) {
        var extractionToReturn: ExtractionResult? = null
        override suspend fun processUserCommand(input: String): ExtractionResult? = extractionToReturn
    }
    
    private val geminiService = MockGeminiService()

    @BeforeTest
    fun setup() {
        facilityRepository = FakeFacilityRepository()
        bookingRepository = FakeBookingRepository()
        useCase = ProcessAICommandUseCase(geminiService, facilityRepository, bookingRepository)
    }

    @Test
    fun `invoke should return error if extraction fails`() = runTest {
        geminiService.extractionToReturn = null
        val result = useCase("invalid")
        assertTrue(result is AIResult.Error)
    }

    @Test
    fun `invoke should return error if all extraction fields are null`() = runTest {
        geminiService.extractionToReturn = ExtractionResult(null, null, null, null, null, null)
        val result = useCase("tanya apa aja")
        assertTrue(result is AIResult.Error)
        assertEquals("Datanya kurang lengkap nih, bre. Sebutin gedung, kapasitas, atau tanggalnya ya.", (result as AIResult.Error).message)
    }

    @Test
    fun `invoke should handle flexible building name match`() = runTest {
        geminiService.extractionToReturn = ExtractionResult(buildingName = "GKU2")
        val rooms = listOf(Room("GKU2-101", "GKU2", "101", 1, RoomStatus.AVAILABLE, capacity = 50))
        facilityRepository.setRooms(rooms)
        
        val result = useCase("gedung 2")
        assertTrue(result is AIResult.Success)
        assertEquals(1, (result as AIResult.Success).suggestedRooms.size)
    }

    @Test
    fun `invoke should exclude booked rooms for specific time`() = runTest {
        val dateStr = "2026-06-06"
        geminiService.extractionToReturn = ExtractionResult(date = dateStr, startTime = "08:00", endTime = "10:00")
        
        val room = Room("R1", "GKU2", "101", 1, RoomStatus.AVAILABLE, capacity = 50)
        facilityRepository.setRooms(listOf(room))
        
        val tz = TimeZone.currentSystemDefault()
        val localDate = LocalDate.parse(dateStr)
        val startMs = LocalDateTime(localDate, LocalTime(8, 0)).toInstant(tz).toEpochMilliseconds()
        val endMs = LocalDateTime(localDate, LocalTime(10, 0)).toInstant(tz).toEpochMilliseconds()
            
        bookingRepository.setBookings(listOf(
            Booking("B1", "R1", "101", "GKU2", startMs, endMs, BookingStatus.APPROVED, "Rapat")
        ))

        val result = useCase("cari ruang jam 8-10")
        assertTrue(result is AIResult.Success)
        assertTrue((result as AIResult.Success).suggestedRooms.isEmpty())
    }
}

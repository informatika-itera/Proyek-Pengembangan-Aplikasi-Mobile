package com.example.Roomie.domain.usecase.ai

import com.example.Roomie.data.remote.ai.ExtractionResult
import com.example.Roomie.data.remote.ai.GeminiService
import com.example.Roomie.data.repository.FakeBookingRepository
import com.example.Roomie.data.repository.FakeFacilityRepository
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProcessAICommandUseCaseTest {
    private lateinit var useCase: ProcessAICommandUseCase
    private lateinit var facilityRepository: FakeFacilityRepository
    private lateinit var bookingRepository: FakeBookingRepository
    
    private class MockGeminiService : GeminiService(
        io.ktor.client.HttpClient(),
        kotlinx.serialization.json.Json {}
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
    fun `invoke should return success with suggested rooms`() = runTest {
        geminiService.extractionToReturn = ExtractionResult(
            capacity = 40,
            buildingName = "GKU2"
        )
        
        val rooms = listOf(
            Room("1", "GKU2", "101", 1, RoomStatus.AVAILABLE, capacity = 50),
            Room("2", "GKU1", "201", 1, RoomStatus.AVAILABLE, capacity = 10)
        )
        facilityRepository.setRooms(rooms)
        
        val result = useCase("cari ruang GKU2 40 orang")
        
        assertTrue(result is AIResult.Success)
        assertEquals(1, result.suggestedRooms.size)
        assertEquals("1", result.suggestedRooms[0].id)
    }
}

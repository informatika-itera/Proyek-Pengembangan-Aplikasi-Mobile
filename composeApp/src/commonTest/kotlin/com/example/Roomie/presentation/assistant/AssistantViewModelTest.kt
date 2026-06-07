package com.example.Roomie.presentation.assistant

import com.example.Roomie.data.remote.ai.ExtractionResult
import com.example.Roomie.domain.model.Booking
import com.example.Roomie.domain.model.Building
import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.usecase.ai.AIResult
import com.example.Roomie.domain.usecase.ai.ProcessAICommandUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AssistantViewModel
    
    private class FakeProcessAICommandUseCase : ProcessAICommandUseCase(
        geminiService = object : com.example.Roomie.data.remote.ai.GeminiService(
            io.ktor.client.HttpClient(),
            kotlinx.serialization.json.Json {}
        ) {},
        facilityRepository = object : com.example.Roomie.domain.repository.FacilityRepository {
            override fun getBuildings() = flowOf(emptyList<Building>())
            override fun getRoomsByFloor(b: String, f: Int) = flowOf(emptyList<Room>())
            override fun getRoomsByBuilding(b: String) = flowOf(emptyList<Room>())
            override fun searchRooms(q: String) = flowOf(emptyList<Room>())
            override fun searchRoomsFiltered(q: String, min: Int, max: Int) = flowOf(emptyList<Room>())
            override fun getRoomById(id: String) = flowOf(null)
            override suspend fun updateRoomStatus(id: String, s: com.example.Roomie.domain.model.RoomStatus, b: String?, m: String?) {}
        },
        bookingRepository = object : com.example.Roomie.domain.repository.BookingRepository {
            override fun getAllBookings() = flowOf(emptyList<Booking>())
            override suspend fun addBooking(b: Booking) = Result.success(Unit)
            override suspend fun updateBookingStatus(id: String, s: com.example.Roomie.domain.model.BookingStatus) = Result.success(Unit)
            override suspend fun deleteBooking(id: String) = Result.success(Unit)
            override suspend fun checkConflict(id: String, s: Long, e: Long) = false
            override suspend fun getServerTime() = 0L
            override suspend fun cleanupExpiredBookings(t: Long) = 0
        }
    ) {
        var resultToReturn: AIResult = AIResult.Error("Fail")
        override suspend fun invoke(userInput: String): AIResult = resultToReturn
    }

    private lateinit var fakeUseCase: FakeProcessAICommandUseCase

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeUseCase = FakeProcessAICommandUseCase()
        viewModel = AssistantViewModel(fakeUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have welcome message`() {
        val state = viewModel.uiState.value
        assertEquals(1, state.messages.size)
        assertFalse(state.messages[0].isUser)
    }

    @Test
    fun `sendCommand should add user message and then AI response`() = runTest {
        val query = "Cari ruang"
        fakeUseCase.resultToReturn = AIResult.Success(
            extractedData = ExtractionResult(capacity = 10),
            suggestedRooms = emptyList()
        )
        
        viewModel.onQueryChange(query)
        viewModel.sendCommand()
        
        assertEquals(2, viewModel.uiState.value.messages.size)
        assertTrue(viewModel.uiState.value.messages.last().isUser)
        assertTrue(viewModel.uiState.value.isLoading)

        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(3, viewModel.uiState.value.messages.size)
        assertFalse(viewModel.uiState.value.messages.last().isUser)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}

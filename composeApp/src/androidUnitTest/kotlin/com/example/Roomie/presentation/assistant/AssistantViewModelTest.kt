package com.example.Roomie.presentation.assistant

import com.example.Roomie.domain.model.Room
import com.example.Roomie.domain.model.RoomStatus
import com.example.Roomie.domain.usecase.ai.AIResult
import com.example.Roomie.domain.usecase.ai.ProcessAICommandUseCase
import com.example.Roomie.data.remote.ai.ExtractionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import io.mockk.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: AssistantViewModel
    private val mockUseCase = mockk<ProcessAICommandUseCase>()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AssistantViewModel(mockUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendCommand Success - coverage logic`() = runTest {
        val rooms = listOf(Room("R1", "G1", "101", 1, RoomStatus.AVAILABLE))
        val extraction = ExtractionResult(buildingName = "G1")
        coEvery { mockUseCase(any()) } returns AIResult.Success(extraction, rooms)
        
        viewModel.onQueryChange("Cari ruang")
        viewModel.sendCommand()
        
        assertEquals(1, viewModel.uiState.value.suggestedRooms.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `sendCommand Error - coverage logic`() = runTest {
        coEvery { mockUseCase(any()) } returns AIResult.Error("Gagal")
        
        viewModel.onQueryChange("Error test")
        viewModel.sendCommand()
        
        assertTrue(viewModel.uiState.value.messages.any { it.text == "Gagal" })
    }

    @Test
    fun `resetAssistant - coverage logic`() {
        viewModel.onQueryChange("test")
        viewModel.resetAssistant()
        assertEquals("", viewModel.uiState.value.currentQuery)
    }
}

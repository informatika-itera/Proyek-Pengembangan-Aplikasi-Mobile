package com.example.mapenumkm.presentation.screens.ai

import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.Transaction
import com.example.mapenumkm.domain.repository.AIRepository
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.repository.TransactionRepository
import com.example.mapenumkm.domain.usecase.GenerateIdeasUseCase
import com.example.mapenumkm.domain.usecase.ImproveWritingUseCase
import com.example.mapenumkm.domain.usecase.SummarizeNoteUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class AIAssistantViewModelTest {

    private val aiRepository: AIRepository = mockk()
    private val summarizeUseCase: SummarizeNoteUseCase = mockk()
    private val improveWritingUseCase: ImproveWritingUseCase = mockk()
    private val generateIdeasUseCase: GenerateIdeasUseCase = mockk()
    private val transactionRepository: TransactionRepository = mockk()
    private val noteRepository: NoteRepository = mockk()
    
    private lateinit var viewModel: AIAssistantViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Setup initial data for init {} block
        coEvery { transactionRepository.getAllTransactions() } returns flowOf(emptyList<com.example.mapenumkm.domain.model.Transaction>())
        coEvery { noteRepository.getAllNotes() } returns flowOf(emptyList<com.example.mapenumkm.domain.model.Note>())
        
        viewModel = AIAssistantViewModel(
            aiRepository,
            summarizeUseCase,
            improveWritingUseCase,
            generateIdeasUseCase,
            transactionRepository,
            noteRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial summary should be generated`() = runTest {
        // Assertions for initial state
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(2, viewModel.uiState.value.messages.size)
        assertTrue(viewModel.uiState.value.messages[1].isSummary)
    }

    @Test
    fun `onInputTextChange updates inputText`() {
        viewModel.onInputTextChange("Hello")
        assertEquals("Hello", viewModel.uiState.value.inputText)
    }

    @Test
    fun `sendMessage success updates messages`() = runTest {
        val responseText = "AI Response"
        coEvery { aiRepository.businessChat(any<String>(), any<String>()) } returns Result.success(responseText)
        coEvery { transactionRepository.getAllTransactions() } returns flowOf(emptyList<com.example.mapenumkm.domain.model.Transaction>())
        coEvery { noteRepository.getAllNotes() } returns flowOf(emptyList<com.example.mapenumkm.domain.model.Note>())

        viewModel.onInputTextChange("User Message")
        viewModel.sendMessage()
        advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.messages.size)
        assertEquals(responseText, viewModel.uiState.value.messages.last().text)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `sendMessage failure updates error`() = runTest {
        val errorMsg = "AI Error"
        coEvery { aiRepository.businessChat(any<String>(), any<String>()) } returns Result.failure(Exception(errorMsg))
        coEvery { transactionRepository.getAllTransactions() } returns flowOf(emptyList<com.example.mapenumkm.domain.model.Transaction>())
        coEvery { noteRepository.getAllNotes() } returns flowOf(emptyList<com.example.mapenumkm.domain.model.Note>())

        viewModel.onInputTextChange("User Message")
        viewModel.sendMessage()
        advanceUntilIdle()

        assertEquals(errorMsg, viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onQuickAction sends message`() = runTest {
        val action = "Summarize"
        coEvery { aiRepository.businessChat(any<String>(), any<String>()) } returns Result.success("OK")
        
        viewModel.onQuickAction(action)
        advanceUntilIdle()
        
        coVerify { aiRepository.businessChat(eq(action), any<String>()) }
    }
}

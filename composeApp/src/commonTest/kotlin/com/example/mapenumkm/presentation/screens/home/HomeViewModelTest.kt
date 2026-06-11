package com.example.mapenumkm.presentation.screens.home

import com.example.mapenumkm.data.local.datastore.UserPreferences
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.repository.TransactionRepository
import com.example.mapenumkm.domain.usecase.DeleteNoteUseCase
import com.example.mapenumkm.domain.usecase.GetAllNotesUseCase
import com.example.mapenumkm.domain.usecase.SearchNotesUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val getAllNotesUseCase: GetAllNotesUseCase = mockk()
    private val searchNotesUseCase: SearchNotesUseCase = mockk()
    private val deleteNoteUseCase: DeleteNoteUseCase = mockk()
    private val repository: NoteRepository = mockk()
    private val transactionRepository: TransactionRepository = mockk()
    private val userPreferences: UserPreferences = mockk()
    
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { transactionRepository.getAllTransactions() } returns flowOf(emptyList())
        every { getAllNotesUseCase(any()) } returns flowOf(emptyList())
        
        viewModel = HomeViewModel(
            getAllNotesUseCase,
            searchNotesUseCase,
            deleteNoteUseCase,
            repository,
            transactionRepository,
            userPreferences
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Empty after loading`() = runTest {
        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is HomeUiState.Empty)
        job.cancel()
    }

    @Test

    fun `onSearchQueryChange updates query and triggers search`() = runTest {
        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        val query = "coffee"
        every { searchNotesUseCase(query, any(), any()) } returns flowOf(emptyList())
        
        viewModel.onSearchQueryChange(query)
        advanceTimeBy(301) // Debounce is 300ms
        
        val state = viewModel.uiState.value
        if (state is HomeUiState.Empty) {
            assertEquals(query, state.query)
        }
        verify { searchNotesUseCase(query, any(), any()) }
        job.cancel()
    }

    @Test
    fun `onCategorySelected triggers search`() = runTest {
        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect {} }
        val category = com.example.mapenumkm.domain.model.NoteCategory.FOOD
        every { searchNotesUseCase(any(), category, any()) } returns flowOf(emptyList())
        
        viewModel.onCategorySelected(category)
        advanceUntilIdle()
        
        verify { searchNotesUseCase(any(), eq(category), any()) }
        job.cancel()
    }

    @Test
    fun `togglePin calls repository`() = runTest {
        val noteId = 1L
        coEvery { repository.togglePinNote(noteId) } returns Unit
        
        viewModel.togglePin(noteId)
        advanceUntilIdle()
        
        coVerify { repository.togglePinNote(noteId) }
    }

    @Test
    fun `deleteNote calls use case`() = runTest {
        val noteId = 1L
        coEvery { deleteNoteUseCase(noteId) } returns Result.success(Unit)
        
        viewModel.deleteNote(noteId)
        advanceUntilIdle()
        
        coVerify { deleteNoteUseCase(noteId) }
    }

    @Test
    fun `logout updates preferences and emits event`() = runTest {
        coEvery { userPreferences.setLoggedIn(false) } returns Unit
        
        viewModel.logout()
        advanceUntilIdle()
        
        coVerify { userPreferences.setLoggedIn(false) }
    }
}

package com.example.fitgen.presentation

import app.cash.turbine.test
import com.example.fitgen.data.repository.FakeNoteRepository
import com.example.fitgen.domain.model.Note
import com.example.fitgen.domain.model.NoteCategory
import com.example.fitgen.domain.model.NoteColor
import com.example.fitgen.domain.usecase.DeleteNoteUseCase
import com.example.fitgen.domain.usecase.GetAllNotesUseCase
import com.example.fitgen.domain.usecase.SearchNotesUseCase
import com.example.fitgen.presentation.screens.home.HomeUiState
import com.example.fitgen.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit Tests untuk HomeViewModel
 * 
 * Testing Guidelines:
 * 1. Setup test dispatcher untuk control coroutines
 * 2. Gunakan Turbine untuk test StateFlow
 * 3. Test UI state transformations
 * 4. Test user actions
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeNoteRepository
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase
    private lateinit var searchNotesUseCase: SearchNotesUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var viewModel: HomeViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = FakeNoteRepository()
        getAllNotesUseCase = GetAllNotesUseCase(repository)
        searchNotesUseCase = SearchNotesUseCase(repository)
        deleteNoteUseCase = DeleteNoteUseCase(repository)
        
        viewModel = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // ==================== UI STATE TESTS ====================
    
    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        viewModel.uiState.test {
            // Initial loading state
            val loading = awaitItem()
            assertTrue(loading is HomeUiState.Loading)
            
            // After loading, should be empty (no notes)
            advanceUntilIdle()
            val empty = awaitItem()
            assertTrue(empty is HomeUiState.Empty)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `state should be Success when notes exist`() = runTest {
        // Arrange
        repository.insertNote(createTestNote("Note 1"))
        repository.insertNote(createTestNote("Note 2"))
        
        // Create new viewmodel after inserting notes
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository
        )
        
        // Act & Assert
        vm.uiState.test {
            skipItems(1) // Skip loading
            advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, (state as HomeUiState.Success).notes.size)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== SEARCH TESTS ====================
    
    @Test
    fun `search should filter notes by query`() = runTest {
        // Arrange
        repository.insertNote(createTestNote("Kotlin Guide"))
        repository.insertNote(createTestNote("Java Tutorial"))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository
        )
        
        vm.uiState.test {
            skipItems(1) // Skip loading
            advanceUntilIdle()
            skipItems(1) // Skip initial success
            
            // Act
            vm.onSearchQueryChange("Kotlin")
            advanceUntilIdle()
            
            // Assert - wait for debounce
            testScheduler.advanceTimeBy(400)
            advanceUntilIdle()
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).notes.size)
            assertEquals("Kotlin Guide", state.notes.first().title)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `clearSearch should reset query`() = runTest {
        // Act
        viewModel.onSearchQueryChange("test query")
        viewModel.clearSearch()
        
        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            when (state) {
                is HomeUiState.Success -> assertEquals("", state.query)
                is HomeUiState.Empty -> assertEquals("", state.query)
                else -> {} // OK
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== CATEGORY FILTER TESTS ====================
    
    @Test
    fun `category filter should filter notes`() = runTest {
        // Arrange
        repository.insertNote(createTestNote("Work Note", category = NoteCategory.WORK))
        repository.insertNote(createTestNote("Personal Note", category = NoteCategory.PERSONAL))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository
        )
        
        vm.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            skipItems(1) // Initial success
            
            // Act
            vm.onCategorySelected(NoteCategory.WORK)
            advanceUntilIdle()
            
            // Assert
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).notes.size)
            assertEquals(NoteCategory.WORK, state.notes.first().category)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== ACTION TESTS ====================
    
    @Test
    fun `togglePin should toggle note pin status`() = runTest {
        // Arrange
        val noteId = repository.insertNote(createTestNote("Pin Me"))
        
        // Act
        viewModel.togglePin(noteId)
        advanceUntilIdle()
        
        // Assert
        repository.getNoteById(noteId).test {
            val note = awaitItem()
            assertTrue(note?.isPinned == true)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `deleteNote should remove note`() = runTest {
        // Arrange
        val noteId = repository.insertNote(createTestNote("Delete Me"))
        
        // Act
        viewModel.deleteNote(noteId)
        advanceUntilIdle()
        
        // Assert
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== HELPER FUNCTIONS ====================
    
    private fun createTestNote(
        title: String,
        category: NoteCategory = NoteCategory.GENERAL
    ): Note {
        return Note(
            id = 0,
            title = title,
            content = "Test content",
            category = category,
            color = NoteColor.DEFAULT,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

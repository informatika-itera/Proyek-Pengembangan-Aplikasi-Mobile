package com.example.hujjah.presentation.screens.notes

import app.cash.turbine.test
import com.example.hujjah.data.repository.FakeNoteRepository
import com.example.hujjah.domain.model.Note
import com.example.hujjah.domain.usecase.DeleteNoteUseCase
import com.example.hujjah.domain.usecase.GetAllNotesUseCase
import com.example.hujjah.domain.usecase.SearchNotesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeNoteRepository
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase
    private lateinit var searchNotesUseCase: SearchNotesUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var viewModel: NotesViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        getAllNotesUseCase = GetAllNotesUseCase(repository)
        searchNotesUseCase = SearchNotesUseCase(repository)
        deleteNoteUseCase = DeleteNoteUseCase(repository)
        
        viewModel = NotesViewModel(
            repository = repository,
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        viewModel.viewModelScope.coroutineContext.cancelChildren()
        Dispatchers.resetMain()
    }

    @Test
    fun `initialState should load notes and extract distinct categories`() = runTest(testDispatcher) {
        // Arrange
        repository.insertNote(Note(title = "Note 1", content = "Content 1", category = "Ibadah"))
        repository.insertNote(Note(title = "Note 2", content = "Content 2", category = "Hadits"))
        repository.insertNote(Note(title = "Note 3", content = "Content 3", category = "Ibadah"))
        
        testScheduler.advanceUntilIdle()

        // Act & Assert
        viewModel.uiState.test {
            val finalState = awaitItem()
            assertEquals(3, finalState.notes.size)
            assertEquals(2, finalState.categories.size)
            assertTrue(finalState.categories.contains("Ibadah"))
            assertTrue(finalState.categories.contains("Hadits"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChanged should trigger search and update notes list`() = runTest(testDispatcher) {
        // Arrange
        repository.insertNote(Note(title = "Kotlin Guide", content = "Learn kotlin", category = "Tech"))
        repository.insertNote(Note(title = "Java Book", content = "Learn java", category = "Tech"))
        testScheduler.advanceUntilIdle()

        // Act
        viewModel.onSearchQueryChanged("Kotlin")
        testScheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Kotlin", state.searchQuery)
            assertEquals(1, state.notes.size)
            assertEquals("Kotlin Guide", state.notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onCategorySelected should filter notes by category`() = runTest(testDispatcher) {
        // Arrange
        repository.insertNote(Note(title = "Note 1", content = "Content 1", category = "Spiritual"))
        repository.insertNote(Note(title = "Note 2", content = "Content 2", category = "Work"))
        testScheduler.advanceUntilIdle()

        // Act
        viewModel.onCategorySelected("Spiritual")
        testScheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Spiritual", state.selectedCategory)
            assertEquals(1, state.notes.size)
            assertEquals("Note 1", state.notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `togglePin should call repository and emit event`() = runTest(testDispatcher) {
        // Arrange
        val noteId = repository.insertNote(Note(title = "Pin Me", content = "Content"))
        testScheduler.advanceUntilIdle()

        // Act & Assert
        viewModel.events.test {
            viewModel.togglePin(noteId)
            testScheduler.advanceUntilIdle()
            
            assertEquals(NotesEvent.NotePinnedToggled, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        // Verify it is pinned in repository
        repository.getNoteById(noteId).test {
            val note = awaitItem()
            assertTrue(note?.isPinned == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote should call use case and emit delete event`() = runTest(testDispatcher) {
        // Arrange
        val noteId = repository.insertNote(Note(title = "To Delete", content = "Content"))
        testScheduler.advanceUntilIdle()

        // Act & Assert
        viewModel.events.test {
            viewModel.deleteNote(noteId)
            testScheduler.advanceUntilIdle()
            
            assertEquals(NotesEvent.NoteDeleted, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

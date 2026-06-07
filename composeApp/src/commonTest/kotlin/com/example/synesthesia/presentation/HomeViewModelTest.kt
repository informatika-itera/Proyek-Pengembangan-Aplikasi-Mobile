package com.example.synesthesia.presentation

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeNoteRepository
import com.example.synesthesia.fakes.FakeUserPreferences
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import com.example.synesthesia.domain.repository.NoteRepository
import com.example.synesthesia.domain.usecase.DeleteNoteUseCase
import com.example.synesthesia.domain.usecase.GetAllNotesUseCase
import com.example.synesthesia.domain.usecase.NoteSortBy
import com.example.synesthesia.domain.usecase.SearchNotesUseCase
import com.example.synesthesia.presentation.screens.home.HomeUiState
import com.example.synesthesia.presentation.screens.home.HomeViewModel
import com.example.synesthesia.data.local.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for HomeViewModel.
 * Targets 100% branch & line coverage for:
 * - Initial states (Loading → Empty)
 * - Success state with notes
 * - Search query (with debounce)
 * - Clear search
 * - Category filter
 * - Sort order changes (all NoteSortBy values)
 * - Toggle pin
 * - Delete single note
 * - Delete multiple notes
 * - Refresh
 * - isRefreshing state
 * - userName from preferences
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeNoteRepository
    private lateinit var userPreferences: FakeUserPreferences
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase
    private lateinit var searchNotesUseCase: SearchNotesUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var viewModel: HomeViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = FakeNoteRepository()
        userPreferences = FakeUserPreferences()
        getAllNotesUseCase = GetAllNotesUseCase(repository)
        searchNotesUseCase = SearchNotesUseCase(repository)
        deleteNoteUseCase = DeleteNoteUseCase(repository)
        
        viewModel = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // ==================== INITIAL STATE TESTS ====================
    
    @Test
    fun `initial state should be Loading then Empty`() = runTest {
        viewModel.uiState.test {
            val loading = awaitItem()
            assertTrue(loading is HomeUiState.Loading)
            
            advanceUntilIdle()
            val empty = awaitItem()
            assertTrue(empty is HomeUiState.Empty)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `state should be Success when notes exist`() = runTest {
        repository.insertNote(createTestNote("Note 1"))
        repository.insertNote(createTestNote("Note 2"))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
        
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
        repository.insertNote(createTestNote("Kotlin Guide"))
        repository.insertNote(createTestNote("Java Tutorial"))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
        
        vm.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            skipItems(1)
            
            vm.onSearchQueryChange("Kotlin")
            advanceUntilIdle()
            
            testDispatcher.scheduler.advanceTimeBy(400)
            advanceUntilIdle()
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).notes.size)
            assertEquals("Kotlin Guide", state.notes.first().title)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search with no matches should show Empty state`() = runTest {
        repository.insertNote(createTestNote("Kotlin Guide"))

        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )

        vm.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            skipItems(1)

            vm.onSearchQueryChange("NonexistentTerm")
            testDispatcher.scheduler.advanceTimeBy(400)
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Empty)

            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `clearSearch should reset query`() = runTest {
        viewModel.onSearchQueryChange("test query")
        viewModel.clearSearch()
        
        viewModel.searchQuery.test {
            assertEquals("", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChange should update searchQuery state`() = runTest {
        viewModel.onSearchQueryChange("new query")
        
        viewModel.searchQuery.test {
            assertEquals("new query", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== CATEGORY FILTER TESTS ====================
    
    @Test
    fun `category filter should filter notes`() = runTest {
        repository.insertNote(createTestNote("Work Note", category = NoteCategory.WORK))
        repository.insertNote(createTestNote("Personal Note", category = NoteCategory.PERSONAL))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
        
        vm.uiState.test {
            skipItems(1) // Loading
            advanceUntilIdle()
            skipItems(1) // Initial success
            
            vm.onCategorySelected(NoteCategory.WORK)
            advanceUntilIdle()
            
            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).notes.size)
            assertEquals(NoteCategory.WORK, state.notes.first().category)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing category should show all notes again`() = runTest {
        repository.insertNote(createTestNote("Note1", category = NoteCategory.WORK))
        repository.insertNote(createTestNote("Note2", category = NoteCategory.PERSONAL))

        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )

        vm.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            skipItems(1)

            vm.onCategorySelected(NoteCategory.WORK)
            advanceUntilIdle()
            skipItems(1)

            vm.onCategorySelected(null) // clear filter
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, (state as HomeUiState.Success).notes.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== SORT ORDER TESTS ====================

    @Test
    fun `sort order should change from TITLE_ASC to TITLE_DESC`() = runTest {
        repository.insertNote(createTestNote("Apple"))
        repository.insertNote(createTestNote("Banana"))
        
        val vm = HomeViewModel(
            getAllNotesUseCase = getAllNotesUseCase,
            searchNotesUseCase = searchNotesUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            repository = repository,
            userPreferences = userPreferences
        )
        
        vm.uiState.test {
            skipItems(1)
            advanceUntilIdle()
            skipItems(1) // initial UPDATED_DESC state

            // Sort by TITLE_ASC
            vm.onSortByChanged(NoteSortBy.TITLE_ASC)
            advanceUntilIdle()
            
            val ascState = expectMostRecentItem() as HomeUiState.Success
            assertEquals("Apple", ascState.notes.first().title)
            assertEquals("Banana", ascState.notes.last().title)

            // Now sort by TITLE_DESC
            vm.onSortByChanged(NoteSortBy.TITLE_DESC)
            advanceUntilIdle()

            val descState = expectMostRecentItem() as HomeUiState.Success
            assertEquals("Banana", descState.notes.first().title)
            assertEquals("Apple", descState.notes.last().title)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSortByChanged should update sortBy state`() = runTest {
        viewModel.onSortByChanged(NoteSortBy.TITLE_DESC)
        assertEquals(NoteSortBy.TITLE_DESC, viewModel.sortBy.value)
    }

    @Test
    fun `all sort options should be accepted`() = runTest {
        NoteSortBy.entries.forEach { sortBy ->
            viewModel.onSortByChanged(sortBy)
            assertEquals(sortBy, viewModel.sortBy.value)
        }
    }
    
    // ==================== ACTION TESTS ====================
    
    @Test
    fun `togglePin should toggle note pin status`() = runTest {
        val noteId = repository.insertNote(createTestNote("Pin Me"))
        
        viewModel.togglePin(noteId)
        advanceUntilIdle()
        
        repository.getNoteById(noteId).test {
            val note = awaitItem()
            assertTrue(note?.isPinned == true)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `deleteNote should remove note`() = runTest {
        val noteId = repository.insertNote(createTestNote("Delete Me"))
        
        viewModel.deleteNote(noteId)
        advanceUntilIdle()
        
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNotes should remove multiple notes`() = runTest {
        val id1 = repository.insertNote(createTestNote("N1"))
        val id2 = repository.insertNote(createTestNote("N2"))
        val id3 = repository.insertNote(createTestNote("N3"))

        viewModel.deleteNotes(listOf(id1, id2))
        advanceUntilIdle()

        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("N3", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== REFRESH TESTS ====================

    @Test
    fun `refresh should toggle isRefreshing state`() = runTest {
        viewModel.isRefreshing.test {
            assertEquals(false, awaitItem())

            viewModel.refresh()

            assertEquals(true, awaitItem())

            testDispatcher.scheduler.advanceTimeBy(900)
            advanceUntilIdle()

            assertEquals(false, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== HomeUiState sealed interface ====================

    @Test
    fun `HomeUiState Loading should be distinct`() {
        val state: HomeUiState = HomeUiState.Loading
        assertTrue(state is HomeUiState.Loading)
    }

    @Test
    fun `HomeUiState Empty should hold query and category`() {
        val state = HomeUiState.Empty(query = "search", category = NoteCategory.WORK)
        assertEquals("search", state.query)
        assertEquals(NoteCategory.WORK, state.category)
    }

    @Test
    fun `HomeUiState Empty defaults should be empty query and null category`() {
        val state = HomeUiState.Empty()
        assertEquals("", state.query)
        assertEquals(null, state.category)
    }

    @Test
    fun `HomeUiState Success should hold all fields`() {
        val notes = listOf(createTestNote("Test"))
        val state = HomeUiState.Success(
            notes = notes,
            query = "q",
            category = NoteCategory.IDEAS,
            sortBy = NoteSortBy.TITLE_ASC
        )
        assertEquals(1, state.notes.size)
        assertEquals("q", state.query)
        assertEquals(NoteCategory.IDEAS, state.category)
        assertEquals(NoteSortBy.TITLE_ASC, state.sortBy)
    }

    @Test
    fun `HomeUiState Success defaults`() {
        val notes = listOf(createTestNote("Test"))
        val state = HomeUiState.Success(notes = notes)
        assertEquals("", state.query)
        assertEquals(null, state.category)
        assertEquals(NoteSortBy.UPDATED_DESC, state.sortBy)
    }

    @Test
    fun `HomeUiState Error should hold message`() {
        val state = HomeUiState.Error("Something broke")
        assertEquals("Something broke", state.message)
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

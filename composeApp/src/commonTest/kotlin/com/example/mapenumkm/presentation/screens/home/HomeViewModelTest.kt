package com.example.mapenumkm.presentation.screens.home

import app.cash.turbine.test
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.model.NoteColor
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.usecase.DeleteNoteUseCase
import com.example.mapenumkm.domain.usecase.GetAllNotesUseCase
import com.example.mapenumkm.domain.usecase.SearchNotesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeNoteRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        val getAllNotesUseCase = GetAllNotesUseCase(repository)
        val searchNotesUseCase = SearchNotesUseCase(repository)
        val deleteNoteUseCase = DeleteNoteUseCase(repository)
        
        viewModel = HomeViewModel(
            getAllNotesUseCase,
            searchNotesUseCase,
            deleteNoteUseCase,
            repository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Loading or Success empty`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertTrue(initialState is HomeUiState.Loading || initialState is HomeUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query change should eventually update state`() = runTest {
        repository.insertNote(createTestNote(title = "Target"))
        
        viewModel.uiState.test {
            // Skip initial Loading
            var state = awaitItem()
            if (state is HomeUiState.Loading) state = awaitItem()
            
            viewModel.onSearchQueryChange("Target")
            
            // Advance time for debounce (300ms)
            advanceTimeBy(400)
            
            state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(1, (state as HomeUiState.Success).notes.size)
            assertEquals("Target", state.notes[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dashboard statistics should be calculated correctly`() = runTest {
        repository.insertNote(createTestNote(title = "Product 1", price = 10.0, stock = 10)) // Value: 100
        repository.insertNote(createTestNote(title = "Product 2", price = 50.0, stock = 2))  // Value: 100, Low stock
        
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is HomeUiState.Loading) state = awaitItem()
            
            assertTrue(state is HomeUiState.Success)
            assertEquals(2, state.totalProducts)
            assertEquals(200.0, state.totalStockValue)
            assertEquals(1, state.lowStockCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty search result should show Empty state`() = runTest {
        repository.insertNote(createTestNote(title = "Existing"))
        
        viewModel.uiState.test {
            var state = awaitItem()
            if (state is HomeUiState.Loading) state = awaitItem()
            
            viewModel.onSearchQueryChange("NonExistent")
            advanceTimeBy(400)
            
            state = awaitItem()
            assertTrue(state is HomeUiState.Empty)
            assertEquals("NonExistent", (state as HomeUiState.Empty).query)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestNote(
        id: Long = 0,
        title: String = "Test",
        content: String = "Content",
        price: Double = 0.0,
        stock: Int = 0,
        category: NoteCategory = NoteCategory.FOOD,
        isPinned: Boolean = false
    ): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            price = price,
            stock = stock,
            category = category,
            color = NoteColor.DEFAULT,
            isPinned = isPinned,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

class FakeNoteRepository : NoteRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L
    
    override fun getAllNotes(): Flow<List<Note>> = notes
    override fun getPinnedNotes(): Flow<List<Note>> = notes.map { it.filter { n -> n.isPinned } }
    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> = notes.map { it.filter { n -> n.category == category } }
    override fun searchNotes(query: String): Flow<List<Note>> = notes.map { it.filter { n -> n.title.contains(query, true) || n.content.contains(query, true) } }
    override fun getNoteById(id: Long): Flow<Note?> = notes.map { it.find { n -> n.id == id } }
    override suspend fun insertNote(note: Note): Long {
        val id = if (note.id == 0L) nextId++ else note.id
        notes.update { it + note.copy(id = id) }
        return id
    }
    override suspend fun updateNote(note: Note) {
        notes.update { it.map { n -> if (n.id == note.id) note else n } }
    }
    override suspend fun deleteNote(id: Long) {
        notes.update { it.filter { n -> n.id != id } }
    }
    override suspend fun togglePinNote(id: Long) {
        notes.update { it.map { n -> if (n.id == id) n.copy(isPinned = !n.isPinned) else n } }
    }
    override suspend fun deleteNotes(ids: List<Long>) {
        notes.update { it.filter { n -> n.id !in ids } }
    }
}

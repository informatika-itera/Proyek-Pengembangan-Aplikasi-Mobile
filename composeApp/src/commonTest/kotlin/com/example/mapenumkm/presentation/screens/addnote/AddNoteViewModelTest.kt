package com.example.mapenumkm.presentation.screens.addnote

import app.cash.turbine.test
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.model.NoteColor
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.usecase.SaveNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: FakeNoteRepository
    private lateinit var viewModel: AddNoteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        val saveNoteUseCase = SaveNoteUseCase(repository)
        viewModel = AddNoteViewModel(repository, saveNoteUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveNote should succeed when fields are valid`() = runTest {
        viewModel.onTitleChange("Bakso")
        viewModel.onPriceChange("15000")
        viewModel.onStockChange("10")
        
        viewModel.saveNote()
        
        viewModel.events.test {
            val event = awaitItem()
            assertTrue(event is AddNoteEvent.NoteSaved)
            cancelAndIgnoreRemainingEvents()
        }
        
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Bakso", notes[0].title)
            assertEquals(15000.0, notes[0].price)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveNote should show error when title is empty`() = runTest {
        viewModel.onTitleChange("")
        viewModel.saveNote()
        
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Judul atau konten harus diisi", state.titleError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onPriceChange should only accept numeric input`() = runTest {
        viewModel.onPriceChange("12a3")
        assertEquals("", viewModel.uiState.value.price)
        
        viewModel.onPriceChange("12.5")
        assertEquals("12.5", viewModel.uiState.value.price)
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
        val id = nextId++
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

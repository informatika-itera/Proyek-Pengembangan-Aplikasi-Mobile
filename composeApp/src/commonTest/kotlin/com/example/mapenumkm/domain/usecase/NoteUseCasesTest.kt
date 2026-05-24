package com.example.mapenumkm.domain.usecase

import app.cash.turbine.test
import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.model.NoteColor
import com.example.mapenumkm.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoteUseCasesTest {

    private lateinit var repository: FakeNoteRepository
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase
    private lateinit var searchNotesUseCase: SearchNotesUseCase
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase

    @BeforeTest
    fun setup() {
        repository = FakeNoteRepository()
        getAllNotesUseCase = GetAllNotesUseCase(repository)
        searchNotesUseCase = SearchNotesUseCase(repository)
        saveNoteUseCase = SaveNoteUseCase(repository)
        deleteNoteUseCase = DeleteNoteUseCase(repository)
    }

    @Test
    fun `GetAllNotesUseCase should return pinned notes first`() = runTest {
        val note1 = createTestNote(id = 1, title = "Unpinned", isPinned = false)
        val note2 = createTestNote(id = 2, title = "Pinned", isPinned = true)
        
        repository.insertNote(note1)
        repository.insertNote(note2)

        getAllNotesUseCase().test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            assertEquals(true, notes[0].isPinned)
            assertEquals("Pinned", notes[0].title)
            assertEquals(false, notes[1].isPinned)
            assertEquals("Unpinned", notes[1].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllNotesUseCase should sort by price ascending`() = runTest {
        val note1 = createTestNote(id = 1, title = "Expensive", price = 100.0)
        val note2 = createTestNote(id = 2, title = "Cheap", price = 10.0)
        
        repository.insertNote(note1)
        repository.insertNote(note2)

        getAllNotesUseCase(NoteSortBy.PRICE_ASC).test {
            val notes = awaitItem()
            assertEquals("Cheap", notes[0].title)
            assertEquals("Expensive", notes[1].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchNotesUseCase should filter by query and category`() = runTest {
        val note1 = createTestNote(title = "Apple", category = NoteCategory.FOOD)
        val note2 = createTestNote(title = "Banana", category = NoteCategory.FOOD)
        val note3 = createTestNote(title = "Orange", category = NoteCategory.OTHER)
        
        repository.insertNote(note1)
        repository.insertNote(note2)
        repository.insertNote(note3)

        searchNotesUseCase(query = "a", category = NoteCategory.FOOD).test {
            val notes = awaitItem()
            // "Apple" and "Banana" both have 'a', and both are FOOD.
            assertEquals(2, notes.size)
            assertTrue(notes.all { it.category == NoteCategory.FOOD })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveNoteUseCase should return failure for empty note`() = runTest {
        val emptyNote = Note(title = "", content = "")
        
        val result = saveNoteUseCase(emptyNote)
        
        assertTrue(result.isFailure)
        assertEquals("Note tidak boleh kosong", result.exceptionOrNull()?.message)
    }

    @Test
    fun `SaveNoteUseCase should call insert for new note`() = runTest {
        val newNote = createTestNote(id = 0, title = "New")
        
        val result = saveNoteUseCase(newNote)
        
        assertTrue(result.isSuccess)
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("New", notes[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `DeleteNoteUseCase should call repository delete`() = runTest {
        val id = repository.insertNote(createTestNote(title = "To Delete"))
        
        deleteNoteUseCase(id)
        
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
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
        isPinned: Boolean = false,
        createdAt: Instant = Clock.System.now(),
        updatedAt: Instant = Clock.System.now()
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
            createdAt = createdAt,
            updatedAt = updatedAt
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

package com.example.foodsaver.data.repository

import app.cash.turbine.test
import com.example.foodsaver.domain.model.Note
import com.example.foodsaver.domain.model.NoteCategory
import com.example.foodsaver.domain.model.NoteColor
import com.example.foodsaver.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NoteRepositoryTest {
    
    private lateinit var repository: FakeNoteRepository
    
    @BeforeTest
    fun setup() {
        repository = FakeNoteRepository()
    }
    
    @Test
    fun `insertNote should return new note id`() = runTest {
        val note = createTestNote(title = "Test Note")
        
        val id = repository.insertNote(note)
        
        assertTrue(id > 0)
    }
    
    @Test
    fun `insertNote should add note to list`() = runTest {
        val note = createTestNote(title = "New Note")
        
        repository.insertNote(note)
        
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("New Note", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getAllNotes should return all notes`() = runTest {
        repository.insertNote(createTestNote(title = "Note 1"))
        repository.insertNote(createTestNote(title = "Note 2"))
        
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getNoteById should return correct note`() = runTest {
        val id = repository.insertNote(createTestNote(title = "Find Me"))
        
        repository.getNoteById(id).test {
            val note = awaitItem()
            assertNotNull(note)
            assertEquals("Find Me", note.title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getNoteById should return null for non-existent id`() = runTest {
        repository.getNoteById(999).test {
            val note = awaitItem()
            assertEquals(null, note)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `searchNotes should find notes by title`() = runTest {
        repository.insertNote(createTestNote(title = "Kotlin Tutorial"))
        repository.insertNote(createTestNote(title = "Java Guide"))
        
        repository.searchNotes("Kotlin").test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Kotlin Tutorial", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `searchNotes should find notes by content`() = runTest {
        repository.insertNote(createTestNote(title = "Recipe", content = "Add tomatoes"))
        repository.insertNote(createTestNote(title = "Shopping", content = "Buy milk"))
        
        repository.searchNotes("tomatoes").test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Recipe", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `deleteNote should remove note from list`() = runTest {
        val id = repository.insertNote(createTestNote(title = "To Delete"))
        
        repository.deleteNote(id)
        
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `updateNote should modify existing note`() = runTest {
        val id = repository.insertNote(createTestNote(title = "Original"))
        
        val updatedNote = createTestNote(id = id, title = "Updated")
        repository.updateNote(updatedNote)
        
        repository.getNoteById(id).test {
            val note = awaitItem()
            assertEquals("Updated", note?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    private fun createTestNote(
        id: Long = 0,
        title: String = "Test",
        content: String = "Content",
        category: NoteCategory = NoteCategory.GENERAL
    ): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            category = category,
            color = NoteColor.DEFAULT,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

class FakeNoteRepository : NoteRepository {
    
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L
    
    override fun getAllNotes(): Flow<List<Note>> = notes
    
    override fun getPinnedNotes(): Flow<List<Note>> {
        return notes.map { list -> list.filter { it.isPinned } }
    }
    
    override fun getNotesByCategory(category: NoteCategory): Flow<List<Note>> {
        return notes.map { list -> list.filter { it.category == category } }
    }
    
    override fun searchNotes(query: String): Flow<List<Note>> {
        return notes.map { list ->
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
    }
    
    override fun getNoteById(id: Long): Flow<Note?> {
        return notes.map { list -> list.find { it.id == id } }
    }
    
    override suspend fun insertNote(note: Note): Long {
        val id = nextId++
        val newNote = note.copy(id = id)
        notes.update { it + newNote }
        return id
    }
    
    override suspend fun updateNote(note: Note) {
        notes.update { list ->
            list.map { if (it.id == note.id) note else it }
        }
    }
    
    override suspend fun deleteNote(id: Long) {
        notes.update { list -> list.filter { it.id != id } }
    }
    
    override suspend fun togglePinNote(id: Long) {
        notes.update { list ->
            list.map { 
                if (it.id == id) it.copy(isPinned = !it.isPinned) else it 
            }
        }
    }
    
    override suspend fun deleteNotes(ids: List<Long>) {
        notes.update { list -> list.filter { it.id !in ids } }
    }
}

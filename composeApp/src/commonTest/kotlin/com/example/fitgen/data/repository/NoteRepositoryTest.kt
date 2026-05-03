package com.example.fitgen.data.repository

import app.cash.turbine.test
import com.example.fitgen.domain.model.Note
import com.example.fitgen.domain.model.NoteCategory
import com.example.fitgen.domain.model.NoteColor
import com.example.fitgen.domain.repository.NoteRepository
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

/**
 * Unit Tests untuk NoteRepository
 * 
 * Testing Guidelines:
 * 1. Gunakan FakeRepository untuk isolasi
 * 2. Test satu behavior per test
 * 3. Gunakan Turbine untuk test Flow
 * 4. Follow AAA pattern (Arrange, Act, Assert)
 */
class NoteRepositoryTest {
    
    private lateinit var repository: FakeNoteRepository
    
    @BeforeTest
    fun setup() {
        repository = FakeNoteRepository()
    }
    
    // ==================== INSERT TESTS ====================
    
    @Test
    fun `insertNote should return new note id`() = runTest {
        // Arrange
        val note = createTestNote(title = "Test Note")
        
        // Act
        val id = repository.insertNote(note)
        
        // Assert
        assertTrue(id > 0)
    }
    
    @Test
    fun `insertNote should add note to list`() = runTest {
        // Arrange
        val note = createTestNote(title = "New Note")
        
        // Act
        repository.insertNote(note)
        
        // Assert
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("New Note", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== GET TESTS ====================
    
    @Test
    fun `getAllNotes should return all notes`() = runTest {
        // Arrange
        repository.insertNote(createTestNote(title = "Note 1"))
        repository.insertNote(createTestNote(title = "Note 2"))
        
        // Act & Assert
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getNoteById should return correct note`() = runTest {
        // Arrange
        val id = repository.insertNote(createTestNote(title = "Find Me"))
        
        // Act & Assert
        repository.getNoteById(id).test {
            val note = awaitItem()
            assertNotNull(note)
            assertEquals("Find Me", note.title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `getNoteById should return null for non-existent id`() = runTest {
        // Act & Assert
        repository.getNoteById(999).test {
            val note = awaitItem()
            assertEquals(null, note)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== SEARCH TESTS ====================
    
    @Test
    fun `searchNotes should find notes by title`() = runTest {
        // Arrange
        repository.insertNote(createTestNote(title = "Kotlin Tutorial"))
        repository.insertNote(createTestNote(title = "Java Guide"))
        
        // Act & Assert
        repository.searchNotes("Kotlin").test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Kotlin Tutorial", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `searchNotes should find notes by content`() = runTest {
        // Arrange
        repository.insertNote(createTestNote(title = "Recipe", content = "Add tomatoes"))
        repository.insertNote(createTestNote(title = "Shopping", content = "Buy milk"))
        
        // Act & Assert
        repository.searchNotes("tomatoes").test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Recipe", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== DELETE TESTS ====================
    
    @Test
    fun `deleteNote should remove note from list`() = runTest {
        // Arrange
        val id = repository.insertNote(createTestNote(title = "To Delete"))
        
        // Act
        repository.deleteNote(id)
        
        // Assert
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== UPDATE TESTS ====================
    
    @Test
    fun `updateNote should modify existing note`() = runTest {
        // Arrange
        val id = repository.insertNote(createTestNote(title = "Original"))
        
        // Act
        val updatedNote = createTestNote(id = id, title = "Updated")
        repository.updateNote(updatedNote)
        
        // Assert
        repository.getNoteById(id).test {
            val note = awaitItem()
            assertEquals("Updated", note?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    // ==================== HELPER FUNCTIONS ====================
    
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

/**
 * Fake Repository untuk Testing
 * 
 * In-memory implementation yang tidak bergantung pada database.
 * Digunakan untuk unit testing tanpa side effects.
 */
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

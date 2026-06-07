package com.example.synesthesia.data.repository

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeNoteRepository
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import com.example.synesthesia.domain.repository.NoteRepository
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
 * 3. Gunakan Turbine untuk test Flow*-
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

    @Test
    fun `togglePin should toggle isPinned status`() = runTest {
        // Arrange
        val id = repository.insertNote(createTestNote(title = "Pin Test"))

        // Act & Assert
        repository.togglePinNote(id)
        repository.getNoteById(id).test {
            assertTrue(awaitItem()?.isPinned == true)
        }

        repository.togglePinNote(id)
        repository.getNoteById(id).test {
            assertTrue(awaitItem()?.isPinned == false)
        }
    }

    @Test
    fun `deleteNotes bulk should remove multiple notes`() = runTest {
        // Arrange
        val id1 = repository.insertNote(createTestNote(title = "N1"))
        val id2 = repository.insertNote(createTestNote(title = "N2"))
        val id3 = repository.insertNote(createTestNote(title = "N3"))

        // Act
        repository.deleteNotes(listOf(id1, id2))

        // Assert
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("N3", notes.first().title)
        }
    }

    @Test
    fun `getPinnedNotes should return only pinned notes`() = runTest {
        // Arrange
        repository.insertNote(createTestNote(title = "N1", isPinned = true))
        repository.insertNote(createTestNote(title = "N2", isPinned = false))

        // Act & Assert
        repository.getPinnedNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertTrue(notes.first().isPinned)
        }
    }
    
    // ==================== HELPER FUNCTIONS ====================
    
    private fun createTestNote(
        id: Long = 0,
        title: String = "Test",
        content: String = "Content",
        category: NoteCategory = NoteCategory.GENERAL,
        isPinned: Boolean = false
    ): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            category = category,
            color = NoteColor.DEFAULT,
            isPinned = isPinned,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

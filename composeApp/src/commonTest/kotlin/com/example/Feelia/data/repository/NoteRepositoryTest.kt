package com.example.Feelia.data.repository

import app.cash.turbine.test
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.domain.repository.NoteRepository
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NoteRepositoryTest {

    private lateinit var repository: FakeNoteRepository

    @BeforeTest
    fun setup() {
        repository = FakeNoteRepository()
    }

    // ==================== INSERT TESTS ====================

    @Test
    fun `insertNote should return positive id`() = runTest {
        val note = createTestNote("Hari ini menyenangkan")
        val id = repository.insertNote(note)
        assertTrue(id > 0)
    }

    @Test
    fun `insertNote should add note to list`() = runTest {
        val note = createTestNote("Jurnal pertama")
        repository.insertNote(note)
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Jurnal pertama", notes.first().content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertNote with HAPPY emotion should persist emotion`() = runTest {
        val note = createTestNote("Senang banget hari ini", Emotion.HAPPY)
        val id = repository.insertNote(note)
        repository.getNoteById(id).test {
            val result = awaitItem()
            assertEquals(Emotion.HAPPY, result?.emotion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== GET TESTS ====================

    @Test
    fun `getAllNotes should return all inserted notes`() = runTest {
        repository.insertNote(createTestNote("Jurnal 1"))
        repository.insertNote(createTestNote("Jurnal 2"))
        repository.insertNote(createTestNote("Jurnal 3"))
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(3, notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getNoteById should return correct note`() = runTest {
        val id = repository.insertNote(createTestNote("Cari jurnal ini"))
        repository.getNoteById(id).test {
            val note = awaitItem()
            assertNotNull(note)
            assertEquals("Cari jurnal ini", note.content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getNoteById should return null for non-existent id`() = runTest {
        repository.getNoteById(999L).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getNotesByEmotion should filter correctly`() = runTest {
        repository.insertNote(createTestNote("Senang", Emotion.HAPPY))
        repository.insertNote(createTestNote("Sedih", Emotion.SAD))
        repository.insertNote(createTestNote("Senang lagi", Emotion.HAPPY))
        repository.getNotesByEmotion(Emotion.HAPPY).test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            assertTrue(notes.all { it.emotion == Emotion.HAPPY })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getNotesByEmotion should return empty if none match`() = runTest {
        repository.insertNote(createTestNote("Senang", Emotion.HAPPY))
        repository.getNotesByEmotion(Emotion.ANGRY).test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== SEARCH TESTS ====================

    @Test
    fun `searchNotes should find by content keyword`() = runTest {
        repository.insertNote(createTestNote("Hari ini sangat menyenangkan"))
        repository.insertNote(createTestNote("Sedih banget hari ini"))
        repository.insertNote(createTestNote("Cuaca cerah dan indah"))
        repository.searchNotes("hari ini").test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchNotes should be case insensitive`() = runTest {
        repository.insertNote(createTestNote("Belajar Kotlin multiplatform"))
        repository.searchNotes("kotlin").test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchNotes should return empty for no match`() = runTest {
        repository.insertNote(createTestNote("Jurnal biasa"))
        repository.searchNotes("tidakada").test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== DELETE TESTS ====================

    @Test
    fun `deleteNote should remove note from list`() = runTest {
        val id = repository.insertNote(createTestNote("Akan dihapus"))
        repository.deleteNote(id)
        repository.getAllNotes().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote should not affect other notes`() = runTest {
        val id1 = repository.insertNote(createTestNote("Jurnal 1"))
        repository.insertNote(createTestNote("Jurnal 2"))
        repository.deleteNote(id1)
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Jurnal 2", notes.first().content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== UPDATE TESTS ====================

    @Test
    fun `updateNote should change content`() = runTest {
        val id = repository.insertNote(createTestNote("Konten lama"))
        val updated = createTestNote("Konten baru").copy(id = id)
        repository.updateNote(updated)
        repository.getNoteById(id).test {
            assertEquals("Konten baru", awaitItem()?.content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateNote should change emotion`() = runTest {
        val id = repository.insertNote(createTestNote("Netral", Emotion.NEUTRAL))
        val updated = createTestNote("Senang sekarang", Emotion.HAPPY).copy(id = id)
        repository.updateNote(updated)
        repository.getNoteById(id).test {
            assertEquals(Emotion.HAPPY, awaitItem()?.emotion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== PIN TESTS ====================

    @Test
    fun `togglePinNote should set isPinned to true`() = runTest {
        val id = repository.insertNote(createTestNote("Pin ini"))
        repository.togglePinNote(id)
        repository.getNoteById(id).test {
            assertTrue(awaitItem()?.isPinned == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `togglePinNote twice should return to unpinned`() = runTest {
        val id = repository.insertNote(createTestNote("Toggle dua kali"))
        repository.togglePinNote(id)
        repository.togglePinNote(id)
        repository.getNoteById(id).test {
            assertTrue(awaitItem()?.isPinned == false)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== HELPER ====================

    private fun createTestNote(
        content: String = "Test jurnal",
        emotion: Emotion = Emotion.NEUTRAL
    ) = Note(
        content = content,
        emotion = emotion,
        isPinned = false,
        createdAt = Clock.System.now(),
        updatedAt = Clock.System.now()
    )
}

class FakeNoteRepository : NoteRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L

    override fun getAllNotes(): Flow<List<Note>> = notes
    override fun getPinnedNotes(): Flow<List<Note>> =
        notes.map { it.filter { n -> n.isPinned } }
    override fun getNotesByEmotion(emotion: Emotion): Flow<List<Note>> =
        notes.map { it.filter { n -> n.emotion == emotion } }
    override fun searchNotes(query: String): Flow<List<Note>> =
        notes.map { it.filter { n -> n.content.contains(query, ignoreCase = true) } }
    override fun getNoteById(id: Long): Flow<Note?> =
        notes.map { it.find { n -> n.id == id } }
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
}
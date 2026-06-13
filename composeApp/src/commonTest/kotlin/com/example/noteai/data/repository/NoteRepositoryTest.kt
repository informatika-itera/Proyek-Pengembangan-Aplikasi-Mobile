package com.example.noteai.data.repository

import app.cash.turbine.test
import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.NoteCategory
import com.example.noteai.domain.model.NoteColor
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoteRepositoryTest {
    private lateinit var repository: FakeNoteRepository
    
    @BeforeTest
    fun setup() { repository = FakeNoteRepository() }
    
    @Test
    fun insertNote() = runTest {
        val note = createNote("Title")
        repository.insertNote(note)
        repository.getAllNotes().test {
            assertEquals(1, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun searchNotes() = runTest {
        repository.insertNote(createNote("Kotlin"))
        repository.searchNotes("Kotlin").test {
            assertEquals(1, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteNote() = runTest {
        val id = repository.insertNote(createNote("To Delete"))
        repository.deleteNote(id)
        repository.getAllNotes().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createNote(title: String) = Note(
        id = 0, title = title, content = "Content", category = NoteCategory.GENERAL,
        color = NoteColor.DEFAULT, isPinned = false,
        createdAt = Clock.System.now(), updatedAt = Clock.System.now()
    )
}

class FakeNoteRepository : NoteRepository {
    private val notes = MutableStateFlow<List<Note>>(emptyList())
    private var nextId = 1L
    override fun getAllNotes(): Flow<List<Note>> = notes
    override fun getPinnedNotes() = notes.map { list -> list.filter { it.isPinned } }
    override fun getNotesByCategory(category: NoteCategory) = notes.map { list -> list.filter { it.category == category } }
    override fun searchNotes(query: String) = notes.map { list -> list.filter { it.title.contains(query, true) } }
    override fun getNoteById(id: Long) = notes.map { list -> list.find { it.id == id } }
    override suspend fun insertNote(note: Note): Long {
        val id = nextId++; notes.update { it + note.copy(id = id) }; return id
    }
    override suspend fun updateNote(note: Note) { notes.update { list -> list.map { if (it.id == note.id) note else it } } }
    override suspend fun deleteNote(id: Long) { notes.update { list -> list.filter { it.id != id } } }
    override suspend fun togglePinNote(id: Long) { }
    override suspend fun deleteNotes(ids: List<Long>) { }
}

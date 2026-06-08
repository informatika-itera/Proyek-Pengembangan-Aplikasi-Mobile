package com.example.fitkos.domain.usecase

import app.cash.turbine.test
import com.example.fitkos.data.repository.FakeNoteRepository
import com.example.fitkos.domain.model.Note
import com.example.fitkos.domain.model.NoteCategory
import com.example.fitkos.domain.model.NoteColor
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
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
    fun `GetAllNotesUseCase should sort pinned notes first`() = runTest {
        val note1 = createTestNote(title = "A", isPinned = false)
        val note2 = createTestNote(title = "B", isPinned = true)
        
        repository.insertNote(note1)
        repository.insertNote(note2)

        getAllNotesUseCase().test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            assertTrue(notes[0].isPinned)
            assertEquals("B", notes[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveNoteUseCase should fail for empty note`() = runTest {
        val emptyNote = createTestNote(title = "", content = "")
        val result = saveNoteUseCase(emptyNote)
        assertTrue(result.isFailure)
    }

    @Test
    fun `SearchNotesUseCase should filter by category`() = runTest {
        repository.insertNote(createTestNote("Breakfast", category = NoteCategory.BREAKFAST))
        repository.insertNote(createTestNote("Lunch", category = NoteCategory.LUNCH))

        searchNotesUseCase("", NoteCategory.BREAKFAST).test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals(NoteCategory.BREAKFAST, notes[0].category)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createTestNote(
        title: String,
        content: String = "Content",
        isPinned: Boolean = false,
        category: NoteCategory = NoteCategory.BREAKFAST
    ): Note {
        return Note(
            id = 0,
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

package com.example.rewind.domain.usecase

import app.cash.turbine.test
import com.example.rewind.data.repository.FakeAIRepository
import com.example.rewind.data.repository.FakeNoteRepository
import com.example.rewind.domain.model.Note
import com.example.rewind.domain.model.NoteCategory
import com.example.rewind.domain.model.NoteColor
import com.example.rewind.domain.repository.WritingStyle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NoteUseCasesTest {

    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var aiRepository: FakeAIRepository

    @BeforeTest
    fun setup() {
        noteRepository = FakeNoteRepository()
        aiRepository = FakeAIRepository()
    }

    // ==================== HELPER ====================

    private fun createTestNote(
        id: Long = 0,
        title: String = "Test",
        content: String = "Content",
        category: NoteCategory = NoteCategory.GENERAL,
        isPinned: Boolean = false,
        createdAt: Instant = Instant.fromEpochMilliseconds(1000),
        updatedAt: Instant = Instant.fromEpochMilliseconds(1000)
    ): Note = Note(
        id = id,
        title = title,
        content = content,
        category = category,
        color = NoteColor.DEFAULT,
        isPinned = isPinned,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    // ==================== GetAllNotesUseCase ====================

    @Test
    fun `GetAllNotes - pinned notes come before unpinned notes`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Unpinned", isPinned = false))
        noteRepository.insertNote(createTestNote(title = "Pinned", isPinned = true))
        noteRepository.insertNote(createTestNote(title = "Also Unpinned", isPinned = false))
        val useCase = GetAllNotesUseCase(noteRepository)

        // Act & Assert
        useCase().test {
            val notes = awaitItem()
            assertEquals(3, notes.size)
            assertTrue(notes.first().isPinned)
            assertEquals("Pinned", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllNotes - TITLE_ASC sorts alphabetically ascending`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Cherry"))
        noteRepository.insertNote(createTestNote(title = "Apple"))
        noteRepository.insertNote(createTestNote(title = "Banana"))
        val useCase = GetAllNotesUseCase(noteRepository)

        // Act & Assert
        useCase(NoteSortBy.TITLE_ASC).test {
            val notes = awaitItem()
            assertEquals("Apple", notes[0].title)
            assertEquals("Banana", notes[1].title)
            assertEquals("Cherry", notes[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllNotes - TITLE_DESC sorts alphabetically descending`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Apple"))
        noteRepository.insertNote(createTestNote(title = "Cherry"))
        noteRepository.insertNote(createTestNote(title = "Banana"))
        val useCase = GetAllNotesUseCase(noteRepository)

        // Act & Assert
        useCase(NoteSortBy.TITLE_DESC).test {
            val notes = awaitItem()
            assertEquals("Cherry", notes[0].title)
            assertEquals("Banana", notes[1].title)
            assertEquals("Apple", notes[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllNotes - CREATED_ASC sorts by creation date ascending`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Newest", createdAt = Instant.fromEpochMilliseconds(3000)))
        noteRepository.insertNote(createTestNote(title = "Oldest", createdAt = Instant.fromEpochMilliseconds(1000)))
        noteRepository.insertNote(createTestNote(title = "Middle", createdAt = Instant.fromEpochMilliseconds(2000)))
        val useCase = GetAllNotesUseCase(noteRepository)

        // Act & Assert
        useCase(NoteSortBy.CREATED_ASC).test {
            val notes = awaitItem()
            assertEquals("Oldest", notes[0].title)
            assertEquals("Middle", notes[1].title)
            assertEquals("Newest", notes[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllNotes - CREATED_DESC sorts by creation date descending`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Newest", createdAt = Instant.fromEpochMilliseconds(3000)))
        noteRepository.insertNote(createTestNote(title = "Oldest", createdAt = Instant.fromEpochMilliseconds(1000)))
        noteRepository.insertNote(createTestNote(title = "Middle", createdAt = Instant.fromEpochMilliseconds(2000)))
        val useCase = GetAllNotesUseCase(noteRepository)

        // Act & Assert
        useCase(NoteSortBy.CREATED_DESC).test {
            val notes = awaitItem()
            assertEquals("Newest", notes[0].title)
            assertEquals("Middle", notes[1].title)
            assertEquals("Oldest", notes[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllNotes - UPDATED_ASC sorts by update date ascending`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Last Updated", updatedAt = Instant.fromEpochMilliseconds(3000)))
        noteRepository.insertNote(createTestNote(title = "First Updated", updatedAt = Instant.fromEpochMilliseconds(1000)))
        noteRepository.insertNote(createTestNote(title = "Mid Updated", updatedAt = Instant.fromEpochMilliseconds(2000)))
        val useCase = GetAllNotesUseCase(noteRepository)

        // Act & Assert
        useCase(NoteSortBy.UPDATED_ASC).test {
            val notes = awaitItem()
            assertEquals("First Updated", notes[0].title)
            assertEquals("Mid Updated", notes[1].title)
            assertEquals("Last Updated", notes[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllNotes - UPDATED_DESC sorts by update date descending`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Last Updated", updatedAt = Instant.fromEpochMilliseconds(3000)))
        noteRepository.insertNote(createTestNote(title = "First Updated", updatedAt = Instant.fromEpochMilliseconds(1000)))
        noteRepository.insertNote(createTestNote(title = "Mid Updated", updatedAt = Instant.fromEpochMilliseconds(2000)))
        val useCase = GetAllNotesUseCase(noteRepository)

        // Act & Assert
        useCase(NoteSortBy.UPDATED_DESC).test {
            val notes = awaitItem()
            assertEquals("Last Updated", notes[0].title)
            assertEquals("Mid Updated", notes[1].title)
            assertEquals("First Updated", notes[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetAllNotes - empty list returns empty`() = runTest {
        // Arrange
        val useCase = GetAllNotesUseCase(noteRepository)

        // Act & Assert
        useCase().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== SearchNotesUseCase ====================

    @Test
    fun `SearchNotes - blank query and no category returns all notes`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Note A"))
        noteRepository.insertNote(createTestNote(title = "Note B"))
        val useCase = SearchNotesUseCase(noteRepository)

        // Act & Assert
        useCase("").test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchNotes - blank query with category returns filtered by category`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Work Note", category = NoteCategory.WORK))
        noteRepository.insertNote(createTestNote(title = "Personal Note", category = NoteCategory.PERSONAL))
        noteRepository.insertNote(createTestNote(title = "Another Work", category = NoteCategory.WORK))
        val useCase = SearchNotesUseCase(noteRepository)

        // Act & Assert
        useCase("", NoteCategory.WORK).test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            assertTrue(notes.all { it.category == NoteCategory.WORK })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchNotes - query without category returns searched notes`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Kotlin Tutorial"))
        noteRepository.insertNote(createTestNote(title = "Java Guide"))
        noteRepository.insertNote(createTestNote(title = "Kotlin Advanced"))
        val useCase = SearchNotesUseCase(noteRepository)

        // Act & Assert
        useCase("Kotlin").test {
            val notes = awaitItem()
            assertEquals(2, notes.size)
            assertTrue(notes.all { it.title.contains("Kotlin") })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchNotes - query with category returns searched and filtered notes`() = runTest {
        // Arrange
        noteRepository.insertNote(createTestNote(title = "Kotlin Work", category = NoteCategory.WORK))
        noteRepository.insertNote(createTestNote(title = "Kotlin Personal", category = NoteCategory.PERSONAL))
        noteRepository.insertNote(createTestNote(title = "Java Work", category = NoteCategory.WORK))
        val useCase = SearchNotesUseCase(noteRepository)

        // Act & Assert
        useCase("Kotlin", NoteCategory.WORK).test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Kotlin Work", notes.first().title)
            assertEquals(NoteCategory.WORK, notes.first().category)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== SaveNoteUseCase ====================

    @Test
    fun `SaveNote - empty title and content returns failure`() = runTest {
        // Arrange
        val useCase = SaveNoteUseCase(noteRepository)
        val emptyNote = createTestNote(title = "", content = "")

        // Act
        val result = useCase(emptyNote)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `SaveNote - new note with id 0 inserts and returns new id`() = runTest {
        // Arrange
        val useCase = SaveNoteUseCase(noteRepository)
        val newNote = createTestNote(id = 0, title = "New Note", content = "Some content")

        // Act
        val result = useCase(newNote)

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!! > 0)

        // Verify it was actually inserted
        noteRepository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("New Note", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveNote - existing note with id greater than 0 updates`() = runTest {
        // Arrange
        val useCase = SaveNoteUseCase(noteRepository)
        val insertedId = noteRepository.insertNote(createTestNote(title = "Original"))
        val updatedNote = createTestNote(id = insertedId, title = "Updated", content = "Updated content")

        // Act
        val result = useCase(updatedNote)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(insertedId, result.getOrNull())

        // Verify it was actually updated
        noteRepository.getNoteById(insertedId).test {
            val note = awaitItem()
            assertEquals("Updated", note?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SaveNote - title only is valid`() = runTest {
        // Arrange
        val useCase = SaveNoteUseCase(noteRepository)
        val note = createTestNote(title = "Only Title", content = "")

        // Act
        val result = useCase(note)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `SaveNote - content only is valid`() = runTest {
        // Arrange
        val useCase = SaveNoteUseCase(noteRepository)
        val note = createTestNote(title = "", content = "Only content here")

        // Act
        val result = useCase(note)

        // Assert
        assertTrue(result.isSuccess)
    }

    // ==================== DeleteNoteUseCase ====================

    @Test
    fun `DeleteNote - successfully deletes existing note`() = runTest {
        // Arrange
        val id = noteRepository.insertNote(createTestNote(title = "To Delete"))
        val useCase = DeleteNoteUseCase(noteRepository)

        // Act
        val result = useCase(id)

        // Assert
        assertTrue(result.isSuccess)
        noteRepository.getAllNotes().test {
            val notes = awaitItem()
            assertTrue(notes.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== SummarizeNoteUseCase ====================

    @Test
    fun `SummarizeNote - short content less than 50 chars returns failure`() = runTest {
        // Arrange
        val useCase = SummarizeNoteUseCase(aiRepository)
        val shortContent = "Too short"

        // Act
        val result = useCase(shortContent)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `SummarizeNote - valid content returns success`() = runTest {
        // Arrange
        val useCase = SummarizeNoteUseCase(aiRepository)
        val longContent = "This is a sufficiently long piece of content that exceeds fifty characters for summarization."
        aiRepository.shouldSucceed = true
        aiRepository.fakeResult = "Summary of the content"

        // Act
        val result = useCase(longContent)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Summary of the content", result.getOrNull())
    }

    @Test
    fun `SummarizeNote - AI failure is propagated`() = runTest {
        // Arrange
        val useCase = SummarizeNoteUseCase(aiRepository)
        val longContent = "This is a sufficiently long piece of content that exceeds fifty characters for summarization."
        aiRepository.shouldSucceed = false
        aiRepository.errorMessage = "AI service unavailable"

        // Act
        val result = useCase(longContent)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("AI service unavailable", result.exceptionOrNull()?.message)
    }

    // ==================== ImproveWritingUseCase ====================

    @Test
    fun `ImproveWriting - blank content returns failure`() = runTest {
        // Arrange
        val useCase = ImproveWritingUseCase(aiRepository)

        // Act
        val result = useCase("")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `ImproveWriting - valid content returns success`() = runTest {
        // Arrange
        val useCase = ImproveWritingUseCase(aiRepository)
        aiRepository.shouldSucceed = true
        aiRepository.fakeResult = "Improved writing"

        // Act
        val result = useCase("Some rough draft text")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Improved writing", result.getOrNull())
    }

    @Test
    fun `ImproveWriting - with specific style returns success`() = runTest {
        // Arrange
        val useCase = ImproveWritingUseCase(aiRepository)
        aiRepository.shouldSucceed = true
        aiRepository.fakeResult = "Formal improved writing"

        // Act
        val result = useCase("Some rough draft text", WritingStyle.FORMAL)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Formal improved writing", result.getOrNull())
    }

    // ==================== GenerateIdeasUseCase ====================

    @Test
    fun `GenerateIdeas - blank topic returns failure`() = runTest {
        // Arrange
        val useCase = GenerateIdeasUseCase(aiRepository)

        // Act
        val result = useCase("")

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `GenerateIdeas - valid topic returns success`() = runTest {
        // Arrange
        val useCase = GenerateIdeasUseCase(aiRepository)
        aiRepository.shouldSucceed = true
        aiRepository.fakeIdeas = listOf("Idea A", "Idea B", "Idea C")

        // Act
        val result = useCase("Mobile App Development")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        assertEquals(listOf("Idea A", "Idea B", "Idea C"), result.getOrNull())
    }

    @Test
    fun `GenerateIdeas - AI failure is propagated`() = runTest {
        // Arrange
        val useCase = GenerateIdeasUseCase(aiRepository)
        aiRepository.shouldSucceed = false
        aiRepository.errorMessage = "AI service unavailable"

        // Act
        val result = useCase("Some topic")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("AI service unavailable", result.exceptionOrNull()?.message)
    }
}

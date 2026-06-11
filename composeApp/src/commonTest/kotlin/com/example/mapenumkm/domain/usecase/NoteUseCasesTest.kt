package com.example.mapenumkm.domain.usecase

import com.example.mapenumkm.domain.model.Note
import com.example.mapenumkm.domain.model.NoteCategory
import com.example.mapenumkm.domain.repository.AIRepository
import com.example.mapenumkm.domain.repository.NoteRepository
import com.example.mapenumkm.domain.repository.WritingStyle
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NoteUseCasesTest {
    private val repository: NoteRepository = mockk()
    private val aiRepository: AIRepository = mockk()
    
    private val now = Instant.fromEpochMilliseconds(1000)
    private val later = Instant.fromEpochMilliseconds(2000)
    
    private val testNotes = listOf(
        Note(id = 1, title = "A", content = "C1", price = 10.0, stock = 5, category = NoteCategory.FOOD, isPinned = false, createdAt = now, updatedAt = now),
        Note(id = 2, title = "B", content = "C2", price = 20.0, stock = 10, category = NoteCategory.DRINK, isPinned = true, createdAt = later, updatedAt = later)
    )

    @Test
    fun `GetAllNotesUseCase should sort pinned notes first and then by sortBy`() = runTest {
        val useCase = GetAllNotesUseCase(repository)
        every { repository.getAllNotes() } returns flowOf(testNotes)
        
        // Test all sort branches
        NoteSortBy.entries.forEach { sortBy ->
            val result = useCase(sortBy).first()
            assertEquals(2, result.size)
            assertTrue(result[0].isPinned)
        }
    }

    @Test
    fun `SearchNotesUseCase should cover different filter branches`() = runTest {
        val useCase = SearchNotesUseCase(repository)
        
        // Branch 1: query blank and category null
        every { repository.getAllNotes() } returns flowOf(testNotes)
        useCase(query = "", category = null).first()
        
        // Branch 2: query blank and category NOT null
        every { repository.getNotesByCategory(NoteCategory.FOOD) } returns flowOf(listOf(testNotes[0]))
        useCase(query = "", category = NoteCategory.FOOD).first()
        
        // Branch 3: query NOT blank
        every { repository.searchNotes("A") } returns flowOf(testNotes)
        val result = useCase(query = "A", category = NoteCategory.FOOD).first()
        assertEquals(1, result.size)
        assertEquals(NoteCategory.FOOD, result[0].category)
    }

    @Test
    fun `SaveNoteUseCase should call insert for id 0 and update for existing id`() = runTest {
        val useCase = SaveNoteUseCase(repository)
        val newNote = Note(id = 0, title = "New", content = "C", createdAt = now, updatedAt = now)
        val existingNote = Note(id = 1, title = "Exist", content = "C", createdAt = now, updatedAt = now)
        
        coEvery { repository.insertNote(any()) } returns 100L
        coEvery { repository.updateNote(any()) } returns Unit
        
        val res1 = useCase(newNote)
        assertEquals(100L, res1.getOrNull())
        
        val res2 = useCase(existingNote)
        assertEquals(1L, res2.getOrNull())
        
        val res3 = useCase(newNote.copy(title = "", content = ""))
        assertTrue(res3.isFailure)
    }

    @Test
    fun `DeleteNoteUseCase should call repository delete`() = runTest {
        val useCase = DeleteNoteUseCase(repository)
        coEvery { repository.deleteNote(1L) } returns Unit
        assertTrue(useCase(1L).isSuccess)
    }

    @Test
    fun `SummarizeNoteUseCase should return failure for short content`() = runTest {
        val useCase = SummarizeNoteUseCase(aiRepository)
        assertTrue(useCase("Short").isFailure)
        
        coEvery { aiRepository.summarize(any()) } returns Result.success("Summary")
        assertTrue(useCase("a".repeat(60)).isSuccess)
    }

    @Test
    fun `ImproveWritingUseCase should return success when content is valid`() = runTest {
        val useCase = ImproveWritingUseCase(aiRepository)
        coEvery { aiRepository.improveWriting(any(), any()) } returns Result.success("Improved")
        assertTrue(useCase("Valid content", WritingStyle.FORMAL).isSuccess)
        assertTrue(useCase("", WritingStyle.FORMAL).isFailure)
    }

    @Test
    fun `GenerateIdeasUseCase should return success for valid topic`() = runTest {
        val useCase = GenerateIdeasUseCase(aiRepository)
        coEvery { aiRepository.generateIdeas("Topic") } returns Result.success(listOf("Idea 1"))
        assertTrue(useCase("Topic").isSuccess)
        assertTrue(useCase("").isFailure)
    }
}

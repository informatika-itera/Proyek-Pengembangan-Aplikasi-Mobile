package com.studymate.presentation.screens.notes

import app.cash.turbine.test
import com.studymate.domain.model.ActivityDay
import com.studymate.domain.model.Note
import com.studymate.domain.repository.AIRepository
import com.studymate.domain.repository.ActivityRepository
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.usecase.RefineNoteUseCase
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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private lateinit var viewModel: NotesViewModel
    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var activityRepository: FakeActivityRepository
    private lateinit var aiRepository: FakeAIRepository
    private lateinit var refineNoteUseCase: RefineNoteUseCase

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        noteRepository = FakeNoteRepository()
        activityRepository = FakeActivityRepository()
        aiRepository = FakeAIRepository()
        refineNoteUseCase = RefineNoteUseCase(aiRepository, noteRepository)
        viewModel = NotesViewModel(noteRepository, refineNoteUseCase, activityRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Empty if no notes`() = runTest {
        viewModel.uiState.test {
            // Depending on how combine works, it might emit Loading first or jump to Empty
            val state = awaitItem()
            if (state is NotesUiState.Loading) {
                assertTrue(awaitItem() is NotesUiState.Empty)
            } else {
                assertTrue(state is NotesUiState.Empty)
            }
        }
    }

    @Test
    fun `addNote should emit success and update activity`() = runTest {
        viewModel.addNote("Title", "Content", "Subject")
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        noteRepository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Title", notes[0].title)
        }
        
        assertEquals(1, activityRepository.noteCreationCount)
    }

    @Test
    fun `search should filter notes correctly`() = runTest {
        noteRepository.insertNote(Note(id = 1, title = "Kotlin", rawContent = "Content", subject = "Prog"))
        noteRepository.insertNote(Note(id = 2, title = "Java", rawContent = "Content", subject = "Prog"))
        
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChange("Kotlin")
        
        viewModel.uiState.test {
            // First item might be the state before search took effect if not handled carefully
            var state = awaitItem()
            if (state is NotesUiState.Success && state.notes.size == 2) {
                state = awaitItem()
            }
            
            assertTrue(state is NotesUiState.Success, "Expected Success state but got $state")
            assertEquals(1, (state as NotesUiState.Success).notes.size)
            assertEquals("Kotlin", state.notes[0].title)
        }
    }

    @Test
    fun `deleteNote should remove note`() = runTest {
        val id = noteRepository.insertNote(Note(id = 1, title = "To Delete", rawContent = "Content", subject = "Sub"))
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.deleteNote(id)
        testDispatcher.scheduler.advanceUntilIdle()
        
        noteRepository.getAllNotes().test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    // Fakes
    class FakeNoteRepository : NoteRepository {
        private val _notes = MutableStateFlow<List<Note>>(emptyList())
        override fun getAllNotes(): Flow<List<Note>> = _notes
        override fun getNoteById(id: Long): Flow<Note?> = _notes.map { it.find { n -> n.id == id } }
        override fun getNotesBySubject(subject: String): Flow<List<Note>> = _notes.map { it.filter { n -> n.subject == subject } }
        override suspend fun insertNote(note: Note): Long {
            val id = 1L
            _notes.update { it + note.copy(id = id) }
            return id
        }
        override suspend fun updateNote(note: Note) {
            _notes.update { it.map { n -> if (n.id == note.id) note else n } }
        }
        override suspend fun deleteNote(id: Long) {
            _notes.update { it.filter { n -> n.id != id } }
        }
    }

    class FakeActivityRepository : ActivityRepository {
        var noteCreationCount = 0
        override fun getActivityHeatmap(days: Int): Flow<List<ActivityDay>> = MutableStateFlow(emptyList())
        override suspend fun recordQuizCompletion() {}
        override suspend fun recordNoteCreation() { noteCreationCount++ }
        override suspend fun getMonthlyQuizCount(): Int = 0
    }

    class FakeAIRepository : AIRepository {
        override suspend fun refineNote(subject: String, title: String, content: String): Result<String> {
            return Result.success("Refined: $content")
        }
        override suspend fun generateQuiz(subject: String, title: String, noteContent: String, questionCount: Int): Result<String> = Result.success("")
        override suspend fun generateMantra(): Result<String> = Result.success("Mantra")
    }
}

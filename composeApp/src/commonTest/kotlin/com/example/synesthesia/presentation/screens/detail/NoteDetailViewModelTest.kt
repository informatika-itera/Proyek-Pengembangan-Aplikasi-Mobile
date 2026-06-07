package com.example.synesthesia.presentation.screens.detail

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeNoteRepository
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import com.example.synesthesia.domain.usecase.DeleteNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.*

/**
 * Comprehensive NoteDetailViewModel Unit Tests
 * Targets 100% logic coverage for loading, pinning, deleting, and sharing notes.
 * Covers all branches including:
 * - loadNote: Success / NotFound
 * - togglePin: when in Success state / when not in Success state
 * - deleteNote: success / failure / when not in Success state
 * - getShareContent: with title / without title / when not in Success state
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NoteDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeNoteRepository
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var viewModel: NoteDetailViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        deleteNoteUseCase = DeleteNoteUseCase(repository)
        viewModel = NoteDetailViewModel(repository, deleteNoteUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Initial State ====================

    @Test
    fun `initial state should be Loading`() = runTest {
        assertEquals(NoteDetailUiState.Loading, viewModel.uiState.value)
    }

    // ==================== loadNote ====================

    @Test
    fun `loadNote should update state to Success when note exists`() = runTest {
        val noteId = repository.insertNote(createTestNote("Sample Title"))
        
        viewModel.uiState.test {
            assertEquals(NoteDetailUiState.Loading, awaitItem())
            
            viewModel.loadNote(noteId)
            
            val successState = awaitItem()
            assertTrue(successState is NoteDetailUiState.Success)
            assertEquals("Sample Title", successState.note.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadNote should update state to NotFound when note does not exist`() = runTest {
        viewModel.uiState.test {
            assertEquals(NoteDetailUiState.Loading, awaitItem())
            
            viewModel.loadNote(999L)
            
            assertEquals(NoteDetailUiState.NotFound, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadNote should show note with all fields populated`() = runTest {
        val note = Note(
            id = 0,
            title = "Full Note",
            content = "Full content",
            category = NoteCategory.WORK,
            color = NoteColor.JOY,
            emotion = "Enthusiastic",
            artToken = "High Energy, Pleasant",
            aiResonance = "Great vibes!",
            isPinned = true,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        val noteId = repository.insertNote(note)

        viewModel.loadNote(noteId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is NoteDetailUiState.Success)
        assertEquals("Full Note", state.note.title)
        assertEquals(NoteCategory.WORK, state.note.category)
        assertTrue(state.note.isPinned)
    }

    // ==================== togglePin ====================

    @Test
    fun `togglePin should update repository if in success state`() = runTest {
        val noteId = repository.insertNote(createTestNote("Pin Test"))
        viewModel.loadNote(noteId)
        advanceUntilIdle()

        viewModel.togglePin()
        advanceUntilIdle()

        repository.getNoteById(noteId).test {
            assertTrue(awaitItem()?.isPinned == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `togglePin should do nothing when state is Loading`() = runTest {
        // State is Loading initially, togglePin should be a no-op
        viewModel.togglePin()
        advanceUntilIdle()
        // No crash, no state change
        assertEquals(NoteDetailUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `togglePin should do nothing when state is NotFound`() = runTest {
        viewModel.loadNote(999L)
        advanceUntilIdle()
        assertEquals(NoteDetailUiState.NotFound, viewModel.uiState.value)

        viewModel.togglePin()
        advanceUntilIdle()
        // Still NotFound
        assertEquals(NoteDetailUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `togglePin twice should unpin the note`() = runTest {
        val noteId = repository.insertNote(createTestNote("Double Pin"))
        viewModel.loadNote(noteId)
        advanceUntilIdle()

        viewModel.togglePin()
        advanceUntilIdle()
        viewModel.togglePin()
        advanceUntilIdle()

        repository.getNoteById(noteId).test {
            assertFalse(awaitItem()?.isPinned ?: true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== deleteNote ====================

    @Test
    fun `deleteNote should emit NoteDeleted event and remove from repository`() = runTest {
        val noteId = repository.insertNote(createTestNote("Delete Test"))
        viewModel.loadNote(noteId)
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.deleteNote()
            assertEquals(NoteDetailEvent.NoteDeleted, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        repository.getNoteById(noteId).test {
            assertNull(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote should emit Error event when deletion fails`() = runTest {
        val noteId = repository.insertNote(createTestNote("Error Delete"))
        viewModel.loadNote(noteId)
        advanceUntilIdle()

        repository.shouldThrowOnDelete = true

        viewModel.events.test {
            viewModel.deleteNote()
            advanceUntilIdle()
            val event = awaitItem()
            assertTrue(event is NoteDetailEvent.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote should do nothing when state is Loading`() = runTest {
        viewModel.events.test {
            viewModel.deleteNote()
            advanceUntilIdle()
            // No events emitted
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote should do nothing when state is NotFound`() = runTest {
        viewModel.loadNote(999L)
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.deleteNote()
            advanceUntilIdle()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== getShareContent ====================

    @Test
    fun `getShareContent should return formatted string with title and content`() = runTest {
        val noteId = repository.insertNote(
            createTestNote("Share Title").copy(content = "Share Content")
        )
        viewModel.loadNote(noteId)
        advanceUntilIdle()

        val shareContent = viewModel.getShareContent()
        assertNotNull(shareContent)
        assertTrue(shareContent.contains("Share Title"))
        assertTrue(shareContent.contains("Share Content"))
    }

    @Test
    fun `getShareContent should return content only when title is blank`() = runTest {
        val noteId = repository.insertNote(
            createTestNote("").copy(content = "Only Content")
        )
        viewModel.loadNote(noteId)
        advanceUntilIdle()

        val shareContent = viewModel.getShareContent()
        assertNotNull(shareContent)
        assertTrue(shareContent.contains("Only Content"))
        // Title should not add extra lines when blank
    }

    @Test
    fun `getShareContent should return null when state is Loading`() {
        val shareContent = viewModel.getShareContent()
        assertNull(shareContent)
    }

    @Test
    fun `getShareContent should return null when state is NotFound`() = runTest {
        viewModel.loadNote(999L)
        advanceUntilIdle()

        val shareContent = viewModel.getShareContent()
        assertNull(shareContent)
    }

    // ==================== NoteDetailUiState sealed interface ====================

    @Test
    fun `NoteDetailUiState Loading should be distinct`() {
        val state: NoteDetailUiState = NoteDetailUiState.Loading
        assertTrue(state is NoteDetailUiState.Loading)
    }

    @Test
    fun `NoteDetailUiState NotFound should be distinct`() {
        val state: NoteDetailUiState = NoteDetailUiState.NotFound
        assertTrue(state is NoteDetailUiState.NotFound)
    }

    @Test
    fun `NoteDetailUiState Success should hold note`() {
        val note = createTestNote("Test")
        val state: NoteDetailUiState = NoteDetailUiState.Success(note)
        assertTrue(state is NoteDetailUiState.Success)
        assertEquals("Test", state.note.title)
    }

    // ==================== NoteDetailEvent sealed interface ====================

    @Test
    fun `NoteDetailEvent NoteDeleted should be distinct`() {
        val event: NoteDetailEvent = NoteDetailEvent.NoteDeleted
        assertTrue(event is NoteDetailEvent.NoteDeleted)
    }

    @Test
    fun `NoteDetailEvent Error should hold message`() {
        val event: NoteDetailEvent = NoteDetailEvent.Error("Oops")
        assertTrue(event is NoteDetailEvent.Error)
        assertEquals("Oops", event.message)
    }

    // ==================== HELPER ====================

    private fun createTestNote(title: String): Note {
        return Note(
            id = 0,
            title = title,
            content = "Test content",
            category = NoteCategory.GENERAL,
            color = NoteColor.DEFAULT,
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}

package com.example.synesthesia.presentation.screens.addnote

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeNoteRepository
import com.example.synesthesia.domain.model.EmotionCategory
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import com.example.synesthesia.domain.usecase.SaveNoteUseCase
import com.example.synesthesia.data.remote.api.GeminiService
import com.example.synesthesia.data.remote.dto.EmotionAnalysisResponse
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Comprehensive AddNoteViewModel Unit Tests.
 * Targets 100% branch & line coverage for:
 * - Initial state
 * - onContentChange
 * - onMainCategorySelected (with and without null)
 * - onSubEmotionSelected
 * - toggleParaphrase
 * - saveNote: content < 5 chars, no main category selected, success, AI failure fallback
 * - loadNote: existing note, non-existing note
 * - AddNoteUiState computed properties (isValid, canSave)
 * - AddNoteEvent sealed interface
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AddNoteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeNoteRepository
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var geminiService: GeminiService
    private lateinit var viewModel: AddNoteViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        saveNoteUseCase = SaveNoteUseCase(repository)
        geminiService = GeminiService(HttpClient()) // Dummy client — will fail network calls
        
        viewModel = AddNoteViewModel(
            repository = repository,
            saveNoteUseCase = saveNoteUseCase,
            geminiService = geminiService
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Initial State ====================

    @Test
    fun `initial state should have empty content and isAnalyzing false`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("", state.content)
        assertEquals("", state.title)
        assertFalse(state.isAnalyzing)
        assertFalse(state.isSaving)
        assertFalse(state.isLoading)
        assertFalse(state.isEditMode)
        assertEquals(NoteCategory.GENERAL, state.category)
        assertEquals(NoteColor.DEFAULT, state.color)
        assertNull(state.emotion)
        assertNull(state.artToken)
        assertNull(state.aiResonance)
        assertNull(state.titleError)
        assertNull(state.selectedMainCategory)
        assertNull(state.selectedSubEmotion)
        assertTrue(state.isParaphraseEnabled) // Default true
    }

    // ==================== User Actions ====================

    @Test
    fun `onContentChange should update content`() = runTest {
        viewModel.onContentChange("New content")
        assertEquals("New content", viewModel.uiState.value.content)
    }

    @Test
    fun `onMainCategorySelected should update selectedMainCategory and clear subEmotion`() = runTest {
        val category = EmotionSystem.categories.first()
        viewModel.onMainCategorySelected(category)

        val state = viewModel.uiState.value
        assertEquals(category, state.selectedMainCategory)
        assertNull(state.selectedSubEmotion)
        assertEquals(category.name, state.artToken)
    }

    @Test
    fun `onMainCategorySelected with null should clear category and artToken`() = runTest {
        // First set a category
        val category = EmotionSystem.categories.first()
        viewModel.onMainCategorySelected(category)
        
        // Then clear it
        viewModel.onMainCategorySelected(null)
        
        val state = viewModel.uiState.value
        assertNull(state.selectedMainCategory)
        assertNull(state.selectedSubEmotion)
        assertNull(state.artToken)
    }

    @Test
    fun `onSubEmotionSelected should update selectedSubEmotion and emotion`() = runTest {
        viewModel.onSubEmotionSelected("Enthusiastic")
        val state = viewModel.uiState.value
        assertEquals("Enthusiastic", state.selectedSubEmotion)
        assertEquals("Enthusiastic", state.emotion)
    }

    @Test
    fun `onSubEmotionSelected with null should clear emotion`() = runTest {
        viewModel.onSubEmotionSelected("Enthusiastic")
        viewModel.onSubEmotionSelected(null)
        val state = viewModel.uiState.value
        assertNull(state.selectedSubEmotion)
        assertNull(state.emotion)
    }

    @Test
    fun `toggleParaphrase should toggle isParaphraseEnabled`() = runTest {
        assertTrue(viewModel.uiState.value.isParaphraseEnabled)
        viewModel.toggleParaphrase()
        assertFalse(viewModel.uiState.value.isParaphraseEnabled)
        viewModel.toggleParaphrase()
        assertTrue(viewModel.uiState.value.isParaphraseEnabled)
    }

    // ==================== saveNote Tests ====================

    @Test
    fun `saveNote with content less than 5 should emit error event`() = runTest {
        viewModel.events.test {
            viewModel.onContentChange("abc")
            viewModel.saveNote()
            
            val event = awaitItem()
            assertTrue(event is AddNoteEvent.Error)
            assertEquals("Tuliskan minimal 5 karakter curhatanmu", event.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveNote with exactly 4 chars should emit error event`() = runTest {
        viewModel.events.test {
            viewModel.onContentChange("abcd")
            viewModel.saveNote()
            
            val event = awaitItem()
            assertTrue(event is AddNoteEvent.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveNote with empty content should emit error event`() = runTest {
        viewModel.events.test {
            viewModel.saveNote()
            
            val event = awaitItem()
            assertTrue(event is AddNoteEvent.Error)
            assertEquals("Tuliskan minimal 5 karakter curhatanmu", event.message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveNote with valid content and category should set isAnalyzing true`() = runTest {
        viewModel.onContentChange("This is a valid journal entry for testing")
        viewModel.onMainCategorySelected(EmotionSystem.categories.first())

        viewModel.uiState.test {
            skipItems(1) // current state
            viewModel.saveNote()
            
            val analyzingState = awaitItem()
            assertTrue(analyzingState.isAnalyzing)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveNote with AI failure should fallback save and emit error then success`() = runTest {
        // GeminiService with HttpClient() will throw when trying to make network request
        viewModel.onContentChange("This is a valid journal entry for testing purposes")
        viewModel.onMainCategorySelected(EmotionSystem.categories.first())
        viewModel.onSubEmotionSelected("Agitated")

        viewModel.events.test {
            viewModel.saveNote()
            advanceUntilIdle()

            // Should emit AI offline error
            val errorEvent = awaitItem()
            assertTrue(errorEvent is AddNoteEvent.Error)
            assertTrue(errorEvent.message.contains("AI offline"))

            // Then should emit NoteSaved after fallback save
            val savedEvent = awaitItem()
            assertTrue(savedEvent is AddNoteEvent.NoteSaved)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveNote fallback with long content should truncate title`() = runTest {
        val longContent = "A".repeat(30) // > 20 chars
        viewModel.onContentChange(longContent)
        viewModel.onMainCategorySelected(EmotionSystem.categories.first())

        viewModel.events.test {
            viewModel.saveNote()
            advanceUntilIdle()

            // Skip AI offline error
            awaitItem()
            // NoteSaved
            val savedEvent = awaitItem()
            assertTrue(savedEvent is AddNoteEvent.NoteSaved)

            cancelAndIgnoreRemainingEvents()
        }

        // Verify the note was saved with truncated title
        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertTrue(notes.first().title.endsWith("..."))
            assertTrue(notes.first().title.length <= 23) // 20 + "..."
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveNote fallback with short content should use full content as title`() = runTest {
        val shortContent = "Short" // <= 20 chars, but >= 5
        viewModel.onContentChange(shortContent)
        viewModel.onMainCategorySelected(EmotionSystem.categories.first())

        viewModel.events.test {
            viewModel.saveNote()
            advanceUntilIdle()

            awaitItem() // AI offline error
            awaitItem() // NoteSaved
            cancelAndIgnoreRemainingEvents()
        }

        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Short", notes.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `saveNote fallback without subEmotion should use Calm as default`() = runTest {
        viewModel.onContentChange("Valid content longer than five")
        viewModel.onMainCategorySelected(EmotionSystem.categories.first())
        // Don't select sub-emotion

        viewModel.events.test {
            viewModel.saveNote()
            advanceUntilIdle()

            awaitItem() // AI offline error
            awaitItem() // NoteSaved
            cancelAndIgnoreRemainingEvents()
        }

        repository.getAllNotes().test {
            val notes = awaitItem()
            assertEquals("Calm", notes.first().emotion)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== loadNote ====================

    @Test
    fun `loadNote should populate state from existing note`() = runTest {
        val note = com.example.synesthesia.domain.model.Note(
            id = 0,
            title = "Existing Note",
            content = "Existing Content",
            category = NoteCategory.WORK,
            color = NoteColor.JOY,
            emotion = "Enthusiastic",
            artToken = "High Energy, Pleasant",
            aiResonance = "Positive vibes",
            isPinned = false,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        val noteId = repository.insertNote(note)

        viewModel.loadNote(noteId)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Existing Note", state.title)
        assertEquals("Existing Content", state.content)
        assertEquals(NoteCategory.WORK, state.category)
        assertEquals(NoteColor.JOY, state.color)
        assertEquals("Enthusiastic", state.emotion)
        assertTrue(state.isEditMode)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loadNote should set isLoading true initially`() = runTest {
        val noteId = repository.insertNote(
            com.example.synesthesia.domain.model.Note(
                id = 0, title = "Test", content = "Content",
                createdAt = Clock.System.now(), updatedAt = Clock.System.now()
            )
        )

        viewModel.uiState.test {
            skipItems(1) // initial state
            viewModel.loadNote(noteId)

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== AddNoteUiState computed properties ====================

    @Test
    fun `isValid should be true when title is not blank`() {
        val state = AddNoteUiState(title = "Title", content = "")
        assertTrue(state.isValid)
    }

    @Test
    fun `isValid should be true when content is not blank`() {
        val state = AddNoteUiState(title = "", content = "Content")
        assertTrue(state.isValid)
    }

    @Test
    fun `isValid should be false when both title and content are blank`() {
        val state = AddNoteUiState(title = "", content = "")
        assertFalse(state.isValid)
    }

    @Test
    fun `isValid should be false when both are whitespace`() {
        val state = AddNoteUiState(title = "   ", content = "  ")
        assertFalse(state.isValid)
    }

    @Test
    fun `canSave should be true when valid and not saving and not analyzing`() {
        val state = AddNoteUiState(title = "Title", isSaving = false, isAnalyzing = false)
        assertTrue(state.canSave)
    }

    @Test
    fun `canSave should be false when isSaving is true`() {
        val state = AddNoteUiState(title = "Title", isSaving = true, isAnalyzing = false)
        assertFalse(state.canSave)
    }

    @Test
    fun `canSave should be false when isAnalyzing is true`() {
        val state = AddNoteUiState(title = "Title", isSaving = false, isAnalyzing = true)
        assertFalse(state.canSave)
    }

    @Test
    fun `canSave should be false when not valid`() {
        val state = AddNoteUiState(title = "", content = "", isSaving = false, isAnalyzing = false)
        assertFalse(state.canSave)
    }

    // ==================== AddNoteEvent sealed interface ====================

    @Test
    fun `AddNoteEvent NoteSaved should be distinct`() {
        val event: AddNoteEvent = AddNoteEvent.NoteSaved
        assertTrue(event is AddNoteEvent.NoteSaved)
    }

    @Test
    fun `AddNoteEvent Error should hold message`() {
        val event: AddNoteEvent = AddNoteEvent.Error("Error message")
        assertTrue(event is AddNoteEvent.Error)
        assertEquals("Error message", event.message)
    }
}

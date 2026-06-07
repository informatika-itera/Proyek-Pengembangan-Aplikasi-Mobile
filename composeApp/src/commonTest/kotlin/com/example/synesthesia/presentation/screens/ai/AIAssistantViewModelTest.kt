package com.example.synesthesia.presentation.screens.ai

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeAIRepository
import com.example.synesthesia.domain.repository.WritingStyle
import com.example.synesthesia.domain.usecase.GenerateIdeasUseCase
import com.example.synesthesia.domain.usecase.ImproveWritingUseCase
import com.example.synesthesia.domain.usecase.SummarizeNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Comprehensive unit tests for AIAssistantViewModel.
 * Targets 100% branch & line coverage for all actions, events, and state transitions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AIAssistantViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var aiRepository: FakeAIRepository
    private lateinit var summarizeUseCase: SummarizeNoteUseCase
    private lateinit var improveWritingUseCase: ImproveWritingUseCase
    private lateinit var generateIdeasUseCase: GenerateIdeasUseCase
    private lateinit var viewModel: AIAssistantViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        aiRepository = FakeAIRepository()
        summarizeUseCase = SummarizeNoteUseCase(aiRepository)
        improveWritingUseCase = ImproveWritingUseCase(aiRepository)
        generateIdeasUseCase = GenerateIdeasUseCase(aiRepository)
        viewModel = AIAssistantViewModel(
            aiRepository = aiRepository,
            summarizeUseCase = summarizeUseCase,
            improveWritingUseCase = improveWritingUseCase,
            generateIdeasUseCase = generateIdeasUseCase
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== Initial State ====================

    @Test
    fun `initial state should have empty input and SUMMARIZE action`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("", state.inputText)
        assertEquals(AIAction.SUMMARIZE, state.selectedAction)
        assertEquals(WritingStyle.NEUTRAL, state.writingStyle)
        assertEquals("English", state.targetLanguage)
        assertFalse(state.isLoading)
        assertNull(state.result)
        assertNull(state.error)
    }

    @Test
    fun `initial canExecute should be false when input is blank`() {
        assertFalse(viewModel.uiState.value.canExecute)
    }

    // ==================== User Input Actions ====================

    @Test
    fun `setInitialText should set inputText from non-null value`() = runTest {
        viewModel.setInitialText("Pre-filled text")
        assertEquals("Pre-filled text", viewModel.uiState.value.inputText)
    }

    @Test
    fun `setInitialText should not change state for null value`() = runTest {
        viewModel.setInitialText(null)
        assertEquals("", viewModel.uiState.value.inputText)
    }

    @Test
    fun `onInputTextChange should update inputText and clear error`() = runTest {
        // Set an error first
        viewModel.executeAction() // blank input -> error
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.error != null)

        // Now change input
        viewModel.onInputTextChange("New text")
        assertEquals("New text", viewModel.uiState.value.inputText)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onActionSelected should update selected action`() = runTest {
        viewModel.onActionSelected(AIAction.TRANSLATE)
        assertEquals(AIAction.TRANSLATE, viewModel.uiState.value.selectedAction)
    }

    @Test
    fun `onWritingStyleChange should update writing style`() = runTest {
        viewModel.onWritingStyleChange(WritingStyle.FORMAL)
        assertEquals(WritingStyle.FORMAL, viewModel.uiState.value.writingStyle)
    }

    @Test
    fun `onTargetLanguageChange should update target language`() = runTest {
        viewModel.onTargetLanguageChange("Japanese")
        assertEquals("Japanese", viewModel.uiState.value.targetLanguage)
    }

    @Test
    fun `canExecute should be true when input is non-blank and not loading`() = runTest {
        viewModel.onInputTextChange("Valid input")
        assertTrue(viewModel.uiState.value.canExecute)
    }

    // ==================== executeAction - Blank Input ====================

    @Test
    fun `executeAction with blank input should set error`() = runTest {
        viewModel.executeAction()
        advanceUntilIdle()
        assertEquals("Masukkan teks terlebih dahulu", viewModel.uiState.value.error)
    }

    // ==================== executeAction - SUMMARIZE ====================

    @Test
    fun `executeAction SUMMARIZE success should update result`() = runTest {
        val longText = "A".repeat(100)
        aiRepository.summarizeResult = Result.success("Summary output")
        viewModel.onInputTextChange(longText)
        viewModel.onActionSelected(AIAction.SUMMARIZE)

        viewModel.uiState.test {
            skipItems(1) // current state

            viewModel.executeAction()

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)
            assertNull(loadingState.result)

            advanceUntilIdle()
            val resultState = awaitItem()
            assertFalse(resultState.isLoading)
            assertEquals("Summary output", resultState.result)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `executeAction SUMMARIZE failure should set error`() = runTest {
        val longText = "Short" // Too short for summarize (< 50 chars)
        viewModel.onInputTextChange(longText)
        viewModel.onActionSelected(AIAction.SUMMARIZE)

        viewModel.executeAction()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.error != null)
    }

    // ==================== executeAction - GENERATE_IDEAS ====================

    @Test
    fun `executeAction GENERATE_IDEAS success should format ideas as numbered list`() = runTest {
        aiRepository.generateIdeasResult = Result.success(listOf("Idea A", "Idea B", "Idea C"))
        viewModel.onInputTextChange("Technology")
        viewModel.onActionSelected(AIAction.GENERATE_IDEAS)

        viewModel.executeAction()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.result!!.contains("1. Idea A"))
        assertTrue(state.result!!.contains("2. Idea B"))
        assertTrue(state.result!!.contains("3. Idea C"))
    }

    @Test
    fun `executeAction GENERATE_IDEAS failure should set error`() = runTest {
        aiRepository.generateIdeasResult = Result.failure(Exception("No ideas"))
        viewModel.onInputTextChange("Valid topic")
        viewModel.onActionSelected(AIAction.GENERATE_IDEAS)

        viewModel.executeAction()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
    }

    // ==================== executeAction - IMPROVE_WRITING ====================

    @Test
    fun `executeAction IMPROVE_WRITING success should update result`() = runTest {
        aiRepository.improveWritingResult = Result.success("Improved version")
        viewModel.onInputTextChange("Some text to improve")
        viewModel.onActionSelected(AIAction.IMPROVE_WRITING)

        viewModel.executeAction()
        advanceUntilIdle()

        assertEquals("Improved version", viewModel.uiState.value.result)
    }

    @Test
    fun `executeAction IMPROVE_WRITING failure should set error`() = runTest {
        aiRepository.improveWritingResult = Result.failure(Exception("AI offline"))
        viewModel.onInputTextChange("Text to improve")
        viewModel.onActionSelected(AIAction.IMPROVE_WRITING)

        viewModel.executeAction()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
    }

    // ==================== executeAction - TRANSLATE ====================

    @Test
    fun `executeAction TRANSLATE success should update result`() = runTest {
        aiRepository.translateResult = Result.success("Translated text")
        viewModel.onInputTextChange("Hello world")
        viewModel.onActionSelected(AIAction.TRANSLATE)

        viewModel.executeAction()
        advanceUntilIdle()

        assertEquals("Translated text", viewModel.uiState.value.result)
    }

    @Test
    fun `executeAction TRANSLATE failure should set error`() = runTest {
        aiRepository.translateResult = Result.failure(Exception("Translation failed"))
        viewModel.onInputTextChange("Text to translate")
        viewModel.onActionSelected(AIAction.TRANSLATE)

        viewModel.executeAction()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
    }

    // ==================== executeAction - SUGGEST_TITLE ====================

    @Test
    fun `executeAction SUGGEST_TITLE success should update result`() = runTest {
        aiRepository.suggestTitleResult = Result.success("Great Title")
        viewModel.onInputTextChange("Some content for a note")
        viewModel.onActionSelected(AIAction.SUGGEST_TITLE)

        viewModel.executeAction()
        advanceUntilIdle()

        assertEquals("Great Title", viewModel.uiState.value.result)
    }

    @Test
    fun `executeAction SUGGEST_TITLE failure should set error`() = runTest {
        aiRepository.suggestTitleResult = Result.failure(Exception("No title"))
        viewModel.onInputTextChange("Content")
        viewModel.onActionSelected(AIAction.SUGGEST_TITLE)

        viewModel.executeAction()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
    }

    // ==================== executeAction - CHAT ====================

    @Test
    fun `executeAction CHAT success should update result`() = runTest {
        aiRepository.chatResult = Result.success("AI Response")
        viewModel.onInputTextChange("How are you?")
        viewModel.onActionSelected(AIAction.CHAT)

        viewModel.executeAction()
        advanceUntilIdle()

        assertEquals("AI Response", viewModel.uiState.value.result)
    }

    @Test
    fun `executeAction CHAT failure should set error`() = runTest {
        aiRepository.chatResult = Result.failure(Exception("Chat error"))
        viewModel.onInputTextChange("Hello")
        viewModel.onActionSelected(AIAction.CHAT)

        viewModel.executeAction()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.error != null)
    }

    // ==================== copyResult ====================

    @Test
    fun `copyResult should emit CopyToClipboard event when result exists`() = runTest {
        // First, set a result
        aiRepository.chatResult = Result.success("Copy me")
        viewModel.onInputTextChange("test")
        viewModel.onActionSelected(AIAction.CHAT)
        viewModel.executeAction()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.copyResult()
            val event = awaitItem()
            assertTrue(event is AIAssistantEvent.CopyToClipboard)
            assertEquals("Copy me", (event as AIAssistantEvent.CopyToClipboard).text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `copyResult should not emit event when result is null`() = runTest {
        // result is null by default
        viewModel.events.test {
            viewModel.copyResult()
            // No events should be emitted
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== applyToNote ====================

    @Test
    fun `applyToNote should emit ApplyToNote event when result exists`() = runTest {
        aiRepository.chatResult = Result.success("Apply me")
        viewModel.onInputTextChange("test")
        viewModel.onActionSelected(AIAction.CHAT)
        viewModel.executeAction()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.applyToNote()
            val event = awaitItem()
            assertTrue(event is AIAssistantEvent.ApplyToNote)
            assertEquals("Apply me", (event as AIAssistantEvent.ApplyToNote).text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applyToNote should not emit event when result is null`() = runTest {
        viewModel.events.test {
            viewModel.applyToNote()
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== AIAction enum ====================

    @Test
    fun `AIAction enum should have correct display names`() {
        assertEquals("Ringkas", AIAction.SUMMARIZE.displayName)
        assertEquals("Ide", AIAction.GENERATE_IDEAS.displayName)
        assertEquals("Perbaiki", AIAction.IMPROVE_WRITING.displayName)
        assertEquals("Terjemah", AIAction.TRANSLATE.displayName)
        assertEquals("Judul", AIAction.SUGGEST_TITLE.displayName)
        assertEquals("Tanya", AIAction.CHAT.displayName)
    }

    @Test
    fun `AIAction enum should have correct descriptions`() {
        assertEquals("Buat ringkasan dari teks", AIAction.SUMMARIZE.description)
        assertEquals("Generate ide berdasarkan topik", AIAction.GENERATE_IDEAS.description)
        assertEquals("Perbaiki tulisan", AIAction.IMPROVE_WRITING.description)
        assertEquals("Terjemahkan ke bahasa lain", AIAction.TRANSLATE.description)
        assertEquals("Sarankan judul", AIAction.SUGGEST_TITLE.description)
        assertEquals("Tanya AI tentang apapun", AIAction.CHAT.description)
    }

    @Test
    fun `AIAction enum should have 6 entries`() {
        assertEquals(6, AIAction.entries.size)
    }

    // ==================== AIAssistantUiState ====================

    @Test
    fun `AIAssistantUiState canExecute should be false when blank input`() {
        val state = AIAssistantUiState(inputText = "")
        assertFalse(state.canExecute)
    }

    @Test
    fun `AIAssistantUiState canExecute should be false when loading`() {
        val state = AIAssistantUiState(inputText = "text", isLoading = true)
        assertFalse(state.canExecute)
    }

    @Test
    fun `AIAssistantUiState canExecute should be true when valid input and not loading`() {
        val state = AIAssistantUiState(inputText = "text", isLoading = false)
        assertTrue(state.canExecute)
    }

    @Test
    fun `AIAssistantUiState canExecute should be false for whitespace only input`() {
        val state = AIAssistantUiState(inputText = "   ")
        assertFalse(state.canExecute)
    }
}

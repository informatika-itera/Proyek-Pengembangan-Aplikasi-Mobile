package com.example.rewind.presentation

import app.cash.turbine.test
import com.example.rewind.data.repository.FakeAIRepository
import com.example.rewind.domain.repository.WritingStyle
import com.example.rewind.domain.usecase.GenerateIdeasUseCase
import com.example.rewind.domain.usecase.ImproveWritingUseCase
import com.example.rewind.domain.usecase.SummarizeNoteUseCase
import com.example.rewind.presentation.screens.ai.AIAction
import com.example.rewind.presentation.screens.ai.AIAssistantEvent
import com.example.rewind.presentation.screens.ai.AIAssistantViewModel
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AIAssistantViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeAIRepository: FakeAIRepository
    private lateinit var viewModel: AIAssistantViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeAIRepository = FakeAIRepository()
        viewModel = AIAssistantViewModel(
            aiRepository = fakeAIRepository,
            summarizeUseCase = SummarizeNoteUseCase(fakeAIRepository),
            improveWritingUseCase = ImproveWritingUseCase(fakeAIRepository),
            generateIdeasUseCase = GenerateIdeasUseCase(fakeAIRepository)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState has correct default values`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("", state.inputText)
        assertEquals(AIAction.CHAT, state.selectedAction)
        assertEquals(WritingStyle.NEUTRAL, state.writingStyle)
        assertFalse(state.isLoading)
        assertNull(state.result)
        assertNull(state.error)
    }

    @Test
    fun `onInputTextChange updates inputText and clears existing error`() = runTest {
        viewModel.executeAction()
        advanceUntilIdle()

        viewModel.onInputTextChange("Tell me about Interstellar")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Tell me about Interstellar", state.inputText)
        assertNull(state.error)
    }

    @Test
    fun `onActionSelected updates selectedAction in uiState`() = runTest {
        viewModel.onActionSelected(AIAction.SUMMARIZE)
        advanceUntilIdle()

        assertEquals(AIAction.SUMMARIZE, viewModel.uiState.value.selectedAction)
    }

    @Test
    fun `executeAction with blank input sets error and does not trigger loading`() = runTest {
        viewModel.executeAction()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertFalse(state.isLoading)
        assertNull(state.result)
    }

    @Test
    fun `executeAction CHAT success updates result and clears loading state`() = runTest {
        fakeAIRepository.fakeResult = "Parasite is a masterclass in tension."
        viewModel.onInputTextChange("Tell me about Parasite")
        viewModel.onActionSelected(AIAction.CHAT)

        viewModel.executeAction()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Parasite is a masterclass in tension.", state.result)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `copyResult emits CopyToClipboard event with correct result text`() = runTest {
        fakeAIRepository.fakeResult = "Inception bends reality."
        viewModel.onInputTextChange("Describe Inception")
        viewModel.executeAction()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.copyResult()
            val event = awaitItem()
            assertTrue(event is AIAssistantEvent.CopyToClipboard)
            assertEquals("Inception bends reality.", (event as AIAssistantEvent.CopyToClipboard).text)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `applyToNote emits ApplyToNote event with correct result text`() = runTest {
        fakeAIRepository.fakeResult = "A stunning visual journey."
        viewModel.onInputTextChange("Describe Interstellar")
        viewModel.executeAction()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.applyToNote()
            val event = awaitItem()
            assertTrue(event is AIAssistantEvent.ApplyToNote)
            assertEquals("A stunning visual journey.", (event as AIAssistantEvent.ApplyToNote).text)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
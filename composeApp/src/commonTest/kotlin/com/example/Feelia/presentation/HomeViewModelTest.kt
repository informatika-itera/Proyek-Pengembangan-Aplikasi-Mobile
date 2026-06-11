package com.example.Feelia.presentation

import app.cash.turbine.test
import com.example.Feelia.data.repository.FakeNoteRepository
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.domain.usecase.DeleteNoteUseCase
import com.example.Feelia.domain.usecase.GetAllNotesUseCase
import com.example.Feelia.domain.usecase.SearchNotesUseCase
import com.example.Feelia.presentation.screens.home.HomeUiState
import com.example.Feelia.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeNoteRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeNoteRepository()
        viewModel = HomeViewModel(
            getAllNotesUseCase = GetAllNotesUseCase(fakeRepository),
            searchNotesUseCase = SearchNotesUseCase(fakeRepository),
            deleteNoteUseCase = DeleteNoteUseCase(fakeRepository),
            repository = fakeRepository
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        viewModel.uiState.test {
            assertIs<HomeUiState.Loading>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty repository shows Empty state`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<HomeUiState.Empty>(state)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `notes available shows Success state`() = runTest {
        fakeRepository.insertNote(createTestNote("Jurnal hari ini", Emotion.HAPPY))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<HomeUiState.Success>(state)
            assertEquals(1, state.notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `multiple notes all appear in Success state`() = runTest {
        fakeRepository.insertNote(createTestNote("Jurnal 1", Emotion.HAPPY))
        fakeRepository.insertNote(createTestNote("Jurnal 2", Emotion.SAD))
        fakeRepository.insertNote(createTestNote("Jurnal 3", Emotion.ANXIOUS))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            assertIs<HomeUiState.Success>(state)
            assertEquals(3, state.notes.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emotion filter shows only matching notes`() = runTest {
        fakeRepository.insertNote(createTestNote("Senang banget", Emotion.HAPPY))
        fakeRepository.insertNote(createTestNote("Sedih hari ini", Emotion.SAD))
        fakeRepository.insertNote(createTestNote("Bahagia", Emotion.HAPPY))
        viewModel.onEmotionSelected(Emotion.HAPPY)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            if (state is HomeUiState.Success) {
                assertEquals(2, state.notes.size)
                assertTrue(state.notes.all { it.emotion == Emotion.HAPPY })
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clear emotion filter shows all notes`() = runTest {
        fakeRepository.insertNote(createTestNote("Senang", Emotion.HAPPY))
        fakeRepository.insertNote(createTestNote("Sedih", Emotion.SAD))
        viewModel.onEmotionSelected(Emotion.HAPPY)
        viewModel.onEmotionSelected(null)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            if (state is HomeUiState.Success) {
                assertEquals(2, state.notes.size)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `delete note removes it from list`() = runTest {
        val id = fakeRepository.insertNote(createTestNote("Akan dihapus"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.deleteNote(id)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            assertIs<HomeUiState.Empty>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle pin updates note pinned state`() = runTest {
        val id = fakeRepository.insertNote(createTestNote("Pin jurnal ini"))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.togglePin(id)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            if (state is HomeUiState.Success) {
                assertTrue(state.notes.first { it.id == id }.isPinned)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pinned notes appear first in list`() = runTest {
        val id1 = fakeRepository.insertNote(createTestNote("Normal note"))
        val id2 = fakeRepository.insertNote(createTestNote("Akan di-pin"))
        fakeRepository.togglePinNote(id2)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiState.test {
            val state = awaitItem()
            if (state is HomeUiState.Success) {
                assertEquals(id2, state.notes.first().id)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

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
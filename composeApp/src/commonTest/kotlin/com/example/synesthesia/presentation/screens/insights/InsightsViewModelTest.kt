package com.example.synesthesia.presentation.screens.insights

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeAIRepository
import com.example.synesthesia.fakes.FakeNoteRepository
import com.example.synesthesia.fakes.FakeUserPreferences
import com.example.synesthesia.domain.model.EmotionSystem
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.model.NoteColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeNoteRepository
    private lateinit var userPreferences: FakeUserPreferences
    private lateinit var aiRepository: FakeAIRepository
    private lateinit var viewModel: InsightsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        userPreferences = FakeUserPreferences()
        aiRepository = FakeAIRepository()
        viewModel = InsightsViewModel(repository, userPreferences, aiRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState should be Empty initially when no notes exist`() = runTest {
        viewModel.uiState.test {
            skipItems(1) // Loading
            assertTrue(awaitItem() is InsightsUiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState should be Success when notes exist`() = runTest {
        repository.insertNote(
            Note(
                id = 0, title = "T", content = "C", category = NoteCategory.GENERAL, color = NoteColor.DEFAULT,
                emotion = "Enthusiastic", artToken = EmotionSystem.categories.first().name,
                isPinned = false, createdAt = Clock.System.now(), updatedAt = Clock.System.now()
            )
        )

        viewModel.uiState.test {
            skipItems(1) // Loading
            val success = awaitItem() as InsightsUiState.Success
            assertEquals(1, success.totalMemories)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleEditProfile should flip isEditingProfile`() = runTest {
        viewModel.isEditingProfile.test {
            assertFalse(awaitItem())
            viewModel.toggleEditProfile()
            assertTrue(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateProfile should update user preferences and turn off editing`() = runTest {
        viewModel.toggleEditProfile()
        viewModel.updateProfile("New Name", "New Bio", "uri")
        advanceUntilIdle()

        assertFalse(viewModel.isEditingProfile.value)
    }

    @Test
    fun `triggerWeeklySummary with no notes should set empty message`() = runTest {
        viewModel.weeklySummary.test {
            assertEquals(null, awaitItem())
            viewModel.triggerWeeklySummary()
            advanceUntilIdle()
            assertEquals("Belum ada catatan untuk dianalisis.", expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `triggerWeeklySummary with notes should generate summary from AI`() = runTest {
        repository.insertNote(
            Note(
                id = 0, title = "T", content = "C", category = NoteCategory.GENERAL, color = NoteColor.DEFAULT,
                emotion = "Enthusiastic", isPinned = false, createdAt = Clock.System.now(), updatedAt = Clock.System.now()
            )
        )
        aiRepository.chatResult = Result.success("AI Weekly Summary")

        viewModel.weeklySummary.test {
            assertEquals(null, awaitItem())
            viewModel.triggerWeeklySummary()
            advanceUntilIdle()
            assertEquals("AI Weekly Summary", expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `triggerWeeklySummary failure should set error message`() = runTest {
        repository.insertNote(
            Note(
                id = 0, title = "T", content = "C", category = NoteCategory.GENERAL, color = NoteColor.DEFAULT,
                emotion = "Sad", isPinned = false, createdAt = Clock.System.now(), updatedAt = Clock.System.now()
            )
        )
        aiRepository.chatResult = Result.failure(Exception("Error"))

        viewModel.weeklySummary.test {
            assertEquals(null, awaitItem())
            viewModel.triggerWeeklySummary()
            advanceUntilIdle()
            assertEquals("Gagal memproses analisis jiwa.", expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

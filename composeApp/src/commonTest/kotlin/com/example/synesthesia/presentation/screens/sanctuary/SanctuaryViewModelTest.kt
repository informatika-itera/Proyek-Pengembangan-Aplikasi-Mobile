package com.example.synesthesia.presentation.screens.sanctuary

import app.cash.turbine.test
import com.example.synesthesia.fakes.FakeAIRepository
import com.example.synesthesia.fakes.FakeNoteRepository
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
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class SanctuaryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeNoteRepository
    private lateinit var aiRepository: FakeAIRepository
    private lateinit var viewModel: SanctuaryViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNoteRepository()
        aiRepository = FakeAIRepository()
        viewModel = SanctuaryViewModel(repository, aiRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `generateAiRecommendation with no notes should suggest writing first journal`() = runTest {
        viewModel.generateAiRecommendation()
        advanceUntilIdle()
        
        viewModel.aiRecommendation.test {
            val recommendation = awaitItem()
            assertNotNull(recommendation)
            assertEquals("Mulai Menulis", recommendation?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `generateAiRecommendation with notes should use AI response`() = runTest {
        repository.insertNote(
            Note(
                id = 0, title = "T", content = "C", category = NoteCategory.GENERAL, color = NoteColor.DEFAULT,
                emotion = "Enthusiastic", isPinned = false, createdAt = Clock.System.now(), updatedAt = Clock.System.now()
            )
        )
        aiRepository.chatResult = Result.success("""{ "ritualId": "meditation", "reason": "Test Reason" }""")
        
        viewModel.generateAiRecommendation()
        advanceUntilIdle()

        viewModel.aiRecommendation.test {
            val recommendation = awaitItem()
            assertNotNull(recommendation)
            assertEquals("Rekomendasi AI ✨", recommendation?.title)
            assertEquals("Test Reason", recommendation?.message)
            assertTrue(recommendation?.suggestedRituals?.contains("meditation") == true)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startGrounding should reset step and list`() = runTest {
        viewModel.startGrounding()
        viewModel.groundingStep.test {
            assertEquals(1, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.groundingList.test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addGroundingInput should progress through steps and trigger AI on step 6`() = runTest {
        viewModel.startGrounding()
        
        for (i in 1..5) {
            viewModel.addGroundingInput("Item $i")
        }
        advanceUntilIdle()
        
        assertEquals(6, viewModel.groundingStep.value)
        assertEquals(5, viewModel.groundingList.value.size)
        
        // AI should be triggered
        assertNotNull(viewModel.vaultAiResponse.value)
    }

    @Test
    fun `worryVault flow should work correctly`() = runTest {
        viewModel.setWorryText("I'm worried about tests")
        assertEquals("I'm worried about tests", viewModel.worryVaultText.value)
        
        viewModel.lockWorryVault()
        advanceUntilIdle()
        
        assertTrue(viewModel.isVaultLocked.value)
        assertNotNull(viewModel.vaultAiResponse.value)
        
        viewModel.resetVault()
        assertEquals("", viewModel.worryVaultText.value)
        assertFalse(viewModel.isVaultLocked.value)
        assertEquals(null, viewModel.vaultAiResponse.value)
    }
    
    @Test
    fun `resetGrounding should clear all states`() = runTest {
        viewModel.startGrounding()
        viewModel.addGroundingInput("Test")
        viewModel.resetGrounding()
        
        assertEquals(0, viewModel.groundingStep.value)
        assertTrue(viewModel.groundingList.value.isEmpty())
        assertEquals(null, viewModel.vaultAiResponse.value)
    }
    
    @Test
    fun `lockWorryVault with empty text should not lock`() = runTest {
        viewModel.setWorryText("   ")
        viewModel.lockWorryVault()
        assertFalse(viewModel.isVaultLocked.value)
    }
}

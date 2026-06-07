package com.example.bridgebit.presentation.screens.detail

import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.TranslationRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class TranslationDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TranslationRepository
    private lateinit var viewModel: TranslationDetailViewModel

    private val sampleTranslation = Translation(
        id = 10L,
        sourceText = "Blockchain",
        translatedText = "Rantai Blok",
        sourceLanguage = "Inggris",
        targetLanguage = "Indonesia",
        category = "Keuangan & Kripto",
        isVaulted = false,
        createdAt = 5000L,
        updatedAt = 5000L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = TranslationDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Test 1: Initial state ────────────────────────────────────────────────
    @Test
    fun `initial uiState is Loading`() {
        assertIs<DetailUiState.Loading>(viewModel.uiState.value)
    }

    // ─── Test 2: Load existing translation → Success ──────────────────────────
    @Test
    fun `loadTranslationDetails emits Success when translation exists`() = runTest {
        every { repository.getTranslationById(10L) } returns flowOf(sampleTranslation)

        viewModel.loadTranslationDetails(10L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<DetailUiState.Success>(state)
        assertEquals("Blockchain", state.translation.sourceText)
        assertEquals("Rantai Blok", state.translation.translatedText)
        assertEquals(10L, state.translation.id)
    }

    // ─── Test 3: Load non-existent translation → Error ───────────────────────
    @Test
    fun `loadTranslationDetails emits Error when translation not found`() = runTest {
        every { repository.getTranslationById(99L) } returns flowOf(null)

        viewModel.loadTranslationDetails(99L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<DetailUiState.Error>(state)
        assertEquals("Data terjemahan tidak ditemukan", state.message)
    }

    // ─── Test 4: Correct category loaded ─────────────────────────────────────
    @Test
    fun `loadTranslationDetails loads correct category from translation`() = runTest {
        every { repository.getTranslationById(10L) } returns flowOf(sampleTranslation)

        viewModel.loadTranslationDetails(10L)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<DetailUiState.Success>(state)
        assertEquals("Keuangan & Kripto", state.translation.category)
    }
}
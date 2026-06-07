package com.example.bridgebit.presentation.screens.dashboard

import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.usecase.DeleteTranslationUseCase
import com.example.bridgebit.domain.usecase.SearchHistoryUseCase
import com.example.bridgebit.domain.usecase.ToggleVaultStatusUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var searchHistoryUseCase: SearchHistoryUseCase
    private lateinit var deleteTranslationUseCase: DeleteTranslationUseCase
    private lateinit var toggleVaultStatusUseCase: ToggleVaultStatusUseCase
    private lateinit var viewModel: DashboardViewModel

    private val historyFlow = MutableStateFlow<List<Translation>>(emptyList())

    private val sampleTranslations = listOf(
        Translation(
            id = 1L, sourceText = "Hello", translatedText = "Halo",
            sourceLanguage = "Inggris", targetLanguage = "Indonesia",
            category = "Umum", isVaulted = false,
            createdAt = 1000L, updatedAt = 1000L
        ),
        Translation(
            id = 2L, sourceText = "Smart Contract", translatedText = "Kontrak Pintar",
            sourceLanguage = "Inggris", targetLanguage = "Indonesia",
            category = "Keuangan & Kripto", isVaulted = true,
            createdAt = 2000L, updatedAt = 2000L
        ),
        Translation(
            id = 3L, sourceText = "Machine Learning", translatedText = "Pembelajaran Mesin",
            sourceLanguage = "Inggris", targetLanguage = "Indonesia",
            category = "Teknologi & IT", isVaulted = false,
            createdAt = 3000L, updatedAt = 3000L
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        searchHistoryUseCase = mockk()
        deleteTranslationUseCase = mockk(relaxed = true)
        toggleVaultStatusUseCase = mockk(relaxed = true)

        every { searchHistoryUseCase(any()) } returns historyFlow

        viewModel = DashboardViewModel(
            searchHistoryUseCase,
            deleteTranslationUseCase,
            toggleVaultStatusUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Helper: subscribe agar WhileSubscribed aktif + skip debounce 300ms
    private fun TestScope.collectAndIdle(): Job {
        val job = launch { viewModel.uiState.collect {} }
        return job
    }

    private suspend fun TestScope.skipDebounceAndIdle() {
        advanceTimeBy(301)
        advanceUntilIdle()
    }

    // ─── Test 1 ───────────────────────────────────────────────────────────────
    @Test
    fun `initial uiState is Loading`() {
        assertEquals(DashboardUiState.Loading, viewModel.uiState.value)
    }

    // ─── Test 2 ───────────────────────────────────────────────────────────────
    @Test
    fun `when history is empty, uiState becomes Empty`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = emptyList()
        skipDebounceAndIdle()

        assertEquals(DashboardUiState.Empty, viewModel.uiState.value)
        job.cancel()
    }

    // ─── Test 3 ───────────────────────────────────────────────────────────────
    @Test
    fun `when history has data, uiState becomes Success with correct list`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = sampleTranslations
        skipDebounceAndIdle()

        val state = viewModel.uiState.value
        assertIs<DashboardUiState.Success>(state)
        assertEquals(3, state.history.size)
        job.cancel()
    }

    // ─── Test 4 ───────────────────────────────────────────────────────────────
    @Test
    fun `when vaultOnly filter is on, only vaulted items are shown`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = sampleTranslations
        skipDebounceAndIdle()

        viewModel.toggleVaultFilter()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<DashboardUiState.Success>(state)
        assertTrue(state.history.all { it.isVaulted })
        assertEquals(1, state.history.size)
        assertEquals(2L, state.history.first().id)
        job.cancel()
    }

    // ─── Test 5 ───────────────────────────────────────────────────────────────
    @Test
    fun `toggling vault filter twice resets to show all items`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = sampleTranslations
        skipDebounceAndIdle()

        viewModel.toggleVaultFilter()
        viewModel.toggleVaultFilter()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<DashboardUiState.Success>(state)
        assertEquals(3, state.history.size)
        job.cancel()
    }

    // ─── Test 6 ───────────────────────────────────────────────────────────────
    @Test
    fun `when category filter is set, only matching items are shown`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = sampleTranslations
        skipDebounceAndIdle()

        viewModel.setCategoryFilter("Teknologi & IT")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<DashboardUiState.Success>(state)
        assertEquals(1, state.history.size)
        assertEquals("Teknologi & IT", state.history.first().category)
        job.cancel()
    }

    // ─── Test 7 ───────────────────────────────────────────────────────────────
    @Test
    fun `when language filter is set, items matching source or target language are shown`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = sampleTranslations
        skipDebounceAndIdle()

        viewModel.setLanguageFilter("Inggris")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<DashboardUiState.Success>(state)
        assertEquals(3, state.history.size)
        job.cancel()
    }

    // ─── Test 8 ───────────────────────────────────────────────────────────────
    @Test
    fun `resetFilters clears all active filters`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = sampleTranslations
        skipDebounceAndIdle()

        viewModel.toggleVaultFilter()
        viewModel.setCategoryFilter("Umum")
        viewModel.setLanguageFilter("Indonesia")
        viewModel.resetFilters()
        advanceUntilIdle()

        assertEquals(FilterState(), viewModel.filterState.value)

        val state = viewModel.uiState.value
        assertIs<DashboardUiState.Success>(state)
        assertEquals(3, state.history.size)
        job.cancel()
    }

    // ─── Test 9 ───────────────────────────────────────────────────────────────
    @Test
    fun `when category filter has no matching items, uiState becomes Empty`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = sampleTranslations
        skipDebounceAndIdle()

        viewModel.setCategoryFilter("Hiburan & Hobi")
        advanceUntilIdle()

        assertEquals(DashboardUiState.Empty, viewModel.uiState.value)
        job.cancel()
    }

    // ─── Test 10 ──────────────────────────────────────────────────────────────
    @Test
    fun `deleteTranslation invokes DeleteTranslationUseCase with correct id`() = runTest {
        viewModel.deleteTranslation(42L)
        advanceUntilIdle()

        coVerify(exactly = 1) { deleteTranslationUseCase(42L) }
    }

    // ─── Test 11 ──────────────────────────────────────────────────────────────
    @Test
    fun `toggleVaultStatus invokes ToggleVaultStatusUseCase with correct id`() = runTest {
        viewModel.toggleVaultStatus(7L)
        advanceUntilIdle()

        coVerify(exactly = 1) { toggleVaultStatusUseCase(7L) }
    }

    // ─── Test 12 ──────────────────────────────────────────────────────────────
    @Test
    fun `onSearchQueryChange updates searchQuery state`() {
        viewModel.onSearchQueryChange("hello")
        assertEquals("hello", viewModel.searchQuery.value)
    }

    // ─── Test 13 ──────────────────────────────────────────────────────────────
    @Test
    fun `vault filter and category filter applied together show only matching vaulted items`() = runTest {
        val job = collectAndIdle()
        historyFlow.value = sampleTranslations
        skipDebounceAndIdle()

        viewModel.toggleVaultFilter()
        viewModel.setCategoryFilter("Keuangan & Kripto")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<DashboardUiState.Success>(state)
        assertEquals(1, state.history.size)
        assertEquals(2L, state.history.first().id)
        assertTrue(state.history.first().isVaulted)
        job.cancel()
    }
}
package com.example.bridgebit.presentation.screens.vault

import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.usecase.DeleteTranslationUseCase
import com.example.bridgebit.domain.usecase.GetVaultPhrasesUseCase
import com.example.bridgebit.domain.usecase.ToggleVaultStatusUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class VaultViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getVaultPhrasesUseCase: GetVaultPhrasesUseCase
    private lateinit var toggleVaultStatusUseCase: ToggleVaultStatusUseCase
    private lateinit var deleteTranslationUseCase: DeleteTranslationUseCase
    private lateinit var viewModel: VaultViewModel

    private val vaultFlow = MutableStateFlow<List<Translation>>(emptyList())

    private val vaultedItems = listOf(
        Translation(
            id = 1L, sourceText = "Smart Contract", translatedText = "Kontrak Pintar",
            sourceLanguage = "Inggris", targetLanguage = "Indonesia",
            category = "Keuangan & Kripto", isVaulted = true,
            createdAt = 1000L, updatedAt = 1000L
        ),
        Translation(
            id = 2L, sourceText = "Neural Network", translatedText = "Jaringan Saraf",
            sourceLanguage = "Inggris", targetLanguage = "Indonesia",
            category = "Teknologi & IT", isVaulted = true,
            createdAt = 2000L, updatedAt = 2000L
        ),
        Translation(
            id = 3L, sourceText = "Algorithm", translatedText = "Algoritma",
            sourceLanguage = "Inggris", targetLanguage = "Indonesia",
            category = "Teknologi & IT", isVaulted = true,
            createdAt = 3000L, updatedAt = 3000L
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getVaultPhrasesUseCase = mockk()
        toggleVaultStatusUseCase = mockk(relaxed = true)
        deleteTranslationUseCase = mockk(relaxed = true)

        every { getVaultPhrasesUseCase() } returns vaultFlow

        viewModel = VaultViewModel(
            getVaultPhrasesUseCase,
            toggleVaultStatusUseCase,
            deleteTranslationUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Helper: subscribe dulu agar WhileSubscribed aktif, lalu idle
    private fun TestScope.collectAndIdle(): kotlinx.coroutines.Job {
        return launch { viewModel.groupedVaultPhrases.collect {} }
    }

    // ─── Test 1 ───────────────────────────────────────────────────────────────
    @Test
    fun `initial groupedVaultPhrases is empty map`() = runTest {
        val job = collectAndIdle()
        advanceUntilIdle()

        assertTrue(viewModel.groupedVaultPhrases.value.isEmpty())
        job.cancel()
    }

    // ─── Test 2 ───────────────────────────────────────────────────────────────
    @Test
    fun `vaulted items are grouped by category correctly`() = runTest {
        val job = collectAndIdle()
        vaultFlow.value = vaultedItems
        advanceUntilIdle()

        val grouped = viewModel.groupedVaultPhrases.value
        assertEquals(2, grouped.keys.size)
        assertEquals(1, grouped["Keuangan & Kripto"]?.size)
        assertEquals(2, grouped["Teknologi & IT"]?.size)
        job.cancel()
    }

    // ─── Test 3 ───────────────────────────────────────────────────────────────
    @Test
    fun `when vault is empty, groupedVaultPhrases is empty map`() = runTest {
        val job = collectAndIdle()
        vaultFlow.value = emptyList()
        advanceUntilIdle()

        assertTrue(viewModel.groupedVaultPhrases.value.isEmpty())
        job.cancel()
    }

    // ─── Test 4 ───────────────────────────────────────────────────────────────
    @Test
    fun `unvaultTranslation calls ToggleVaultStatusUseCase with correct id`() = runTest {
        viewModel.unvaultTranslation(1L)
        advanceUntilIdle()

        coVerify(exactly = 1) { toggleVaultStatusUseCase(1L) }
    }

    // ─── Test 5 ───────────────────────────────────────────────────────────────
    @Test
    fun `deleteTranslation calls DeleteTranslationUseCase with correct id`() = runTest {
        viewModel.deleteTranslation(2L)
        advanceUntilIdle()

        coVerify(exactly = 1) { deleteTranslationUseCase(2L) }
    }

    // ─── Test 6 ───────────────────────────────────────────────────────────────
    @Test
    fun `grouped categories match the categories in vault items`() = runTest {
        val job = collectAndIdle()
        vaultFlow.value = vaultedItems
        advanceUntilIdle()

        val grouped = viewModel.groupedVaultPhrases.value
        val expectedCategories = vaultedItems.map { it.category }.toSet()
        assertEquals(expectedCategories, grouped.keys)
        job.cancel()
    }
}
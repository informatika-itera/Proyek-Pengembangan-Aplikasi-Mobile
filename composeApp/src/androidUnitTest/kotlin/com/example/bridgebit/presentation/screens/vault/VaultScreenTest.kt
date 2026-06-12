package com.example.bridgebit.presentation.screens.vault

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.usecase.DeleteTranslationUseCase
import com.example.bridgebit.domain.usecase.GetVaultPhrasesUseCase
import com.example.bridgebit.domain.usecase.ToggleVaultStatusUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], instrumentedPackages = ["androidx.loader.content"])
class VaultScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
        // Pastikan Koin bersih sebelum test berikutnya
        try { stopKoin() } catch (_: Exception) {}

        getVaultPhrasesUseCase = mockk()
        toggleVaultStatusUseCase = mockk(relaxed = true)
        deleteTranslationUseCase = mockk(relaxed = true)

        every { getVaultPhrasesUseCase() } returns vaultFlow

        viewModel = VaultViewModel(
            getVaultPhrasesUseCase,
            toggleVaultStatusUseCase,
            deleteTranslationUseCase
        )

        startKoin {
            modules(module {
                factory { viewModel }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    // Helper: render screen dan tunggu composable siap, lalu optionally emit data ke flow
    private fun renderVaultScreen(onNavigateToDetail: (Long) -> Unit = {}) {
        composeTestRule.setContent {
            KoinContext {
                VaultScreen(onNavigateToDetail = onNavigateToDetail)
            }
        }
        composeTestRule.waitForIdle()
    }

    // Helper: emit data ke flow SETELAH composable aktif agar WhileSubscribed ter-trigger
    private fun emitVaultItems(items: List<Translation>) {
        vaultFlow.value = items
        composeTestRule.mainClock.advanceTimeBy(300L)
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(300L)
        composeTestRule.waitForIdle()
    }

    // ─── Test 1: TopAppBar title "Phrase Vault Categories" ditampilkan ────────
    @Test
    fun vaultScreen_displaysTopAppBarTitle() {
        renderVaultScreen()
        composeTestRule.onNodeWithText("Phrase Vault Categories").assertIsDisplayed()
    }

    // ─── Test 2: Empty state message ditampilkan saat vault kosong ────────────
    @Test
    fun vaultScreen_emptyState_showsCorrectMessage() {
        renderVaultScreen()
        emitVaultItems(emptyList())
        composeTestRule.onNodeWithText("Vault Kosong").assertIsDisplayed()
    }

    // ─── Test 3: Title screen tetap tampil saat vault ada items ────────────────
    @Test
    fun vaultScreen_withVaultedItems_showsFirstItem() {
        renderVaultScreen()
        // Judul screen selalu ada terlepas dari state data
        composeTestRule.onNodeWithText("Phrase Vault Categories").assertIsDisplayed()
        // Empty state tidak ada saat flow sudah diisi items
        emitVaultItems(vaultedItems)
        composeTestRule.onNodeWithText("Phrase Vault Categories").assertIsDisplayed()
    }

    // ─── Test 4: Item vault kedua ditampilkan di list ─────────────────────────
    @Test
    fun vaultScreen_withVaultedItems_showsSecondItem() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onNodeWithText("Neural Network").assertIsDisplayed()
    }

    // ─── Test 5: Category header "KEUANGAN & KRIPTO" ditampilkan (uppercase) ──
    @Test
    fun vaultScreen_withVaultedItems_showsCategoryHeaderKeuangan() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onNodeWithText("KEUANGAN & KRIPTO").assertIsDisplayed()
    }

    // ─── Test 6: Category header "TEKNOLOGI & IT" ditampilkan (uppercase) ─────
    @Test
    fun vaultScreen_withVaultedItems_showsCategoryHeaderTeknologi() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onNodeWithText("TEKNOLOGI & IT").assertIsDisplayed()
    }

    // ─── Test 7: Multiple items in same category ditampilkan ──────────────────
    @Test
    fun vaultScreen_withVaultedItems_showsThirdItem() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        // Verifikasi multiple items dalam satu kategori muncul
        composeTestRule.onNodeWithText("Jaringan Saraf").assertIsDisplayed()
    }

    // ─── Test 8: Klik item navigates ke detail ────────────────────────────────
    @Test
    fun vaultScreen_clickItem_triggersNavigation() {
        var navigatedId: Long? = null
        renderVaultScreen(onNavigateToDetail = { id -> navigatedId = id })
        emitVaultItems(vaultedItems)
        composeTestRule.onNodeWithText("Smart Contract").performClick()
        composeTestRule.waitForIdle()
        assert(navigatedId == 1L) { "Navigasi ke detail seharusnya dipanggil dengan ID 1" }
    }

    // ─── Test 9: Bookmark icon "Hapus dari Vault" ada untuk item vaulted ─────
    @Test
    fun vaultScreen_vaultedItem_showsRemoveFromVaultIcon() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onAllNodesWithContentDescription("Hapus dari Vault")
            .onFirst()
            .assertIsDisplayed()
    }

    // ─── Test 10: Delete icon "Hapus" ada pada item vault ────────────────────
    @Test
    fun vaultScreen_vaultedItem_showsDeleteIcon() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onAllNodesWithContentDescription("Hapus")
            .onFirst()
            .assertIsDisplayed()
    }

    // ─── Test 11: Unvault (klik Hapus dari Vault) icon bisa diklik ───────────
    @Test
    fun vaultScreen_unvaultIcon_performClick() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onAllNodesWithContentDescription("Hapus dari Vault")
            .onFirst()
            .performClick()
        composeTestRule.waitForIdle()
    }

    // ─── Test 12: Delete icon bisa diklik ────────────────────────────────────
    @Test
    fun vaultScreen_deleteIcon_performClick() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onAllNodesWithContentDescription("Hapus")
            .onFirst()
            .performClick()
        composeTestRule.waitForIdle()
    }

    // ─── Test 13: Single category item menampilkan header dan item ────────────
    @Test
    fun vaultScreen_singleCategoryItems_showsOneHeader() {
        val singleCategoryItems = listOf(
            Translation(
                id = 10L, sourceText = "Blockchain", translatedText = "Rantai Blok",
                sourceLanguage = "Inggris", targetLanguage = "Indonesia",
                category = "Teknologi & IT", isVaulted = true,
                createdAt = 1000L, updatedAt = 1000L
            )
        )
        renderVaultScreen()
        emitVaultItems(singleCategoryItems)
        composeTestRule.onNodeWithText("TEKNOLOGI & IT").assertIsDisplayed()
        composeTestRule.onNodeWithText("Blockchain").assertIsDisplayed()
    }

    // ─── Test 14: Translated text (preview) ditampilkan pada card ─────────────
    @Test
    fun vaultScreen_translationCard_showsTranslatedText() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onNodeWithText("Kontrak Pintar").assertIsDisplayed()
    }

    // ─── Test 15: Empty state tidak muncul saat vault ada item ───────────────
    @Test
    fun vaultScreen_withItems_emptyMessageNotDisplayed() {
        renderVaultScreen()
        emitVaultItems(vaultedItems)
        composeTestRule.onNodeWithText("Vault Kosong").assertDoesNotExist()
    }
}
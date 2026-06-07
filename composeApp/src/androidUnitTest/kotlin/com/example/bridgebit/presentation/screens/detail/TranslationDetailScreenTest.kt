package com.example.bridgebit.presentation.screens.detail

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.TranslationRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class TranslationDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: TranslationRepository
    private lateinit var viewModel: TranslationDetailViewModel

    private val sampleTranslation = Translation(
        id = 1L,
        sourceText = "Decentralized Finance",
        translatedText = "Keuangan Terdesentralisasi",
        sourceLanguage = "Inggris",
        targetLanguage = "Indonesia",
        category = "Keuangan & Kripto",
        isVaulted = false,
        createdAt = 1000L,
        updatedAt = 1000L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        try { stopKoin() } catch (_: Exception) {}

        repository = mockk()
        // Default mock agar LaunchedEffect tidak crash
        every { repository.getTranslationById(any()) } returns flowOf(sampleTranslation)

        viewModel = TranslationDetailViewModel(repository)

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

    // Helper: render dan tunggu data dimuat (LaunchedEffect + coroutine)
    private fun renderAndWait(
        translationId: Long = 1L,
        onNavigateBack: () -> Unit = {},
        onNavigateToEdit: (Long) -> Unit = {}
    ) {
        composeTestRule.setContent {
            KoinContext {
                TranslationDetailScreen(
                    translationId = translationId,
                    onNavigateBack = onNavigateBack,
                    onNavigateToEdit = onNavigateToEdit
                )
            }
        }
        // Tunggu LaunchedEffect + coroutine selesai
        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()
    }

    // ─── Test 1: TopAppBar title ditampilkan ─────────────────────────────────
    @Test
    fun detailScreen_displaysTopAppBarTitle() {
        renderAndWait()
        composeTestRule.onNodeWithText("Detail Terjemahan").assertIsDisplayed()
    }

    // ─── Test 2: Tombol Kembali ditampilkan ──────────────────────────────────
    @Test
    fun detailScreen_backButton_isDisplayed() {
        renderAndWait()
        composeTestRule.onNodeWithContentDescription("Kembali").assertIsDisplayed()
    }

    // ─── Test 3: Tombol Kembali meng-trigger navigasi ────────────────────────
    @Test
    fun detailScreen_backButton_onClick_triggersNavigation() {
        var navigatedBack = false
        renderAndWait(onNavigateBack = { navigatedBack = true })
        composeTestRule.onNodeWithContentDescription("Kembali").performClick()
        composeTestRule.waitForIdle()
        assert(navigatedBack) { "onNavigateBack seharusnya dipanggil" }
    }

    // ─── Test 4: Tombol Edit ditampilkan ─────────────────────────────────────
    @Test
    fun detailScreen_editButton_isDisplayed() {
        renderAndWait()
        composeTestRule.onNodeWithContentDescription("Edit Terjemahan").assertIsDisplayed()
    }

    // ─── Test 5: Tombol Edit meng-trigger navigasi ke edit ───────────────────
    @Test
    fun detailScreen_editButton_onClick_navigatesToEditWithCorrectId() {
        var editedId: Long? = null
        renderAndWait(
            translationId = 1L,
            onNavigateToEdit = { id -> editedId = id }
        )
        composeTestRule.onNodeWithContentDescription("Edit Terjemahan").performClick()
        composeTestRule.waitForIdle()
        assert(editedId == 1L) { "onNavigateToEdit seharusnya dipanggil dengan ID 1, tapi dapat $editedId" }
    }

    // ─── Test 6: Success state menampilkan source text ───────────────────────
    @Test
    fun detailScreen_successState_showsSourceText() {
        renderAndWait()
        composeTestRule.onNodeWithText("Decentralized Finance").assertIsDisplayed()
    }

    // ─── Test 7: Success state menampilkan translated text ───────────────────
    @Test
    fun detailScreen_successState_showsTranslatedText() {
        renderAndWait()
        composeTestRule.onNodeWithText("Keuangan Terdesentralisasi").assertIsDisplayed()
    }

    // ─── Test 8: Label bahasa sumber "Dari: Inggris" ─────────────────────────
    @Test
    fun detailScreen_successState_showsSourceLanguageLabel() {
        renderAndWait()
        composeTestRule.onNodeWithText("Dari: Inggris").assertIsDisplayed()
    }

    // ─── Test 9: Label bahasa target "Ke: Indonesia" ─────────────────────────
    @Test
    fun detailScreen_successState_showsTargetLanguageLabel() {
        renderAndWait()
        composeTestRule.onNodeWithText("Ke: Indonesia").assertIsDisplayed()
    }

    // ─── Test 10: Tombol Salin ditampilkan di Success state ──────────────────
    @Test
    fun detailScreen_successState_showsCopyButton() {
        renderAndWait()
        composeTestRule.onNodeWithContentDescription("Salin").assertIsDisplayed()
    }

    // ─── Test 11: Tombol Salin bisa diklik tanpa crash ───────────────────────
    @Test
    fun detailScreen_copyButton_onClick_doesNotCrash() {
        renderAndWait()
        composeTestRule.onNodeWithContentDescription("Salin").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Detail Terjemahan").assertIsDisplayed()
    }

    // ─── Test 12: Error state menampilkan pesan error ────────────────────────
    @Test
    fun detailScreen_errorState_showsErrorMessage() {
        every { repository.getTranslationById(999L) } returns flowOf(null)
        renderAndWait(translationId = 999L)
        composeTestRule.onNodeWithText("Data terjemahan tidak ditemukan").assertIsDisplayed()
    }

    // ─── Test 13: Error state tidak menampilkan tombol Salin ─────────────────
    @Test
    fun detailScreen_errorState_doesNotShowCopyButton() {
        every { repository.getTranslationById(999L) } returns flowOf(null)
        renderAndWait(translationId = 999L)
        composeTestRule.onNodeWithContentDescription("Salin").assertDoesNotExist()
    }

    // ─── Test 14: Translation lain dimuat dengan benar ───────────────────────
    @Test
    fun detailScreen_successState_withDifferentTranslation() {
        val otherTranslation = Translation(
            id = 2L, sourceText = "Neural Network", translatedText = "Jaringan Saraf",
            sourceLanguage = "Inggris", targetLanguage = "Indonesia",
            category = "Teknologi & IT", isVaulted = true,
            createdAt = 2000L, updatedAt = 2000L
        )
        every { repository.getTranslationById(2L) } returns flowOf(otherTranslation)
        renderAndWait(translationId = 2L)
        composeTestRule.onNodeWithText("Neural Network").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jaringan Saraf").assertIsDisplayed()
    }

    // ─── Test 15: Bahasa sumber lain ditampilkan dengan benar ────────────────
    @Test
    fun detailScreen_successState_showsCorrectLanguageLabelsForDifferentTranslation() {
        val jaTranslation = Translation(
            id = 3L, sourceText = "Sakura", translatedText = "Bunga Sakura",
            sourceLanguage = "Jepang", targetLanguage = "Indonesia",
            category = "Umum", isVaulted = false,
            createdAt = 3000L, updatedAt = 3000L
        )
        every { repository.getTranslationById(3L) } returns flowOf(jaTranslation)
        renderAndWait(translationId = 3L)
        composeTestRule.onNodeWithText("Dari: Jepang").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ke: Indonesia").assertIsDisplayed()
    }

    // ─── Test 16: Tombol Edit tetap ada di Error state ───────────────────────
    @Test
    fun detailScreen_editButton_alwaysVisible() {
        every { repository.getTranslationById(999L) } returns flowOf(null)
        renderAndWait(translationId = 999L)
        composeTestRule.onNodeWithContentDescription("Edit Terjemahan").assertIsDisplayed()
    }

    // ─── Test 17: ViewModel state awal adalah Loading ────────────────────────
    @Test
    fun detailScreen_initialState_isLoading() {
        val freshVM = TranslationDetailViewModel(repository)
        assert(freshVM.uiState.value is DetailUiState.Loading) {
            "State awal seharusnya Loading"
        }
    }

    // ─── Test 18: Success state menampilkan semua elemen UI secara bersamaan ──
    @Test
    fun detailScreen_allSuccessElements_displayed() {
        renderAndWait(translationId = 1L)
        // Verifikasi semua elemen success state ada
        composeTestRule.onNodeWithText("Dari: Inggris").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ke: Indonesia").assertIsDisplayed()
        composeTestRule.onNodeWithText("Decentralized Finance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Keuangan Terdesentralisasi").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Salin").assertIsDisplayed()
    }

    // ─── Test 19: Bahasa Korea dimuat dengan benar ────────────────────────────
    @Test
    fun detailScreen_successState_koreanSourceLanguage() {
        val krTranslation = Translation(
            id = 5L, sourceText = "감사합니다", translatedText = "Terima Kasih",
            sourceLanguage = "Korea", targetLanguage = "Indonesia",
            category = "Umum", isVaulted = false,
            createdAt = 5000L, updatedAt = 5000L
        )
        every { repository.getTranslationById(5L) } returns flowOf(krTranslation)
        renderAndWait(translationId = 5L)
        composeTestRule.onNodeWithText("Dari: Korea").assertIsDisplayed()
        composeTestRule.onNodeWithText("Terima Kasih").assertIsDisplayed()
    }

    // ─── Test 20: Screen tetap stabil setelah data dimuat ────────────────────
    @Test
    fun detailScreen_multipleElements_renderedTogether() {
        renderAndWait(translationId = 1L)
        composeTestRule.onNodeWithText("Detail Terjemahan").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Edit Terjemahan").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Kembali").assertIsDisplayed()
        composeTestRule.onNodeWithText("Decentralized Finance").assertIsDisplayed()
    }
}

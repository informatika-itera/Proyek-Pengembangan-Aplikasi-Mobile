package com.example.bridgebit.presentation.screens.insights

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.model.QuizQuestion
import com.example.bridgebit.domain.repository.AIRepository
import com.example.bridgebit.domain.usecase.GetAllHistoryUseCase
import io.mockk.every
import io.mockk.coEvery
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
class InsightsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getAllHistoryUseCase: GetAllHistoryUseCase
    private lateinit var aiRepository: AIRepository
    private lateinit var viewModel: InsightsViewModel

    private val historyFlow = MutableStateFlow<List<Translation>>(emptyList())

    private val richHistory = listOf(
        Translation(
            id = 1L, sourceText = "Hello World", translatedText = "Halo Dunia",
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
        ),
        Translation(
            id = 4L, sourceText = "Blockchain", translatedText = "Rantai Blok",
            sourceLanguage = "Inggris", targetLanguage = "Indonesia",
            category = "Keuangan & Kripto", isVaulted = false,
            createdAt = 4000L, updatedAt = 4000L
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        try { stopKoin() } catch (_: Exception) {}

        getAllHistoryUseCase = mockk()
        aiRepository = mockk(relaxed = true)

        every { getAllHistoryUseCase() } returns historyFlow

        viewModel = InsightsViewModel(getAllHistoryUseCase, aiRepository)

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

    // Helper: render InsightsScreen
    private fun renderInsightsScreen(initialEmit: Boolean = true) {
        composeTestRule.setContent {
            KoinContext {
                InsightsScreen()
            }
        }
        if (initialEmit) {
            emitHistory(emptyList())
        } else {
            composeTestRule.waitForIdle()
        }
    }

    // Helper: emit data setelah render agar WhileSubscribed aktif
    private fun emitHistory(items: List<Translation>) {
        historyFlow.value = items
        composeTestRule.mainClock.advanceTimeBy(400L)
        composeTestRule.waitForIdle()
    }

    // ─── Test 1: TopAppBar title "Statistik Belajar" ditampilkan ─────────────
    @Test
    fun insightsScreen_displaysTopAppBarTitle() {
        renderInsightsScreen()
        composeTestRule.onNodeWithText("Statistik Belajar").assertIsDisplayed()
    }

    // ─── Test 2: Kartu "Total Terjemahan" ditampilkan ─────────────────────────
    @Test
    fun insightsScreen_totalTranslationsCard_isDisplayed() {
        renderInsightsScreen()
        composeTestRule.onNodeWithText("Total Terjemahan").assertIsDisplayed()
    }

    // ─── Test 3: Kartu "Kategori Favorit" ditampilkan ─────────────────────────
    @Test
    fun insightsScreen_topCategoryCard_isDisplayed() {
        renderInsightsScreen()
        composeTestRule.onNodeWithText("Kategori Favorit").assertIsDisplayed()
    }

    // ─── Test 4: Label "Arah Bahasa Paling Sering" ditampilkan ───────────────
    @Test
    fun insightsScreen_topLanguagePairLabel_isDisplayed() {
        renderInsightsScreen()
        composeTestRule.onNodeWithText("Arah Bahasa Paling Sering").assertIsDisplayed()
    }

    // ─── Test 5: Section "Distribusi Topik" ditampilkan ──────────────────────
    @Test
    fun insightsScreen_topicsDistributionSection_isDisplayed() {
        renderInsightsScreen()
        composeTestRule.onNodeWithText("Distribusi Topik").performScrollTo().assertIsDisplayed()
    }

    // ─── Test 6: Default state total "0" ditampilkan ─────────────────────────
    @Test
    fun insightsScreen_defaultState_showsZeroTranslations() {
        renderInsightsScreen()
        emitHistory(emptyList())
        composeTestRule.onAllNodesWithText("0").onFirst().performScrollTo().assertIsDisplayed()
    }

    // ─── Test 7: Dengan data, total menampilkan jumlah benar ──────────────────
    @Test
    fun insightsScreen_withHistory_showsCorrectTotalTranslations() {
        renderInsightsScreen()
        emitHistory(richHistory)
        composeTestRule.onAllNodesWithText("4").onFirst().performScrollTo().assertIsDisplayed()
    }

    // ─── Test 8: Empty state Distribusi Topik menampilkan pesan ───────────────
    @Test
    fun insightsScreen_emptyState_showsNoDataMessage() {
        renderInsightsScreen()
        emitHistory(emptyList())
        composeTestRule.onNodeWithText("Belum ada data riwayat.").performScrollTo().assertIsDisplayed()
    }

    // ─── Test 9: Pesan empty state tidak ada saat ada data ───────────────────
    @Test
    fun insightsScreen_withHistory_noEmptyDataMessage() {
        renderInsightsScreen()
        emitHistory(richHistory)
        composeTestRule.onNodeWithText("Belum ada data riwayat.").assertDoesNotExist()
    }

    // ─── Test 10: FAB "Uji Kosakata (AI Quiz)" ada di composable ────────────
    @Test
    fun insightsScreen_quizFab_exists() {
        renderInsightsScreen()
        // FAB semantics di-merge oleh parent, gunakan useUnmergedTree
        composeTestRule.onNodeWithText("Uji Kosakata (AI Quiz)", useUnmergedTree = true).assertExists()
    }

    // ─── Test 11: Distribusi topik menampilkan kategori "Umum" ────────────────
    @Test
    fun insightsScreen_withHistory_showsCategoryUmum() {
        renderInsightsScreen()
        emitHistory(richHistory)
        // Kategori di bawah list mungkin di luar viewport, gunakan assertExists
        composeTestRule.onAllNodesWithText("Umum").onFirst().assertExists()
    }

    // ─── Test 12: Distribusi topik menampilkan kategori "Keuangan & Kripto" ───
    @Test
    fun insightsScreen_withHistory_showsCategoryKeuangan() {
        renderInsightsScreen()
        emitHistory(richHistory)
        composeTestRule.onAllNodesWithText("Keuangan & Kripto").onFirst().assertIsDisplayed()
    }

    // ─── Test 13: Distribusi topik menampilkan kategori "Teknologi & IT" ──────
    @Test
    fun insightsScreen_withHistory_showsCategoryTeknologi() {
        renderInsightsScreen()
        emitHistory(richHistory)
        // Teknologi & IT mungkin di bawah viewport
        composeTestRule.onAllNodesWithText("Teknologi & IT").onFirst().assertExists()
    }

    // ─── Test 14: Jumlah count kategori "2" untuk Keuangan ditampilkan ────────
    @Test
    fun insightsScreen_withHistory_showsKeuanganCount() {
        renderInsightsScreen()
        emitHistory(richHistory)
        // Keuangan & Kripto ada 2 item
        composeTestRule.onAllNodesWithText("2").onFirst().assertExists()
    }

    // ─── Test 15: Jumlah count kategori "1" ada di distribusi ─────────────────
    @Test
    fun insightsScreen_withHistory_showsSingleCategoryCount() {
        renderInsightsScreen()
        emitHistory(richHistory)
        // Count items mungkin di bawah viewport
        composeTestRule.onAllNodesWithText("1").onFirst().assertExists()
    }

    // ─── Test 16: ViewModel initial state totalTranslations = 0 ──────────────
    @Test
    fun insightsScreen_viewModel_initialTotal_isZero() {
        assert(viewModel.uiState.value.totalTranslations == 0) {
            "Total awal seharusnya 0"
        }
    }

    // ─── Test 17: ViewModel initial state topCategory = "-" ──────────────────
    @Test
    fun insightsScreen_viewModel_initialTopCategory_isDash() {
        assert(viewModel.uiState.value.topCategory == "-") {
            "topCategory awal seharusnya '-'"
        }
    }

    // ─── Test 18: ViewModel quiz initial selectedQuestionCount = 5 ────────────
    @Test
    fun insightsScreen_viewModel_initialQuestionCount_isFive() {
        assert(viewModel.selectedQuestionCount.value == 5) {
            "selectedQuestionCount awal seharusnya 5"
        }
    }

    // ─── Test 19: setQuestionCount mengubah state ────────────────────────────
    @Test
    fun insightsScreen_viewModel_setQuestionCount_updates() {
        viewModel.setQuestionCount(10)
        assert(viewModel.selectedQuestionCount.value == 10) {
            "selectedQuestionCount seharusnya 10 setelah set"
        }
    }

    // ─── Test 20: Quiz initial state — no questions ──────────────────────────
    @Test
    fun insightsScreen_viewModel_quizInitial_noQuestions() {
        assert(viewModel.quizQuestions.value.isEmpty()) {
            "Quiz questions awal seharusnya kosong"
        }
        assert(!viewModel.isQuizFinished.value) {
            "Quiz seharusnya belum selesai"
        }
    }

    // ─── Test 21: Screen title tetap ada setelah data berubah ────────────────
    @Test
    fun insightsScreen_titleStaysVisibleAfterDataEmit() {
        renderInsightsScreen()
        emitHistory(richHistory)
        composeTestRule.onNodeWithText("Statistik Belajar").assertIsDisplayed()
    }

    // ─── Test 22: Metric cards tetap ada setelah data berubah ────────────────
    @Test
    fun insightsScreen_metricCardsStayAfterDataEmit() {
        renderInsightsScreen()
        emitHistory(richHistory)
        composeTestRule.onNodeWithText("Total Terjemahan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kategori Favorit").assertIsDisplayed()
    }

    // ─── Test 23: Screen tanpa data tetap render tanpa crash ─────────────────
    @Test
    fun insightsScreen_withNoData_rendersWithoutCrash() {
        renderInsightsScreen()
        emitHistory(emptyList())
        composeTestRule.onNodeWithText("Statistik Belajar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Terjemahan").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Distribusi Topik").performScrollTo().assertIsDisplayed()
    }

    // ─── Test 24: resetQuiz me-reset semua quiz state ────────────────────────
    @Test
    fun insightsScreen_viewModel_resetQuiz_clearsState() {
        viewModel.setQuestionCount(10)
        viewModel.resetQuiz()
        assert(viewModel.quizQuestions.value.isEmpty())
        assert(viewModel.currentQuestionIndex.value == 0)
        assert(viewModel.selectedAnswerIndex.value == null)
        assert(!viewModel.isQuizFinished.value)
        assert(viewModel.correctAnswersCount.value == 0)
    }

    // ─── Test 25: Distribusi topik section hilang saat empty dan muncul saat data ada ─
    @Test
    fun insightsScreen_distributionSection_togglesByData() {
        renderInsightsScreen()
        // Pertama: empty
        emitHistory(emptyList())
        composeTestRule.onNodeWithText("Belum ada data riwayat.").performScrollTo().assertIsDisplayed()
        // Kedua: ada data
        emitHistory(richHistory)
        composeTestRule.onNodeWithText("Belum ada data riwayat.").assertDoesNotExist()
        composeTestRule.onAllNodesWithText("Keuangan & Kripto").onFirst().assertExists()
    }

    // ─── NEW TEST CASES FOR QUIZ UI FLOW (COVERAGE BOOST) ────────────────────

    // ─── Test 26: Quiz Dialog - Menampilkan pesan error jika riwayat kosong ──
    @Test
    fun insightsScreen_quizDialog_emptyHistory_showsError() {
        renderInsightsScreen()
        emitHistory(emptyList())

        // Buka Dialog
        composeTestRule.onNodeWithText("Uji Kosakata (AI Quiz)", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        // Klik Mulai Kuis
        composeTestRule.onNodeWithText("Mulai Kuis", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        // Pesan error harus muncul karena history kosong
        composeTestRule.onNodeWithText("Tambahkan terjemahan ke riwayat terlebih dahulu!", useUnmergedTree = true).assertExists()
    }

    // ─── Test 27: Quiz Dialog - Menampilkan soal setelah generate sukses ─────
    @Test
    fun insightsScreen_quizDialog_generateSuccess_showsQuestion() {
        // Mock respon AI menggunakan generateQuiz() yang baru
        val mockQuestions = listOf(
            QuizQuestion(
                question = "Apa arti dari Smart Contract?",
                options = listOf("Kontrak Pintar", "Kontrak Bodoh", "Kontrak Biasa", "Tidak tahu"),
                correctOptionIndex = 0,
                explanation = "Smart Contract adalah Kontrak Pintar."
            ),
            QuizQuestion(
                question = "Apa arti Machine Learning?",
                options = listOf("Mesin Jahit", "Pembelajaran Mesin", "Mesin Waktu", "Mesin Ketik"),
                correctOptionIndex = 1,
                explanation = "Machine Learning adalah Pembelajaran Mesin."
            ),
            QuizQuestion(
                question = "Apa arti Blockchain?",
                options = listOf("Rantai Sepeda", "Rantai Blok", "Rantai Emas", "Rantai Kapal"),
                correctOptionIndex = 1,
                explanation = "Blockchain adalah Rantai Blok."
            )
        )

        coEvery { aiRepository.generateQuiz(any(), any()) } returns Result.success(mockQuestions)

        renderInsightsScreen()
        emitHistory(richHistory)

        // Buka Dialog
        composeTestRule.onNodeWithText("Uji Kosakata (AI Quiz)", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        // Pilih 3 soal agar sesuai dengan mock
        composeTestRule.onNodeWithText("3", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        // Klik Mulai Kuis
        composeTestRule.onNodeWithText("Mulai Kuis", useUnmergedTree = true).performClick()
        // Tunggu coroutine generateQuiz selesai
        composeTestRule.mainClock.advanceTimeBy(2000L)
        composeTestRule.waitForIdle()

        // Harus masuk ke Halaman Sedang Kuis, cek apakah judul soal pertama muncul
        composeTestRule.onNodeWithText("Apa arti dari Smart Contract?", useUnmergedTree = true).assertExists()
        // Cek apakah opsi A muncul (new UI renders option text directly without "A. " prefix)
        composeTestRule.onNodeWithText("Kontrak Pintar", useUnmergedTree = true).assertExists()
    }

}

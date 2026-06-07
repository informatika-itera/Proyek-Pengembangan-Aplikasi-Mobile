package com.example.bridgebit.presentation.screens.insights

import com.example.bridgebit.domain.model.Translation
import com.example.bridgebit.domain.repository.AIRepository
import com.example.bridgebit.domain.usecase.GetAllHistoryUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

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
        getAllHistoryUseCase = mockk()
        aiRepository = mockk()

        every { getAllHistoryUseCase() } returns historyFlow

        viewModel = InsightsViewModel(getAllHistoryUseCase, aiRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Helper: subscribe agar WhileSubscribed aktif
    private fun TestScope.collectUiState(): kotlinx.coroutines.Job {
        return launch { viewModel.uiState.collect {} }
    }

    // ─── Test 1 ───────────────────────────────────────────────────────────────
    @Test
    fun `initial uiState has zero total translations`() = runTest {
        val job = collectUiState()
        advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.totalTranslations)
        job.cancel()
    }

    // ─── Test 2 ───────────────────────────────────────────────────────────────
    @Test
    fun `uiState totalTranslations reflects size of history`() = runTest {
        val job = collectUiState()
        historyFlow.value = richHistory
        advanceUntilIdle()

        assertEquals(4, viewModel.uiState.value.totalTranslations)
        job.cancel()
    }

    // ─── Test 3 ───────────────────────────────────────────────────────────────
    @Test
    fun `uiState topCategory is the most frequently occurring category`() = runTest {
        val job = collectUiState()
        historyFlow.value = richHistory
        advanceUntilIdle()

        assertEquals("Keuangan & Kripto", viewModel.uiState.value.topCategory)
        job.cancel()
    }

    // ─── Test 4 ───────────────────────────────────────────────────────────────
    @Test
    fun `uiState topicsDistribution counts each category correctly`() = runTest {
        val job = collectUiState()
        historyFlow.value = richHistory
        advanceUntilIdle()

        val dist = viewModel.uiState.value.topicsDistribution
        assertEquals(1, dist["Umum"])
        assertEquals(2, dist["Keuangan & Kripto"])
        assertEquals(1, dist["Teknologi & IT"])
        job.cancel()
    }

    // ─── Test 5 ───────────────────────────────────────────────────────────────
    @Test
    fun `uiState topLanguagePair reflects most common source-to-target pair`() = runTest {
        val job = collectUiState()
        historyFlow.value = richHistory
        advanceUntilIdle()

        assertEquals("Inggris ➔ Indonesia", viewModel.uiState.value.topLanguagePair)
        job.cancel()
    }

    // ─── Test 6 ───────────────────────────────────────────────────────────────
    @Test
    fun `when history is empty, topCategory and topLanguagePair show default`() = runTest {
        val job = collectUiState()
        historyFlow.value = emptyList()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(0, state.totalTranslations)
        assertEquals("Belum ada", state.topCategory)
        assertEquals("Belum ada", state.topLanguagePair)
        job.cancel()
    }

    // ─── Test 7 ───────────────────────────────────────────────────────────────
    @Test
    fun `initial quiz state has no questions and is not finished`() {
        assertTrue(viewModel.quizQuestions.value.isEmpty())
        assertFalse(viewModel.isQuizFinished.value)
        assertEquals(0, viewModel.correctAnswersCount.value)
        assertEquals(0, viewModel.currentQuestionIndex.value)
    }

    // ─── Test 8 ───────────────────────────────────────────────────────────────
    @Test
    fun `setQuestionCount updates selectedQuestionCount correctly`() {
        viewModel.setQuestionCount(10)
        assertEquals(10, viewModel.selectedQuestionCount.value)
    }

    // ─── Test 9 ───────────────────────────────────────────────────────────────
    @Test
    fun `generateQuiz with empty history sets quizError message`() = runTest {
        every { getAllHistoryUseCase() } returns MutableStateFlow(emptyList())
        viewModel = InsightsViewModel(getAllHistoryUseCase, aiRepository)

        viewModel.generateQuiz()
        advanceUntilIdle()

        assertNotNull(viewModel.quizError.value)
        assertTrue(viewModel.quizError.value!!.contains("riwayat"))
    }

    // Helper: inject quiz questions langsung via reflection
    private fun injectQuestions(questions: List<QuizQuestion>) {
        val field = viewModel.javaClass.getDeclaredField("_quizQuestions")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = field.get(viewModel) as MutableStateFlow<List<QuizQuestion>>
        flow.value = questions
    }

    // ─── Test 10 ──────────────────────────────────────────────────────────────
    @Test
    fun `answerQuestion increments correctAnswersCount when answer is correct`() = runTest {
        injectQuestions(listOf(
            QuizQuestion("Apa arti Blockchain?",
                listOf("Rantai Blok", "Koin Digital", "Server", "Dompet"),
                correctOptionIndex = 0, explanation = "")
        ))

        viewModel.answerQuestion(0)
        advanceUntilIdle()

        assertEquals(1, viewModel.correctAnswersCount.value)
    }

    // ─── Test 11 ──────────────────────────────────────────────────────────────
    @Test
    fun `answerQuestion does not increment score when answer is wrong`() = runTest {
        injectQuestions(listOf(
            QuizQuestion("Q1", listOf("A", "B", "C", "D"), correctOptionIndex = 0, explanation = "")
        ))

        viewModel.answerQuestion(2) // jawaban salah
        advanceUntilIdle()

        assertEquals(0, viewModel.correctAnswersCount.value)
    }

    // ─── Test 12 ──────────────────────────────────────────────────────────────
    @Test
    fun `nextQuestion advances currentQuestionIndex`() = runTest {
        injectQuestions(listOf(
            QuizQuestion("Q1", listOf("A", "B", "C", "D"), 0, ""),
            QuizQuestion("Q2", listOf("A", "B", "C", "D"), 1, "")
        ))

        viewModel.answerQuestion(0)
        viewModel.nextQuestion()
        advanceUntilIdle()

        assertEquals(1, viewModel.currentQuestionIndex.value)
    }

    // ─── Test 13 ──────────────────────────────────────────────────────────────
    @Test
    fun `nextQuestion on last question sets isQuizFinished to true`() = runTest {
        injectQuestions(listOf(
            QuizQuestion("Q1", listOf("A", "B", "C", "D"), 0, "")
        ))

        viewModel.answerQuestion(0)
        viewModel.nextQuestion()
        advanceUntilIdle()

        assertTrue(viewModel.isQuizFinished.value)
    }

    // ─── Test 14 ──────────────────────────────────────────────────────────────
    @Test
    fun `resetQuiz resets all quiz-related state to initial values`() = runTest {
        injectQuestions(listOf(
            QuizQuestion("Q1", listOf("A", "B", "C", "D"), 0, "")
        ))
        viewModel.answerQuestion(0)
        viewModel.nextQuestion()
        advanceUntilIdle()

        viewModel.resetQuiz()
        advanceUntilIdle()

        assertTrue(viewModel.quizQuestions.value.isEmpty())
        assertEquals(0, viewModel.currentQuestionIndex.value)
        assertEquals(null, viewModel.selectedAnswerIndex.value)
        assertFalse(viewModel.isQuizFinished.value)
        assertEquals(0, viewModel.correctAnswersCount.value)
    }
}
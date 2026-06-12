package com.example.bridgebit.presentation.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgebit.domain.repository.AIRepository
import com.example.bridgebit.domain.usecase.GetAllHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * UI state for the Insights screen.
 *
 * @param isLoading             True if data is still being prepared/loaded (to show shimmer).
 * @param totalTranslations     Total number of translations in history.
 * @param currentStreak         Current active consecutive learning days.
 * @param topicsDistribution    Map of category → count, used for the topic bar chart.
 * @param topLanguagePair       Most-used source→target language direction string.
 * @param topCategory           Name of the most-translated category.
 * @param dailyTranslationStats List of (dayLabel, count) for the last 7 days bar chart.
 *                              dayLabel is a short string like "Mon", "Tue", etc.
 */
data class InsightsUiState(
    val isLoading: Boolean = true,
    val totalTranslations: Int = 0,
    val currentStreak: Int = 0,
    val weeklyTranslations: Int = 0,
    val weeklyGoal: Int = 20,
    val topicsDistribution: Map<String, Int> = emptyMap(),
    val topLanguagePair: String = "-",
    val topCategory: String = "-",
    val dailyTranslationStats: List<Pair<String, Int>> = emptyList()
)

class InsightsViewModel(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val aiRepository: AIRepository
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = getAllHistoryUseCase()
        .map { history ->
            val topics = history.groupBy { it.category }.mapValues { it.value.size }
            val topCat = topics.maxByOrNull { it.value }?.key ?: "Belum ada"
            val topLang = history.groupBy { "${it.sourceLanguage} ➔ ${it.targetLanguage}" }
                .maxByOrNull { it.value.size }?.key ?: "Belum ada"


            val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
            val dailyMap = mutableMapOf<String, Int>()
            dayNames.forEach { dailyMap[it] = 0 }
            history.forEach { item ->

                val dayIndex = ((item.createdAt / 86_400_000L + 4) % 7).toInt()
                val dayName = dayNames[dayIndex]
                dailyMap[dayName] = (dailyMap[dayName] ?: 0) + 1
            }
            val dailyStats = dayNames.map { it to (dailyMap[it] ?: 0) }
            val weeklySum = dailyStats.sumOf { it.second }


            val currentEpochDay = Clock.System.now().toEpochMilliseconds() / 86_400_000L
            val uniqueDays = history.map { it.createdAt / 86_400_000L }.distinct().sortedDescending()
            var streak = 0
            if (uniqueDays.isNotEmpty()) {
                val latestDay = uniqueDays.first()
                if (latestDay == currentEpochDay || latestDay == currentEpochDay - 1) {
                    var expectedDay = latestDay
                    for (day in uniqueDays) {
                        if (day == expectedDay) {
                            streak++
                            expectedDay--
                        } else {
                            break
                        }
                    }
                }
            }

            InsightsUiState(
                isLoading = false,
                totalTranslations = history.size,
                currentStreak = streak,
                weeklyTranslations = weeklySum,
                topicsDistribution = topics,
                topLanguagePair = topLang,
                topCategory = topCat,
                dailyTranslationStats = dailyStats
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState(isLoading = true))

    // ── Quiz State ────────────────────────────────────────────────────────────

    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex = _selectedAnswerIndex.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished = _isQuizFinished.asStateFlow()

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount = _correctAnswersCount.asStateFlow()

    private val _quizError = MutableStateFlow<String?>(null)
    val quizError = _quizError.asStateFlow()

    private val _isLoadingQuiz = MutableStateFlow(false)
    val isLoadingQuiz = _isLoadingQuiz.asStateFlow()

    private val _selectedQuestionCount = MutableStateFlow(5)
    val selectedQuestionCount = _selectedQuestionCount.asStateFlow()

    fun setQuestionCount(count: Int) {
        _selectedQuestionCount.value = count
    }

    /**
     * Generates quiz questions via the AI repository.
     *
     * The fix for the "only 1 question" bug lives in [AIRepository.generateQuiz]:
     * it uses Gemini's JSON mode and `kotlinx.serialization` to parse a full
     * `List<QuizQuestion>` instead of the brittle "|||" text-delimiter approach.
     */
    fun generateQuiz() {
        viewModelScope.launch {
            _isLoadingQuiz.value = true
            _quizError.value = null
            resetQuizState(keepQuestionCount = true)

            try {
                val history = getAllHistoryUseCase().first()
                if (history.isEmpty()) {
                    _quizError.value = "Tambahkan terjemahan ke riwayat terlebih dahulu!"
                    _isLoadingQuiz.value = false
                    return@launch
                }

                val numQuestions = _selectedQuestionCount.value

                // Build vocabulary list from history entries
                val wordPairs = history.flatMap { item ->
                    val words = item.sourceText.split(Regex("\\s+"))
                        .map { it.replace(Regex("[^a-zA-Z]"), "").lowercase() }
                        .filter { it.isNotBlank() }
                    words.map { "$it (${item.targetLanguage})" }
                }.distinct()

                if (wordPairs.size < numQuestions) {
                    _quizError.value =
                        "Kosakata di riwayatmu tidak cukup untuk membuat $numQuestions soal " +
                        "(hanya ada ${wordPairs.size} kata unik)."
                    _isLoadingQuiz.value = false
                    return@launch
                }

                val vocabularyList = wordPairs.shuffled().take(numQuestions).joinToString(", ")


                aiRepository.generateQuiz(numQuestions, vocabularyList)
                    .onSuccess { questions ->
                        if (questions.isNotEmpty()) {
                            _quizQuestions.value = questions
                        } else {
                            _quizError.value = "AI tidak menghasilkan soal. Coba lagi."
                        }
                    }
                    .onFailure {
                        _quizError.value = "Gagal membuat kuis: ${it.message ?: "Pastikan internet aktif."}"
                    }

            } catch (e: Exception) {
                _quizError.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoadingQuiz.value = false
            }
        }
    }

    fun answerQuestion(index: Int) {
        if (_selectedAnswerIndex.value == null) {
            _selectedAnswerIndex.value = index
            val currentQ = _quizQuestions.value[_currentQuestionIndex.value]
            if (index == currentQ.correctOptionIndex) {
                _correctAnswersCount.value += 1
            }
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _quizQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
            _selectedAnswerIndex.value = null
        } else {
            _isQuizFinished.value = true
        }
    }

    private fun resetQuizState(keepQuestionCount: Boolean = false) {
        _quizQuestions.value = emptyList()
        _currentQuestionIndex.value = 0
        _selectedAnswerIndex.value = null
        _quizError.value = null
        _isQuizFinished.value = false
        _correctAnswersCount.value = 0
        if (!keepQuestionCount) {
            _selectedQuestionCount.value = 5
        }
    }

    fun resetQuiz() {
        resetQuizState()
    }
}
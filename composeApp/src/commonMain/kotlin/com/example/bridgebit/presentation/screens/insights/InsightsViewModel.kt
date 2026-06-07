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

data class InsightsUiState(
    val totalTranslations: Int = 0,
    val topicsDistribution: Map<String, Int> = emptyMap(),
    val topLanguagePair: String = "-",
    val topCategory: String = "-"
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

class InsightsViewModel(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val aiRepository: AIRepository
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = getAllHistoryUseCase()
        .map { history ->
            val topics = history.groupBy { it.category }.mapValues { it.value.size }

            // Mencari Kategori Paling Sering Dipakai
            val topCat = topics.maxByOrNull { it.value }?.key ?: "Belum ada"

            // Mencari Pasangan Bahasa Paling Sering (Contoh: Indonesia -> Inggris)
            val topLang = history.groupBy { "${it.sourceLanguage} ➔ ${it.targetLanguage}" }
                .maxByOrNull { it.value.size }?.key ?: "Belum ada"

            InsightsUiState(
                totalTranslations = history.size,
                topicsDistribution = topics,
                topLanguagePair = topLang,
                topCategory = topCat
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState())

    // STATE KUIS & SKOR (Tetap sama seperti sebelumnya)
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
                val wordPairs = history.flatMap { item ->
                    val words = item.sourceText.split(Regex("\\s+"))
                        .map { it.replace(Regex("[^a-zA-Z]"), "").lowercase() }
                        .filter { it.isNotBlank() }
                    words.map { "$it (ke ${item.targetLanguage})" }
                }.distinct()

                if (wordPairs.size < numQuestions) {
                    _quizError.value = "Kosakata di riwayatmu tidak cukup untuk membuat $numQuestions soal (hanya ada ${wordPairs.size} kata unik)."
                    _isLoadingQuiz.value = false
                    return@launch
                }

                val vocabularyList = wordPairs.shuffled().take(numQuestions).joinToString(", ")

                // PROMPT BARU: Format A/B/C/D dengan pemisah mutlak "|||"
                val prompt = """
                    Tugasmu membuat TEPAT $numQuestions soal kuis untuk menguji kosakata berikut: $vocabularyList.
                    
                    ATURAN MUTLAK:
                    1. Jumlah soal WAJIB $numQuestions. Tidak boleh kurang!
                    2. Kamu WAJIB memisahkan setiap soal dengan teks "|||" (tiga garis lurus).
                    3. Jangan ada teks pengantar atau penutup. Langsung berikan soalnya.
                    
                    Gunakan format baku ini untuk SETIAP soal:
                    PERTANYAAN: [Tulis pertanyaan di sini]
                    A. [Opsi 1]
                    B. [Opsi 2]
                    C. [Opsi 3]
                    D. [Opsi 4]
                    KUNCI: [Pilih salah satu huruf: A, B, C, atau D]
                    PENJELASAN: [Tulis penjelasan singkat]
                    |||
                """.trimIndent()

                aiRepository.chat(prompt).onSuccess { result ->
                    val questions = parseQuiz(result)
                    if (questions.isNotEmpty()) {
                        // Pastikan tidak mengambil lebih dari yang diminta
                        _quizQuestions.value = questions.take(numQuestions)
                    } else {
                        _quizError.value = "Gagal memproses format AI. AI mengirim:\n\n$result"
                    }
                }.onFailure {
                    _quizError.value = "Gagal membuat kuis. Pastikan internet aktif."
                }
            } catch (e: Exception) {
                _quizError.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoadingQuiz.value = false
            }
        }
    }

    // PARSER BARU BERBASIS PEMISAH "|||" (SANGAT TAHAN BANTING)
    private fun parseQuiz(text: String): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        // 1. Potong blok tepat di teks "|||"
        val blocks = text.split("|||").map { it.trim() }.filter { it.isNotBlank() }

        for (block in blocks) {
            val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.isEmpty()) continue

            var q = ""
            val ops = mutableListOf<String>()
            var ansIndex = 0
            var expl = "Jawaban benar."

            // 2. Ekstrak data baris demi baris dari blok soal tersebut
            for (line in lines) {
                val upperLine = line.uppercase()

                if (upperLine.startsWith("PERTANYAAN:")) {
                    q = line.substringAfter(":").replace("*", "").trim()
                } else if (upperLine.matches(Regex("^[A-D][\\.\\)]\\s+.*"))) {
                    // Menangkap format "A. Jawaban" atau "A) Jawaban"
                    ops.add(line.substring(2).replace("*", "").trim())
                } else if (upperLine.startsWith("KUNCI:")) {
                    val key = line.substringAfter(":").replace("*", "").trim().uppercase()
                    ansIndex = when {
                        key.contains("A") -> 0
                        key.contains("B") -> 1
                        key.contains("C") -> 2
                        key.contains("D") -> 3
                        else -> 0
                    }
                } else if (upperLine.startsWith("PENJELASAN:")) {
                    expl = line.substringAfter(":").replace("*", "").trim()
                }
            }

            // Fallback (Jaga-jaga jika AI lupa menulis "PERTANYAAN:" tapi langsung menulis soalnya)
            if (q.isBlank() && lines.isNotEmpty() && !lines.first().uppercase().matches(Regex("^[A-D][\\.\\)]\\s+.*"))) {
                q = lines.first().replace(Regex("^\\d+\\.\\s*"), "").replace("*", "").trim()
            }

            // Fallback jika opsi kurang dari 4
            while (ops.size < 4) {
                ops.add("Semua jawaban salah")
            }

            // Jika soal berhasil ditangkap minimal pertanyaannya, masukkan ke dalam kuis
            if (q.isNotBlank()) {
                questions.add(QuizQuestion(q, ops.take(4), ansIndex, expl))
            }
        }
        return questions
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
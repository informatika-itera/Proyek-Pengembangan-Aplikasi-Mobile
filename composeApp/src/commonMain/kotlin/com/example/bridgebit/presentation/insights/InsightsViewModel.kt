package com.example.bridgebit.presentation.screens.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bridgebit.domain.repository.AIRepository
import com.example.bridgebit.domain.usecase.GetAllHistoryUseCase
import com.example.bridgebit.domain.usecase.GetVaultPhrasesUseCase
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
    val totalVaulted: Int = 0,
    val topicsDistribution: Map<String, Int> = emptyMap()
)

class InsightsViewModel(
    getAllHistoryUseCase: GetAllHistoryUseCase,
    private val getVaultPhrasesUseCase: GetVaultPhrasesUseCase,
    private val aiRepository: AIRepository
) : ViewModel() {

    // 1. Membaca riwayat dan menghitung statistik per kategori (Topik Terpopuler)
    val uiState: StateFlow<InsightsUiState> = getAllHistoryUseCase()
        .map { history ->
            val topics = history.groupBy { it.category }.mapValues { it.value.size }
            InsightsUiState(
                totalTranslations = history.size,
                totalVaulted = history.count { it.isVaulted },
                topicsDistribution = topics
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InsightsUiState())

    // 2. State untuk fitur AI Quiz
    private val _quizState = MutableStateFlow<String?>(null)
    val quizState = _quizState.asStateFlow()

    private val _isLoadingQuiz = MutableStateFlow(false)
    val isLoadingQuiz = _isLoadingQuiz.asStateFlow()

    // 3. Fungsi untuk membuat kuis dari kata-kata di Vault
    fun generateQuiz() {
        viewModelScope.launch {
            _isLoadingQuiz.value = true
            _quizState.value = null

            try {
                // Ambil daftar kata dari Vault secara realtime
                val phrases = getVaultPhrasesUseCase().first()
                if (phrases.isEmpty()) {
                    _quizState.value = "Tambahkan beberapa kata ke Vault terlebih dahulu untuk membuat kuis!"
                    _isLoadingQuiz.value = false
                    return@launch
                }

                // Ambil maksimal 7 kata di Vault sebagai bahan kuis
                val vocabularyList = phrases.take(7).joinToString(", ") {
                    "${it.sourceText} (${it.targetLanguage})"
                }

                val prompt = "Buat 3 soal kuis pilihan ganda singkat untuk mengetes hafalan kosakata berikut: $vocabularyList. Berikan kunci jawabannya di bagian paling bawah."

                // Panggil AI Gemini
                aiRepository.chat(prompt).onSuccess { result ->
                    _quizState.value = result
                }.onFailure {
                    _quizState.value = "Gagal membuat kuis. Pastikan internet/API Key aktif."
                }
            } catch (e: Exception) {
                _quizState.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoadingQuiz.value = false
            }
        }
    }
}
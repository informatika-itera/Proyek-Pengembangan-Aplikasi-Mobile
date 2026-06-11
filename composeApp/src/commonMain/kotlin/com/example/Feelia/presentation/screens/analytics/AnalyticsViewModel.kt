package com.example.Feelia.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.usecase.GetAIWeeklyInsightUseCase
import com.example.Feelia.domain.usecase.GetFrequentWordsUseCase
import com.example.Feelia.domain.usecase.GetWeeklyInsightUseCase
import com.example.Feelia.domain.usecase.MoodTrendData
import com.example.Feelia.domain.usecase.WeeklyInsight
import com.example.Feelia.domain.usecase.WordFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AnalyticsViewModel(
    private val getWeeklyInsightUseCase: GetWeeklyInsightUseCase,
    private val getFrequentWordsUseCase: GetFrequentWordsUseCase,
    private val getAIWeeklyInsightUseCase: GetAIWeeklyInsightUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        loadWeeklyInsight()
        loadFrequentWords()
    }

    private fun loadWeeklyInsight() {
        viewModelScope.launch {
            getWeeklyInsightUseCase()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { insight ->
                    _uiState.update { it.copy(weeklyInsight = insight, isLoading = false) }
                }
        }
    }

    private fun loadFrequentWords(emotion: Emotion? = null) {
        viewModelScope.launch {
            getFrequentWordsUseCase(emotion)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { words ->
                    _uiState.update { it.copy(frequentWords = words) }
                }
        }
    }

    fun onWordFilterChanged(emotion: Emotion?) {
        _uiState.update { it.copy(selectedWordFilter = emotion) }
        loadFrequentWords(emotion)
    }

    fun loadAIInsight() {
        _uiState.update { it.copy(isLoadingAIInsight = true, aiInsight = null, aiInsightError = null) }
        viewModelScope.launch {
            getAIWeeklyInsightUseCase()
                .onSuccess { insight ->
                    _uiState.update { it.copy(aiInsight = insight, isLoadingAIInsight = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            aiInsightError = error.message ?: "Gagal memuat insight",
                            isLoadingAIInsight = false
                        )
                    }
                }
        }
    }
}

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val weeklyInsight: WeeklyInsight? = null,
    val frequentWords: List<WordFrequency> = emptyList(),
    val selectedWordFilter: Emotion? = null,
    val aiInsight: String? = null,
    val isLoadingAIInsight: Boolean = false,
    val aiInsightError: String? = null,
    val error: String? = null
)
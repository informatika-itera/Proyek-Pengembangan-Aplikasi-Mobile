package com.example.gamenews.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.AIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AIRecommendationUiState(
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null
)

class AIRecommendationViewModel(
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIRecommendationUiState())
    val uiState: StateFlow<AIRecommendationUiState> = _uiState.asStateFlow()

    fun recommendByWishlist(games: List<Game>) {
        if (_uiState.value.isLoading || _uiState.value.result != null) return

        val gameList = games.take(5).joinToString(", ") { it.title }
        val prompt = "Rekomendasikan 5 game mirip dengan: $gameList. Format: Nama - Alasan singkat (1 kalimat)."

        execute(prompt)
    }

    fun recommendByGenre(genre: String) {
        if (_uiState.value.isLoading) return

        val prompt = "Rekomendasikan 5 game $genre terbaik. Format: Nama - Alasan singkat (1 kalimat)."

        execute(prompt)
    }

    fun recommendByCustomInput(userInput: String) {
        if (_uiState.value.isLoading) return

        val prompt = """
            User mencari rekomendasi game dengan deskripsi: "$userInput".
            Rekomendasikan 5 game yang paling cocok.
            Format: Nama Game - Alasan singkat (1 kalimat).
        """.trimIndent()

        execute(prompt)
    }

    private fun execute(prompt: String) {
        _uiState.update { it.copy(isLoading = true, error = null, result = null) }
        viewModelScope.launch {
            aiRepository.chat(prompt)
                .onSuccess { result ->
                    _uiState.update { it.copy(isLoading = false, result = result) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message ?: "Gagal mendapat rekomendasi") }
                }
        }
    }

    fun reset() {
        _uiState.update { AIRecommendationUiState() }
    }
}
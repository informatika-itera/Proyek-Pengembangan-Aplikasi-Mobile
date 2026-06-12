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

        val prompt = when (games.size) {
            1 -> {
                """
                Berikan TEPAT 3 rekomendasi game yang mirip dengan "${games[0].title}" bergenre ${games[0].genre}.
                Tampilkan dengan format:
                1. Nama Game - alasan satu kalimat kenapa mirip
                2. Nama Game - alasan satu kalimat kenapa mirip
                3. Nama Game - alasan satu kalimat kenapa mirip
                Jangan tambahkan teks lain selain 3 rekomendasi tersebut.
                """.trimIndent()
            }
            2 -> {
                """
                Berikan TEPAT 3 rekomendasi game berdasarkan wishlist berikut:
                - 2 game mirip dengan "${games[0].title}" (${games[0].genre})
                - 1 game mirip dengan "${games[1].title}" (${games[1].genre})
                Tampilkan dengan format:
                1. Nama Game - alasan satu kalimat
                2. Nama Game - alasan satu kalimat
                3. Nama Game - alasan satu kalimat
                Jangan tambahkan teks lain selain 3 rekomendasi tersebut.
                """.trimIndent()
            }
            else -> {
                val top3 = games.take(3)
                """
                Berikan TEPAT 3 rekomendasi game berdasarkan wishlist berikut:
                - 1 game mirip dengan "${top3[0].title}" (${top3[0].genre})
                - 1 game mirip dengan "${top3[1].title}" (${top3[1].genre})
                - 1 game mirip dengan "${top3[2].title}" (${top3[2].genre})
                Tampilkan dengan format:
                1. Nama Game - alasan satu kalimat
                2. Nama Game - alasan satu kalimat
                3. Nama Game - alasan satu kalimat
                Jangan tambahkan teks lain selain 3 rekomendasi tersebut.
                """.trimIndent()
            }
        }

        execute(prompt)
    }

    fun recommendByGenre(genre: String) {
        if (_uiState.value.isLoading) return

        val prompt = """
            Berikan TEPAT 3 rekomendasi game terbaik bergenre $genre.
            Tampilkan dengan format:
            1. Nama Game - alasan satu kalimat kenapa bagus
            2. Nama Game - alasan satu kalimat kenapa bagus
            3. Nama Game - alasan satu kalimat kenapa bagus
            Jangan tambahkan teks lain selain 3 rekomendasi tersebut.
        """.trimIndent()

        execute(prompt)
    }

    fun recommendByCustomInput(userInput: String) {
        if (_uiState.value.isLoading) return

        val prompt = """
            User mencari game dengan deskripsi: "$userInput"
            Berikan TEPAT 3 rekomendasi game yang paling cocok.
            Tampilkan dengan format:
            1. Nama Game - alasan satu kalimat kenapa cocok
            2. Nama Game - alasan satu kalimat kenapa cocok
            3. Nama Game - alasan satu kalimat kenapa cocok
            Jangan tambahkan teks lain selain 3 rekomendasi tersebut.
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
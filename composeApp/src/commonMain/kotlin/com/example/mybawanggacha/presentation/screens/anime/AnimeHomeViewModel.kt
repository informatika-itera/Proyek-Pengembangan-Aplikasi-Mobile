package com.example.mybawanggacha.presentation.screens.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimeHomeViewModel(
    private val animeRepository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeHomeUiState>(AnimeHomeUiState.Loading)
    val uiState: StateFlow<AnimeHomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = AnimeHomeUiState.Loading

            runCatching {
                animeRepository.getRecommendations()
            }.onSuccess { recommendations ->
                _uiState.value = AnimeHomeUiState.Success(recommendations)
            }.onFailure { error ->
                _uiState.value = AnimeHomeUiState.Error(
                    error.message ?: "Gagal memuat rekomendasi anime"
                )
            }
        }
    }
}

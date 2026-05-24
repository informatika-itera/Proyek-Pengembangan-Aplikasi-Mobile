package com.example.musickeep.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musickeep.domain.model.Music
import com.example.musickeep.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MusicDetailUiState(
    val music: Music? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MusicDetailViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicDetailUiState())
    val uiState: StateFlow<MusicDetailUiState> = _uiState.asStateFlow()

    fun loadMusic(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val music = repository.getMusicById(id)
            if (music != null) {
                _uiState.update { it.copy(music = music, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Lagu tidak ditemukan") }
            }
        }
    }
}

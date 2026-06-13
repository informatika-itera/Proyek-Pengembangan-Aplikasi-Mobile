package com.soundletter.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val letterRepository: LetterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Note>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Note>>> = _uiState.asStateFlow()

    init {
        loadLetters()
    }

    fun loadLetters() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // Menggabungkan flow dari surat lokal dan global, diurutkan descending (terbaru di atas)
            combine(
                letterRepository.getLetters(),
                letterRepository.getGlobalLetters()
            ) { local, global ->
                (local + global).sortedByDescending { it.createdAt }
            }
            .catch { e -> 
                _uiState.value = UiState.Error(e.message ?: "Unknown Error") 
            }
            .collect { combinedLetters -> 
                _uiState.value = UiState.Success(combinedLetters) 
            }
        }
    }
}

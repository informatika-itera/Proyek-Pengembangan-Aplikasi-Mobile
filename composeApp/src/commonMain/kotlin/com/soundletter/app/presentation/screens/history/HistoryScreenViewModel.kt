package com.soundletter.app.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryScreenViewModel(
    private val letterRepository: LetterRepository
) : ViewModel() {

    private val _historyState = MutableStateFlow<UiState<List<Note>>>(UiState.Idle)
    val historyState: StateFlow<UiState<List<Note>>> = _historyState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = UiState.Loading
            try {
                letterRepository.getLetters()
                    .catch { e ->
                        _historyState.value = UiState.Error(e.message ?: "Unknown Error")
                    }
                    .collect { letters ->
                        _historyState.value = UiState.Success(letters)
                    }
            } catch (e: Exception) {
                _historyState.value = UiState.Error(e.message ?: "Failed to load history")
            }
        }
    }

    fun deleteLetter(id: Long) {
        viewModelScope.launch {
            letterRepository.deleteLetter(id)
        }
    }
}

package com.soundletter.app.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailMessageScreenViewModel(
    private val letterRepository: LetterRepository
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<Note>>(UiState.Idle)
    val state: StateFlow<UiState<Note>> = _state.asStateFlow()

    fun loadMessage(id: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val noteId = id.toLongOrNull() ?: 0L
                val result = letterRepository.getLetterById(noteId)
                if (result != null) {
                    _state.value = UiState.Success(result)
                } else {
                    _state.value = UiState.Error("Letter not found")
                }
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}

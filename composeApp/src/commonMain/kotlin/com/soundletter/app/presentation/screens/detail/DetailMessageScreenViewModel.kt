package com.soundletter.app.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailState(
    val message: Note? = null,
    val isLoading: Boolean = false
)

class DetailMessageScreenViewModel(
    private val letterRepository: LetterRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    fun loadMessage(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val noteId = id.toLongOrNull() ?: 0L
            val result = letterRepository.getLetterById(noteId)
            _state.update { it.copy(message = result, isLoading = false) }
        }
    }
}

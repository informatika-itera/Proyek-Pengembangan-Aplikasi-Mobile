package com.soundletter.app.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soundletter.app.domain.model.Note
import com.soundletter.app.domain.repository.LetterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeState(
    val letters: List<Note> = emptyList(),
    val isLoading: Boolean = false
)

class HomeScreenViewModel(
    private val letterRepository: LetterRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadLetters()
    }

    private fun loadLetters() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            letterRepository.getLetters().collect { letters ->
                _state.update { it.copy(letters = letters, isLoading = false) }
            }
        }
    }
}

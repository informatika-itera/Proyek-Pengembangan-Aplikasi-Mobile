package id.pusakakata.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val words: List<Word>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    object Empty : HomeUiState()
}

class HomeViewModel(private val repository: WordRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            repository.getAllWords()
                .onStart { _uiState.value = HomeUiState.Loading }
                .catch { _uiState.value = HomeUiState.Error(it.message ?: "Unknown Error") }
                .collect { words ->
                    _uiState.value = if (words.isEmpty()) HomeUiState.Empty else HomeUiState.Success(words)
                }
        }
    }

    fun deleteWord(id: String) {
        viewModelScope.launch {
            repository.deleteWord(id)
        }
    }
}

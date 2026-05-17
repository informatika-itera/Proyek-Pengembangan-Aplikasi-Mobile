package id.pusakakata.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val word: Word) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class DetailViewModel(
    private val repository: WordRepository,
    private val wordId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    init {
        loadWord()
    }

    private fun loadWord() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            val word = repository.getWordById(wordId)
            if (word != null) {
                _uiState.value = DetailUiState.Success(word)
            } else {
                _uiState.value = DetailUiState.Error("Kata tidak ditemukan")
            }
        }
    }
}

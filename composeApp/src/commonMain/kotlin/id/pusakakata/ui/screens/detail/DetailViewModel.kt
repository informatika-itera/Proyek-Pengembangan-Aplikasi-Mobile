package id.pusakakata.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: ItemRepository,
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

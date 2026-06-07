package id.pusakakata.ui.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface FavoriteUiState {
    object Loading : FavoriteUiState
    data class Success(val words: List<Word>) : FavoriteUiState
    object Empty : FavoriteUiState
}

class FavoriteViewModel(private val repository: ItemRepository) : ViewModel() {
    val uiState: StateFlow<FavoriteUiState> = repository.getFavoriteWords()
        .map { words ->
            if (words.isEmpty()) FavoriteUiState.Empty else FavoriteUiState.Success(words)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavoriteUiState.Loading)

    fun toggleFavorite(wordId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(wordId)
        }
    }

    fun deleteWord(id: String) {
        viewModelScope.launch {
            repository.deleteWord(id)
        }
    }
}

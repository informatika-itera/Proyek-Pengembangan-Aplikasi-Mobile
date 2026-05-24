package id.pusakakata.ui.screens.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed interface FlashcardUiState {
    object Loading : FlashcardUiState
    data class Success(
        val words: List<Word>,
        val currentIndex: Int = 0,
        val isFlipped: Boolean = false,
        val isFinished: Boolean = false
    ) : FlashcardUiState {
        val currentWord: Word? get() = if (words.isNotEmpty() && currentIndex < words.size) words[currentIndex] else null
    }
    object Empty : FlashcardUiState
}

class FlashcardViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<FlashcardUiState>(FlashcardUiState.Loading)
    val uiState: StateFlow<FlashcardUiState> = _uiState

    init {
        loadFlashcards()
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            val allWords = repository.getAllWords().first()
            val dueWords = allWords.filter { 
                val nextReview = it.srsData.nextReview?.toEpochMilliseconds() ?: 0L
                nextReview <= Clock.System.now().toEpochMilliseconds()
            }.shuffled()

            _uiState.value = if (dueWords.isEmpty()) FlashcardUiState.Empty else FlashcardUiState.Success(dueWords)
        }
    }

    fun flipCard() {
        val state = _uiState.value
        if (state is FlashcardUiState.Success) {
            _uiState.value = state.copy(isFlipped = !state.isFlipped)
        }
    }

    fun nextCard(quality: Int) {
        val state = _uiState.value
        if (state is FlashcardUiState.Success) {
            viewModelScope.launch {
                val currentWord = state.currentWord ?: return@launch
                // Implementasi SM-2 Algorithm via Repository
                repository.updateSrs(currentWord.id, quality)
                
                if (state.currentIndex + 1 < state.words.size) {
                    _uiState.value = state.copy(
                        currentIndex = state.currentIndex + 1,
                        isFlipped = false
                    )
                } else {
                    _uiState.value = state.copy(isFinished = true)
                }
            }
        }
    }
}

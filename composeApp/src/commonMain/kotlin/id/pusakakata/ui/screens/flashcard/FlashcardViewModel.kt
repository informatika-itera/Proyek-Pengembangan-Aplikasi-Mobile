package id.pusakakata.ui.screens.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class FlashcardUiState(
    val wordsToReview: List<Word> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false
)

class FlashcardViewModel(private val repository: WordRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            repository.getAllWords().first().let { allWords ->
                val now = Clock.System.now()
                val toReview = allWords.filter { 
                    it.srsData.nextReview == null || it.srsData.nextReview!! <= now 
                }.shuffled()
                
                _uiState.update { it.copy(wordsToReview = toReview, isLoading = false) }
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun submitReview(quality: Int) {
        val currentWord = _uiState.value.wordsToReview.getOrNull(_uiState.value.currentIndex) ?: return
        
        viewModelScope.launch {
            val newSrsData = currentWord.srsData.copy(
                level = (currentWord.srsData.level + 1).coerceAtMost(5),
                intervalDays = (currentWord.srsData.intervalDays + quality).coerceAtLeast(1)
            )
            repository.updateWord(currentWord.copy(srsData = newSrsData))
            moveToNext()
        }
    }

    private fun moveToNext() {
        _uiState.update { state ->
            val nextIndex = state.currentIndex + 1
            if (nextIndex >= state.wordsToReview.size) {
                state.copy(isFinished = true)
            } else {
                state.copy(currentIndex = nextIndex, isFlipped = false)
            }
        }
    }
}

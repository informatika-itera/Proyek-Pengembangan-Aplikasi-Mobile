package id.pusakakata.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.pusakakata.domain.model.Word
import id.pusakakata.domain.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface QuizUiState {
    object Loading : QuizUiState
    data class Question(
        val word: Word,
        val options: List<String>,
        val correctAnswer: String,
        val quizMessage: String = "Menganalisis kosa kata dari pusaka anda..."
    ) : QuizUiState
    data class Finished(val isCorrect: Boolean) : QuizUiState
    object Empty : QuizUiState
}

class QuizViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        generateQuestion()
    }

    fun generateQuestion() {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val allWords = repository.getRandomWords(10)
            if (allWords.size < 3) {
                _uiState.value = QuizUiState.Empty
                return@launch
            }

            val correctWord = allWords.random()
            val otherDefinitions = allWords
                .filter { it.id != correctWord.id }
                .shuffled()
                .take(2)
                .map { it.definition }
            
            val options = (otherDefinitions + correctWord.definition).shuffled()

            _uiState.value = QuizUiState.Question(
                word = correctWord,
                options = options,
                correctAnswer = correctWord.definition,
                quizMessage = "AI Pusaka telah merumuskan tantangan untukmu!"
            )
        }
    }

    fun submitAnswer(answer: String) {
        val state = _uiState.value
        if (state is QuizUiState.Question) {
            viewModelScope.launch {
                val isCorrect = (answer == state.correctAnswer)
                if (isCorrect) {
                    repository.addTokens(1)
                    // Update SRS jika benar (Quality 4 = Berhasil)
                    repository.updateSrs(state.word.id, 4)
                } else {
                    // Update SRS jika salah (Quality 1 = Gagal)
                    repository.updateSrs(state.word.id, 1)
                }
                _uiState.value = QuizUiState.Finished(isCorrect)
            }
        }
    }
    
    fun updateSrsAndNext(difficulty: Int) {
        // Diganti dengan submitAnswer yang lebih otomatis
        generateQuestion()
    }
}

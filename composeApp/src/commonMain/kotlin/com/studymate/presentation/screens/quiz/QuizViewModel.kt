package com.studymate.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.data.remote.dto.QuizResponseDto
import com.studymate.domain.model.Quiz
import com.studymate.domain.repository.AIRepository
import com.studymate.domain.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed interface QuizUiState {
    object Idle : QuizUiState
    object Loading : QuizUiState
    data class Success(val quizzes: List<Quiz>, val currentIndex: Int = 0) : QuizUiState
    data class Error(val message: String) : QuizUiState
}

class QuizViewModel(
    private val aiRepository: AIRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Idle)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun generateQuizFromLatestNote() {
        if (_uiState.value is QuizUiState.Loading) return
        
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            
            try {
                val notes = noteRepository.getAllNotes().first()
                val latestNote = notes.maxByOrNull { it.updatedAt }
                
                if (latestNote == null) {
                    _uiState.value = QuizUiState.Error("Belum ada catatan untuk dibuat kuis.")
                    return@launch
                }

                val result = aiRepository.generateQuiz(latestNote.refinedContent ?: latestNote.rawContent)
                result.onSuccess { jsonString ->
                    try {
                        val cleanedJson = jsonString.trim()
                            .removePrefix("```json")
                            .removeSuffix("```")
                            .trim()

                        val quizResponse = json.decodeFromString<QuizResponseDto>(cleanedJson)
                        if (quizResponse.questions.isEmpty()) {
                            _uiState.value = QuizUiState.Error("AI tidak menemukan materi yang cukup untuk membuat soal.")
                        } else {
                            val quizList = quizResponse.questions.map { dto ->
                                Quiz(
                                    question = dto.question,
                                    options = dto.options,
                                    correctAnswerIndex = dto.correct,
                                    explanation = dto.explanation
                                )
                            }
                            _uiState.value = QuizUiState.Success(quizList)
                        }
                    } catch (e: Exception) {
                        _uiState.value = QuizUiState.Error("Gagal memproses soal kuis: ${e.message}")
                    }
                }.onFailure {
                    _uiState.value = QuizUiState.Error(it.message ?: "Gagal membuat kuis.")
                }
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error("Gagal mengambil data catatan.")
            }
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (state is QuizUiState.Success) {
            if (state.currentIndex < state.quizzes.size - 1) {
                _uiState.value = state.copy(currentIndex = state.currentIndex + 1)
            } else {
                _uiState.value = QuizUiState.Idle // Quiz finished
            }
        }
    }
}

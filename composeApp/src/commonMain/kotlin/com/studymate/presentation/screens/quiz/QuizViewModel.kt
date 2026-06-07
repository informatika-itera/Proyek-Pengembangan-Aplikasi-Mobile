package com.studymate.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studymate.data.remote.dto.QuizResponseDto
import com.studymate.domain.model.QuizHistory
import com.studymate.domain.model.QuizQuestion
import com.studymate.domain.model.QuizSession
import com.studymate.domain.model.Note
import com.studymate.domain.repository.AIRepository
import com.studymate.domain.repository.NoteRepository
import com.studymate.domain.repository.QuizRepository
import com.studymate.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

sealed interface QuizUiState {
    data object History : QuizUiState
    data object Loading : QuizUiState
    data class ActiveSession(
        val note: Note,
        val questions: List<QuizQuestion>,
        val currentIndex: Int = 0,
        val answers: Map<Int, Int> = emptyMap(),
        val isFinished: Boolean = false
    ) : QuizUiState
    data class Error(val message: String) : QuizUiState
}

class QuizViewModel(
    private val aiRepository: AIRepository,
    private val noteRepository: NoteRepository,
    private val quizRepository: QuizRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.History)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<QuizHistory>> = quizRepository.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<Note>> = noteRepository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startQuiz(note: Note) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            
            val content = """
                Judul: ${note.title}
                Mata Kuliah: ${note.subject}
                Isi: ${note.refinedContent ?: note.rawContent}
            """.trimIndent()

            val result = aiRepository.generateQuiz(content)
            result.onSuccess { jsonString ->
                try {
                    val cleanedJson = jsonString.trim()
                        .removePrefix("```json")
                        .removeSuffix("```")
                        .trim()

                    val quizResponse = json.decodeFromString<QuizResponseDto>(cleanedJson)
                    val questions = quizResponse.questions.map { dto ->
                        QuizQuestion(
                            question = dto.question,
                            options = dto.options,
                            correctAnswerIndex = dto.correct,
                            explanation = dto.explanation
                        )
                    }
                    
                    _uiState.value = QuizUiState.ActiveSession(
                        note = note,
                        questions = questions
                    )
                } catch (e: Exception) {
                    _uiState.value = QuizUiState.Error("Gagal memproses soal: ${e.message}")
                }
            }.onFailure {
                _uiState.value = QuizUiState.Error(it.message ?: "Gagal membuat kuis.")
            }
        }
    }

    fun submitAnswer(questionIndex: Int, answerIndex: Int) {
        val state = _uiState.value
        if (state is QuizUiState.ActiveSession) {
            val updatedAnswers = state.answers + (questionIndex to answerIndex)
            _uiState.value = state.copy(answers = updatedAnswers)
            
            if ((questionIndex == state.questions.size - 1)) {
                finishQuiz(state.copy(answers = updatedAnswers))
            } else {
                _uiState.value = state.copy(
                    answers = updatedAnswers,
                    currentIndex = state.currentIndex + 1
                )
            }
        }
    }

    private fun finishQuiz(session: QuizUiState.ActiveSession) {
        val correctCount = session.answers.filter { (idx, answer) ->
            session.questions[idx].correctAnswerIndex == answer
        }.size
        
        val history = QuizHistory(
            noteId = session.note.id,
            noteTitle = session.note.title,
            subject = session.note.subject,
            score = correctCount,
            totalQuestions = session.questions.size,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        
        viewModelScope.launch {
            quizRepository.insertHistory(history)
            activityRepository.recordQuizCompletion()
            _uiState.value = session.copy(isFinished = true)
        }
    }

    fun backToHistory() {
        _uiState.value = QuizUiState.History
    }

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            quizRepository.deleteHistory(id)
        }
    }
}

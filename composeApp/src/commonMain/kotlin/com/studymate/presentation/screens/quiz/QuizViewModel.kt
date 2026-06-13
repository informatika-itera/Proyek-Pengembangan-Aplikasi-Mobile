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
    data class Review(val history: QuizHistory) : QuizUiState
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

    val allSubjects: StateFlow<List<String>> = notes.map { list ->
        list.map { it.subject }.filter { it.isNotBlank() }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startQuiz(note: Note, questionCount: Int = 5) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            
            val result = aiRepository.generateQuiz(
                subject = note.subject,
                title = note.title,
                noteContent = note.refinedContent ?: note.rawContent,
                questionCount = questionCount
            )
            result.onSuccess { jsonString ->
                try {
                    val cleanedJson = extractJson(jsonString)
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
                    _uiState.value = QuizUiState.Error("Gagal memproses soal: ${e.message}\n\nRespon Mentah: ${jsonString.take(100)}...")
                }
            }.onFailure {
                _uiState.value = QuizUiState.Error(it.message ?: "Gagal membuat kuis.")
            }
        }
    }

    fun startAdvancedQuiz(subject: String, selectedNotes: List<Note>, questionCount: Int) {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            
            val combinedContent = selectedNotes.joinToString("\n\n") { it.refinedContent ?: it.rawContent }
            val result = aiRepository.generateQuiz(
                subject = subject,
                title = "Advanced Quiz: $subject",
                noteContent = combinedContent,
                questionCount = questionCount
            )
            
            result.onSuccess { jsonString ->
                try {
                    val cleanedJson = extractJson(jsonString)
                    val quizResponse = json.decodeFromString<QuizResponseDto>(cleanedJson)
                    val questions = quizResponse.questions.take(questionCount).map { dto ->
                        QuizQuestion(
                            question = dto.question,
                            options = dto.options,
                            correctAnswerIndex = dto.correct,
                            explanation = dto.explanation
                        )
                    }
                    
                    _uiState.value = QuizUiState.ActiveSession(
                        note = selectedNotes.firstOrNull() ?: Note(title = "Advanced Quiz", subject = subject, rawContent = ""),
                        questions = questions
                    )
                } catch (e: Exception) {
                    _uiState.value = QuizUiState.Error("Gagal memproses soal: ${e.message}\n\nRespon Mentah: ${jsonString.take(100)}...")
                }
            }.onFailure {
                _uiState.value = QuizUiState.Error(it.message ?: "Gagal membuat kuis.")
            }
        }
    }

    private fun extractJson(input: String): String {
        val firstBrace = input.indexOf('{')
        val lastBrace = input.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return input.substring(firstBrace, lastBrace + 1)
        }
        return input.trim().removePrefix("```json").removeSuffix("```").trim()
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
            createdAt = Clock.System.now().toEpochMilliseconds(),
            questions = session.questions,
            userAnswers = session.answers
        )
        
        viewModelScope.launch {
            quizRepository.insertHistory(history)
            activityRepository.recordQuizCompletion()
            _uiState.value = session.copy(isFinished = true)
        }
    }

    fun viewHistory(history: QuizHistory) {
        _uiState.value = QuizUiState.Review(history)
    }

    fun reviewCurrentSession() {
        val state = _uiState.value
        if (state is QuizUiState.ActiveSession && state.isFinished) {
            val history = QuizHistory(
                noteId = state.note.id,
                noteTitle = state.note.title,
                subject = state.note.subject,
                score = state.answers.filter { (idx, ans) -> state.questions[idx].correctAnswerIndex == ans }.size,
                totalQuestions = state.questions.size,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                questions = state.questions,
                userAnswers = state.answers
            )
            _uiState.value = QuizUiState.Review(history)
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

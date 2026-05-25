package com.example.Feelia.presentation.screens.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Feelia.domain.model.Emotion
import com.example.Feelia.domain.model.Note
import com.example.Feelia.domain.repository.NoteRepository
import com.example.Feelia.domain.usecase.DetectEmotionUseCase
import com.example.Feelia.domain.usecase.SaveNoteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AddNoteViewModel(
    private val repository: NoteRepository,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val detectEmotionUseCase: DetectEmotionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddNoteUiState())
    val uiState: StateFlow<AddNoteUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddNoteEvent>()
    val events: SharedFlow<AddNoteEvent> = _events.asSharedFlow()

    private var currentNoteId: Long? = null
    private var detectEmotionJob: Job? = null

    fun loadNote(noteId: Long) {
        currentNoteId = noteId
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getNoteById(noteId).collect { note ->
                note?.let {
                    _uiState.update { state ->
                        state.copy(
                            content = note.content,
                            emotion = note.emotion,
                            isLoading = false,
                            isEditMode = true,
                            createdAt = note.createdAt
                        )
                    }
                }
            }
        }
    }

    fun onContentChange(content: String) {
        _uiState.update { it.copy(content = content, contentError = null) }
        // Auto-detect emosi setelah user berhenti mengetik 1.5 detik
        if (content.trim().length >= 15) {
            scheduleEmotionDetection(content)
        }
    }

    fun onEmotionChange(emotion: Emotion) {
        _uiState.update { it.copy(emotion = emotion, isEmotionAutoDetected = false) }
    }

    fun saveNote() {
        val state = _uiState.value
        if (state.content.isBlank()) {
            _uiState.update { it.copy(contentError = "Ceritakan harimu dulu ya 😊") }
            return
        }
        if (state.content.trim().length < 10) {
            _uiState.update { it.copy(contentError = "Ceritakan lebih banyak (min. 10 karakter)") }
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val note = Note(
                id = currentNoteId ?: 0,
                content = state.content.trim(),
                emotion = state.emotion,
                createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                updatedAt = Clock.System.now()
            )
            saveNoteUseCase(note)
                .onSuccess { _events.emit(AddNoteEvent.NoteSaved) }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(AddNoteEvent.Error(error.message ?: "Gagal menyimpan"))
                }
        }
    }

    private fun scheduleEmotionDetection(content: String) {
        detectEmotionJob?.cancel()
        detectEmotionJob = viewModelScope.launch {
            delay(1500)
            _uiState.update { it.copy(isDetectingEmotion = true) }
            detectEmotionUseCase(content)
                .onSuccess { emotion ->
                    _uiState.update {
                        it.copy(
                            emotion = emotion,
                            isDetectingEmotion = false,
                            isEmotionAutoDetected = true
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isDetectingEmotion = false) }
                }
        }
    }
}

data class AddNoteUiState(
    val content: String = "",
    val emotion: Emotion = Emotion.NEUTRAL,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val isDetectingEmotion: Boolean = false,
    val isEmotionAutoDetected: Boolean = false,
    val contentError: String? = null,
    val createdAt: Instant = Clock.System.now()
) {
    val canSave: Boolean get() = content.isNotBlank() && !isSaving
    val charCount: Int get() = content.length
}

sealed interface AddNoteEvent {
    data object NoteSaved : AddNoteEvent
    data class Error(val message: String) : AddNoteEvent
}
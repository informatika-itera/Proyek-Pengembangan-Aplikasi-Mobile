package com.example.noteai.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Note
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.usecase.DeleteNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    private val repository: NoteRepository,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<NoteDetailUiState>(NoteDetailUiState.Loading)
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<NoteDetailEvent>()
    val events: SharedFlow<NoteDetailEvent> = _events.asSharedFlow()
    
    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            repository.getNoteById(noteId).collect { note ->
                _uiState.value = if (note != null) {
                    NoteDetailUiState.Success(note)
                } else {
                    NoteDetailUiState.NotFound
                }
            }
        }
    }
    
    fun togglePin() {
        val currentState = _uiState.value
        if (currentState is NoteDetailUiState.Success) {
            viewModelScope.launch {
                repository.togglePinNote(currentState.note.id)
            }
        }
    }
    
    fun deleteNote() {
        val currentState = _uiState.value
        if (currentState is NoteDetailUiState.Success) {
            viewModelScope.launch {
                deleteNoteUseCase(currentState.note.id)
                    .onSuccess {
                        _events.emit(NoteDetailEvent.NoteDeleted)
                    }
                    .onFailure { error ->
                        _events.emit(NoteDetailEvent.Error(error.message ?: "Gagal menghapus"))
                    }
            }
        }
    }
    
    fun getShareContent(): String? {
        val currentState = _uiState.value
        return if (currentState is NoteDetailUiState.Success) {
            val note = currentState.note
            buildString {
                if (note.title.isNotBlank()) {
                    appendLine(note.title)
                    appendLine()
                }
                append(note.content)
            }
        } else null
    }
}

sealed interface NoteDetailUiState {
    data object Loading : NoteDetailUiState
    data class Success(val note: Note) : NoteDetailUiState
    data object NotFound : NoteDetailUiState
}

sealed interface NoteDetailEvent {
    data object NoteDeleted : NoteDetailEvent
    data class Error(val message: String) : NoteDetailEvent
}

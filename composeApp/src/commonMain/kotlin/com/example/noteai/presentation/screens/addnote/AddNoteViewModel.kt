package com.example.noteai.presentation.screens.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteai.domain.model.Note
import com.example.noteai.domain.model.NoteColor
import com.example.noteai.domain.model.VulnSeverity
import com.example.noteai.domain.model.VulnStatus
import com.example.noteai.domain.model.VulnType
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.domain.usecase.SaveNoteUseCase
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
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddNoteUiState())
    val uiState: StateFlow<AddNoteUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AddNoteEvent>()
    val events: SharedFlow<AddNoteEvent> = _events.asSharedFlow()
    
    private var currentNoteId: Long? = null
    
    fun loadNote(noteId: Long) {
        currentNoteId = noteId
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            repository.getNoteById(noteId).collect { note ->
                note?.let {
                    _uiState.update { state ->
                        state.copy(
                            title = note.title,
                            content = note.content,
                            targetUrl = note.targetUrl,
                            vulnType = note.vulnType,
                            severity = note.severity,
                            status = note.status,
                            isLoading = false,
                            isEditMode = true,
                            createdAt = note.createdAt
                        )
                    }
                }
            }
        }
    }
    
    // ==================== USER ACTIONS ====================
    
    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }
    
    fun onContentChange(content: String) {
        _uiState.update { it.copy(content = content) }
    }
    
    fun onTargetUrlChange(url: String) {
        _uiState.update { it.copy(targetUrl = url) }
    }
    
    fun onVulnTypeChange(vulnType: VulnType) {
        _uiState.update { it.copy(vulnType = vulnType) }
    }
    
    fun onSeverityChange(severity: VulnSeverity) {
        _uiState.update { it.copy(severity = severity) }
    }
    
    fun onStatusChange(status: VulnStatus) {
        _uiState.update { it.copy(status = status) }
    }
    
    fun saveNote() {
        val state = _uiState.value
        
        if (state.title.isBlank() && state.content.isBlank()) {
            _uiState.update { it.copy(titleError = "Judul atau deskripsi harus diisi") }
            return
        }
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            val note = Note(
                id = currentNoteId ?: 0,
                title = state.title.trim(),
                content = state.content.trim(),
                targetUrl = state.targetUrl.trim(),
                vulnType = state.vulnType,
                severity = state.severity,
                status = state.status,
                createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                updatedAt = Clock.System.now()
            )
            
            saveNoteUseCase(note)
                .onSuccess {
                    _events.emit(AddNoteEvent.NoteSaved)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(AddNoteEvent.Error(error.message ?: "Gagal menyimpan"))
                }
        }
    }
    
    fun applyAISuggestion(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }
    
    fun applyAITitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }
}

data class AddNoteUiState(
    val title: String = "",
    val content: String = "",
    val targetUrl: String = "",
    val vulnType: VulnType = VulnType.OTHER,
    val severity: VulnSeverity = VulnSeverity.NONE,
    val status: VulnStatus = VulnStatus.NEW,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val createdAt: Instant = Clock.System.now()
) {
    val isValid: Boolean
        get() = title.isNotBlank() || content.isNotBlank()
    
    val canSave: Boolean
        get() = isValid && !isSaving
}

sealed interface AddNoteEvent {
    data object NoteSaved : AddNoteEvent
    data class Error(val message: String) : AddNoteEvent
}

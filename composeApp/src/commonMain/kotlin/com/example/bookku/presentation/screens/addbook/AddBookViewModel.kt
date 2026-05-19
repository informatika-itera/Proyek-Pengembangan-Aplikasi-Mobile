package com.example.bookku.presentation.screens.addbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.model.BookRating
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.usecase.SaveNoteUseCase
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

class AddBookViewModel(
    private val repository: NoteRepository,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState: StateFlow<AddBookUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AddBookEvent>()
    val events: SharedFlow<AddBookEvent> = _events.asSharedFlow()
    
    private var currentNoteId: Long? = null
    
    fun loadNote(noteId: Long) {
        currentNoteId = noteId
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            repository.getBookById(noteId).collect { book ->
                book?.let {
                    _uiState.update { state ->
                        state.copy(
                            title = book.title,
                            content = book.content,
                            category = book.category,
                            color = book.color,
                            isLoading = false,
                            isEditMode = true,
                            createdAt = book.createdAt
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
    
    fun onCategoryChange(category: BookGenre) {
        _uiState.update { it.copy(category = category) }
    }
    
    fun onColorChange(color: BookRating) {
        _uiState.update { it.copy(color = color) }
    }
    
    fun saveNote() {
        val state = _uiState.value
        
        if (state.title.isBlank() && state.content.isBlank()) {
            _uiState.update { it.copy(titleError = "Judul atau konten harus diisi") }
            return
        }
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            val book = Book(
                id = currentNoteId ?: 0,
                title = state.title.trim(),
                content = state.content.trim(),
                category = state.category,
                color = state.color,
                createdAt = if (currentNoteId == null) Clock.System.now() else state.createdAt,
                updatedAt = Clock.System.now()
            )
            
            saveNoteUseCase(book)
                .onSuccess {
                    _events.emit(AddBookEvent.BookSaved)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(AddBookEvent.Error(error.message ?: "Gagal menyimpan"))
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

data class AddBookUiState(
    val title: String = "",
    val content: String = "",
    val category: BookGenre = BookGenre.GENERAL,
    val color: BookRating = BookRating.DEFAULT,
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

sealed interface AddBookEvent {
    data object BookSaved : AddBookEvent
    data class Error(val message: String) : AddBookEvent
}

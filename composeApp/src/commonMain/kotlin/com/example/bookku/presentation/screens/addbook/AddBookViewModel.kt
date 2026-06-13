package com.example.bookku.presentation.screens.addbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.model.BookRating
import com.example.bookku.domain.repository.AuthRepository
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.usecase.SaveNoteUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AddBookViewModel(
    private val repository: NoteRepository,
    private val authRepository: AuthRepository,
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState: StateFlow<AddBookUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<AddBookEvent>()
    val events: SharedFlow<AddBookEvent> = _events.asSharedFlow()
    
    private var currentNoteId: Long? = null
    
    fun loadNote(noteId: Long) {
        if (currentNoteId == noteId) return
        currentNoteId = noteId
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId()
            repository.getBookById(noteId).collect { book ->
                if (book != null) {
                    if (book.userId != currentUserId) {
                        _events.emit(AddBookEvent.Error("Anda tidak memiliki izin untuk mengedit buku ini"))
                        _uiState.update { it.copy(isLoading = false) }
                        return@collect
                    }
                    _uiState.update { state ->
                        state.copy(
                            title = book.title,
                            author = book.author,
                            coverUrl = book.coverUrl,
                            content = book.content,
                            category = book.category,
                            color = book.color,
                            totalPages = book.totalPages,
                            isLoading = false,
                            isEditMode = true,
                            createdAt = book.createdAt
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }
    
    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onAuthorChange(author: String) {
        _uiState.update { it.copy(author = author) }
    }

    fun onCoverUrlChange(url: String) {
        _uiState.update { it.copy(coverUrl = url) }
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

    fun onTotalPagesChange(pages: Int) {
        _uiState.update { it.copy(totalPages = pages) }
    }
    
    fun saveNote() {
        val state = _uiState.value
        
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Judul harus diisi") }
            return
        }
        
        _uiState.update { it.copy(isSaving = true) }
        
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: ""
            val book = Book(
                id = currentNoteId ?: 0,
                userId = userId,
                title = state.title.trim(),
                author = state.author.trim(),
                coverUrl = state.coverUrl.trim(),
                content = state.content.trim(),
                category = state.category,
                color = state.color,
                totalPages = state.totalPages,
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
}

data class AddBookUiState(
    val title: String = "",
    val author: String = "",
    val coverUrl: String = "",
    val content: String = "",
    val category: BookGenre = BookGenre.FICTION,
    val color: BookRating = BookRating.DEFAULT,
    val totalPages: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val titleError: String? = null,
    val createdAt: Instant = Clock.System.now()
) {
    val isValid: Boolean
        get() = title.isNotBlank()
    
    val canSave: Boolean
        get() = isValid && !isSaving
}

sealed interface AddBookEvent {
    data object BookSaved : AddBookEvent
    data class Error(val message: String) : AddBookEvent
}

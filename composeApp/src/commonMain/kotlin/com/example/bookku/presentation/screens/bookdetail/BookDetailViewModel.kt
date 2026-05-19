package com.example.bookku.presentation.screens.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.usecase.deleteBookUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val repository: NoteRepository,
    private val deleteBookUseCase: deleteBookUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<NoteDetailUiState>(NoteDetailUiState.Loading)
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<NoteDetailEvent>()
    val events: SharedFlow<NoteDetailEvent> = _events.asSharedFlow()
    
    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            repository.getBookById(noteId).collect { book ->
                _uiState.value = if (book != null) {
                    NoteDetailUiState.Success(book)
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
                repository.togglePinNote(currentState.book.id)
            }
        }
    }
    
    fun deleteBook() {
        val currentState = _uiState.value
        if (currentState is NoteDetailUiState.Success) {
            viewModelScope.launch {
                deleteBookUseCase(currentState.book.id)
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
            val book = currentState.book
            buildString {
                if (book.title.isNotBlank()) {
                    appendLine(book.title)
                    appendLine()
                }
                append(book.content)
            }
        } else null
    }
}

sealed interface NoteDetailUiState {
    data object Loading : NoteDetailUiState
    data class Success(val book: Book) : NoteDetailUiState
    data object NotFound : NoteDetailUiState
}

sealed interface NoteDetailEvent {
    data object NoteDeleted : NoteDetailEvent
    data class Error(val message: String) : NoteDetailEvent
}

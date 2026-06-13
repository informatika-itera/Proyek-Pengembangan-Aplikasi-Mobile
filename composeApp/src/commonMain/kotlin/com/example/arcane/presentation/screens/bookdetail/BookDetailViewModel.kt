package com.example.arcane.presentation.screens.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.domain.model.Folder
import com.example.arcane.domain.repository.BookRepository
import com.example.arcane.domain.repository.FolderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val repository: BookRepository,
    private val folderRepository: FolderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BookDetailEvent>()
    val events: SharedFlow<BookDetailEvent> = _events.asSharedFlow()

    private val _allFolders = MutableStateFlow<List<Folder>>(emptyList())
    val allFolders: StateFlow<List<Folder>> = _allFolders.asStateFlow()

    private val _bookFolders = MutableStateFlow<List<Folder>>(emptyList())
    val bookFolders: StateFlow<List<Folder>> = _bookFolders.asStateFlow()

    init {
        viewModelScope.launch {
            folderRepository.getAllFolders().collect { folders ->
                _allFolders.value = folders
            }
        }
    }

    fun loadBook(googleBookId: String, localId: Long) {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            if (localId > 0) {
                viewModelScope.launch {
                    folderRepository.getFoldersForBook(localId).collect { folders ->
                        _bookFolders.value = folders
                    }
                }
                repository.getBookById(localId).collect { book ->
                    _uiState.value = book?.let {
                        BookDetailUiState.Success(it)
                    } ?: BookDetailUiState.Error("Buku tidak ditemukan")
                }
            } else {
                val localBook = repository.getBookByGoogleId(googleBookId)
                if (localBook != null) {
                    _uiState.value = BookDetailUiState.Success(localBook)
                } else {
                    val remoteBook = repository.getBookDetail(googleBookId)
                    _uiState.value = if (remoteBook != null) {
                        BookDetailUiState.NotInLibrary(remoteBook)
                    } else {
                        BookDetailUiState.Error("Buku tidak ditemukan")
                    }
                }
            }
        }
    }
    fun saveToLibrary(book: Book) {
        viewModelScope.launch {
            try {
                repository.saveBook(book)
                _events.emit(BookDetailEvent.ShowSnackbar("✓ Buku berhasil ditambahkan ke perpustakaan!"))
                _uiState.value = BookDetailUiState.NotInLibrary(book, isSaved = true)
            } catch (e: Exception) {
                _events.emit(BookDetailEvent.ShowSnackbar("Gagal menyimpan buku"))
            }
        }
    }

    fun updateStatus(status: ReadingStatus) {
        val currentState = _uiState.value
        if (currentState is BookDetailUiState.Success) {
            viewModelScope.launch {
                repository.updateBookStatus(currentState.book.id, status)
            }
        }
    }

    fun saveNotesAndRating(notes: String, rating: Int?) {
        val currentState = _uiState.value
        if (currentState is BookDetailUiState.Success) {
            viewModelScope.launch {
                repository.updateBookNotesAndRating(currentState.book.id, notes, rating)
                _events.emit(BookDetailEvent.ShowSnackbar("Catatan berhasil disimpan"))
            }
        }
    }

    fun toggleFolder(folder: Folder) {
        val currentState = _uiState.value
        if (currentState is BookDetailUiState.Success) {
            val bookId = currentState.book.id
            viewModelScope.launch {
                val isCurrentlyInFolder = _bookFolders.value.any { it.id == folder.id }
                if (isCurrentlyInFolder) {
                    folderRepository.removeBookFromFolder(bookId, folder.id)
                } else {
                    folderRepository.addBookToFolder(bookId, folder.id)
                }
            }
        }
    }

    fun deleteFromLibrary() {
        val currentState = _uiState.value
        if (currentState is BookDetailUiState.Success) {
            viewModelScope.launch {
                repository.deleteBook(currentState.book.id)
                _events.emit(BookDetailEvent.BookDeleted)
            }
        }
    }
}

sealed interface BookDetailUiState {
    data object Loading : BookDetailUiState
    data class Success(val book: Book) : BookDetailUiState
    data class NotInLibrary(val book: Book, val isSaved: Boolean = false) : BookDetailUiState
    data class Error(val message: String) : BookDetailUiState
}

sealed interface BookDetailEvent {
    data object BookDeleted : BookDetailEvent
    data class ShowSnackbar(val message: String) : BookDetailEvent
}
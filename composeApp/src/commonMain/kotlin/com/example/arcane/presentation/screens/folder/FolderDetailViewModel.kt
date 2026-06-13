package com.example.arcane.presentation.screens.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.Folder
import com.example.arcane.domain.repository.BookRepository
import com.example.arcane.domain.repository.FolderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FolderDetailViewModel(
    private val folderRepository: FolderRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _folder = MutableStateFlow<Folder?>(null)
    val folder: StateFlow<Folder?> = _folder

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())
    val allBooks: StateFlow<List<Book>> = _allBooks

    init {
        viewModelScope.launch {
            bookRepository.getAllBooks().collect { books ->
                _allBooks.value = books
            }
        }
    }

    fun loadFolder(folderId: Long) {
        viewModelScope.launch {
            _folder.value = folderRepository.getFolderById(folderId)
            folderRepository.getBooksInFolder(folderId).collect {
                _books.value = it
            }
        }
    }

    fun toggleBookInFolder(bookId: Long, inFolder: Boolean) {
        val currentFolderId = _folder.value?.id ?: return
        viewModelScope.launch {
            if (inFolder) {
                folderRepository.addBookToFolder(bookId, currentFolderId)
            } else {
                folderRepository.removeBookFromFolder(bookId, currentFolderId)
            }
        }
    }
}

package com.example.hujjah.presentation.screens.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hujjah.domain.model.islamic.BookmarkReference
import com.example.hujjah.domain.repository.hujjah.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BookmarkUiState {
    data object Loading : BookmarkUiState
    data class Success(val bookmarks: List<BookmarkReference>) : BookmarkUiState
    data class Error(val message: String) : BookmarkUiState
    data object Empty : BookmarkUiState
}

class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarkUiState>(BookmarkUiState.Loading)
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            try {
                bookmarkRepository.getBookmarks().collect { bookmarks ->
                    _uiState.value = if (bookmarks.isEmpty()) {
                        BookmarkUiState.Empty
                    } else {
                        BookmarkUiState.Success(bookmarks)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = BookmarkUiState.Error(
                    e.message ?: "Gagal memuat bookmark"
                )
            }
        }
    }

    fun deleteBookmark(referenceId: String) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(referenceId)
        }
    }
}

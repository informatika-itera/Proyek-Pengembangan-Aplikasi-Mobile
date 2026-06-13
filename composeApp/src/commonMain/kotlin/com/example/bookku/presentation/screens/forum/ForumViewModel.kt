package com.example.bookku.presentation.screens.forum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.repository.AuthRepository
import com.example.bookku.domain.repository.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ForumViewModel(
    private val repository: NoteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForumUiState>(ForumUiState.Loading)
    val uiState: StateFlow<ForumUiState> = _uiState.asStateFlow()

    init {
        loadCommunityFeed()
    }

    fun loadCommunityFeed() {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId() ?: ""
            
            repository.getAllNotes()
                .onStart { _uiState.value = ForumUiState.Loading }
                .catch { e -> _uiState.value = ForumUiState.Error(e.message ?: "Gagal memuat forum") }
                .collect { books ->
                    // Filter: Hanya tampilkan buku dari USER LAIN di Forum
                    val communityBooks = books.filter { it.userId != currentUserId }
                    
                    if (communityBooks.isEmpty()) {
                        _uiState.value = ForumUiState.Empty
                    } else {
                        _uiState.value = ForumUiState.Success(communityBooks)
                    }
                }
        }
    }
}

sealed interface ForumUiState {
    data object Loading : ForumUiState
    data class Success(val posts: List<Book>) : ForumUiState
    data object Empty : ForumUiState
    data class Error(val message: String) : ForumUiState
}

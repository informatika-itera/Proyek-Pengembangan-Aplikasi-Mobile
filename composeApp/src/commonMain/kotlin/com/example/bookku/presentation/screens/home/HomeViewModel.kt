package com.example.bookku.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.usecase.deleteBookUseCase
import com.example.bookku.domain.usecase.GetAllNotesUseCase
import com.example.bookku.domain.usecase.NoteSortBy
import com.example.bookku.domain.usecase.SearchNotesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val deleteBookUseCase: deleteBookUseCase,
    private val repository: NoteRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<BookGenre?>(null)
    private val _sortBy = MutableStateFlow(NoteSortBy.UPDATED_DESC)
    private val _isLoading = MutableStateFlow(false)
    
    private val debouncedSearchQuery = _searchQuery.debounce(300)
    
    val sortBy: StateFlow<NoteSortBy> = _sortBy
    
    val uiState: StateFlow<HomeUiState> = combine(
        debouncedSearchQuery,
        _selectedCategory,
        _sortBy
    ) { query, category, sortBy ->
        Triple(query, category, sortBy)
    }.flatMapLatest { (query, category, sortBy) ->
        if (query.isBlank() && category == null) {
            getAllNotesUseCase(sortBy)
        } else {
            searchNotesUseCase(query, category)
        }
    }.combine(_isLoading) { books, isLoading ->
        when {
            isLoading -> HomeUiState.Loading
            books.isEmpty() -> HomeUiState.Empty(
                query = _searchQuery.value,
                category = _selectedCategory.value
            )
            else -> HomeUiState.Success(
                books = books,
                query = _searchQuery.value,
                category = _selectedCategory.value,
                sortBy = _sortBy.value
            )
        }
    }.catch { e ->
        emit(HomeUiState.Error(e.message ?: "Terjadi kesalahan"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )
    
    // ==================== USER ACTIONS ====================
    
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    fun onCategorySelected(category: BookGenre?) {
        _selectedCategory.value = category
    }
    
    fun onSortByChanged(sortBy: NoteSortBy) {
        _sortBy.value = sortBy
    }
    
    fun togglePin(noteId: Long) {
        viewModelScope.launch {
            repository.togglePinNote(noteId)
        }
    }
    
    fun deleteBook(noteId: Long) {
        viewModelScope.launch {
            deleteBookUseCase(noteId)
        }
    }
    
    fun deleteBooks(noteIds: List<Long>) {
        viewModelScope.launch {
            repository.deleteBooks(noteIds)
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    
    data class Success(
        val books: List<Book>,
        val query: String = "",
        val category: BookGenre? = null,
        val sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC
    ) : HomeUiState
    
    data class Empty(
        val query: String = "",
        val category: BookGenre? = null
    ) : HomeUiState
    
    data class Error(val message: String) : HomeUiState
}

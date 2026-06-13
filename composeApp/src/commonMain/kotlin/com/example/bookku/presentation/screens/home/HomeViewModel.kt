package com.example.bookku.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookku.data.local.datastore.UserPreferences
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.model.NoteSortBy
import com.example.bookku.domain.repository.AuthRepository
import com.example.bookku.domain.repository.NoteRepository
import com.example.bookku.domain.usecase.deleteBookUseCase
import com.example.bookku.domain.usecase.GetAllNotesUseCase
import com.example.bookku.domain.usecase.SearchNotesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val deleteBookUseCase: deleteBookUseCase,
    private val repository: NoteRepository,
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    val isDarkMode = userPreferences.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val userName = userPreferences.userName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Pembaca"
    )
    
    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = isDarkMode.value
            userPreferences.setDarkMode(!current)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _selectedCategory = MutableStateFlow<BookGenre?>(null)
    val selectedCategory: StateFlow<BookGenre?> = _selectedCategory
    
    private val _sortBy = MutableStateFlow(NoteSortBy.UPDATED_DESC)
    val sortBy: StateFlow<NoteSortBy> = _sortBy

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery,
        _selectedCategory,
        _sortBy,
        authRepository.currentUser
    ) { query, category, sortBy, user ->
        Quadruple(query, category, sortBy, user?.id ?: "")
    }.debounce { (query, _, _, _) ->
        if (query.isEmpty()) 0L else 300L
    }.flatMapLatest { (query, category, sortBy, userId) ->
        _isLoading.value = true
        
        val flow = if (query.isBlank() && category == null) {
            repository.getNotesByUser(userId)
        } else {
            searchNotesUseCase(query, category).map { books -> 
                books.filter { it.userId == userId }
            }
        }
        
        flow.map { books ->
            _isLoading.value = false
            // Terapkan pengurutan Pin dan SortBy secara manual agar konsisten
            val sortedBooks = sortHomeNotes(books, sortBy)
            
            if (sortedBooks.isEmpty()) {
                HomeUiState.Empty(query = query, category = category)
            } else {
                HomeUiState.Success(
                    books = sortedBooks,
                    query = query,
                    category = category,
                    sortBy = sortBy
                )
            }
        }
    }.catch { e ->
        _isLoading.value = false
        emit(HomeUiState.Error(e.message ?: "Terjadi kesalahan"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    private fun sortHomeNotes(books: List<Book>, sortBy: NoteSortBy): List<Book> {
        val (pinned, unpinned) = books.partition { it.isPinned }
        return sortList(pinned, sortBy) + sortList(unpinned, sortBy)
    }

    private fun sortList(books: List<Book>, sortBy: NoteSortBy): List<Book> {
        return when (sortBy) {
            NoteSortBy.TITLE_ASC -> books.sortedBy { it.title.lowercase() }
            NoteSortBy.TITLE_DESC -> books.sortedByDescending { it.title.lowercase() }
            NoteSortBy.CREATED_ASC -> books.sortedBy { it.createdAt }
            NoteSortBy.CREATED_DESC -> books.sortedByDescending { it.createdAt }
            NoteSortBy.UPDATED_ASC -> books.sortedBy { it.updatedAt }
            NoteSortBy.UPDATED_DESC -> books.sortedByDescending { it.updatedAt }
        }
    }
    
    fun onSearchQueryChange(query: String) { _searchQuery.value = query }
    fun clearSearch() { _searchQuery.value = "" }
    fun onCategorySelected(category: BookGenre?) { _selectedCategory.value = category }
    fun onSortByChanged(sortBy: NoteSortBy) { _sortBy.value = sortBy }
    
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
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val books: List<Book>,
        val query: String = "",
        val category: BookGenre? = null,
        val sortBy: NoteSortBy = NoteSortBy.UPDATED_DESC
    ) : HomeUiState
    data class Empty(val query: String = "", val category: BookGenre? = null) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

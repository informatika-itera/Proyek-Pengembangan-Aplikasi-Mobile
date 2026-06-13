package com.example.arcane.presentation.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.repository.BookRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

val BOOK_GENRES = listOf(
    "Fiction", "Non-Fiction", "Science", "History",
    "Technology", "Philosophy", "Psychology", "Biography",
    "Fantasy", "Mystery", "Romance", "Self-Help"
)

@OptIn(FlowPreview::class)
class ExploreViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>("Fiction")
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Initial(isOffline = false))
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        _searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) {
                    searchBooks(query)
                } else if (_selectedGenre.value != null) {
                    searchBooks(_selectedGenre.value!!)
                } else {
                    searchBooks("Fiction")
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onGenreSelected(genre: String?) {
        val targetGenre = genre ?: "Fiction"
        _selectedGenre.value = targetGenre
        searchBooks(targetGenre)
    }

    fun refreshExplore(query: String, genre: String?) {
        viewModelScope.launch {
            _isRefreshing.value = true
            if (query.isNotBlank()) {
                searchBooks(query)
            } else {
                searchBooks(genre ?: "Fiction")
            }
            kotlinx.coroutines.delay(500)
            _isRefreshing.value = false
        }
    }

    private fun searchBooks(query: String) {
        viewModelScope.launch {
            _uiState.value = ExploreUiState.Loading
            try {
                val results = repository.searchBooks(query)
                _uiState.value = if (results.isEmpty()) {
                    ExploreUiState.Empty(isOffline = false)
                } else {
                    ExploreUiState.Success(results, isOffline = false)
                }
            } catch (e: Exception) {
                try {
                    val localBooks = repository.getAllBooks().first()

                    if (localBooks.isNotEmpty()) {
                        val currentGenre = _selectedGenre.value ?: "Fiction"

                        val filteredBooks = localBooks.filter { book ->
                            book.categories.any { it.contains(currentGenre, ignoreCase = true) } ||
                                    book.description.contains(currentGenre, ignoreCase = true) ||
                                    query.contains(book.title, ignoreCase = true)
                        }

                        if (filteredBooks.isNotEmpty()) {
                            _uiState.value = ExploreUiState.Success(filteredBooks, isOffline = true)
                        } else {
                            _uiState.value = ExploreUiState.Empty(isOffline = true)
                        }
                    } else {
                        _uiState.value = ExploreUiState.Error("Koneksi terputus. Perpustakaan lokal kamu masih kosong.")
                    }
                } catch (localException: Exception) {
                    _uiState.value = ExploreUiState.Error("Gagal memuat data offline")
                }
            }
        }
    }
}

sealed interface ExploreUiState {
    data class Initial(val isOffline: Boolean = false) : ExploreUiState
    data object Loading : ExploreUiState
    data class Success(val books: List<Book>, val isOffline: Boolean = false) : ExploreUiState
    data class Empty(val isOffline: Boolean = false) : ExploreUiState
    data class Error(val message: String) : ExploreUiState
}
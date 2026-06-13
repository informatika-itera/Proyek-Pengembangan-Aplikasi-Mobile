package com.example.arcane.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.domain.repository.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedStatus = MutableStateFlow<ReadingStatus?>(null)
    val selectedStatus: StateFlow<ReadingStatus?> = _selectedStatus

    private val _isLoading = MutableStateFlow(false)

    private val debouncedSearchQuery = _searchQuery.debounce(300)

    val uiState: StateFlow<HomeUiState> = combine(
        debouncedSearchQuery,
        _selectedStatus
    ) { query, status ->
        query to status
    }.flatMapLatest { (query, status) ->
        _isLoading.value = true
        if (status != null) {
            repository.getBooksByStatus(status)
        } else {
            repository.getAllBooks()
        }
    }.combine(_searchQuery) { books, query ->
        val filteredBooks = if (query.isBlank()) {
            books
        } else {
            books.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.authorsFormatted.contains(query, ignoreCase = true)
            }
        }

        _isLoading.value = false

        if (filteredBooks.isEmpty()) {
            HomeUiState.Empty
        } else {
            HomeUiState.Success(filteredBooks)
        }
    }.catch { e ->
        _isLoading.value = false
        emit(HomeUiState.Error(e.message ?: "Terjadi kesalahan"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            refresh()
            kotlinx.coroutines.delay(500)
            _isRefreshing.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusSelected(status: ReadingStatus?) {
        _selectedStatus.value = status
    }

    fun refresh() {
        val current = _selectedStatus.value
        _selectedStatus.value = current
    }

    fun deleteBook(bookId: Long) {
        viewModelScope.launch {
            repository.deleteBook(bookId)
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    val books: List<Book> get() = emptyList()
    data class Success(override val books: List<Book>) : HomeUiState
    data object Empty : HomeUiState
    data class Error(val message: String) : HomeUiState
}
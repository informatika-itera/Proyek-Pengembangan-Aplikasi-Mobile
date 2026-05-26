package com.example.hujjah.presentation.screens.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hujjah.domain.model.islamic.HadithBookItem
import com.example.hujjah.domain.model.islamic.HadithItem
import com.example.hujjah.domain.repository.hujjah.HujjahRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

data class HadithUiState(
    val books: List<HadithBookItem> = emptyList(),
    val currentBookId: String? = null,
    val currentBookName: String? = null,
    val hadiths: List<HadithItem> = emptyList(),
    val isLoading: Boolean = false,
    val isPageLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@OptIn(FlowPreview::class)
class HadithViewModel(
    private val hujjahRepository: HujjahRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HadithUiState())
    val uiState: StateFlow<HadithUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _currentBookId = MutableStateFlow<String?>(null)

    private var currentPage = 1
    private val pageSize = 20

    init {
        loadBooks()

        viewModelScope.launch {
            combine(_currentBookId, _searchQuery) { bookId, query ->
                bookId to query
            }
            .debounce(300)
            .distinctUntilChanged()
            .collect { (bookId, query) ->
                if (bookId != null) {
                    performSearchInternal(bookId, query)
                }
            }
        }
    }

    private fun loadBooks() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                hujjahRepository.getHadithBooks(forceRefresh = false).collect { booksList ->
                    _uiState.value = _uiState.value.copy(
                        books = booksList,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Gagal memuat kitab hadits"
                )
            }
        }
    }

    fun selectBook(bookId: String, bookName: String) {
        currentPage = 1
        _currentBookId.value = if (bookId.isEmpty()) null else bookId
        _searchQuery.value = ""
        _uiState.value = _uiState.value.copy(
            currentBookId = if (bookId.isEmpty()) null else bookId,
            currentBookName = if (bookName.isEmpty()) null else bookName,
            hadiths = emptyList(),
            searchQuery = "",
            error = null
        )
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun performSearch() {
        val bookId = _currentBookId.value ?: return
        val query = _searchQuery.value
        performSearchInternal(bookId, query)
    }

    private fun performSearchInternal(bookId: String, query: String) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isBlank()) {
            // Reset to normal pagination (page 1)
            currentPage = 1
            _uiState.value = _uiState.value.copy(
                hadiths = emptyList(),
                error = null
            )
            fetchHadiths(bookId, initialLoad = true)
            return
        }

        val hadithNumber = trimmedQuery.toIntOrNull()
        if (hadithNumber == null || hadithNumber <= 0) {
            _uiState.value = _uiState.value.copy(error = "Masukkan nomor hadis yang valid")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                hujjahRepository.getHadithRange(bookId, hadithNumber, hadithNumber, forceRefresh = false).collect { results ->
                    _uiState.value = _uiState.value.copy(
                        hadiths = results,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Koneksi lambat. Menampilkan data luring."
                )
            }
        }
    }

    fun fetchNextPage() {
        val bookId = _currentBookId.value ?: return
        if (_uiState.value.isPageLoading || _uiState.value.searchQuery.isNotBlank()) return

        _uiState.value = _uiState.value.copy(isPageLoading = true)
        fetchHadiths(bookId, initialLoad = false)
    }

    private fun fetchHadiths(bookId: String, initialLoad: Boolean) {
        if (initialLoad) {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }

        viewModelScope.launch {
            val start = (currentPage - 1) * pageSize + 1
            val end = start + pageSize - 1

            try {
                hujjahRepository.getHadithRange(bookId, start, end, forceRefresh = false).collect { newHadiths ->
                    _uiState.value = _uiState.value.copy(
                        hadiths = if (initialLoad) newHadiths else _uiState.value.hadiths + newHadiths,
                        isLoading = false,
                        isPageLoading = false
                    )
                    if (newHadiths.isNotEmpty()) {
                        currentPage++
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isPageLoading = false,
                    error = "Koneksi lambat. Menampilkan data luring."
                )
            }
        }
    }
}

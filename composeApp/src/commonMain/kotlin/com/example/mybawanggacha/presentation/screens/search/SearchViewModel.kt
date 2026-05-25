package com.example.mybawanggacha.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {
    private val _filters = MutableStateFlow(MediaSearchFilters())
    val filters: StateFlow<MediaSearchFilters> = _filters.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var searchJob: Job? = null
    private var lastRequestedLoadMorePage: Int? = null

    fun updateFilters(filters: MediaSearchFilters) {
        _filters.value = filters
    }

    fun resetFilters() {
        searchJob?.cancel()
        lastRequestedLoadMorePage = null
        _filters.value = MediaSearchFilters(mediaType = _filters.value.mediaType)
        _isRefreshing.value = false
        _uiState.value = SearchUiState.Idle
    }

    fun submitSearch() {
        lastRequestedLoadMorePage = null
        search(page = 1, append = false, keepCurrentContent = false)
    }

    fun refresh() {
        if (_isRefreshing.value) return

        val hasContent = _uiState.value is SearchUiState.Success
        if (hasContent) {
            _isRefreshing.value = true
        }

        lastRequestedLoadMorePage = null
        search(
            page = 1,
            append = false,
            keepCurrentContent = hasContent
        )
    }

    fun loadNextPage() {
        val current = _uiState.value as? SearchUiState.Success ?: return
        val nextPage = current.nextPage ?: return

        if (current.isLoadingMore) return
        if (_isRefreshing.value) return
        if (searchJob?.isActive == true) return
        if (lastRequestedLoadMorePage == nextPage) return

        lastRequestedLoadMorePage = nextPage
        _uiState.value = current.copy(isLoadingMore = true)
        search(page = nextPage, append = true, keepCurrentContent = false)
    }

    private fun search(
        page: Int,
        append: Boolean,
        keepCurrentContent: Boolean
    ) {
        if (!append) {
            searchJob?.cancel()
        }

        searchJob = viewModelScope.launch {
            val currentFilters = _filters.value
            val previousSuccess = _uiState.value as? SearchUiState.Success
            val previousItems = previousSuccess?.items.orEmpty()

            if (!append && !keepCurrentContent) {
                _uiState.value = SearchUiState.Loading
            }

            runCatching {
                repository.search(filters = currentFilters, page = page)
            }.onSuccess { result ->
                val mergedItems = if (append) {
                    (previousItems + result.items).distinctBy { item ->
                        "${item.mediaType}:${item.malId}"
                    }
                } else {
                    result.items
                }

                if (page == 1) {
                    lastRequestedLoadMorePage = null
                }

                _uiState.value = SearchUiState.Success(
                    items = mergedItems,
                    nextPage = result.nextPage,
                    isLoadingMore = false
                )
            }.onFailure { error ->
                if (append && previousSuccess != null) {
                    lastRequestedLoadMorePage = null
                    _uiState.value = previousSuccess.copy(isLoadingMore = false)
                } else if (keepCurrentContent && previousSuccess != null) {
                    _uiState.value = previousSuccess.copy(isLoadingMore = false)
                } else {
                    _uiState.value = SearchUiState.Error(
                        message = error.message ?: "Gagal mencari data dari Jikan"
                    )
                }
            }

            _isRefreshing.value = false
        }
    }
}

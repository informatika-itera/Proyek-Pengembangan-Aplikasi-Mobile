package com.itera.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itera.news.domain.model.Article
import com.itera.news.domain.repository.NewsRepository
import com.itera.news.domain.usecase.GetMbgNewsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOrder {
    NEWEST, OLDEST, A_Z, Z_A
}

class NewsViewModel(
    private val getMbgNewsUseCase: GetMbgNewsUseCase,
    private val repository: NewsRepository
) : ViewModel() {

    // Raw loading/error state — tidak terpengaruh oleh filter kategori
    private val _loadingState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory = _selectedCategory.asStateFlow()

    // Source of truth untuk semua artikel (tidak pernah difilter)
    private val _allArticles = MutableStateFlow<List<Article>>(emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST)
    val sortOrder = _sortOrder.asStateFlow()

    // uiState di-derive dari _allArticles (bukan dari uiState itu sendiri),
    // sehingga switching tab tidak memfilter list yang sudah terfilter
    val uiState: StateFlow<NewsUiState> = combine(_loadingState, _allArticles, _selectedCategory, _sortOrder) { loadState, allArticles, category, sort ->
        when (loadState) {
            is NewsUiState.Loading -> NewsUiState.Loading
            is NewsUiState.Error -> loadState
            is NewsUiState.Success -> {
                val filtered = if (category == "Semua") allArticles
                               else allArticles.filter { it.category == category }
                val sorted = when (sort) {
                    SortOrder.NEWEST -> filtered.sortedByDescending { it.publishedAt }
                    SortOrder.OLDEST -> filtered.sortedBy { it.publishedAt }
                    SortOrder.A_Z    -> filtered.sortedBy { it.title.lowercase() }
                    SortOrder.Z_A    -> filtered.sortedByDescending { it.title.lowercase() }
                }
                NewsUiState.Success(sorted)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NewsUiState.Loading)

    val bookmarkedArticles: StateFlow<List<Article>> = repository.getBookmarkedArticles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private var searchJob: Job? = null

    init {
        fetchNews()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            fetchNews(query.ifEmpty { null })
        }
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onSortOrderSelected(sort: SortOrder) {
        _sortOrder.value = sort
    }

    fun fetchNews(query: String? = null) {
        viewModelScope.launch {
            _loadingState.value = NewsUiState.Loading
            getMbgNewsUseCase(query).collect { result ->
                result.fold(
                    onSuccess = { articles ->
                        _allArticles.value = articles
                        _loadingState.value = NewsUiState.Success(articles)
                    },
                    onFailure = { error -> _loadingState.value = NewsUiState.Error(error.message ?: "Error") }
                )
            }
        }
    }
    
    fun refreshNews() {
        viewModelScope.launch {
            _isRefreshing.value = true
            getMbgNewsUseCase(_searchQuery.value.ifEmpty { null }).collect { result ->
                result.fold(
                    onSuccess = { articles ->
                        _allArticles.value = articles
                        _loadingState.value = NewsUiState.Success(articles)
                    },
                    onFailure = { error -> _loadingState.value = NewsUiState.Error(error.message ?: "Error") }
                )
            }
            _isRefreshing.value = false
        }
    }

    fun saveArticle(article: Article) = viewModelScope.launch { repository.saveArticle(article) }
    
    fun deleteArticle(article: Article) = viewModelScope.launch { repository.deleteArticle(article) }
    
    fun toggleBookmark(article: Article, isBookmarked: Boolean) {
        viewModelScope.launch {
            if (isBookmarked) {
                deleteArticle(article)
            } else {
                saveArticle(article)
            }
        }
    }
    
    fun isArticleBookmarked(url: String): StateFlow<Boolean> {
        return repository.isArticleBookmarked(url)
            .stateIn(viewModelScope, SharingStarted.Lazily, false)
    }
    
    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
        }
    }
}

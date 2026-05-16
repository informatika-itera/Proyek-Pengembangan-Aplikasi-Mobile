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

class NewsViewModel(
    private val getMbgNewsUseCase: GetMbgNewsUseCase,
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _allArticles = MutableStateFlow<List<Article>>(emptyList())

    val uiState: StateFlow<NewsUiState> = combine(_uiState, _selectedCategory) { state, category ->
        if (state is NewsUiState.Success) {
            val filtered = if (category == "Semua") {
                state.articles
            } else {
                state.articles.filter { it.category == category }
            }
            NewsUiState.Success(filtered)
        } else {
            state
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

    fun fetchNews(query: String? = null) {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            getMbgNewsUseCase(query).collect { result ->
                result.fold(
                    onSuccess = { articles -> 
                        _allArticles.value = articles
                        _uiState.value = NewsUiState.Success(articles) 
                    },
                    onFailure = { error -> _uiState.value = NewsUiState.Error(error.message ?: "Error") }
                )
            }
        }
    }

    fun saveArticle(article: Article) = viewModelScope.launch { repository.saveArticle(article) }
    fun deleteArticle(article: Article) = viewModelScope.launch { repository.deleteArticle(article) }
    fun isArticleBookmarked(url: String): StateFlow<Boolean> {
        return repository.isArticleBookmarked(url)
            .stateIn(viewModelScope, SharingStarted.Lazily, false)
    }
}
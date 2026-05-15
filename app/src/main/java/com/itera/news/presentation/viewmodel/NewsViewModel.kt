package com.itera.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itera.news.domain.model.Article
import com.itera.news.domain.repository.NewsRepository
import com.itera.news.domain.usecase.GetMbgNewsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewsViewModel(
    private val getMbgNewsUseCase: GetMbgNewsUseCase,
    private val repository: NewsRepository // Tambahkan parameter repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    // Ambil daftar artikel yang di-bookmark secara realtime
    val bookmarkedArticles: StateFlow<List<Article>> = repository.getBookmarkedArticles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchNews()
    }

    fun fetchNews() {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            getMbgNewsUseCase().collect { result ->
                result.fold(
                    onSuccess = { articles -> _uiState.value = NewsUiState.Success(articles) },
                    onFailure = { error -> _uiState.value = NewsUiState.Error(error.message ?: "Error") }
                )
            }
        }
    }

    // Fungsi CRUD Bookmark
    fun saveArticle(article: Article) = viewModelScope.launch { repository.saveArticle(article) }
    fun deleteArticle(article: Article) = viewModelScope.launch { repository.deleteArticle(article) }
    fun isArticleBookmarked(url: String): StateFlow<Boolean> {
        return repository.isArticleBookmarked(url)
            .stateIn(viewModelScope, SharingStarted.Lazily, false)
    }
}
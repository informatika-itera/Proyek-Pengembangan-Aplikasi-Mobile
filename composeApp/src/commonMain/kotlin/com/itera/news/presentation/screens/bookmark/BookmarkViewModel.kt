package com.itera.news.presentation.screens.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itera.news.domain.model.Article
import com.itera.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookmarkViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarkUiState>(BookmarkUiState.Loading)
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    init {
        loadBookmarkedArticles()
    }

    private fun loadBookmarkedArticles() {
        viewModelScope.launch {
            repository.getBookmarkedArticles()
                .catch { e ->
                    _uiState.value = BookmarkUiState.Error(e.message ?: "Unknown Error")
                }
                .collect { articles ->
                    if (articles.isEmpty()) {
                        _uiState.value = BookmarkUiState.Empty
                    } else {
                        _uiState.value = BookmarkUiState.Success(articles)
                    }
                }
        }
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch {
            try {
                repository.deleteArticle(article)
            } catch (e: Exception) {
                // Ignore or handle delete error
            }
        }
    }
}

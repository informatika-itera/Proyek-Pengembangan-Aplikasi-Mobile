package com.itera.news.presentation.screens.bookmark

import com.itera.news.domain.model.Article

sealed interface BookmarkUiState {
    data object Loading : BookmarkUiState
    data class Success(
        val articles: List<Article>
    ) : BookmarkUiState
    data class Error(
        val message: String
    ) : BookmarkUiState
    data object Empty : BookmarkUiState
}

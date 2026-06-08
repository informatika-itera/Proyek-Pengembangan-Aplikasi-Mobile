package id.my.sinanonym.mybawanggacha.presentation.screens.search

import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterOption

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Error(val message: String) : SearchUiState
    data class Success(
        val items: List<MediaSearchItem>,
        val nextPage: Int?,
        val isLoadingMore: Boolean = false
    ) : SearchUiState {
        val canLoadMore: Boolean
            get() = nextPage != null
    }
}

data class SearchFilterMetadataUiState(
    val genres: List<SearchFilterOption> = emptyList(),
    val related: List<SearchFilterOption> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

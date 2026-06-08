package id.my.sinanonym.mybawanggacha.presentation.screens.anime.detail

import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeDetail
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeEpisode

sealed interface AnimeDetailUiState {
    data object Loading : AnimeDetailUiState
    data class Success(
        val anime: AnimeDetail,
        val episodes: List<AnimeEpisode> = emptyList(),
        val libraryEntryId: Long? = null,
        val isRefreshing: Boolean = false
    ) : AnimeDetailUiState
    data class Error(val message: String) : AnimeDetailUiState
}

package id.my.sinanonym.mybawanggacha.presentation.screens.anime.home

import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSummary
import id.my.sinanonym.mybawanggacha.domain.anime.model.RecentAnimeEpisode
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaSummary

sealed interface AnimeHomeUiState {
    data object Loading : AnimeHomeUiState

    data class Success(
        val recommendations: List<AnimeSummary>,
        val randomAnime: List<AnimeSummary>,
        val randomManga: List<MangaSummary>,
        val recentEpisodes: List<RecentAnimeEpisode>
    ) : AnimeHomeUiState

    data class Error(val message: String) : AnimeHomeUiState
}

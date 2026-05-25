package com.example.mybawanggacha.presentation.screens.anime.home

import com.example.mybawanggacha.domain.anime.model.AnimeSummary
import com.example.mybawanggacha.domain.anime.model.RecentAnimeEpisode
import com.example.mybawanggacha.domain.manga.model.MangaSummary

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

package com.example.mybawanggacha.domain.repository

import com.example.mybawanggacha.domain.model.AnimeDetailBundle
import com.example.mybawanggacha.domain.model.AnimeSeason
import com.example.mybawanggacha.domain.model.AnimeSeasonPeriod
import com.example.mybawanggacha.domain.model.AnimeSummary

interface AnimeRepository {
    suspend fun getRecommendations(): List<AnimeSummary>
    suspend fun getCurrentSeasonAnime(): List<AnimeSummary>
    suspend fun getSeasonAnime(year: Int, season: AnimeSeason): List<AnimeSummary>
    suspend fun getUpcomingSeasonAnime(): List<AnimeSummary>
    suspend fun getTopAnime(): List<AnimeSummary>
    suspend fun getAvailableSeasonPeriods(): List<AnimeSeasonPeriod>
    suspend fun getAnimeDetail(malId: Int): AnimeDetailBundle
    suspend fun setEpisodeWatched(animeId: Int, episodeNumber: Int, watched: Boolean)
}

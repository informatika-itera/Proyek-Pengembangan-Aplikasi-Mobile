package com.example.mybawanggacha.domain.anime.repository

import com.example.mybawanggacha.domain.anime.model.AnimeDetailBundle
import com.example.mybawanggacha.domain.anime.model.AnimePage
import com.example.mybawanggacha.domain.anime.model.AnimeSeason
import com.example.mybawanggacha.domain.anime.model.AnimeSeasonPeriod
import com.example.mybawanggacha.domain.anime.model.AnimeSummary
import com.example.mybawanggacha.domain.anime.model.RecentAnimeEpisode

interface AnimeRepository {
    suspend fun getRecommendations(): List<AnimeSummary>
    suspend fun getRandomAnime(): AnimeSummary
    suspend fun getRandomAnimePicks(count: Int): List<AnimeSummary>
    suspend fun getRecentEpisodes(): List<RecentAnimeEpisode>
    suspend fun getCurrentSeasonAnimePage(page: Int): AnimePage
    suspend fun getSeasonAnimePage(year: Int, season: AnimeSeason, page: Int): AnimePage
    suspend fun getUpcomingSeasonAnimePage(page: Int): AnimePage
    suspend fun getTopAnimePage(page: Int): AnimePage
    suspend fun getAvailableSeasonPeriods(): List<AnimeSeasonPeriod>
    suspend fun getAnimeDetail(malId: Int, forceRefresh: Boolean = false): AnimeDetailBundle
    suspend fun setEpisodeWatched(animeId: Int, episodeNumber: Int, watched: Boolean)
}
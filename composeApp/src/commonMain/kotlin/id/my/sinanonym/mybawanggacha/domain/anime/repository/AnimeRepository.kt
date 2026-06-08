package id.my.sinanonym.mybawanggacha.domain.anime.repository

import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeDetailBundle
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimePage
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSeason
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSeasonPeriod
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSummary
import id.my.sinanonym.mybawanggacha.domain.anime.model.RecentAnimeEpisode

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
    suspend fun setEpisodeMarked(animeId: Int, episodeNumber: Int, marked: Boolean)
}
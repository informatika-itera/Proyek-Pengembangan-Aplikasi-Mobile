package com.example.mybawanggacha.domain.repository

import com.example.mybawanggacha.domain.model.AnimeDetailBundle
import com.example.mybawanggacha.domain.model.AnimeSummary

interface AnimeRepository {
    suspend fun getRecommendations(): List<AnimeSummary>
    suspend fun getAnimeDetail(malId: Int): AnimeDetailBundle
    suspend fun setEpisodeWatched(animeId: Int, episodeNumber: Int, watched: Boolean)
}

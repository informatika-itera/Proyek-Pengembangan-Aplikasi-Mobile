package com.example.rewind.domain.repository

import com.example.rewind.data.remote.dto.TmdbMovieDetailDto
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.core.network.NetworkResult

interface TmdbRepository {
    /**
     * Search film & series (multi-search)
     * Mengembalikan list hasil dari TMDB
     */
    suspend fun searchMulti(query: String, page: Int = 1): NetworkResult<List<TmdbMovieDto>>

    /**
     * Ambil detail film berdasarkan TMDB ID
     */
    suspend fun getMovieDetail(tmdbId: Int): NetworkResult<TmdbMovieDetailDto>

    /**
     * Ambil detail TV/series berdasarkan TMDB ID
     */
    suspend fun getTvDetail(tmdbId: Int): NetworkResult<TmdbMovieDetailDto>

    /**
     * Ambil daftar trending minggu ini
     */
    suspend fun getTrending(): NetworkResult<List<TmdbMovieDto>>
}
package com.example.rewind.data.repository

import com.example.rewind.core.network.NetworkResult
import com.example.rewind.data.remote.dto.TmdbMovieDetailDto
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.domain.repository.TmdbRepository

class FakeTmdbRepository : TmdbRepository {

    var searchResult: NetworkResult<List<TmdbMovieDto>> = NetworkResult.Success(emptyList())
    var movieDetailResult: NetworkResult<TmdbMovieDetailDto> = NetworkResult.Success(
        TmdbMovieDetailDto(
            id = 1,
            title = "Fake Movie",
            overview = "A fake movie for testing"
        )
    )
    var tvDetailResult: NetworkResult<TmdbMovieDetailDto> = NetworkResult.Success(
        TmdbMovieDetailDto(
            id = 2,
            name = "Fake TV Show",
            overview = "A fake TV show for testing"
        )
    )
    var trendingResult: NetworkResult<List<TmdbMovieDto>> = NetworkResult.Success(emptyList())

    // Tracking fields for verification
    var lastSearchQuery: String? = null
    var lastSearchPage: Int? = null
    var lastMovieDetailId: Int? = null
    var lastTvDetailId: Int? = null

    override suspend fun searchMulti(query: String, page: Int): NetworkResult<List<TmdbMovieDto>> {
        lastSearchQuery = query
        lastSearchPage = page
        return searchResult
    }

    override suspend fun getMovieDetail(tmdbId: Int): NetworkResult<TmdbMovieDetailDto> {
        lastMovieDetailId = tmdbId
        return movieDetailResult
    }

    override suspend fun getTvDetail(tmdbId: Int): NetworkResult<TmdbMovieDetailDto> {
        lastTvDetailId = tmdbId
        return tvDetailResult
    }

    override suspend fun getTrending(): NetworkResult<List<TmdbMovieDto>> {
        return trendingResult
    }
}

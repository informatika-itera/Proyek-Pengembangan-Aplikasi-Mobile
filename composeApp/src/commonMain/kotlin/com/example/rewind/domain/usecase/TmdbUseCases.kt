package com.example.rewind.domain.usecase

import com.example.rewind.core.network.NetworkResult
import com.example.rewind.data.remote.dto.TmdbMovieDetailDto
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.domain.repository.TmdbRepository

class SearchTmdbUseCase(
    private val repository: TmdbRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1
    ): NetworkResult<List<TmdbMovieDto>> {
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            return NetworkResult.Success(emptyList())
        }
        return repository.searchMulti(query = trimmed, page = page)
    }
}

class GetTrendingUseCase(
    private val repository: TmdbRepository
) {
    suspend operator fun invoke(): NetworkResult<List<TmdbMovieDto>> {
        return repository.getTrending()
    }
}

class GetTmdbDetailUseCase(
    private val repository: TmdbRepository
) {
    suspend operator fun invoke(
        tmdbId: Int,
        isTv: Boolean
    ): NetworkResult<TmdbMovieDetailDto> {
        return if (isTv) {
            repository.getTvDetail(tmdbId)
        } else {
            repository.getMovieDetail(tmdbId)
        }
    }
}
package com.example.mybawanggacha.data.repository.search

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.remote.jikan.mapper.toSearchPage
import com.example.mybawanggacha.data.remote.jikan.source.JikanSearchRemoteDataSource
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.MediaSearchPage
import com.example.mybawanggacha.domain.search.model.SearchMediaType
import com.example.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.withContext

class SearchRepositoryImpl(
    private val remoteDataSource: JikanSearchRemoteDataSource,
    private val dispatchers: AppDispatchers
) : SearchRepository {
    override suspend fun search(
        filters: MediaSearchFilters,
        page: Int
    ): MediaSearchPage = withContext(dispatchers.default) {
        when (filters.mediaType) {
            SearchMediaType.Anime -> remoteDataSource
                .searchAnime(filters = filters, page = page)
                .toSearchPage(mediaType = SearchMediaType.Anime, requestedPage = page)

            SearchMediaType.Manga -> remoteDataSource
                .searchManga(filters = filters, page = page)
                .toSearchPage(mediaType = SearchMediaType.Manga, requestedPage = page)
        }
    }
}

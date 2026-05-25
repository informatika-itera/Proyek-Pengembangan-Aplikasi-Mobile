package com.example.mybawanggacha.data.remote.jikan.source

import com.example.mybawanggacha.core.coroutines.AppDispatchers
import com.example.mybawanggacha.data.remote.jikan.api.JikanService
import com.example.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import kotlinx.coroutines.withContext

class JikanSearchRemoteDataSource(
    private val service: JikanService,
    private val dispatchers: AppDispatchers
) {
    suspend fun searchAnime(
        filters: MediaSearchFilters,
        page: Int
    ): JikanAnimeListResponse = withContext(dispatchers.io) {
        service.fetchAnimeSearch(filters = filters, page = page)
    }

    suspend fun searchManga(
        filters: MediaSearchFilters,
        page: Int
    ): JikanAnimeListResponse = withContext(dispatchers.io) {
        service.fetchMangaSearch(filters = filters, page = page)
    }
}

package id.my.sinanonym.mybawanggacha.data.remote.jikan.source

import id.my.sinanonym.mybawanggacha.core.coroutines.AppDispatchers
import id.my.sinanonym.mybawanggacha.data.remote.jikan.api.JikanService
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper.toSearchFilterOptions
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterMetadata
import kotlinx.coroutines.withContext

class JikanSearchRemoteDataSource(
    private val service: JikanService,
    private val dispatchers: AppDispatchers
) {


    suspend fun getAnimeFilterMetadata(): SearchFilterMetadata = withContext(dispatchers.io) {
        SearchFilterMetadata(
            genres = service.fetchAnimeGenres().toSearchFilterOptions(),
            related = service.fetchProducers().toSearchFilterOptions()
        )
    }

    suspend fun getMangaFilterMetadata(): SearchFilterMetadata = withContext(dispatchers.io) {
        SearchFilterMetadata(
            genres = service.fetchMangaGenres().toSearchFilterOptions(),
            related = service.fetchMagazines().toSearchFilterOptions()
        )
    }

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

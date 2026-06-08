package id.my.sinanonym.mybawanggacha.domain.search.repository

import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchPage
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterMetadata
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType

interface SearchRepository {
    suspend fun search(
        filters: MediaSearchFilters,
        page: Int
    ): MediaSearchPage

    suspend fun getFilterMetadata(mediaType: SearchMediaType): SearchFilterMetadata
}

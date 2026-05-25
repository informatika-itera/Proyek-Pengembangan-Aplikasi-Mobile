package com.example.mybawanggacha.domain.search.repository

import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.MediaSearchPage

interface SearchRepository {
    suspend fun search(
        filters: MediaSearchFilters,
        page: Int
    ): MediaSearchPage
}

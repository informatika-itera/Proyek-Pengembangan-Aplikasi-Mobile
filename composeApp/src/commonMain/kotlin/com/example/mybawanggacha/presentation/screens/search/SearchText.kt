package com.example.mybawanggacha.presentation.screens.search

internal object SearchText {
    const val settingsContentDescription = "Settings"

    const val screenTitle = "Search"
    const val screenSubtitle = "Cari anime atau manga"
    const val queryLabel = "Query"
    const val queryPlaceholder = "Frieren, One Piece, Naruto..."
    const val searchButton = "Cari"
    const val resetButton = "Reset"
    const val applyButton = "Terapkan"
    const val activeFiltersTitle = "Filter aktif"
    const val loadingMore = "Loading more..."

    const val emptyIdleTitle = "Belum mencari"
    const val emptyIdleMessage = "Isi query atau filter, lalu tekan Cari."
    const val emptyResultTitle = "Tidak ada hasil"
    const val emptyResultMessage = "Coba longgarkan filter atau ubah kata kunci."
    const val missingMetadata = "Tidak ada metadata tambahan"

    const val advancedFiltersTitle = "Advanced Filters"
    const val advancedFiltersSubtitle = "Dafter filter lengkap yang dapat digunakan"

    const val generalSectionTitle = "General"
    const val generalSectionSubtitle = "limit, letter, SFW, unapproved"
    const val classificationSectionTitle = "Classification"
    const val classificationSectionSubtitle = "type, status, rating"
    const val scoreDateSectionTitle = "Score & Date"
    const val scoreDateSectionSubtitle = "exact score, score range, date range"
    const val metadataSectionTitle = "Metadata"
    const val metadataAnimeSubtitle = "genres, excluded genres, producers"
    const val metadataMangaSubtitle = "genres, excluded genres, magazines"
    const val sortingSectionTitle = "Sorting"
    const val sortingSectionSubtitle = "order by and direction"

    const val limitLabel = "Limit"
    const val letterLabel = "Letter"
    const val sfwChip = "SFW"
    const val sfwOnlyLabel = "SFW only"
    const val unapprovedLabel = "Include unapproved entries"
    const val typeLabel = "Type"
    const val statusLabel = "Status"
    const val ratingLabel = "Rating"
    const val exactScoreLabel = "Exact Score"
    const val minScoreLabel = "Min Score"
    const val maxScoreLabel = "Max Score"
    const val startDateLabel = "Start Date"
    const val endDateLabel = "End Date"
    const val genreIdsLabel = "Genre IDs"
    const val excludedGenreIdsLabel = "Excluded Genre IDs"
    const val producerIdsLabel = "Producer IDs"
    const val magazineIdsLabel = "Magazine IDs"
    const val orderByLabel = "Order By"
    const val sortLabel = "Sort"

    const val anyTypeLabel = "Any type"
    const val anyStatusLabel = "Any status"
    const val anyRatingLabel = "Any rating"
    const val defaultOrderByLabel = "Relevance / Default"
    const val defaultSortLabel = "Default"
    const val defaultDropdownLabel = "Default"

    const val adultAllowedLabel = "Adult allowed"
    const val unapprovedActiveLabel = "Unapproved"

    fun filterButton(activeFilterCount: Int): String {
        return if (activeFilterCount > 0) "Filter ($activeFilterCount)" else "Filter"
    }

    fun mediaDropdownLabel(label: String): String = "Media: $label"

    fun dropdownValue(label: String, value: String): String = "$label: $value"

    fun mediaIdLabel(mediaLabel: String, malId: Int): String = "$mediaLabel #$malId"
}

internal object SearchTestTags {
    const val queryField = "search_query_field"
    const val searchButton = "search_button"
    const val filterButton = "search_filter_button"
    const val mediaDropdown = "search_media_dropdown"
    const val resultCard = "search_result_card"
}

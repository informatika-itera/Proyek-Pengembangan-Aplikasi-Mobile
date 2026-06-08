package id.my.sinanonym.mybawanggacha.presentation.screens.search

internal object SearchText {
    const val settingsContentDescription = "Settings"

    const val screenTitle = "Search"
    const val screenSubtitle = "Cari anime atau manga"
    const val queryLabel = "Query"
    const val queryPlaceholder = "Cari judul..."
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

    const val advancedFiltersTitle = "Filters"
    const val compactFilterHint = ""

    const val generalSectionTitle = "General"
    const val generalSectionSubtitle = "limit, letter, NSFW, unapproved"
    const val classificationSectionTitle = "Classification"
    const val classificationSectionSubtitle = "type, status, rating"
    const val scoreDateSectionTitle = "Score & Date"
    const val scoreDateSectionSubtitle = "exact score, score range, date range"
    const val metadataSectionTitle = "Metadata"
    const val metadataAnimeSubtitle = "pilih genre include/exclude dan producer"
    const val metadataMangaSubtitle = "pilih genre include/exclude dan magazine"
    const val sortingSectionTitle = "Sorting"
    const val sortingSectionSubtitle = "order by and direction"

    const val limitLabel = "Limit"
    const val letterLabel = "Letter"
    const val nsfwLabel = "NSFW"
    const val allowNsfwLabel = "Allow NSFW"
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
    const val genreSelectorTitle = "Genres"
    const val genreSelectorHint = ""
    const val includedGenreLabel = "Included genres"
    const val excludedGenreLabel = "Excluded genres"
    const val producerIdsLabel = "Producers"
    const val magazineIdsLabel = "Magazines"
    const val producerSelectorHint = ""
    const val magazineSelectorHint = ""
    const val genreSearchLabel = "Cari genre"
    const val metadataSearchLabel = "Cari metadata"
    const val metadataNoMatch = "Tidak ada metadata yang cocok."
    const val showLessMetadata = "Ringkas"
    const val metadataLoading = "Memuat pilihan metadata..."
    const val metadataLoadFailed = "Metadata gagal dimuat. Field ID manual tetap tersedia."
    const val includeChip = "Include"
    const val excludeChip = "Exclude"
    const val orderByLabel = "Order By"
    const val sortLabel = "Sort"

    const val anyTypeLabel = "Any type"
    const val anyStatusLabel = "Any status"
    const val anyRatingLabel = "Any rating"
    const val defaultOrderByLabel = "Relevance / Default"
    const val defaultSortLabel = "Default"
    const val defaultDropdownLabel = "Default"
    const val unapprovedActiveLabel = "Unapproved"

    fun filterButton(activeFilterCount: Int): String {
        return if (activeFilterCount > 0) "Filter ($activeFilterCount)" else "Filter"
    }


    fun showAllMetadata(totalCount: Int): String = "Tampilkan semua ($totalCount)"

    fun metadataCount(visibleCount: Int, totalCount: Int): String = "$visibleCount/$totalCount"

    fun metadataSelectionSummary(includedCount: Int, excludedCount: Int): String {
        return buildList {
            if (includedCount > 0) add("+$includedCount include")
            if (excludedCount > 0) add("-$excludedCount exclude")
        }.joinToString(" • ")
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

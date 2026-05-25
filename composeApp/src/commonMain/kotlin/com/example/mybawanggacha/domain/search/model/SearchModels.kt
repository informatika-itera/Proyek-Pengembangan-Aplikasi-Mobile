package com.example.mybawanggacha.domain.search.model

enum class SearchMediaType(val label: String) {
    Anime("Anime"),
    Manga("Manga")
}

data class MediaSearchFilters(
    val mediaType: SearchMediaType = SearchMediaType.Anime,
    val query: String = "",
    val unapproved: Boolean = false,
    val limit: String = "12",
    val type: String? = null,
    val score: String = "",
    val minScore: String = "",
    val maxScore: String = "",
    val status: String? = null,
    val rating: String? = null,
    val sfw: Boolean = true,
    val genres: String = "",
    val genresExclude: String = "",
    val orderBy: String? = null,
    val sort: String? = null,
    val letter: String = "",
    val producers: String = "",
    val magazines: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

data class MediaSearchItem(
    val malId: Int,
    val mediaType: SearchMediaType,
    val title: String,
    val imageUrl: String?,
    val type: String?,
    val status: String?,
    val score: Double?,
    val rank: Int?,
    val episodes: Int?,
    val chapters: Int?,
    val volumes: Int?
)

data class MediaSearchPage(
    val items: List<MediaSearchItem>,
    val nextPage: Int?,
    val hasNextPage: Boolean
)

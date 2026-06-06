package com.example.rewind.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== SEARCH RESPONSE ====================

@Serializable
data class TmdbSearchResponseDto(
    @SerialName("page") val page: Int = 1,
    @SerialName("results") val results: List<TmdbMovieDto> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 0,
    @SerialName("total_results") val totalResults: Int = 0
)

// ==================== MOVIE / TV ITEM ====================

@Serializable
data class TmdbMovieDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String? = null, // khusus movie
    @SerialName("name") val name: String? = null, // khusus TV/series
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("release_date") val releaseDate: String? = null, // movie
    @SerialName("first_air_date") val firstAirDate: String? = null, // tv
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList(),
    @SerialName("media_type") val mediaType: String? = null, // "movie" | "tv"
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int? = null
) {
    // Helper: ambil judul apapun tipenya
    val displayTitle: String
        get() = title ?: name ?: originalTitle ?: originalName ?: "Unknown"

    // Helper: ambil tanggal rilis apapun tipenya
    val displayDate: String?
        get() = releaseDate ?: firstAirDate

    // Helper: full URL poster
    fun posterUrl(size: String = "w500"): String? =
        posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }

    // Helper: full URL backdrop
    fun backdropUrl(size: String = "w780"): String? =
        backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }

    // Helper: apakah ini TV series
    val isTvSeries: Boolean
        get() = mediaType == "tv" || name != null
}

// ==================== MOVIE DETAIL ====================

@Serializable
data class TmdbMovieDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("genres") val genres: List<TmdbGenreDto> = emptyList(),
    @SerialName("runtime") val runtime: Int? = null,               // menit (movie)
    @SerialName("number_of_episodes") val numberOfEpisodes: Int? = null, // tv
    @SerialName("number_of_seasons") val numberOfSeasons: Int? = null,   // tv
    @SerialName("status") val status: String? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("original_language") val originalLanguage: String? = null
) {
    val displayTitle: String
        get() = title ?: name ?: "Unknown"

    fun posterUrl(size: String = "w500"): String? =
        posterPath?.let { "https://image.tmdb.org/t/p/$size$it" }

    fun backdropUrl(size: String = "w780"): String? =
        backdropPath?.let { "https://image.tmdb.org/t/p/$size$it" }
}

// ==================== GENRE ====================

@Serializable
data class TmdbGenreDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

// ==================== GENRE LIST RESPONSE ====================

@Serializable
data class TmdbGenreListDto(
    @SerialName("genres") val genres: List<TmdbGenreDto> = emptyList()
)

// ==================== GENRE ID MAPPING KE DOMAIN ====================

object TmdbGenreMapper {
    // Mapping TMDB genre ID -> nama genre Rewind
    fun fromGenreIds(ids: List<Int>): String {
        return when {
            ids.contains(28) || ids.contains(10759) -> "ACTION"
            ids.contains(35) -> "COMEDY"
            ids.contains(18) || ids.contains(10766) -> "DRAMA"
            ids.contains(27) -> "HORROR"
            ids.contains(10749) -> "ROMANCE"
            ids.contains(878) || ids.contains(10765) -> "SCIFI"
            ids.contains(53) -> "THRILLER"
            ids.contains(16) -> "ANIMATION"
            ids.contains(14) || ids.contains(10770) -> "FANTASY"
            else -> "OTHER"
        }
    }
}
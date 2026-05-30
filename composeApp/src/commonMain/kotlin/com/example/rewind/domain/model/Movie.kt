package com.example.rewind.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Movie(
    val id: Long = 0,
    val title: String,
    val genre: MovieGenre = MovieGenre.OTHER,
    val type: MovieType = MovieType.MOVIE,
    val status: WatchStatus = WatchStatus.PLAN_TO_WATCH,
    val rating: Float? = null, // 0.0 - 10.0, null = belum dirating
    val review: String = "",
    val totalEpisodes: Int? = null, // null jika tipe MOVIE
    val watchedEpisodes: Int = 0,
    val createdAt: Instant = Clock.System.now(),
    val updatedAt: Instant = Clock.System.now(),
    val posterUrl: String? = null
) {
    val isFavorite: Boolean
        get() = rating != null && rating >= 8.0f

    val progressPercent: Float
        get() = if (totalEpisodes != null && totalEpisodes > 0) {
            (watchedEpisodes.toFloat() / totalEpisodes.toFloat()) * 100f
        } else 0f

    val previewPreview: String
        get() = if (review.length > 100) review.take(100) + "..." else review
    
    val isEmpty: Boolean
        get() = title.isBlank()
}

enum class MovieType(val displayName: String) {
    MOVIE("Film"),
    SERIES("Series"),
    ANIME("Anime"),
    DOCUMENTARY("Dokumenter");

    companion object {
        fun fromString(value: String): MovieType {
            return entries.find { it.name == value} ?: MOVIE
        }
    }
}

enum class WatchStatus(val displayName: String) {
    WATCHING("Sedang Ditonton"),
    COMPLETED("Selesai"),
    PLAN_TO_WATCH("Rencana Ditonton"),
    ON_HOLD("Ditunda"),
    DROPPED("Berhenti");
    
    companion object {
        fun fromString(value: String): WatchStatus {
            return entries.find { it.name == value } ?: PLAN_TO_WATCH
        }
    }
}

enum class MovieGenre(val displayName: String) {
    ACTION("Aksi"),
    COMEDY("Komedi"),
    DRAMA("Drama"),
    HORROR("Horor"),
    ROMANCE("Romansa"),
    SCIFI("Sci-Fi"),
    THRILLER("Thriller"),
    ANIMATION("Animasi"),
    FANTASY("Fantasi"),
    OTHER("Lainnya");
    
    companion object {
        fun fromString(value: String): MovieGenre {
            return entries.find { it.name == value } ?: OTHER
        }
    }
}

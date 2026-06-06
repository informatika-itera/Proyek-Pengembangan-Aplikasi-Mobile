package com.example.rewind.data.local.entity

import com.example.rewind.domain.model.*
import com.example.rewind.data.local.MovieEntity
import kotlinx.datetime.Instant

fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        genre = MovieGenre.fromString(genre),
        type = MovieType.fromString(type),
        status = WatchStatus.fromString(status),
        rating = rating?.toFloat(),
        review = review,
        synopsis = synopsis,
        totalEpisodes = totalEpisodes?.toInt(),
        watchedEpisodes = watchedEpisodes.toInt(),
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        updatedAt = Instant.fromEpochMilliseconds(updatedAt),
        posterUrl = posterUrl
    )
}
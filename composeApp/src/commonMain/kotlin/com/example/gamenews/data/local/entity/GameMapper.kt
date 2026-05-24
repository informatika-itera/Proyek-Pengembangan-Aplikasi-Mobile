package com.example.gamenews.data.mapper

import com.example.gamenews.data.remote.api.GameRemoteEntity
import com.example.gamenews.domain.model.Game
import com.example.gamenews.data.local.GameEntity

fun GameRemoteEntity.toDomain(): Game {
    val rawRating = this.rating?.mean?.times(10) ?: 0.0
    val roundedRating = (rawRating * 10).toLong() / 10.0

    val descriptionText = this.description?.takeIf { it.isNotBlank() }
        ?: this.about?.takeIf { it.isNotBlank() }
        ?: this.shortDescription?.takeIf { it.isNotBlank() }
        ?: this.gameplay?.takeIf { !it.startsWith("http") && it.isNotBlank() }
        ?: ""

    return Game(
        id = this.id,
        title = this.name,
        description = descriptionText,
        genre = this.genre ?: "Unknown",
        rating = roundedRating,
        imageUrl = this.image,
        developer = this.developer,
        releaseYear = this.year?.toInt()
    )
}

fun GameEntity.toDomain(): Game {
    return Game(
        id = this.id.toInt(),
        title = this.title,
        description = this.description,
        genre = this.genre,
        rating = this.rating,
        imageUrl = this.image_url,
        developer = "",
        releaseYear = 0
    )
}
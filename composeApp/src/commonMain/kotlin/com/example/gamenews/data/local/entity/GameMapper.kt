package com.example.gamenews.data.mapper

import com.example.gamenews.data.remote.api.GameRemoteEntity
import com.example.gamenews.domain.model.Game

fun GameRemoteEntity.toDomain(): Game {
    return Game(
        id = id,

        title = name,

        description = shortDescription ?: "",

        genre = genre ?: "Unknown",

        rating = if (rating != null) rating.mean * 10 else 0.0,

        imageUrl = image ?: ""
    )
}
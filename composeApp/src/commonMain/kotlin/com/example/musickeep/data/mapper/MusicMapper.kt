package com.example.musickeep.data.mapper

import com.example.musickeep.data.local.MusicEntity
import com.example.musickeep.domain.model.Music

fun MusicEntity.toDomain(): Music {
    return Music(
        id = id,
        title = title,
        artist = artist,
        genre = genre,
        mood = mood,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

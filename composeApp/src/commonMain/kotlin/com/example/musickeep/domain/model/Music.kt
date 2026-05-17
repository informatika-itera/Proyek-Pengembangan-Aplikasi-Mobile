package com.example.musickeep.domain.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class Music(
    val id: Long? = null,
    val title: String,
    val artist: String,
    val genre: String? = null,
    val mood: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)

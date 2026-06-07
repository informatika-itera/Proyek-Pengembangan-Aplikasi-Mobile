package com.soundletter.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicTrack(
    val title: String,
    val artist: String,
    val previewUrl: String?,
    val albumArtUrl: String?
)

package com.example.mybawanggacha.data.remote.jikan.dto

import kotlinx.serialization.Serializable

@Serializable
data class WatchEpisodesResponse(
    val data: List<WatchEpisodeData> = emptyList(),
    val pagination: JikanPaginationDto? = null
)

@Serializable
data class WatchEpisodeData(
    val entry: AnimeEntry,
    val episodes: List<WatchEpisodeDto> = emptyList(),
    val region_locked: Boolean? = null
)

@Serializable
data class WatchEpisodeDto(
    val mal_id: Int? = null,
    val url: String? = null,
    val title: String? = null,
    val premium: Boolean? = null
)

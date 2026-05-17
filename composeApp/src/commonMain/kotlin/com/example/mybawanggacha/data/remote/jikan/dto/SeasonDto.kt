package com.example.mybawanggacha.data.remote.jikan.dto

import kotlinx.serialization.Serializable

@Serializable
data class JikanSeasonArchiveResponse(
    val data: List<AnimeSeasonArchiveYearDto> = emptyList()
)

@Serializable
data class AnimeSeasonArchiveYearDto(
    val year: Int,
    val seasons: List<String> = emptyList()
)

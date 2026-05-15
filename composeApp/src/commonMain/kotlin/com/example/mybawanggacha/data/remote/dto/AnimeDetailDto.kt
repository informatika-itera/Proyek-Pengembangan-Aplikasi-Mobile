package com.example.mybawanggacha.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnimeDetailResponse(
    val data: AnimeDetailData
)

@Serializable
data class AnimeDetailData(
    val mal_id: Int,
    val url: String,
    val images: AnimeImages,
    val title: String,
    val title_english: String? = null,
    val title_japanese: String? = null,
    val type: String? = null,
    val source: String? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val duration: String? = null,
    val rating: String? = null,
    val score: Double? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val season: String? = null,
    val year: Int? = null,
    val genres: List<GenreDto> = emptyList(),
    val studios: List<StudioDto> = emptyList()
)

@Serializable
data class GenreDto(
    val mal_id: Int,
    val name: String
)

@Serializable
data class StudioDto(
    val mal_id: Int,
    val name: String
)

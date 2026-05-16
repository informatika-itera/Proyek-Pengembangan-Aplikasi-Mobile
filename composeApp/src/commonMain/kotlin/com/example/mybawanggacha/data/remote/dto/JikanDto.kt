package com.example.mybawanggacha.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class JikanAnimeListResponse(
    val data: List<AnimeCatalogItemDto> = emptyList()
)

@Serializable
data class AnimeCatalogItemDto(
    val mal_id: Int,
    val url: String? = null,
    val images: AnimeImages? = null,
    val title: String,
    val title_english: String? = null,
    val type: String? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val score: Double? = null,
    val season: String? = null,
    val year: Int? = null
)

@Serializable
data class JikanSeasonArchiveResponse(
    val data: List<AnimeSeasonArchiveYearDto> = emptyList()
)

@Serializable
data class AnimeSeasonArchiveYearDto(
    val year: Int,
    val seasons: List<String> = emptyList()
)

@Serializable
data class JikanRecommendationsResponse(
    val data: List<RecommendationData>
)

@Serializable
data class RecommendationData(
    val entry: List<AnimeEntry>,
    val content: String? = null
)

@Serializable
data class AnimeEntry(
    val mal_id: Int,
    val url: String,
    val images: AnimeImages,
    val title: String
)

@Serializable
data class AnimeImages(
    val jpg: ImageUrls
)

@Serializable
data class ImageUrls(
    val image_url: String,
    val small_image_url: String,
    val large_image_url: String
)

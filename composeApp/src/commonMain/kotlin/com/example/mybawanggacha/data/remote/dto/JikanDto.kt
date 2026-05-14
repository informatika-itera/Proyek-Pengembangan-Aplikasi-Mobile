package com.example.mybawanggacha.data.remote.dto

import kotlinx.serialization.Serializable

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

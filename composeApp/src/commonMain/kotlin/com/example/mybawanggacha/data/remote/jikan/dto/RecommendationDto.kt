package com.example.mybawanggacha.data.remote.jikan.dto

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

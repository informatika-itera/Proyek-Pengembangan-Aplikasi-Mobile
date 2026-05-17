package com.example.mybawanggacha.data.remote.jikan.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnimeImages(
    val jpg: ImageUrls
)

@Serializable
data class ImageUrls(
    val image_url: String? = null,
    val small_image_url: String? = null,
    val large_image_url: String? = null
)

package com.example.mybawanggacha.data.remote.jikan.dto

import kotlinx.serialization.Serializable

@Serializable
data class MangaDetailResponse(
    val data: MangaDetailData
)

@Serializable
data class MangaDetailData(
    val mal_id: Int,
    val url: String? = null,
    val images: AnimeImages? = null,
    val title: String,
    val title_english: String? = null,
    val title_japanese: String? = null,
    val title_synonyms: List<String> = emptyList(),
    val type: String? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val status: String? = null,
    val publishing: Boolean? = null,
    val published: AnimeAiredDto? = null,
    val score: Double? = null,
    val scored_by: Int? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    val members: Int? = null,
    val favorites: Int? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val authors: List<AnimeNamedResourceDto> = emptyList(),
    val serializations: List<AnimeNamedResourceDto> = emptyList(),
    val genres: List<GenreDto> = emptyList(),
    val explicit_genres: List<AnimeNamedResourceDto> = emptyList(),
    val themes: List<AnimeNamedResourceDto> = emptyList(),
    val demographics: List<AnimeNamedResourceDto> = emptyList(),
    val relations: List<AnimeRelationDto> = emptyList()
)
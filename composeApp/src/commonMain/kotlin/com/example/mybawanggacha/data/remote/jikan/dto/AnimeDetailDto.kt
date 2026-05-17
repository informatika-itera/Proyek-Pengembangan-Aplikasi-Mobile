package com.example.mybawanggacha.data.remote.jikan.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnimeDetailResponse(
    val data: AnimeDetailData
)

@Serializable
data class RelationEntryPreviewResponse(
    val data: RelationEntryPreviewDto
)

@Serializable
data class RelationEntryPreviewDto(
    val mal_id: Int,
    val url: String? = null,
    val images: AnimeImages? = null,
    val title: String? = null,
    val type: String? = null
)

@Serializable
data class AnimeDetailData(
    val mal_id: Int,
    val url: String,
    val images: AnimeImages,
    val trailer: AnimeTrailerDto? = null,
    val titles: List<AnimeTitleDto> = emptyList(),
    val title: String,
    val title_english: String? = null,
    val title_japanese: String? = null,
    val title_synonyms: List<String> = emptyList(),
    val type: String? = null,
    val source: String? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val airing: Boolean? = null,
    val aired: AnimeAiredDto? = null,
    val duration: String? = null,
    val rating: String? = null,
    val score: Double? = null,
    val scored_by: Int? = null,
    val rank: Int? = null,
    val popularity: Int? = null,
    val members: Int? = null,
    val favorites: Int? = null,
    val synopsis: String? = null,
    val background: String? = null,
    val season: String? = null,
    val year: Int? = null,
    val broadcast: AnimeBroadcastDto? = null,
    val producers: List<AnimeNamedResourceDto> = emptyList(),
    val licensors: List<AnimeNamedResourceDto> = emptyList(),
    val studios: List<StudioDto> = emptyList(),
    val genres: List<GenreDto> = emptyList(),
    val explicit_genres: List<AnimeNamedResourceDto> = emptyList(),
    val themes: List<AnimeNamedResourceDto> = emptyList(),
    val demographics: List<AnimeNamedResourceDto> = emptyList(),
    val relations: List<AnimeRelationDto> = emptyList(),
    val theme: AnimeThemeDto? = null,
    val external: List<AnimeExternalLinkDto> = emptyList(),
    val streaming: List<AnimeExternalLinkDto> = emptyList()
)

@Serializable
data class AnimeEpisodesResponse(
    val data: List<AnimeEpisodeDto> = emptyList()
)

@Serializable
data class AnimeEpisodeDto(
    val mal_id: Int,
    val url: String? = null,
    val title: String? = null,
    val title_japanese: String? = null,
    val title_romanji: String? = null,
    val duration: Int? = null,
    val aired: String? = null,
    val filler: Boolean = false,
    val recap: Boolean = false,
    val synopsis: String? = null
)

@Serializable
data class AnimeTrailerDto(
    val youtube_id: String? = null,
    val url: String? = null,
    val embed_url: String? = null
)

@Serializable
data class AnimeTitleDto(
    val type: String,
    val title: String
)

@Serializable
data class AnimeAiredDto(
    val from: String? = null,
    val to: String? = null,
    val string: String? = null
)

@Serializable
data class AnimeBroadcastDto(
    val day: String? = null,
    val time: String? = null,
    val timezone: String? = null,
    val string: String? = null
)

@Serializable
data class GenreDto(
    val mal_id: Int,
    val type: String? = null,
    val name: String,
    val url: String? = null
)

@Serializable
data class StudioDto(
    val mal_id: Int,
    val type: String? = null,
    val name: String,
    val url: String? = null
)

@Serializable
data class AnimeNamedResourceDto(
    val mal_id: Int,
    val type: String? = null,
    val name: String,
    val url: String? = null
)

@Serializable
data class AnimeRelationDto(
    val relation: String,
    val entry: List<AnimeRelationEntryDto> = emptyList()
)

@Serializable
data class AnimeRelationEntryDto(
    val mal_id: Int,
    val type: String? = null,
    val name: String,
    val url: String? = null
)

@Serializable
data class AnimeThemeDto(
    val openings: List<String> = emptyList(),
    val endings: List<String> = emptyList()
)

@Serializable
data class AnimeExternalLinkDto(
    val name: String,
    val url: String
)
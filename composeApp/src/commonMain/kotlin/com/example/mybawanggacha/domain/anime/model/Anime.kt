package com.example.mybawanggacha.domain.anime.model

data class AnimeSummary(
    val malId: Int,
    val title: String,
    val imageUrl: String?,
    val rank: Int? = null,
    val score: Double? = null,
    val rating: String? = null
)

data class AnimePage(
    val items: List<AnimeSummary>,
    val nextPage: Int?,
    val hasNextPage: Boolean
)

data class AnimeDetail(
    val malId: Int,
    val url: String?,
    val imageUrl: String?,
    val title: String,
    val englishTitle: String?,
    val japaneseTitle: String?,
    val titleSynonyms: List<String>,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val status: String?,
    val airing: Boolean?,
    val aired: String?,
    val duration: String?,
    val rating: String?,
    val score: Double?,
    val scoredBy: Int?,
    val rank: Int?,
    val popularity: Int?,
    val members: Int?,
    val favorites: Int?,
    val synopsis: String?,
    val background: String?,
    val season: String?,
    val year: Int?,
    val broadcast: String?,
    val genres: List<String>,
    val explicitGenres: List<String>,
    val themes: List<String>,
    val demographics: List<String>,
    val studios: List<String>,
    val producers: List<String>,
    val licensors: List<String>,
    val relations: List<AnimeRelation>,
    val themeSongs: AnimeThemeSongs,
    val externalLinks: List<AnimeLink>,
    val streamingLinks: List<AnimeLink>,
    val trailerUrl: String?
)

data class AnimeDetailBundle(
    val anime: AnimeDetail,
    val episodes: List<AnimeEpisode>
)

data class AnimeEpisode(
    val number: Int,
    val title: String?,
    val titleJapanese: String?,
    val titleRomanji: String?,
    val aired: String?,
    val filler: Boolean,
    val recap: Boolean,
    val watched: Boolean
)

data class AnimeRelation(
    val relation: String,
    val entries: List<AnimeRelationEntry>
)

data class AnimeRelationEntry(
    val malId: Int,
    val type: String?,
    val name: String,
    val url: String?,
    val preview: AnimeRelationPreview? = null
)

data class AnimeRelationPreview(
    val malId: Int,
    val type: String?,
    val title: String?,
    val imageUrl: String?,
    val url: String?
)

data class AnimeThemeSongs(
    val openings: List<String>,
    val endings: List<String>
)

data class AnimeLink(
    val name: String,
    val url: String
)
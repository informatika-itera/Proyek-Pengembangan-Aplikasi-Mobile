package com.example.mybawanggacha.data.remote.jikan.mapper

import com.example.mybawanggacha.data.remote.jikan.dto.AnimeCatalogItemDto
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeDetailData
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeEpisodeDto
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeExternalLinkDto
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeRelationEntryDto
import com.example.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import com.example.mybawanggacha.data.remote.jikan.dto.RelationEntryPreviewDto
import com.example.mybawanggacha.domain.anime.model.AnimeDetail
import com.example.mybawanggacha.domain.anime.model.AnimeEpisode
import com.example.mybawanggacha.domain.anime.model.AnimeLink
import com.example.mybawanggacha.domain.anime.model.AnimePage
import com.example.mybawanggacha.domain.anime.model.AnimeRelation
import com.example.mybawanggacha.domain.anime.model.AnimeRelationEntry
import com.example.mybawanggacha.domain.anime.model.AnimeRelationPreview
import com.example.mybawanggacha.domain.anime.model.AnimeSummary
import com.example.mybawanggacha.domain.anime.model.AnimeThemeSongs

internal fun JikanAnimeListResponse.toDomainPage(requestedPage: Int): AnimePage {
    val hasNextPage = pagination?.has_next_page == true
    return AnimePage(
        items = data.toSummaryList(),
        nextPage = if (hasNextPage) requestedPage + 1 else null,
        hasNextPage = hasNextPage
    )
}

internal fun List<AnimeCatalogItemDto>.toSummaryList(): List<AnimeSummary> {
    return distinctBy { it.mal_id }
        .map { item ->
            AnimeSummary(
                malId = item.mal_id,
                title = item.title_english?.takeIf { it.isNotBlank() } ?: item.title,
                imageUrl = item.images?.jpg?.large_image_url
                    ?: item.images?.jpg?.image_url,
                rank = item.rank,
                score = item.score,
                rating = item.rating
            )
        }
}

internal fun AnimeDetailData.toDomain(
    relationPreviews: Map<String, AnimeRelationPreview>
): AnimeDetail {
    return AnimeDetail(
        malId = mal_id,
        url = url,
        imageUrl = images.jpg.large_image_url,
        title = title,
        englishTitle = title_english,
        japaneseTitle = title_japanese,
        titleSynonyms = title_synonyms,
        type = type,
        source = source,
        episodes = episodes,
        status = status,
        airing = airing,
        aired = aired?.string,
        duration = duration,
        rating = rating,
        score = score,
        scoredBy = scored_by,
        rank = rank,
        popularity = popularity,
        members = members,
        favorites = favorites,
        synopsis = synopsis,
        background = background,
        season = season,
        year = year,
        broadcast = broadcast?.string,
        genres = genres.map { it.name },
        explicitGenres = explicit_genres.map { it.name },
        themes = themes.map { it.name },
        demographics = demographics.map { it.name },
        studios = studios.map { it.name },
        producers = producers.map { it.name },
        licensors = licensors.map { it.name },
        relations = relations.map { relation ->
            AnimeRelation(
                relation = relation.relation,
                entries = relation.entry.map { entry ->
                    entry.toDomain(preview = relationPreviews[entry.previewKey()])
                }
            )
        },
        themeSongs = AnimeThemeSongs(
            openings = theme?.openings.orEmpty(),
            endings = theme?.endings.orEmpty()
        ),
        externalLinks = external.map { it.toDomain() },
        streamingLinks = streaming.map { it.toDomain() },
        trailerUrl = trailer?.url
            ?: trailer?.embed_url
            ?: trailer?.youtube_id?.let { "https://www.youtube.com/watch?v=$it" }
    )
}

internal fun AnimeEpisodeDto.toDomain(watched: Boolean): AnimeEpisode {
    return AnimeEpisode(
        number = mal_id,
        title = title,
        titleJapanese = title_japanese,
        titleRomanji = title_romanji,
        aired = aired,
        filler = filler,
        recap = recap,
        watched = watched
    )
}

internal fun AnimeRelationEntryDto.toDomain(
    preview: AnimeRelationPreview?
): AnimeRelationEntry {
    return AnimeRelationEntry(
        malId = mal_id,
        type = type,
        name = name,
        url = url,
        preview = preview
    )
}

internal fun RelationEntryPreviewDto.toDomain(): AnimeRelationPreview {
    return AnimeRelationPreview(
        malId = mal_id,
        type = type,
        title = title,
        imageUrl = images?.jpg?.large_image_url,
        url = url
    )
}

internal fun AnimeExternalLinkDto.toDomain(): AnimeLink {
    return AnimeLink(name = name, url = url)
}

internal fun AnimeRelationEntryDto.previewKey(): String {
    return "${type.orEmpty().lowercase()}:$mal_id"
}

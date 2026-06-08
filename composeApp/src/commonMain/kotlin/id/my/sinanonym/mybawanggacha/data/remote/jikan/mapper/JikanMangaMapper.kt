package id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper

import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeCatalogItemDto
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeRelationEntryDto
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.MangaDetailData
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaDetail
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaRelation
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaRelationEntry
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaRelationPreview
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaPage
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaSummary

internal fun JikanAnimeListResponse.toMangaDomainPage(requestedPage: Int): MangaPage {
    val hasNextPage = pagination?.has_next_page == true
    return MangaPage(
        items = data.toMangaSummaryList(),
        nextPage = if (hasNextPage) requestedPage + 1 else null,
        hasNextPage = hasNextPage
    )
}

internal fun List<AnimeCatalogItemDto>.toMangaSummaryList(): List<MangaSummary> {
    return distinctBy { it.mal_id }
        .map { item ->
            MangaSummary(
                malId = item.mal_id,
                title = item.title_english?.takeIf { it.isNotBlank() } ?: item.title,
                imageUrl = item.images?.jpg?.large_image_url
                    ?: item.images?.jpg?.image_url,
                rank = item.rank,
                score = item.score,
                type = item.type
            )
        }
}

internal fun MangaDetailData.toSummary(): MangaSummary {
    return MangaSummary(
        malId = mal_id,
        title = title_english?.takeIf { it.isNotBlank() } ?: title,
        imageUrl = images?.jpg?.large_image_url ?: images?.jpg?.image_url,
        rank = rank,
        score = score,
        type = type
    )
}

internal fun MangaDetailData.toDomain(
    relationPreviews: Map<String, MangaRelationPreview> = emptyMap()
): MangaDetail {
    return MangaDetail(
        malId = mal_id,
        url = url,
        imageUrl = images?.jpg?.large_image_url ?: images?.jpg?.image_url,
        title = title,
        englishTitle = title_english,
        japaneseTitle = title_japanese,
        titleSynonyms = title_synonyms,
        type = type,
        chapters = chapters,
        volumes = volumes,
        status = status,
        publishing = publishing,
        published = published?.string,
        score = score,
        scoredBy = scored_by,
        rank = rank,
        popularity = popularity,
        members = members,
        favorites = favorites,
        synopsis = synopsis,
        background = background,
        authors = authors.map { it.name },
        serializations = serializations.map { it.name },
        genres = genres.map { it.name },
        explicitGenres = explicit_genres.map { it.name },
        themes = themes.map { it.name },
        demographics = demographics.map { it.name },
        relations = relations.map { relation ->
            MangaRelation(
                relation = relation.relation,
                entries = relation.entry.map { entry ->
                    entry.toMangaRelationEntry(preview = relationPreviews[entry.previewKey()])
                }
            )
        }
    )
}

private fun AnimeRelationEntryDto.toMangaRelationEntry(
    preview: MangaRelationPreview?
): MangaRelationEntry {
    return MangaRelationEntry(
        malId = mal_id,
        type = type,
        name = name,
        url = url,
        preview = preview
    )
}

internal fun id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.RelationEntryPreviewDto.toMangaDomain(): MangaRelationPreview {
    return MangaRelationPreview(
        malId = mal_id,
        type = type,
        title = title,
        imageUrl = images?.jpg?.large_image_url ?: images?.jpg?.image_url,
        url = url
    )
}
package id.my.sinanonym.mybawanggacha.data.remote.jikan.mapper

import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.AnimeCatalogItemDto
import id.my.sinanonym.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchPage
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType

internal fun JikanAnimeListResponse.toSearchPage(
    mediaType: SearchMediaType,
    requestedPage: Int
): MediaSearchPage {
    val hasNextPage = pagination?.has_next_page == true
    return MediaSearchPage(
        items = data
            .distinctBy { item -> item.mal_id }
            .map { item -> item.toSearchItem(mediaType) },
        nextPage = if (hasNextPage) requestedPage + 1 else null,
        hasNextPage = hasNextPage
    )
}

private fun AnimeCatalogItemDto.toSearchItem(mediaType: SearchMediaType): MediaSearchItem {
    return MediaSearchItem(
        malId = mal_id,
        mediaType = mediaType,
        title = title_english?.takeIf { it.isNotBlank() } ?: title,
        imageUrl = images?.jpg?.large_image_url ?: images?.jpg?.image_url,
        type = type,
        status = status,
        score = score,
        rank = rank,
        episodes = episodes,
        chapters = chapters,
        volumes = volumes
    )
}

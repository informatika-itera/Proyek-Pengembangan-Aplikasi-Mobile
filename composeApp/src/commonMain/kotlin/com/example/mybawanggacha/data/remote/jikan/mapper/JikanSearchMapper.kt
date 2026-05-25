package com.example.mybawanggacha.data.remote.jikan.mapper

import com.example.mybawanggacha.data.remote.jikan.dto.AnimeCatalogItemDto
import com.example.mybawanggacha.data.remote.jikan.dto.JikanAnimeListResponse
import com.example.mybawanggacha.domain.search.model.MediaSearchItem
import com.example.mybawanggacha.domain.search.model.MediaSearchPage
import com.example.mybawanggacha.domain.search.model.SearchMediaType

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

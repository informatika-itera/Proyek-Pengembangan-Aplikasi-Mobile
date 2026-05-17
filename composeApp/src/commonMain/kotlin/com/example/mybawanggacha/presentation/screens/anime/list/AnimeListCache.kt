package com.example.mybawanggacha.presentation.screens.anime.list

import com.example.mybawanggacha.domain.anime.model.AnimePage
import com.example.mybawanggacha.domain.anime.model.AnimeSummary

internal class AnimeListPageCache {
    private val entries = mutableMapOf<String, AnimeListCacheEntry>()

    fun get(key: String): AnimeListCacheEntry? = entries[key]

    fun put(key: String, entry: AnimeListCacheEntry) {
        entries[key] = entry
    }

    fun contains(key: String): Boolean = key in entries
}

internal data class AnimeListCacheEntry(
    val anime: List<AnimeSummary>,
    val nextPage: Int?,
    val canLoadMore: Boolean
)

internal fun AnimePage.toCacheEntry(): AnimeListCacheEntry {
    return AnimeListCacheEntry(
        anime = items,
        nextPage = nextPage,
        canLoadMore = hasNextPage
    )
}

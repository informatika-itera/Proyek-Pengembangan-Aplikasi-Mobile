package com.example.mybawanggacha.presentation.screens.manga.list

import com.example.mybawanggacha.domain.manga.model.MangaPage
import com.example.mybawanggacha.domain.manga.model.MangaSummary

internal class MangaListPageCache {
    private val entries = mutableMapOf<String, MangaListCacheEntry>()

    fun get(key: String): MangaListCacheEntry? = entries[key]

    fun put(key: String, entry: MangaListCacheEntry) {
        entries[key] = entry
    }

    fun contains(key: String): Boolean = key in entries
}

internal data class MangaListCacheEntry(
    val manga: List<MangaSummary>,
    val nextPage: Int?,
    val canLoadMore: Boolean
)

internal fun MangaPage.toCacheEntry(): MangaListCacheEntry {
    return MangaListCacheEntry(
        manga = items,
        nextPage = nextPage,
        canLoadMore = hasNextPage
    )
}

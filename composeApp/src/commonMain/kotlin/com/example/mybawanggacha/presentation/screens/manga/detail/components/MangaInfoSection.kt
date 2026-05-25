package com.example.mybawanggacha.presentation.screens.manga.detail.components

import androidx.compose.runtime.Composable
import com.example.mybawanggacha.domain.manga.model.MangaDetail

@Composable
internal fun MangaInfoSection(manga: MangaDetail) {
    MangaDetailSectionColumn(title = "Info") {
        MangaDetailTextBlock(
            title = "Publication",
            body = listOf(
                "Type: ${manga.type.orUnknown()}",
                "Status: ${manga.status.orUnknown()}",
                "Publishing: ${manga.publishing?.let { if (it) "Yes" else "No" } ?: "Unknown"}",
                "Published: ${manga.published.orUnknown()}",
                "Chapters: ${manga.chapters.formatNumber()}",
                "Volumes: ${manga.volumes.formatNumber()}"
            ).joinToString(separator = "\n")
        )

        MangaDetailTextBlock(
            title = "Stats",
            body = listOf(
                "Score: ${manga.score?.toString() ?: "Unknown"}",
                "Scored by: ${manga.scoredBy.formatNumber()}",
                "Rank: ${manga.rank?.let { "#$it" } ?: "Unknown"}",
                "Popularity: ${manga.popularity?.let { "#$it" } ?: "Unknown"}",
                "Members: ${manga.members.formatNumber()}",
                "Favorites: ${manga.favorites.formatNumber()}"
            ).joinToString(separator = "\n")
        )

        MangaDetailTextBlockIfNotEmpty(
            title = "Authors",
            body = manga.authors.joinToString()
        )
        MangaDetailTextBlockIfNotEmpty(
            title = "Serializations",
            body = manga.serializations.joinToString()
        )
        MangaDetailTextBlockIfNotEmpty(
            title = "Genres",
            body = manga.genres.joinToString()
        )
        MangaDetailTextBlockIfNotEmpty(
            title = "Themes",
            body = manga.themes.joinToString()
        )
        MangaDetailTextBlockIfNotEmpty(
            title = "Demographics",
            body = manga.demographics.joinToString()
        )
    }
}

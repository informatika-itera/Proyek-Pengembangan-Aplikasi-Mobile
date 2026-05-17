package com.example.mybawanggacha.presentation.screens.anime.detail.components

import androidx.compose.runtime.Composable
import com.example.mybawanggacha.domain.anime.model.AnimeDetail

@Composable
internal fun AnimeInfoSection(anime: AnimeDetail) {
    DetailSectionColumn(title = "Info") {
        DetailTextBlockIfNotEmpty(
            title = "Genres",
            body = anime.genres.joinToString()
        )

        DetailTextBlockIfNotEmpty(
            title = "Themes",
            body = anime.themes.joinToString()
        )

        DetailTextBlockIfNotEmpty(
            title = "Demographics",
            body = anime.demographics.joinToString()
        )

        DetailTextBlockIfNotEmpty(
            title = "Explicit Genres",
            body = anime.explicitGenres.joinToString()
        )

        DetailTextBlockIfNotEmpty(
            title = "Studios",
            body = anime.studios.joinToString()
        )

        DetailTextBlockIfNotEmpty(
            title = "Producers",
            body = anime.producers.joinToString()
        )

        DetailTextBlockIfNotEmpty(
            title = "Licensors",
            body = anime.licensors.joinToString()
        )
    }
}

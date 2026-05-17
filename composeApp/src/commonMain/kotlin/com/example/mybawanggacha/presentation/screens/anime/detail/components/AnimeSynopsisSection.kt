package com.example.mybawanggacha.presentation.screens.anime.detail.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mybawanggacha.domain.anime.model.AnimeDetail

@Composable
internal fun AnimeSynopsisSection(anime: AnimeDetail) {
    DetailSectionColumn(title = "Sinopsis") {
        DetailTextBlock(
            title = anime.title,
            body = anime.synopsis ?: "Belum ada sinopsis dari Jikan."
        )

        anime.background
            ?.takeIf { it.isNotBlank() }
            ?.let { background ->
                Spacer(modifier = Modifier.height(16.dp))
                DetailTextBlock(
                    title = "Background",
                    body = background
                )
            }
    }
}

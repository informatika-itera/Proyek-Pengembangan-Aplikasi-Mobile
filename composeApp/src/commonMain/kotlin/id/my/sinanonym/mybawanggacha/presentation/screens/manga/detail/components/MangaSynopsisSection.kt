package id.my.sinanonym.mybawanggacha.presentation.screens.manga.detail.components

import androidx.compose.runtime.Composable
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaDetail

@Composable
internal fun MangaSynopsisSection(manga: MangaDetail) {
    MangaDetailSectionColumn(title = "Sinopsis") {
        MangaDetailTextBlockIfNotEmpty(
            title = "Synopsis",
            body = manga.synopsis.orEmpty()
        )
        MangaDetailTextBlockIfNotEmpty(
            title = "Background",
            body = manga.background.orEmpty()
        )
        if (manga.synopsis.isNullOrBlank() && manga.background.isNullOrBlank()) {
            MangaDetailTextBlock(
                title = "Belum ada sinopsis",
                body = "Jikan belum menyediakan sinopsis atau background untuk manga ini."
            )
        }
    }
}

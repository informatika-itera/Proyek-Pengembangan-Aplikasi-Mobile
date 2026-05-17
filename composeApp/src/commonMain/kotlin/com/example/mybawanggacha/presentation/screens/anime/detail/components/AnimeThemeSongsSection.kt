package com.example.mybawanggacha.presentation.screens.anime.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mybawanggacha.domain.anime.model.AnimeDetail

@Composable
internal fun AnimeThemeSongsSection(anime: AnimeDetail) {
    val openings = anime.themeSongs.openings
    val endings = anime.themeSongs.endings

    DetailSectionColumn(title = "Theme Songs") {
        if (openings.isEmpty() && endings.isEmpty()) {
            DetailTextBlock(
                title = "Belum ada theme songs",
                body = "Opening dan ending belum tersedia dari Jikan."
            )
            return@DetailSectionColumn
        }

        ThemeSongGroup(
            title = "Openings",
            labelPrefix = "OP",
            songs = openings
        )

        if (openings.isNotEmpty() && endings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(18.dp))
        }

        ThemeSongGroup(
            title = "Endings",
            labelPrefix = "ED",
            songs = endings
        )
    }
}

@Composable
private fun ThemeSongGroup(
    title: String,
    labelPrefix: String,
    songs: List<String>
) {
    if (songs.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${songs.size} lagu",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        songs.forEachIndexed { index, rawSong ->
            ThemeSongCard(
                label = "$labelPrefix ${rawSong.themeSongNumber(fallback = index + 1)}",
                song = rawSong.toThemeSongUiModel()
            )
        }
    }
}

@Composable
private fun ThemeSongCard(
    label: String,
    song: ThemeSongUiModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (song.artist.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "by ${song.artist}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        song.episodes?.let { episodes ->
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = episodes,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class ThemeSongUiModel(
    val title: String,
    val artist: String,
    val episodes: String?
)

private fun String.toThemeSongUiModel(): ThemeSongUiModel {
    val raw = trim()
    val withoutNumber = raw.replace(Regex("^\\s*\\d+\\s*:\\s*"), "").trim()
    val episodeRegex = Regex("""\((eps?\.?\s*[^)]*)\)\s*$""", RegexOption.IGNORE_CASE)
    val episodes = episodeRegex.find(withoutNumber)
        ?.groupValues
        ?.getOrNull(1)
        ?.replaceFirstChar { it.uppercase() }
    val withoutEpisodes = episodeRegex.replace(withoutNumber, "").trim()
    val parts = withoutEpisodes.split(" by ", limit = 2)
    val title = parts.firstOrNull()
        ?.trim()
        ?.trim('"', '“', '”')
        .orEmpty()
        .ifBlank { raw }
    val artist = parts.getOrNull(1)?.trim().orEmpty()

    return ThemeSongUiModel(
        title = title,
        artist = artist,
        episodes = episodes
    )
}

private fun String.themeSongNumber(fallback: Int): String {
    return Regex("^\\s*(\\d+)\\s*:")
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        ?: fallback.toString()
}

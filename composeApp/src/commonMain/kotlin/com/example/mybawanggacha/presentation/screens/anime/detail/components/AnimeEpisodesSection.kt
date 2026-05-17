package com.example.mybawanggacha.presentation.screens.anime.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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
import com.example.mybawanggacha.domain.anime.model.AnimeEpisode

@Composable
internal fun AnimeEpisodeListSection(
    anime: AnimeDetail,
    episodes: List<AnimeEpisode>,
    onEpisodeWatchedChange: (Int, Boolean) -> Unit
) {
    val episodeItems = episodes.map { it.toEpisodeUiModel() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 8.dp, top = 32.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Episode",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (episodes.isNotEmpty()) {
                        "Episode dari ${anime.title}"
                    } else {
                        ""
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (episodeItems.isEmpty()) {
            item {
                DetailTextBlock(
                    title = "Episode tidak tersedia",
                    body = "Jumlah episode untuk anime ${anime.title} pada Jikan."
                )
            }
        } else {
            items(
                items = episodeItems,
                key = { it.number }
            ) { episode ->
                EpisodeRow(
                    episode = episode,
                    onWatchedChange = { watched ->
                        onEpisodeWatchedChange(episode.number, watched)
                    }
                )
            }
        }
    }
}

private fun AnimeEpisode.toEpisodeUiModel(): EpisodeUiModel {
    return EpisodeUiModel(
        number = number,
        title = title.orUnknown(),
        metadata = listOfNotNull(
            titleRomanji?.takeIf { it.isNotBlank() },
            titleJapanese?.takeIf { it.isNotBlank() },
            aired?.takeIf { it.isNotBlank() }
        ).joinToString(" • ").ifBlank { "Tidak ada metadata episode." },
        filler = filler,
        recap = recap,
        watched = watched
    )
}

@Composable
private fun EpisodeRow(
    episode: EpisodeUiModel,
    onWatchedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = episode.number.toString().padStart(2, '0'),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = episode.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    WatchedIndicator(
                        watched = episode.watched,
                        onClick = { onWatchedChange(!episode.watched) }
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = episode.metadata,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                val flags = listOfNotNull(
                    "Filler".takeIf { episode.filler },
                    "Recap".takeIf { episode.recap }
                ).joinToString(" • ")

                if (flags.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = flags,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.64f)
        )
    }
}

@Composable
private fun WatchedIndicator(
    watched: Boolean,
    onClick: () -> Unit
) {
    val shape = CircleShape
    val borderColor = if (watched) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.76f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)
    }
    val backgroundColor = if (watched) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
    }

    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (watched) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class EpisodeUiModel(
    val number: Int,
    val title: String,
    val metadata: String,
    val filler: Boolean,
    val recap: Boolean,
    val watched: Boolean
)
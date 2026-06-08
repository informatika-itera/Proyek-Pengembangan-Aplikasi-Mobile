package id.my.sinanonym.mybawanggacha.presentation.screens.anime.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeDetail
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeEpisode
import kotlin.math.abs

private const val EpisodeSwipeThresholdPx = 96f
private const val EpisodeSwipeMaxPx = 180f

@Composable
internal fun AnimeEpisodeListSection(
    anime: AnimeDetail,
    episodes: List<AnimeEpisode>,
    onEpisodeWatchedChange: (Int, Boolean) -> Unit,
    onEpisodeMarkedChange: (Int, Boolean) -> Unit
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
                    },
                    onMarkedChange = { marked ->
                        onEpisodeMarkedChange(episode.number, marked)
                    }
                )
            }
        }
    }
}

private fun AnimeEpisode.toEpisodeUiModel(): EpisodeUiModel {
    return EpisodeUiModel(
        number = number,
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown",
        metadata = listOfNotNull(
            titleRomanji?.takeIf { it.isNotBlank() },
            titleJapanese?.takeIf { it.isNotBlank() },
            aired?.takeIf { it.isNotBlank() }
        ).joinToString(" • ").ifBlank { "Tidak ada metadata episode." },
        filler = filler,
        recap = recap,
        watched = watched,
        marked = marked
    )
}

@Composable
private fun EpisodeRow(
    episode: EpisodeUiModel,
    onWatchedChange: (Boolean) -> Unit,
    onMarkedChange: (Boolean) -> Unit
) {
    var swipeOffset by remember(episode.number) { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(episode.number, episode.watched, episode.marked) {
                detectHorizontalDragGestures(
                    onDragCancel = { swipeOffset = 0f },
                    onDragEnd = {
                        when {
                            swipeOffset <= -EpisodeSwipeThresholdPx -> {
                                onWatchedChange(!episode.watched)
                            }
                            swipeOffset >= EpisodeSwipeThresholdPx -> {
                                onMarkedChange(!episode.marked)
                            }
                        }
                        swipeOffset = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        swipeOffset = (swipeOffset + dragAmount)
                            .coerceIn(-EpisodeSwipeMaxPx, EpisodeSwipeMaxPx)
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = swipeOffset * 0.18f
                }
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
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = if (episode.watched) 0.42f else 1f
                    ),
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
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = if (episode.watched) 0.48f else 1f
                            ),
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        EpisodeMarkButton(
                            marked = episode.marked,
                            onClick = { onMarkedChange(!episode.marked) }
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = episode.metadata,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (episode.watched) 0.46f else 1f
                        ),
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
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = if (episode.watched) 0.42f else 1f
                            ),
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

        EpisodeSwipeActionOverlay(
            swipeOffset = swipeOffset,
            watched = episode.watched,
            marked = episode.marked
        )
    }
}

@Composable
private fun EpisodeSwipeActionOverlay(
    swipeOffset: Float,
    watched: Boolean,
    marked: Boolean
) {
    val progress = (abs(swipeOffset) / EpisodeSwipeThresholdPx).coerceIn(0f, 1f)
    if (progress <= 0f) return

    val isMarkSwipe = swipeOffset > 0f
    val label = if (isMarkSwipe) {
        if (marked) "Unmark" else "Mark"
    } else {
        if (watched) "Belum ditonton" else "Telah ditonton"
    }
    val icon = if (isMarkSwipe) {
        if (marked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder
    } else {
        if (watched) Icons.Filled.CheckCircle else Icons.Filled.CheckCircleOutline
    }
    val contentAlignment = if (isMarkSwipe) Alignment.TopStart else Alignment.TopEnd
    val actionColor = if (isMarkSwipe) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    }
    val gradient = if (isMarkSwipe) {
        Brush.horizontalGradient(
            colors = listOf(
                actionColor.copy(alpha = 0.30f * progress),
                actionColor.copy(alpha = 0.12f * progress),
                Color.Transparent
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                actionColor.copy(alpha = 0.12f * progress),
                actionColor.copy(alpha = 0.30f * progress)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(MaterialTheme.shapes.large)
            .background(gradient)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = contentAlignment
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f * progress))
                .padding(horizontal = 11.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = actionColor.copy(alpha = 0.92f * progress),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = actionColor.copy(alpha = 0.92f * progress),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EpisodeMarkButton(
    marked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (marked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
            contentDescription = if (marked) "Hapus mark episode" else "Mark episode",
            tint = if (marked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.74f)
            },
            modifier = Modifier.size(20.dp)
        )
    }
}

private data class EpisodeUiModel(
    val number: Int,
    val title: String,
    val metadata: String,
    val filler: Boolean,
    val recap: Boolean,
    val watched: Boolean,
    val marked: Boolean
)

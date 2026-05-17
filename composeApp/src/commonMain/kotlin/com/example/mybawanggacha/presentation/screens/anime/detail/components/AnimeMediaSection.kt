package com.example.mybawanggacha.presentation.screens.anime.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mybawanggacha.domain.anime.model.AnimeDetail
import com.example.mybawanggacha.domain.anime.model.AnimeLink

@Composable
internal fun AnimeMediaSection(anime: AnimeDetail) {
    val uriHandler = LocalUriHandler.current
    val trailerUrl = anime.trailerUrl

    DetailSectionColumn(title = "Media") {
        if (trailerUrl.isNullOrBlank() && anime.streamingLinks.isEmpty() && anime.externalLinks.isEmpty()) {
            DetailTextBlock(
                title = "Belum ada media",
                body = "Jikan belum menyediakan trailer, streaming, atau external link untuk anime ini."
            )
            return@DetailSectionColumn
        }

        if (!trailerUrl.isNullOrBlank()) {
            MediaGroupHeader(title = "Trailer", count = 1)
            MediaLinkCard(
                title = "Watch trailer",
                subtitle = trailerUrl.toReadableUrl(),
                label = trailerUrl.toMediaSourceLabel(),
                onClick = { runCatching { uriHandler.openUri(trailerUrl) } }
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        MediaLinkGroup(
            title = "Streaming",
            links = anime.streamingLinks,
            onOpen = { url -> runCatching { uriHandler.openUri(url) } }
        )

        MediaLinkGroup(
            title = "External",
            links = anime.externalLinks,
            onOpen = { url -> runCatching { uriHandler.openUri(url) } }
        )
    }
}

@Composable
private fun MediaLinkGroup(
    title: String,
    links: List<AnimeLink>,
    onOpen: (String) -> Unit
) {
    if (links.isEmpty()) return

    MediaGroupHeader(title = title, count = links.size)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        links.forEach { link ->
            MediaLinkCard(
                title = link.name,
                subtitle = link.url.toReadableUrl(),
                label = link.url.toMediaSourceLabel(),
                onClick = { onOpen(link.url) }
            )
        }
    }

    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
private fun MediaGroupHeader(
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
        }

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun MediaLinkCard(
    title: String,
    subtitle: String,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "↗",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun String.toReadableUrl(): String {
    return trim()
        .removePrefix("https://")
        .removePrefix("http://")
        .removePrefix("www.")
        .removeSuffix("/")
}

private fun String.toMediaSourceLabel(): String {
    val readable = toReadableUrl().lowercase()
    return when {
        "youtube" in readable || "youtu.be" in readable -> "YT"
        "crunchyroll" in readable -> "CR"
        "netflix" in readable -> "NF"
        "bilibili" in readable -> "BL"
        "anidb" in readable -> "DB"
        "animenewsnetwork" in readable -> "ANN"
        "wikipedia" in readable -> "WK"
        "twitter" in readable || "x.com" in readable -> "X"
        else -> readable.take(2).ifBlank { "URL" }
    }
}

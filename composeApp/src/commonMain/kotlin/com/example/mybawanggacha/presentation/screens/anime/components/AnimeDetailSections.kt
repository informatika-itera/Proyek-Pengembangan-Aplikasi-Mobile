package com.example.mybawanggacha.presentation.screens.anime.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mybawanggacha.domain.model.AnimeDetail
import com.example.mybawanggacha.domain.model.AnimeEpisode
import com.example.mybawanggacha.domain.model.AnimeLink
import com.example.mybawanggacha.domain.model.AnimeRelationEntry
import com.example.mybawanggacha.presentation.components.MBGSideRailItem

internal enum class AnimeDetailSection(
    val key: String,
    val label: String,
    val icon: ImageVector
) {
    Overview("overview", "Overview", Icons.Default.Theaters),
    Synopsis("synopsis", "Sinopsis", Icons.Default.Article),
    Episodes("episodes", "Episode", Icons.Default.PlaylistPlay),
    Info("info", "Info", Icons.Default.Info),
    Media("media", "Media", Icons.Default.Movie),
    Relations("relations", "Relations", Icons.Default.Link),
    ThemeSongs("theme_songs", "Theme Songs", Icons.Default.MusicNote)
}

internal fun animeDetailRailItems(): List<MBGSideRailItem> = AnimeDetailSection.entries.map { section ->
    MBGSideRailItem(
        key = section.key,
        label = section.label,
        icon = section.icon
    )
}

@Composable
internal fun AnimeDetailContent(
    anime: AnimeDetail,
    episodes: List<AnimeEpisode>,
    selectedSection: AnimeDetailSection,
    onEpisodeWatchedChange: (Int, Boolean) -> Unit,
    onRelationEntryClick: (AnimeRelationEntry) -> Unit
) {
    when (selectedSection) {
        AnimeDetailSection.Overview -> AnimeOverviewSection(anime)
        AnimeDetailSection.Synopsis -> AnimeSynopsisSection(anime)
        AnimeDetailSection.Episodes -> AnimeEpisodeListSection(
            anime = anime,
            episodes = episodes,
            onEpisodeWatchedChange = onEpisodeWatchedChange
        )
        AnimeDetailSection.Info -> AnimeInfoSection(anime)
        AnimeDetailSection.Media -> AnimeMediaSection(anime)
        AnimeDetailSection.Relations -> AnimeRelationsSection(
            anime = anime,
            onEntryClick = onRelationEntryClick
        )
        AnimeDetailSection.ThemeSongs -> AnimeThemeSongsSection(anime)
    }
}

@Composable
private fun AnimeOverviewSection(anime: AnimeDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 8.dp, top = 32.dp, end = 20.dp, bottom = 32.dp)
    ) {
        Text(
            text = anime.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        anime.englishTitle
            ?.takeIf { it.isNotBlank() && it != anime.title }
            ?.let { englishTitle ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = englishTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

        anime.japaneseTitle
            ?.takeIf { it.isNotBlank() }
            ?.let { japaneseTitle ->
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = japaneseTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

        if (anime.titleSynonyms.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = anime.titleSynonyms.joinToString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = anime.imageUrl,
                contentDescription = anime.title,
                modifier = Modifier
                    .width(124.dp)
                    .height(178.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScoreCard(
                    score = anime.score,
                    scoredBy = anime.scoredBy
                )
                InfoCard(label = "Type", value = anime.type.orUnknown())
                InfoCard(label = "Episodes", value = anime.episodes.formatNumber())
                InfoCard(label = "Source", value = anime.source.orUnknown())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DetailInfoRows(
            rows = listOf(
                "Season" to formatSeason(anime),
                "Aired" to anime.aired.orUnknown(),
                "Broadcast" to anime.broadcast.orUnknown(),
                "Rating" to anime.rating.orUnknown(),
                "Duration" to anime.duration.orUnknown()
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OverviewMetricGrid(
            items = listOf(
                "Status" to anime.status.orUnknown(),
                "Airing" to anime.airing.formatAiringStatus()
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OverviewMetricGrid(
            items = listOf(
                "Rank" to anime.rank.formatRank(),
                "Popularity" to anime.popularity.formatRank(),
                "Members" to anime.members.formatNumber(),
                "Favorites" to anime.favorites.formatNumber()
            )
        )
    }
}

@Composable
private fun AnimeSynopsisSection(anime: AnimeDetail) {
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

@Composable
private fun AnimeEpisodeListSection(
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
private fun AnimeInfoSection(anime: AnimeDetail) {
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

@Composable
private fun AnimeMediaSection(anime: AnimeDetail) {
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
private fun AnimeRelationsSection(
    anime: AnimeDetail,
    onEntryClick: (AnimeRelationEntry) -> Unit
) {
    DetailSectionColumn(title = "Relations") {
        if (anime.relations.isEmpty()) {
            DetailTextBlock(
                title = "Belum ada relasi",
                body = "Jikan belum menyediakan relasi untuk anime ini."
            )
        } else {
            anime.relations.forEachIndexed { index, relation ->
                RelationGroupTitle(relation.relation)

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    relation.entries.forEach { entry ->
                        RelationEntryCard(
                            entry = entry,
                            onClick = { onEntryClick(entry) }
                        )
                    }
                }

                if (index != anime.relations.lastIndex) {
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun RelationGroupTitle(title: String) {
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
}

@Composable
private fun RelationEntryCard(
    entry: AnimeRelationEntry,
    onClick: () -> Unit
) {
    val type = entry.type.orUnknown().replaceFirstChar { it.uppercase() }
    val imageUrl = entry.preview?.imageUrl

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f))
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(82.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = entry.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = type.take(1),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = entry.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = type,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AnimeThemeSongsSection(anime: AnimeDetail) {
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

@Composable
private fun DetailSectionColumn(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 8.dp, top = 32.dp, end = 20.dp, bottom = 32.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        content()
    }
}

@Composable
private fun ScoreCard(
    score: Double?,
    scoredBy: Int?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = score?.toString() ?: "N/A",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Score • ${scoredBy.formatNumber()} users",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun OverviewMetricGrid(
    items: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { (label, value) ->
                    OverviewMetricCard(
                        label = label,
                        value = value,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun OverviewMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DetailInfoRows(
    rows: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        rows.forEachIndexed { index, (label, value) ->
            DetailInfoRow(
                label = label,
                value = value
            )

            if (index != rows.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)
                )
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.58f)
        )
    }
}

@Composable
private fun InfoCard(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DetailTextBlockIfNotEmpty(
    title: String,
    body: String
) {
    if (body.isBlank()) return

    DetailTextBlock(
        title = title,
        body = body
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun DetailTextBlock(
    title: String,
    body: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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

private fun String?.orUnknown(): String {
    return this?.takeIf { it.isNotBlank() } ?: "Unknown"
}

private fun Int?.formatNumber(): String {
    return this?.toString() ?: "Unknown"
}

private fun Int?.formatRank(): String {
    return this?.let { "#$it" } ?: "Unknown"
}

private fun Boolean?.formatAiringStatus(): String {
    return when (this) {
        true -> "Airing"
        false -> "Not airing"
        null -> "Unknown"
    }
}

private fun formatSeason(anime: AnimeDetail): String {
    return listOfNotNull(
        anime.season?.replaceFirstChar { it.uppercase() },
        anime.year?.toString()
    ).joinToString(" ").ifBlank { "Unknown" }
}

private data class EpisodeUiModel(
    val number: Int,
    val title: String,
    val metadata: String,
    val filler: Boolean,
    val recap: Boolean,
    val watched: Boolean
)
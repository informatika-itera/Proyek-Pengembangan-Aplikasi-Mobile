package com.example.mybawanggacha.presentation.screens.anime

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mybawanggacha.data.remote.dto.AnimeDetailData
import com.example.mybawanggacha.data.remote.dto.AnimeEpisodeDto
import com.example.mybawanggacha.data.remote.dto.AnimeExternalLinkDto
import com.example.mybawanggacha.data.remote.dto.AnimeRelationEntryDto
import com.example.mybawanggacha.data.remote.dto.RelationEntryPreviewDto
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailItem
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private enum class AnimeDetailSection(
    val key: String,
    val label: String
) {
    Overview("overview", "Overview"),
    Synopsis("synopsis", "Sinopsis"),
    Episodes("episodes", "Episode"),
    Info("info", "Info"),
    Media("media", "Media"),
    Relations("relations", "Relations"),
    ThemeSongs("theme_songs", "Theme Songs")
}

private fun animeDetailRailItems(): List<MBGSideRailItem> = AnimeDetailSection.entries.map { section ->
    MBGSideRailItem(
        key = section.key,
        label = section.label
    )
}

@Composable
fun AnimeDetailScreen(
    malId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit = {},
    viewModel: AnimeDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedSection by remember { mutableStateOf(AnimeDetailSection.Overview) }

    LaunchedEffect(malId) {
        viewModel.fetchAnimeDetail(malId)
    }

    MBGSideRailScaffold(
        selectedRailKey = selectedSection.key,
        railItems = animeDetailRailItems(),
        onRailItemClick = { key ->
            selectedSection = AnimeDetailSection.entries.firstOrNull { it.key == key }
                ?: AnimeDetailSection.Overview
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                AnimeDetailUiState.Loading -> LoadingIndicator()
                is AnimeDetailUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = { viewModel.fetchAnimeDetail(malId) }
                )
                is AnimeDetailUiState.Success -> AnimeDetailContent(
                    anime = state.anime,
                    episodes = state.episodes,
                    relationPreviews = state.relationPreviews,
                    selectedSection = selectedSection,
                    onRelationEntryClick = { entry ->
                        if (entry.type.equals("anime", ignoreCase = true)) {
                            onNavigateToAnimeDetail(entry.mal_id)
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Detail ${entry.type.orUnknown()} belum diimplementasikan.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun AnimeDetailContent(
    anime: AnimeDetailData,
    episodes: List<AnimeEpisodeDto>,
    relationPreviews: Map<String, RelationEntryPreviewDto>,
    selectedSection: AnimeDetailSection,
    onRelationEntryClick: (AnimeRelationEntryDto) -> Unit
) {
    when (selectedSection) {
        AnimeDetailSection.Overview -> AnimeOverviewSection(anime)
        AnimeDetailSection.Synopsis -> AnimeSynopsisSection(anime)
        AnimeDetailSection.Episodes -> AnimeEpisodeListSection(anime, episodes)
        AnimeDetailSection.Info -> AnimeInfoSection(anime)
        AnimeDetailSection.Media -> AnimeMediaSection(anime)
        AnimeDetailSection.Relations -> AnimeRelationsSection(
            anime = anime,
            relationPreviews = relationPreviews,
            onEntryClick = onRelationEntryClick
        )
        AnimeDetailSection.ThemeSongs -> AnimeThemeSongsSection(anime)
    }
}

@Composable
private fun AnimeOverviewSection(anime: AnimeDetailData) {
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

        anime.title_english
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

        anime.title_japanese
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

        if (anime.title_synonyms.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = anime.title_synonyms.joinToString(),
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
                model = anime.images.jpg.large_image_url,
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
                    scoredBy = anime.scored_by
                )
                InfoCard(label = "Type", value = anime.type.orUnknown())
                InfoCard(label = "Episodes", value = anime.episodes.formatNumber())
                InfoCard(label = "Source", value = anime.source.orUnknown())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        DetailInfoRows(
            rows = listOf(
                "Status" to anime.status.orUnknown(),
                "Airing" to anime.airing.formatAiringStatus(),
                "Season" to formatSeason(anime),
                "Aired" to anime.aired?.string.orUnknown(),
                "Broadcast" to anime.broadcast?.string.orUnknown(),
                "Rating" to anime.rating.orUnknown(),
                "Duration" to anime.duration.orUnknown(),
                "Rank" to anime.rank.formatRank(),
                "Popularity" to anime.popularity.formatRank(),
                "Members" to anime.members.formatNumber(),
                "Favorites" to anime.favorites.formatNumber()
            )
        )
    }
}

@Composable
private fun AnimeSynopsisSection(anime: AnimeDetailData) {
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
    anime: AnimeDetailData,
    episodes: List<AnimeEpisodeDto>
) {
    val episodeCount = anime.episodes ?: 0
    val episodeItems = if (episodes.isNotEmpty()) {
        episodes.map { it.toEpisodeUiModel() }
    } else if (episodeCount > 0) {
        (1..episodeCount).map { number ->
            EpisodeUiModel(
                number = number,
                title = "Episode $number",
                metadata = "Detail episode belum dimuat dari Jikan.",
                filler = false,
                recap = false,
                watched = false
            )
        }
    } else {
        emptyList()
    }

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
                EpisodeRow(episode)
            }
        }
    }
}

private fun AnimeEpisodeDto.toEpisodeUiModel(): EpisodeUiModel {
    return EpisodeUiModel(
        number = mal_id,
        title = title.orUnknown(),
        metadata = listOfNotNull(
            title_romanji?.takeIf { it.isNotBlank() },
            title_japanese?.takeIf { it.isNotBlank() },
            aired?.takeIf { it.isNotBlank() }
        ).joinToString(" • ").ifBlank { "Tidak ada metadata episode." },
        filler = filler,
        recap = recap,
        watched = false
    )
}

@Composable
private fun AnimeInfoSection(anime: AnimeDetailData) {
    DetailSectionColumn(title = "Info") {
        DetailTextBlockIfNotEmpty(
            title = "Genres",
            body = anime.genres.joinNames { it.name }
        )

        DetailTextBlockIfNotEmpty(
            title = "Themes",
            body = anime.themes.joinNames { it.name }
        )

        DetailTextBlockIfNotEmpty(
            title = "Demographics",
            body = anime.demographics.joinNames { it.name }
        )

        DetailTextBlockIfNotEmpty(
            title = "Explicit Genres",
            body = anime.explicit_genres.joinNames { it.name }
        )

        DetailTextBlockIfNotEmpty(
            title = "Studios",
            body = anime.studios.joinNames { it.name }
        )

        DetailTextBlockIfNotEmpty(
            title = "Producers",
            body = anime.producers.joinNames { it.name }
        )

        DetailTextBlockIfNotEmpty(
            title = "Licensors",
            body = anime.licensors.joinNames { it.name }
        )
    }
}

@Composable
private fun AnimeMediaSection(anime: AnimeDetailData) {
    DetailSectionColumn(title = "Media") {
        val trailerUrl = anime.trailer?.url
            ?: anime.trailer?.embed_url
            ?: anime.trailer?.youtube_id?.let { "YouTube ID: $it" }

        DetailTextBlockIfNotEmpty(
            title = "Trailer",
            body = trailerUrl.orEmpty()
        )

        DetailLinkList(
            title = "Streaming",
            links = anime.streaming
        )

        DetailLinkList(
            title = "External",
            links = anime.external
        )
    }
}

@Composable
private fun AnimeRelationsSection(
    anime: AnimeDetailData,
    relationPreviews: Map<String, RelationEntryPreviewDto>,
    onEntryClick: (AnimeRelationEntryDto) -> Unit
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
                    relation.entry.forEach { entry ->
                        RelationEntryCard(
                            entry = entry,
                            preview = relationPreviews[entry.previewKey()],
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
    entry: AnimeRelationEntryDto,
    preview: RelationEntryPreviewDto?,
    onClick: () -> Unit
) {
    val type = entry.type.orUnknown().replaceFirstChar { it.uppercase() }
    val imageUrl = preview?.images?.jpg?.large_image_url

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

private fun AnimeRelationEntryDto.previewKey(): String {
    return "${type.orEmpty().lowercase()}:$mal_id"
}

@Composable
private fun AnimeThemeSongsSection(anime: AnimeDetailData) {
    DetailSectionColumn(title = "Theme Songs") {
        DetailTextBlockIfNotEmpty(
            title = "Openings",
            body = anime.theme?.openings.orEmpty().joinToString(separator = "\n")
        )

        DetailTextBlockIfNotEmpty(
            title = "Endings",
            body = anime.theme?.endings.orEmpty().joinToString(separator = "\n")
        )

        if (anime.theme?.openings.isNullOrEmpty() && anime.theme?.endings.isNullOrEmpty()) {
            DetailTextBlock(
                title = "Belum ada theme songs",
                body = "Opening dan ending belum tersedia dari Jikan."
            )
        }
    }
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
private fun DetailLinkList(
    title: String,
    links: List<AnimeExternalLinkDto>
) {
    if (links.isEmpty()) return

    DetailTextBlock(
        title = title,
        body = links.joinToString(separator = "\n\n") { link ->
            "${link.name}\n${link.url}"
        }
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun EpisodeRow(episode: EpisodeUiModel) {
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

                    WatchedIndicator(watched = episode.watched)
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
private fun WatchedIndicator(watched: Boolean) {
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
            .border(width = 1.dp, color = borderColor, shape = shape),
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

private fun formatSeason(anime: AnimeDetailData): String {
    return listOfNotNull(
        anime.season?.replaceFirstChar { it.uppercase() },
        anime.year?.toString()
    ).joinToString(" ").ifBlank { "Unknown" }
}

private fun <T> List<T>.joinNames(nameOf: (T) -> String): String {
    return joinToString { nameOf(it) }
}

private data class EpisodeUiModel(
    val number: Int,
    val title: String,
    val metadata: String,
    val filler: Boolean,
    val recap: Boolean,
    val watched: Boolean
)
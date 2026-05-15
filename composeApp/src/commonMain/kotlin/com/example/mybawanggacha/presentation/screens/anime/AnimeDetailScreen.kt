package com.example.mybawanggacha.presentation.screens.anime

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.mybawanggacha.data.remote.dto.AnimeExternalLinkDto
import com.example.mybawanggacha.data.remote.dto.AnimeRelationEntryDto
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailItem
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
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
    viewModel: AnimeDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                    selectedSection = selectedSection
                )
            }
        }
    }
}

@Composable
private fun AnimeDetailContent(
    anime: AnimeDetailData,
    selectedSection: AnimeDetailSection
) {
    when (selectedSection) {
        AnimeDetailSection.Overview -> AnimeOverviewSection(anime)
        AnimeDetailSection.Synopsis -> AnimeSynopsisSection(anime)
        AnimeDetailSection.Episodes -> AnimeEpisodeListSection(anime)
        AnimeDetailSection.Info -> AnimeInfoSection(anime)
        AnimeDetailSection.Media -> AnimeMediaSection(anime)
        AnimeDetailSection.Relations -> AnimeRelationsSection(anime)
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
private fun AnimeEpisodeListSection(anime: AnimeDetailData) {
    val episodeCount = anime.episodes ?: 0
    val episodes = if (episodeCount > 0) {
        (1..episodeCount).map { number ->
            EpisodeUiModel(
                number = number,
                title = "Episode $number",
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
                    text = "Status watched/unwatched masih UI skeleton.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (episodes.isEmpty()) {
            item {
                DetailTextBlock(
                    title = "Episode tidak tersedia",
                    body = "Jumlah episode untuk anime ini tidak tersedia pada Jikan."
                )
            }
        } else {
            items(
                items = episodes,
                key = { it.number }
            ) { episode ->
                EpisodeRow(episode)
            }
        }
    }
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
private fun AnimeRelationsSection(anime: AnimeDetailData) {
    DetailSectionColumn(title = "Relations") {
        if (anime.relations.isEmpty()) {
            DetailTextBlock(
                title = "Belum ada relasi",
                body = "Jikan belum menyediakan relasi untuk anime ini."
            )
        } else {
            anime.relations.forEachIndexed { index, relation ->
                DetailTextBlock(
                    title = relation.relation,
                    body = relation.entry.joinRelationEntries()
                )

                if (index != anime.relations.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
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
                Text(
                    text = episode.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (episode.watched) "Ditonton" else "Belum ditonton",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (episode.watched) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.64f)
        )
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

private fun List<AnimeRelationEntryDto>.joinRelationEntries(): String {
    return if (isEmpty()) {
        "Belum ada data entry."
    } else {
        joinToString(separator = "\n") { entry ->
            listOfNotNull(
                entry.name,
                entry.type?.let { "($it)" }
            ).joinToString(" ")
        }
    }
}

private data class EpisodeUiModel(
    val number: Int,
    val title: String,
    val watched: Boolean
)
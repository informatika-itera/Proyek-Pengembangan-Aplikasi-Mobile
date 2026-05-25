package com.example.mybawanggacha.presentation.screens.discover

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mybawanggacha.domain.anime.model.AnimeSummary
import com.example.mybawanggacha.domain.anime.model.RecentAnimeEpisode
import com.example.mybawanggacha.domain.manga.model.MangaSummary
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailSettingsButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import com.example.mybawanggacha.presentation.components.PullRefreshContainer
import com.example.mybawanggacha.presentation.components.SectionHeader
import com.example.mybawanggacha.presentation.screens.anime.home.AnimeHomeUiState
import com.example.mybawanggacha.presentation.screens.anime.home.AnimeHomeViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToAnimeDetail: (Int) -> Unit,
    onNavigateToMangaDetail: (Int) -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: AnimeHomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.Home,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> Unit
                MBGMainRailKey.Search -> onNavigateToSearch()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
            }
        },
        topAction = {
            MBGRailSettingsButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    ) {
        PullRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    AnimeHomeUiState.Loading -> LoadingIndicator()
                    is AnimeHomeUiState.Error -> ErrorState(
                        message = state.message,
                        onRetry = viewModel::refresh
                    )

                    is AnimeHomeUiState.Success -> HomeDiscoveryContent(
                        recommendations = state.recommendations,
                        randomAnime = state.randomAnime,
                        randomManga = state.randomManga,
                        recentEpisodes = state.recentEpisodes,
                        onAnimeClick = onNavigateToAnimeDetail,
                        onMangaClick = onNavigateToMangaDetail,
                        onOpenAnimeList = onNavigateToAnimeList
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeDiscoveryContent(
    recommendations: List<AnimeSummary>,
    randomAnime: List<AnimeSummary>,
    randomManga: List<MangaSummary>,
    recentEpisodes: List<RecentAnimeEpisode>,
    onAnimeClick: (Int) -> Unit,
    onMangaClick: (Int) -> Unit,
    onOpenAnimeList: () -> Unit
) {
    if (recommendations.isEmpty() && randomAnime.isEmpty() && randomManga.isEmpty() && recentEpisodes.isEmpty()) {
        EmptyState(
            title = "Discovery kosong",
            message = "Jikan belum memberikan data discovery. Coba refresh nanti."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 4.dp, top = 32.dp, end = 18.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Discovery anime dan manga",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (recentEpisodes.isNotEmpty()) {
            item {
                RecentEpisodesSection(
                    episodes = recentEpisodes.take(12),
                    onAnimeClick = onAnimeClick
                )
            }
        }

        if (randomAnime.isNotEmpty() || randomManga.isNotEmpty()) {
            item {
                RandomPickSection(
                    randomAnime = randomAnime,
                    randomManga = randomManga,
                    onAnimeClick = onAnimeClick,
                    onMangaClick = onMangaClick
                )
            }
        }

        if (recommendations.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Anime Overview",
                    onViewAllClick = onOpenAnimeList,
                    modifier = Modifier.fillMaxWidth()
                )

                AnimeOverviewPagedCarousel(
                    recommendations = recommendations.take(24),
                    onAnimeClick = onAnimeClick
                )
            }
        }
    }
}

@Composable
private fun AnimeOverviewPagedCarousel(
    recommendations: List<AnimeSummary>,
    onAnimeClick: (Int) -> Unit
) {
    val pages = recommendations.chunked(4)

    AutoSlidingRow(
        items = pages,
        key = { page -> page.joinToString { it.malId.toString() } },
        autoSlideMillis = 5_500L
    ) { pageItems ->
        Column(
            modifier = Modifier.width(286.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            pageItems.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { anime ->
                        HomeMiniMediaCard(
                            title = anime.title,
                            imageUrl = anime.imageUrl,
                            label = anime.score?.let { "★ ${it.toString().take(4)}" } ?: "Anime",
                            onClick = { onAnimeClick(anime.malId) },
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
}

@Composable
private fun RandomPickSection(
    randomAnime: List<AnimeSummary>,
    randomManga: List<MangaSummary>,
    onAnimeClick: (Int) -> Unit,
    onMangaClick: (Int) -> Unit
) {
    HomeSectionTitle(
        title = "Random Pick",
        subtitle = "Pilihan random untukmu"
    )

    val items = buildList<HomeRandomPick> {
        randomAnime.forEach { anime -> add(HomeRandomPick.Anime(anime)) }
        randomManga.forEach { manga -> add(HomeRandomPick.Manga(manga)) }
    }

    AutoSlidingRow(
        items = items,
        key = { it.key },
        autoSlideMillis = 4_800L
    ) { item ->
        when (item) {
            is HomeRandomPick.Anime -> FeaturedPickCard(
                title = item.anime.title,
                imageUrl = item.anime.imageUrl,
                mediaLabel = "Anime",
                metadata = item.anime.score?.let { "Score ★ ${it.toString().take(4)}" } ?: "Random anime",
                onClick = { onAnimeClick(item.anime.malId) }
            )

            is HomeRandomPick.Manga -> FeaturedPickCard(
                title = item.manga.title,
                imageUrl = item.manga.imageUrl,
                mediaLabel = item.manga.type ?: "Manga",
                metadata = item.manga.score?.let { "Score ★ ${it.toString().take(4)}" } ?: "Random manga",
                onClick = { onMangaClick(item.manga.malId) }
            )
        }
    }
}

@Composable
private fun RecentEpisodesSection(
    episodes: List<RecentAnimeEpisode>,
    onAnimeClick: (Int) -> Unit
) {
    HomeSectionTitle(
        title = "Recent Episodes",
        subtitle = "Episode yang baru saja update"
    )

    AutoSlidingRow(
        items = episodes,
        key = { episode -> "${episode.animeMalId}:${episode.episodeMalId ?: episode.episodeTitle}" },
        autoSlideMillis = 4_200L
    ) { episode ->
        RecentEpisodeCard(
            episode = episode,
            onClick = { onAnimeClick(episode.animeMalId) }
        )
    }
}

@Composable
private fun <T> AutoSlidingRow(
    items: List<T>,
    key: (T) -> Any,
    autoSlideMillis: Long,
    itemContent: @Composable (T) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(items.size) {
        if (items.size <= 1) return@LaunchedEffect

        while (true) {
            delay(autoSlideMillis)
            val nextIndex = (listState.firstVisibleItemIndex + 1) % items.size
            listState.animateScrollToItem(nextIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(end = 20.dp)
    ) {
        items(
            items = items,
            key = key
        ) { item ->
            itemContent(item)
        }
    }
}

@Composable
private fun FeaturedPickCard(
    title: String,
    imageUrl: String?,
    mediaLabel: String,
    metadata: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(282.dp)
            .height(158.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterBox(
                title = title,
                imageUrl = imageUrl,
                width = 84.dp,
                height = 120.dp
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = mediaLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = metadata,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RecentEpisodeCard(
    episode: RecentAnimeEpisode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(268.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterBox(
                title = episode.animeTitle,
                imageUrl = episode.animeImageUrl,
                width = 64.dp,
                height = 90.dp
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.episodeTitle?.takeIf { it.isNotBlank() } ?: "Episode baru",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = episode.animeTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (episode.premium) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Premium",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeMiniMediaCard(
    title: String,
    imageUrl: String?,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(146.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "A",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PosterBox(
    title: String,
    imageUrl: String?,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = title.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HomeSectionTitle(
    title: String,
    subtitle: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

private sealed interface HomeRandomPick {
    val key: String

    data class Anime(val anime: AnimeSummary) : HomeRandomPick {
        override val key: String = "anime:${anime.malId}"
    }

    data class Manga(val manga: MangaSummary) : HomeRandomPick {
        override val key: String = "manga:${manga.malId}"
    }
}

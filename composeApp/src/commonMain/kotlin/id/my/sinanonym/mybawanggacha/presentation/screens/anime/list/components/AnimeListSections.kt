package id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSeasonPeriod
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSummary
import id.my.sinanonym.mybawanggacha.presentation.components.ScreenHeader
import id.my.sinanonym.mybawanggacha.presentation.components.media.MediaPosterCard
import id.my.sinanonym.mybawanggacha.presentation.components.media.MediaPosterSkeletonCard
import id.my.sinanonym.mybawanggacha.presentation.components.EmptyState
import id.my.sinanonym.mybawanggacha.presentation.components.ErrorState
import id.my.sinanonym.mybawanggacha.presentation.components.LoadingIndicator
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.AnimeListTab
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.AnimeListUiState

@Composable
fun ListHeader() {
    ScreenHeader(
        icon = Icons.Default.SmartDisplay,
        title = "Anime List",
        subtitle = "Katalog anime dari Jikan"
    )
}

@Composable
fun ListTabRow(
    selectedTab: AnimeListTab,
    onTabSelected: (AnimeListTab) -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndex = AnimeListTab.entries.indexOf(selectedTab).coerceAtLeast(0)

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 20.dp)
    ) {
        items(
            items = AnimeListTab.entries,
            key = { it.name }
        ) { tab ->
            FilterChip(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                label = {
                    Text(
                        text = tab.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
fun SeasonArchiveRow(
    seasonPeriods: List<AnimeSeasonPeriod>,
    selectedSeasonPeriod: AnimeSeasonPeriod,
    onSeasonSelected: (AnimeSeasonPeriod) -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndex = seasonPeriods.indexOf(selectedSeasonPeriod)

    LaunchedEffect(selectedIndex, seasonPeriods.size) {
        if (selectedIndex >= 0) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 20.dp)
    ) {
        items(
            items = seasonPeriods,
            key = { period -> period.sortValue }
        ) { period ->
            FilterChip(
                selected = period == selectedSeasonPeriod,
                onClick = { onSeasonSelected(period) },
                label = {
                    Text(
                        text = period.displayLabel,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
fun ListContent(
    uiState: AnimeListUiState,
    selectedTab: AnimeListTab,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            AnimeListUiState.Loading -> ListSkeleton()
            is AnimeListUiState.Error -> ErrorState(
                message = uiState.message,
                onRetry = onRetry
            )
            is AnimeListUiState.Success -> Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                PosterGrid(
                    anime = uiState.anime,
                    showTopAnimeBadges = selectedTab == AnimeListTab.TopAnime,
                    canLoadMore = uiState.canLoadMore,
                    isLoadingMore = uiState.isLoadingMore,
                    onLoadMore = onLoadMore,
                    onAnimeClick = onAnimeClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
private fun ListSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        SkeletonLine(width = 174.dp, height = 24.dp)
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonLine(width = 242.dp, height = 14.dp)
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 132.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = 8,
                key = { index -> "anime_skeleton_$index" },
                contentType = { "anime_poster_skeleton" }
            ) {
                MediaPosterSkeletonCard()
            }
        }
    }
}

@Composable
private fun SkeletonLine(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f))
    )
}

@Composable
fun PosterGrid(
    anime: List<AnimeSummary>,
    showTopAnimeBadges: Boolean,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (anime.isEmpty()) {
        EmptyState(
            title = "Data anime kosong",
            message = "Jikan belum memberikan data untuk kategori ini. Coba refresh nanti.",
            modifier = modifier
        )
        return
    }

    val gridState = rememberLazyGridState()
    val shouldLoadMore by remember(gridState, anime.size, canLoadMore, isLoadingMore) {
        derivedStateOf {
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            canLoadMore &&
                !isLoadingMore &&
                lastVisibleIndex >= anime.lastIndex - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
        state = gridState,
        contentPadding = PaddingValues(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = anime,
            key = { it.malId },
            contentType = { "anime_poster" }
        ) { item ->
            MediaPosterCard(
                title = item.title,
                imageUrl = item.imageUrl.orEmpty(),
                leadingBadge = item.takeIf { showTopAnimeBadges }?.rankLabel(),
                trailingBadge = item.takeIf { showTopAnimeBadges }?.scoreLabel(),
                onClick = { onAnimeClick(item.malId) }
            )
        }

        if (isLoadingMore) {
            item(
                span = { GridItemSpan(maxLineSpan) },
                contentType = "anime_loading_more"
            ) {
                LoadingMoreRow()
            }
        }
    }
}

@Composable
private fun LoadingMoreRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Memuat lagi...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private const val LOAD_MORE_THRESHOLD = 6

private fun AnimeSummary.rankLabel(): String? {
    return rank?.let { "#$it" }
}

private fun AnimeSummary.scoreLabel(): String? {
    return score?.let { "★ ${it.toString().take(4)}" }
}

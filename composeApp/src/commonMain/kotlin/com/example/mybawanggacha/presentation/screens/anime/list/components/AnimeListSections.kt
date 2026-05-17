package com.example.mybawanggacha.presentation.screens.anime.list.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mybawanggacha.domain.anime.model.AnimeSeasonPeriod
import com.example.mybawanggacha.domain.anime.model.AnimeSummary
import com.example.mybawanggacha.presentation.components.media.AnimePosterCard
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.screens.anime.list.AnimeListTab
import com.example.mybawanggacha.presentation.screens.anime.list.AnimeListUiState

@Composable
fun AnimeListHeader() {
    Text(
        text = "Anime List",
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = "Katalog anime dari Jikan",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun AnimeListTabRow(
    selectedTab: AnimeListTab,
    onTabSelected: (AnimeListTab) -> Unit
) {
    LazyRow(
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
fun AnimeSeasonArchiveRow(
    seasonPeriods: List<AnimeSeasonPeriod>,
    selectedSeasonPeriod: AnimeSeasonPeriod,
    onSeasonSelected: (AnimeSeasonPeriod) -> Unit
) {
    LazyRow(
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
fun AnimeListContent(
    uiState: AnimeListUiState,
    selectedTab: AnimeListTab,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            AnimeListUiState.Loading -> LoadingIndicator()
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

                AnimeGrid(
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
fun AnimeGrid(
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
            key = { it.malId }
        ) { item ->
            AnimePosterCard(
                title = item.title,
                imageUrl = item.imageUrl.orEmpty(),
                leadingBadge = item.takeIf { showTopAnimeBadges }?.rankLabel(),
                trailingBadge = item.takeIf { showTopAnimeBadges }?.scoreLabel(),
                onClick = { onAnimeClick(item.malId) }
            )
        }

        if (isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AnimeListLoadingMoreRow()
            }
        }
    }
}

@Composable
private fun AnimeListLoadingMoreRow() {
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

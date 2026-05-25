package com.example.mybawanggacha.presentation.screens.manga.list.components

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
import com.example.mybawanggacha.domain.manga.model.MangaSummary
import com.example.mybawanggacha.presentation.screens.manga.list.MangaListTab
import com.example.mybawanggacha.presentation.screens.manga.list.MangaListUiState
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.media.MediaPosterCard
import com.example.mybawanggacha.presentation.components.media.MediaPosterSkeletonCard

@Composable
fun MangaListHeader() {
    Text(
        text = "Manga List",
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = "Katalog manga dari Jikan",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun MangaListTabRow(
    selectedTab: MangaListTab,
    onTabSelected: (MangaListTab) -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndex = MangaListTab.entries.indexOf(selectedTab).coerceAtLeast(0)

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
            items = MangaListTab.entries,
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
fun MangaListContent(
    uiState: MangaListUiState,
    selectedTab: MangaListTab,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onMangaClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            MangaListUiState.Loading -> MangaListSkeleton()
            is MangaListUiState.Error -> ErrorState(
                message = uiState.message,
                onRetry = onRetry
            )
            is MangaListUiState.Success -> Column(
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

                MangaGrid(
                    manga = uiState.manga,
                    showBadges = selectedTab != MangaListTab.Recommendations,
                    canLoadMore = uiState.canLoadMore,
                    isLoadingMore = uiState.isLoadingMore,
                    onLoadMore = onLoadMore,
                    onMangaClick = onMangaClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
private fun MangaListSkeleton() {
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
                key = { index -> "manga_skeleton_$index" },
                contentType = { "manga_poster_skeleton" }
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
private fun MangaGrid(
    manga: List<MangaSummary>,
    showBadges: Boolean,
    canLoadMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onMangaClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (manga.isEmpty()) {
        EmptyState(
            title = "Data manga kosong",
            message = "Jikan belum memberikan data untuk kategori ini. Coba refresh nanti.",
            modifier = modifier
        )
        return
    }

    val gridState = rememberLazyGridState()
    val shouldLoadMore by remember(gridState, manga.size, canLoadMore, isLoadingMore) {
        derivedStateOf {
            val lastVisibleIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            canLoadMore &&
                    !isLoadingMore &&
                    lastVisibleIndex >= manga.lastIndex - LOAD_MORE_THRESHOLD
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
            items = manga,
            key = { it.malId },
            contentType = { "manga_poster" }
        ) { item ->
            MediaPosterCard(
                title = item.title,
                imageUrl = item.imageUrl.orEmpty(),
                leadingBadge = item.takeIf { showBadges }?.rankLabel(),
                trailingBadge = item.takeIf { showBadges }?.scoreLabel(),
                onClick = { onMangaClick(item.malId) }
            )
        }

        if (isLoadingMore) {
            item(
                span = { GridItemSpan(maxLineSpan) },
                contentType = "manga_loading_more"
            ) {
                MangaListLoadingMoreRow()
            }
        }
    }
}

@Composable
private fun MangaListLoadingMoreRow() {
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

private fun MangaSummary.rankLabel(): String? {
    return rank?.let { "#$it" }
}

private fun MangaSummary.scoreLabel(): String? {
    return score?.let { "★ ${it.toString().take(4)}" }
}
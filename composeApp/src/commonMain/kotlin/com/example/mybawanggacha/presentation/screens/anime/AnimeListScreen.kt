package com.example.mybawanggacha.presentation.screens.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mybawanggacha.domain.model.AnimeSeasonPeriod
import com.example.mybawanggacha.domain.model.AnimeSummary
import com.example.mybawanggacha.presentation.components.AnimePosterCard
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnimeListScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit,
    viewModel: AnimeListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val seasonPeriods by viewModel.seasonPeriods.collectAsStateWithLifecycle()
    val selectedSeasonPeriod by viewModel.selectedSeasonPeriod.collectAsStateWithLifecycle()

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.AnimeList,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.AnimeList -> Unit
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
            }
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, top = 32.dp, end = 18.dp)
        ) {
            Text(
                text = "Anime List",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Katalog anime dari Jikan. Status pribadi tetap dikelola di My Library.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            AnimeListTabRow(
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab
            )

            if (selectedTab == AnimeListTab.SeasonArchive) {
                Spacer(modifier = Modifier.height(8.dp))

                AnimeSeasonArchiveRow(
                    seasonPeriods = seasonPeriods,
                    selectedSeasonPeriod = selectedSeasonPeriod,
                    onSeasonSelected = viewModel::selectSeasonPeriod
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimeListContent(
                uiState = uiState,
                onRetry = viewModel::refresh,
                onAnimeClick = onNavigateToAnimeDetail
            )
        }
    }
}

@Composable
private fun AnimeListTabRow(
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
private fun AnimeSeasonArchiveRow(
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
private fun AnimeListContent(
    uiState: AnimeListUiState,
    onRetry: () -> Unit,
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
                    onAnimeClick = onAnimeClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AnimeGrid(
    anime: List<AnimeSummary>,
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

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 132.dp),
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
                onClick = { onAnimeClick(item.malId) }
            )
        }
    }
}

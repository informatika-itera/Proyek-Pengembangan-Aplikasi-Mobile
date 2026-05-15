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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mybawanggacha.data.remote.dto.AnimeEntry
import com.example.mybawanggacha.presentation.components.AnimePosterCard
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import org.koin.compose.viewmodel.koinViewModel

private enum class AnimeListTab(val label: String) {
    Recommendations("Rekomendasi"),
    WatchList("Watch List"),
    Watched("Watched List"),
    NotPlanned("Not Planned List")
}

@Composable
fun AnimeListScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit,
    viewModel: AnimeHomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(AnimeListTab.Recommendations) }

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.AnimeList,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.AnimeList -> Unit
            }
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, top = 48.dp, end = 24.dp)
        ) {
            Text(
                text = "Anime List",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            AnimeListTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    AnimeListTab.Recommendations -> RecommendationListContent(
                        uiState = uiState,
                        onRetry = viewModel::refresh,
                        onAnimeClick = onNavigateToAnimeDetail
                    )
                    AnimeListTab.WatchList -> EmptyState(
                        title = "Watch List masih kosong",
                        message = "Storage dan aksi tambah ke watch list belum dihubungkan."
                    )
                    AnimeListTab.Watched -> EmptyState(
                        title = "Watched List masih kosong",
                        message = "Progress episode akan masuk ke bagian ini nanti."
                    )
                    AnimeListTab.NotPlanned -> EmptyState(
                        title = "Not Planned List masih kosong",
                        message = "Status not planned akan dihubungkan setelah model lokal dibuat."
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeListTabRow(
    selectedTab: AnimeListTab,
    onTabSelected: (AnimeListTab) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimeListTab.entries.forEach { tab ->
            FilterChip(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                label = { Text(tab.label) }
            )
        }
    }
}

@Composable
private fun RecommendationListContent(
    uiState: AnimeHomeUiState,
    onRetry: () -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    when (uiState) {
        AnimeHomeUiState.Loading -> LoadingIndicator()
        is AnimeHomeUiState.Error -> ErrorState(
            message = uiState.message,
            onRetry = onRetry
        )
        is AnimeHomeUiState.Success -> AnimeGrid(
            anime = uiState.recommendations,
            onAnimeClick = onAnimeClick
        )
    }
}

@Composable
private fun AnimeGrid(
    anime: List<AnimeEntry>,
    onAnimeClick: (Int) -> Unit
) {
    if (anime.isEmpty()) {
        EmptyState(
            title = "Belum ada rekomendasi",
            message = "Jikan belum mengembalikan data anime."
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 148.dp),
        contentPadding = PaddingValues(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = anime,
            key = { it.mal_id }
        ) { item ->
            AnimePosterCard(
                title = item.title,
                imageUrl = item.images.jpg.large_image_url,
                onClick = { onAnimeClick(item.mal_id) }
            )
        }
    }
}

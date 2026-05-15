package com.example.mybawanggacha.presentation.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mybawanggacha.data.remote.dto.AnimeEntry
import com.example.mybawanggacha.presentation.components.AnimeOverviewCard
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import com.example.mybawanggacha.presentation.components.MBGRailSettingsButton
import com.example.mybawanggacha.presentation.components.SectionHeader
import com.example.mybawanggacha.presentation.screens.anime.AnimeHomeUiState
import com.example.mybawanggacha.presentation.screens.anime.AnimeHomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToAnimeDetail: (Int) -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: AnimeHomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.Home,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> Unit
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
            }
        },
        topAction = {
            MBGRailSettingsButton(onClick = onNavigateToSettings)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                AnimeHomeUiState.Loading -> LoadingIndicator()
                is AnimeHomeUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = viewModel::refresh
                )
                is AnimeHomeUiState.Success -> HomeAnimeOverview(
                    recommendations = state.recommendations,
                    onAnimeClick = onNavigateToAnimeDetail,
                    onOpenAnimeList = onNavigateToAnimeList
                )
            }
        }
    }
}

@Composable
private fun HomeAnimeOverview(
    recommendations: List<AnimeEntry>,
    onAnimeClick: (Int) -> Unit,
    onOpenAnimeList: () -> Unit
) {
    if (recommendations.isEmpty()) {
        EmptyState(
            title = "Belum ada anime",
            message = "Rekomendasi dari Jikan belum tersedia."
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 4.dp, top = 32.dp, end = 18.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                    text = "Ringkasan anime dari rekomendasi Jikan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            SectionHeader(
                title = "Anime Overview",
                onViewAllClick = onOpenAnimeList,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(
            items = recommendations.take(8).chunked(2),
            key = { row -> row.joinToString { it.mal_id.toString() } }
        ) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { anime ->
                    AnimeOverviewCard(
                        title = anime.title,
                        imageUrl = anime.images.jpg.large_image_url,
                        onClick = { onAnimeClick(anime.mal_id) },
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

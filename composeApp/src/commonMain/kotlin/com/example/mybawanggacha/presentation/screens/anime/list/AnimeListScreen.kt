package com.example.mybawanggacha.presentation.screens.anime.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import com.example.mybawanggacha.presentation.screens.anime.list.components.AnimeListContent
import com.example.mybawanggacha.presentation.screens.anime.list.components.AnimeListHeader
import com.example.mybawanggacha.presentation.screens.anime.list.components.AnimeListTabRow
import com.example.mybawanggacha.presentation.screens.anime.list.components.AnimeSeasonArchiveRow
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
            AnimeListHeader()

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
                selectedTab = selectedTab,
                onRetry = viewModel::refresh,
                onLoadMore = viewModel::loadNextPage,
                onAnimeClick = onNavigateToAnimeDetail
            )
        }
    }
}
package com.example.mybawanggacha.presentation.screens.manga.list

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
import com.example.mybawanggacha.presentation.components.PullRefreshContainer
import com.example.mybawanggacha.presentation.screens.manga.list.components.MangaListContent
import com.example.mybawanggacha.presentation.screens.manga.list.components.MangaListHeader
import com.example.mybawanggacha.presentation.screens.manga.list.components.MangaListTabRow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MangaListScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToMangaDetail: (Int) -> Unit,
    viewModel: MangaListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isRefreshing = (uiState as? MangaListUiState.Success)?.isRefreshing == true

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.MangaList,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.Search -> onNavigateToSearch()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> Unit
            }
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        PullRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 4.dp, top = 32.dp, end = 18.dp)
            ) {
                MangaListHeader()

                Spacer(modifier = Modifier.height(14.dp))

                MangaListTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = viewModel::selectTab
                )

                Spacer(modifier = Modifier.height(16.dp))

                MangaListContent(
                    uiState = uiState,
                    selectedTab = selectedTab,
                    onRetry = viewModel::refresh,
                    onLoadMore = viewModel::loadNextPage,
                    onMangaClick = onNavigateToMangaDetail
                )
            }
        }
    }
}
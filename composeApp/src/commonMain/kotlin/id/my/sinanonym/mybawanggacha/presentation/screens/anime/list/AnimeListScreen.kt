package id.my.sinanonym.mybawanggacha.presentation.screens.anime.list

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
import id.my.sinanonym.mybawanggacha.presentation.components.MBGMainRailKey
import id.my.sinanonym.mybawanggacha.presentation.components.MBGRailBackButton
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailScaffold
import id.my.sinanonym.mybawanggacha.presentation.components.PullRefreshContainer
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.components.ListContent
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.components.ListHeader
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.components.ListTabRow
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.list.components.SeasonArchiveRow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnimeListScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit,
    viewModel: AnimeListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val seasonPeriods by viewModel.seasonPeriods.collectAsStateWithLifecycle()
    val selectedSeasonPeriod by viewModel.selectedSeasonPeriod.collectAsStateWithLifecycle()
    val isRefreshing = (uiState as? AnimeListUiState.Success)?.isRefreshing == true

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.AnimeList,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.Search -> onNavigateToSearch()
                MBGMainRailKey.MyLibrary -> onNavigateToMyLibrary()
                MBGMainRailKey.Gacha -> onNavigateToGacha()
                MBGMainRailKey.AnimeList -> Unit
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
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
                ListHeader()

                Spacer(modifier = Modifier.height(14.dp))

                ListTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = viewModel::selectTab
                )

                if (selectedTab == AnimeListTab.SeasonArchive) {
                    Spacer(modifier = Modifier.height(8.dp))

                    SeasonArchiveRow(
                        seasonPeriods = seasonPeriods,
                        selectedSeasonPeriod = selectedSeasonPeriod,
                        onSeasonSelected = viewModel::selectSeasonPeriod
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ListContent(
                    uiState = uiState,
                    selectedTab = selectedTab,
                    onRetry = viewModel::refresh,
                    onLoadMore = viewModel::loadNextPage,
                    onAnimeClick = onNavigateToAnimeDetail
                )
            }
        }
    }
}
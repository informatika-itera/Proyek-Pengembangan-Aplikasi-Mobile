package id.my.sinanonym.mybawanggacha.presentation.screens.discover

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import id.my.sinanonym.mybawanggacha.domain.anime.model.AnimeSummary
import id.my.sinanonym.mybawanggacha.domain.anime.model.RecentAnimeEpisode
import id.my.sinanonym.mybawanggacha.domain.manga.model.MangaSummary
import id.my.sinanonym.mybawanggacha.presentation.components.EmptyState
import id.my.sinanonym.mybawanggacha.presentation.components.ErrorState
import id.my.sinanonym.mybawanggacha.presentation.components.LoadingIndicator
import id.my.sinanonym.mybawanggacha.presentation.components.MBGMainRailKey
import id.my.sinanonym.mybawanggacha.presentation.components.MBGRailSettingsButton
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailScaffold
import id.my.sinanonym.mybawanggacha.presentation.components.PullRefreshContainer
import id.my.sinanonym.mybawanggacha.presentation.components.ScreenHeader
import id.my.sinanonym.mybawanggacha.presentation.components.SectionHeader
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.home.AnimeHomeUiState
import id.my.sinanonym.mybawanggacha.presentation.screens.anime.home.AnimeHomeViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.discover.components.HomeDiscoveryContent

@Composable
fun HomeScreen(
    onNavigateToAnimeDetail: (Int) -> Unit,
    onNavigateToMangaDetail: (Int) -> Unit,
    onNavigateToMyLibrary: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToGacha: () -> Unit,
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
                MBGMainRailKey.Gacha -> onNavigateToGacha()
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

package id.my.sinanonym.mybawanggacha.presentation.screens.library.list

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryEntry
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryStatus
import id.my.sinanonym.mybawanggacha.domain.library.model.MediaType
import id.my.sinanonym.mybawanggacha.presentation.components.ConfirmationDialog
import id.my.sinanonym.mybawanggacha.presentation.components.EmptyState
import id.my.sinanonym.mybawanggacha.presentation.components.ErrorState
import id.my.sinanonym.mybawanggacha.presentation.components.LoadingIndicator
import id.my.sinanonym.mybawanggacha.presentation.components.MBGMainRailKey
import id.my.sinanonym.mybawanggacha.presentation.components.MBGRailBackButton
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailScaffold
import id.my.sinanonym.mybawanggacha.presentation.components.PullRefreshContainer
import id.my.sinanonym.mybawanggacha.presentation.components.ScreenHeader
import id.my.sinanonym.mybawanggacha.presentation.screens.library.LibraryUiState
import org.koin.compose.viewmodel.koinViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.library.list.components.LibraryEmptyState
import id.my.sinanonym.mybawanggacha.presentation.screens.library.list.components.LibraryEntryList
import id.my.sinanonym.mybawanggacha.presentation.screens.library.list.components.LibraryListSkeleton
import id.my.sinanonym.mybawanggacha.presentation.screens.library.list.components.LibraryStatusFilterRow

@Composable
fun MyListScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToGacha: () -> Unit,
    onNavigateToDetail: (Int, MediaType) -> Unit,
    onEditEntry: (LibraryEntry) -> Unit,
    viewModel: LibraryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<LibraryEntry?>(null) }

    deleteTarget?.let { entry ->
        ConfirmationDialog(
            title = "Hapus dari My Library?",
            message = "${entry.title} akan dihapus dari daftar lokal.",
            confirmText = "Hapus",
            onConfirm = {
                viewModel.deleteEntry(entry)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null },
            isDestructive = true
        )
    }

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.MyLibrary,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.Search -> onNavigateToSearch()
                MBGMainRailKey.MyLibrary -> Unit
                MBGMainRailKey.Gacha -> onNavigateToGacha()
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
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
                ScreenHeader(
                    icon = Icons.Default.CollectionsBookmark,
                    title = "My Library",
                    subtitle = "Daftar yang kamu simpan"
                )

                Spacer(modifier = Modifier.height(14.dp))

                LibraryStatusFilterRow(
                    selectedStatus = selectedStatus,
                    onStatusSelected = viewModel::selectStatus
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxSize()) {
                    when (val state = uiState) {
                        LibraryUiState.Loading -> LibraryListSkeleton()
                        is LibraryUiState.Empty -> LibraryEmptyState(selectedStatus = state.selectedStatus)
                        is LibraryUiState.Error -> ErrorState(message = state.message)
                        is LibraryUiState.Success -> LibraryEntryList(
                            entries = state.entries,
                            onOpenEntry = onNavigateToDetail,
                            onEditEntry = onEditEntry,
                            onDeleteEntry = { deleteTarget = it }
                        )
                    }
                }
            }
        }
    }
}
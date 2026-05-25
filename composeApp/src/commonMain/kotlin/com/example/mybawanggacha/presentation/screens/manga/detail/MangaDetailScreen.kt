package com.example.mybawanggacha.presentation.screens.manga.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mybawanggacha.domain.manga.model.MangaDetail
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import com.example.mybawanggacha.presentation.components.PullRefreshContainer
import com.example.mybawanggacha.presentation.screens.manga.detail.components.MangaDetailContent
import com.example.mybawanggacha.presentation.screens.manga.detail.components.MangaDetailSection
import com.example.mybawanggacha.presentation.screens.manga.detail.components.mangaDetailRailItems
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MangaDetailScreen(
    malId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit = {},
    onNavigateToMangaDetail: (Int) -> Unit = {},
    onNavigateToLibraryEditor: (MangaDetail, Long?) -> Unit = { _, _ -> },
    viewModel: MangaDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing = (uiState as? MangaDetailUiState.Success)?.isRefreshing == true
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedSection by remember { mutableStateOf(MangaDetailSection.Overview) }

    LaunchedEffect(malId) {
        viewModel.fetchMangaDetail(malId)
    }

    MBGSideRailScaffold(
        selectedRailKey = selectedSection.key,
        railItems = mangaDetailRailItems(),
        onRailItemClick = { key ->
            selectedSection = MangaDetailSection.entries.firstOrNull { it.key == key }
                ?: MangaDetailSection.Overview
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        PullRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshMangaDetail(malId) },
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    MangaDetailUiState.Loading -> LoadingIndicator()
                    is MangaDetailUiState.Error -> ErrorState(
                        message = state.message,
                        onRetry = { viewModel.fetchMangaDetail(malId) }
                    )
                    is MangaDetailUiState.Success -> {
                        MangaDetailContent(
                            manga = state.manga,
                            selectedSection = selectedSection,
                            onRelationEntryClick = { entry ->
                                when {
                                    entry.type.equals("anime", ignoreCase = true) -> {
                                        onNavigateToAnimeDetail(entry.malId)
                                    }
                                    entry.type.equals("manga", ignoreCase = true) -> {
                                        onNavigateToMangaDetail(entry.malId)
                                    }
                                    else -> {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Detail ${entry.type?.takeIf { it.isNotBlank() } ?: "Unknown"} belum didukung.",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                }
                            }
                        )

                        val isInLibrary = state.libraryEntryId != null

                        FloatingActionButton(
                            onClick = { onNavigateToLibraryEditor(state.manga, state.libraryEntryId) },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 80.dp)
                        ) {
                            Icon(
                                imageVector = if (isInLibrary) Icons.Default.Edit else Icons.Default.Add,
                                contentDescription = if (isInLibrary) {
                                    "Edit My Library"
                                } else {
                                    "Tambah ke My Library"
                                }
                            )
                        }
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}
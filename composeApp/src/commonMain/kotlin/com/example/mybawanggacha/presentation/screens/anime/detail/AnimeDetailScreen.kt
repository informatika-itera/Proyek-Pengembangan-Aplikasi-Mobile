package com.example.mybawanggacha.presentation.screens.anime.detail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
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
import com.example.mybawanggacha.domain.anime.model.AnimeDetail
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import com.example.mybawanggacha.presentation.screens.anime.detail.components.AnimeDetailContent
import com.example.mybawanggacha.presentation.screens.anime.detail.components.AnimeDetailSection
import com.example.mybawanggacha.presentation.screens.anime.detail.components.animeDetailRailItems
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AnimeDetailScreen(
    malId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToAnimeDetail: (Int) -> Unit = {},
    onNavigateToLibraryEditor: (AnimeDetail, Long?) -> Unit = { _, _ -> },
    viewModel: AnimeDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedSection by remember { mutableStateOf(AnimeDetailSection.Overview) }

    LaunchedEffect(malId) {
        viewModel.fetchAnimeDetail(malId)
    }

    MBGSideRailScaffold(
        selectedRailKey = selectedSection.key,
        railItems = animeDetailRailItems(),
        onRailItemClick = { key ->
            selectedSection = AnimeDetailSection.entries.firstOrNull { it.key == key }
                ?: AnimeDetailSection.Overview
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                AnimeDetailUiState.Loading -> LoadingIndicator()
                is AnimeDetailUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = { viewModel.fetchAnimeDetail(malId) }
                )
                is AnimeDetailUiState.Success -> {
                    AnimeDetailContent(
                        anime = state.anime,
                        episodes = state.episodes,
                        selectedSection = selectedSection,
                        onEpisodeWatchedChange = viewModel::setEpisodeWatched,
                        onRelationEntryClick = { entry ->
                            if (entry.type.equals("anime", ignoreCase = true)) {
                                onNavigateToAnimeDetail(entry.malId)
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Detail ${entry.type?.takeIf { it.isNotBlank() } ?: "Unknown"} belum diimplementasikan.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    )
                    val isInLibrary = state.libraryEntryId != null

                    FloatingActionButton(
                        onClick = { onNavigateToLibraryEditor(state.anime, state.libraryEntryId) },
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
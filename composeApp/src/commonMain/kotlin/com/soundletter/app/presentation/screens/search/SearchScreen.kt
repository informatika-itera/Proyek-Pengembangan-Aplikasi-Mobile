package com.soundletter.app.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.domain.model.Note
import com.soundletter.app.presentation.components.EmptyStateView
import com.soundletter.app.presentation.components.LoadingView
import com.soundletter.app.presentation.screens.home.MessageCard
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
import com.soundletter.app.presentation.theme.SoundLetterColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: SearchScreenViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val query by viewModel.query.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Search Letter",
                        color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(SoundLetterColors.getBackgroundGradient(isDarkMode)))
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                SearchBar(
                    query = query,
                    onQueryChange = { viewModel.onQueryChange(it) },
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search recipient name...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = SearchBarDefaults.colors(
                        containerColor = if (isDarkMode) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.8f)
                    )
                ) {}

                Spacer(modifier = Modifier.height(16.dp))

                when (val state = searchState) {
                    is UiState.Loading -> {
                        LoadingView()
                    }
                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            EmptyStateView(
                                icon = Icons.Default.SearchOff,
                                title = "Tidak Ditemukan",
                                description = "Kami tidak menemukan surat untuk nama tersebut."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.data, key = { it.id }) { note ->
                                    Box(modifier = Modifier.animateItem()) {
                                        MessageCard(
                                            message = note, 
                                            isDarkMode = isDarkMode,
                                            onClick = { onNavigateToDetail(note.id.toString()) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        LaunchedEffect(state.message) {
                            snackbarHostState.showSnackbar("Pencarian gagal: ${state.message}")
                        }
                    }
                    is UiState.Idle -> {
                        EmptyStateView(
                            icon = Icons.Default.Search,
                            title = "Mulai Mencari",
                            description = "Ketik nama penerima untuk menemukan surat tersembunyi."
                        )
                    }
                }
            }
        }
    }
}

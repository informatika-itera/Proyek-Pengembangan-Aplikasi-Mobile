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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soundletter.app.core.util.UiState
import com.soundletter.app.presentation.components.EmptyStateView
import com.soundletter.app.presentation.components.LoadingView
import com.soundletter.app.presentation.screens.home.MessageCard
import com.soundletter.app.presentation.screens.settings.SettingsViewModel
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

    // Adaptive colors for background and search bar
    val backgroundColor = if (isDarkMode) Color(0xFF000000) else Color(0xFFF0F8FF)
    val primaryColor = if (isDarkMode) Color.White else Color(0xFF007ACC)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Cari Surat",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = primaryColor
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = primaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar Polish with higher contrast in Light Mode
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                placeholder = { Text("Ketik nama penerima...", color = if (isDarkMode) Color.Gray else Color.DarkGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = primaryColor.copy(alpha = 0.5f),
                    focusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.1f) else Color.White,
                    unfocusedContainerColor = if (isDarkMode) Color.White.copy(alpha = 0.05f) else Color.White,
                    focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                    unfocusedTextColor = if (isDarkMode) Color.White else Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = searchState) {
                is UiState.Loading -> LoadingView()
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.SearchOff,
                            title = "Tidak Ditemukan",
                            description = "Coba cari dengan kata kunci lain."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.data, key = { it.id }) { note ->
                                MessageCard(
                                    message = note, 
                                    isDarkMode = isDarkMode,
                                    onClick = { onNavigateToDetail(note.id.toString()) }
                                )
                            }
                        }
                    }
                }
                is UiState.Idle -> {
                    EmptyStateView(
                        icon = Icons.Default.Search,
                        title = "Mulai Mencari",
                        description = "Cari pesan musik yang pernah kamu kirim."
                    )
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
                else -> {}
            }
        }
    }
}

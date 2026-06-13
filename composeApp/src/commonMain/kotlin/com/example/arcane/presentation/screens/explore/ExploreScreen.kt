package com.example.arcane.presentation.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arcane.presentation.components.BookCard
import com.example.arcane.presentation.components.EmptyState
import com.example.arcane.presentation.components.ErrorState
import com.example.arcane.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onNavigateToBook: (String) -> Unit,
    initialQuery: String = "",
    viewModel: ExploreViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(initialQuery) {
        if (initialQuery.isNotBlank()) {
            viewModel.onSearchQueryChange(initialQuery)
        }
    }

    val isCurrentlyOffline = when (val state = uiState) {
        is ExploreUiState.Initial -> state.isOffline
        is ExploreUiState.Success -> state.isOffline
        is ExploreUiState.Empty -> state.isOffline
        else -> false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Jelajah Buku",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Temukan literatur baru",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari judul, penulis, atau subjek...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(BOOK_GENRES) { genre ->
                    FilterChip(
                        selected = selectedGenre == genre,
                        onClick = { viewModel.onGenreSelected(genre) },
                        label = { Text(genre) }
                    )
                }
            }

            if (isCurrentlyOffline) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mode Offline: Menampilkan buku yang tersimpan di perpustakaan",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // 🔥 FIX COMPILER: PullToRefreshBox resmi terpasang dengan parameter isRefreshing & onRefresh dinamis seutuhnya lahh!
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshExplore(searchQuery, selectedGenre) },
                modifier = Modifier.fillMaxSize().weight(1f)
            ) {
                when (val state = uiState) {
                    is ExploreUiState.Initial -> {
                        EmptyState(
                            title = "Temukan Literatur",
                            message = "Cari jutaan buku atau pilih genre favoritmu."
                        )
                    }
                    is ExploreUiState.Loading -> {
                        if (state == ExploreUiState.Loading) LoadingIndicator()
                    }
                    is ExploreUiState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.books) { book ->
                                BookCard(
                                    book = book,
                                    onClick = { onNavigateToBook(book.googleBookId) }
                                )
                            }
                        }
                    }
                    // 🔥 FIX UX OFFLINE: Memastikan area Empty dan Error menggunakan LazyColumn full-size agar tarikan swipe gesture terdeteksi sempurna
                    is ExploreUiState.Empty -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                EmptyState(
                                    title = "Buku Tidak Ditemukan",
                                    message = "Coba gunakan kata kunci atau genre lain."
                                )
                            }
                        }
                    }
                    is ExploreUiState.Error -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                ErrorState(message = state.message)
                            }
                        }
                    }
                }
            }
        }
    }
}
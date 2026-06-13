package com.example.arcane.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.presentation.components.BookCard
import com.example.arcane.presentation.components.EmptyState
import com.example.arcane.presentation.components.ErrorState
import com.example.arcane.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToExplore: () -> Unit,
    onNavigateToBook: (String, Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearch) {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = viewModel::onSearchQueryChange,
                            onClear = {
                                viewModel.onSearchQueryChange("")
                                showSearch = false
                            }
                        )
                    } else {
                        Column {
                            Text(
                                text = "Arcane",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Perpustakaan Saya",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Cari")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToExplore,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Tambah Buku",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState is HomeUiState.Success) {
                ReadingStatsRow(books = uiState.books)
            }

            StatusFilterRow(
                selectedStatus = selectedStatus,
                onStatusSelected = viewModel::onStatusSelected
            )

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshData() },
                modifier = Modifier.fillMaxSize().weight(1f)
            ) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingIndicator()

                    is HomeUiState.Success -> {
                        BooksList(
                            books = state.books,
                            onBookClick = { book ->
                                onNavigateToBook(book.googleBookId, book.id)
                            }
                        )
                    }

                    is HomeUiState.Empty -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                EmptyState(
                                    title = if (searchQuery.isNotBlank()) "Tidak Ditemukan" else "Rak Buku Kosong",
                                    message = if (searchQuery.isNotBlank())
                                        "Coba ubah kata kunci pencarian Anda."
                                    else
                                        "Cari buku di menu Jelajah untuk ditambahkan ke perpustakaan Anda.",
                                    icon = {
                                        Icon(
                                            Icons.Outlined.LibraryBooks,
                                            contentDescription = null,
                                            modifier = Modifier.padding(bottom = 8.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    is HomeUiState.Error -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                ErrorState(
                                    message = state.message,
                                    onRetry = { viewModel.refreshData() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingStatsRow(books: List<Book>, modifier: Modifier = Modifier) {
    val total = books.size
    val reading = books.count { it.readingStatus == ReadingStatus.READING }
    val completed = books.count { it.readingStatus == ReadingStatus.COMPLETED }
    val toRead = books.count { it.readingStatus == ReadingStatus.TO_READ }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(label = "Total", value = total.toString(), modifier = Modifier.weight(1f))
        StatCard(label = "Dibaca", value = reading.toString(), modifier = Modifier.weight(1f))
        StatCard(label = "Selesai", value = completed.toString(), modifier = Modifier.weight(1f))
        StatCard(label = "Antrian", value = toRead.toString(), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, onClear: () -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari di perpustakaan...") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            AnimatedVisibility(visible = query.isNotBlank(), enter = fadeIn(), exit = fadeOut()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus")
                }
            }
        }
    )
}

@Composable
private fun StatusFilterRow(selectedStatus: ReadingStatus?, onStatusSelected: (ReadingStatus?) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusSelected(null) },
                label = { Text("Semua") }
            )
        }
        ReadingStatus.entries.forEach { status ->
            item {
                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { onStatusSelected(status) },
                    label = { Text(status.displayName) }
                )
            }
        }
    }
}

@Composable
private fun BooksList(books: List<Book>, onBookClick: (Book) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = books, key = { it.id }) { book ->
            BookCard(book = book, onClick = { onBookClick(book) })
        }
    }
}
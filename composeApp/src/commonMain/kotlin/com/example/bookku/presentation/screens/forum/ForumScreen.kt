package com.example.bookku.presentation.screens.forum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookku.presentation.components.EmptyState
import com.example.bookku.presentation.components.LoadingIndicator
import com.example.bookku.presentation.components.NoteCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: ForumViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Komunitas BookKu", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadCommunityFeed() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is ForumUiState.Loading -> LoadingIndicator()
                
                is ForumUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Apa yang baru hari ini?",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(state.posts, key = { it.id }) { book ->
                            NoteCard(
                                book = book,
                                onClick = { onNavigateToDetail(book.id) },
                                onPinClick = null,    // Hilangkan tombol pin
                                onDeleteClick = null, // Hilangkan tombol hapus
                                isOwner = false       // Proteksi tambahan: bukan pemilik
                            )
                        }
                    }
                }

                is ForumUiState.Empty -> {
                    EmptyState(
                        title = "Forum Masih Sepi",
                        message = "Jadilah yang pertama membagikan buku di komunitas!",
                        icon = {
                            Icon(Icons.AutoMirrored.Filled.Chat, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        }
                    )
                }

                is ForumUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

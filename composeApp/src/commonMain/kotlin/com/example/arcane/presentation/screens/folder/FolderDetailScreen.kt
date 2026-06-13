package com.example.arcane.presentation.screens.folder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailScreen(
    folderId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToBook: (String, Long) -> Unit,
    viewModel: FolderDetailViewModel = koinViewModel()
) {
    val folder by viewModel.folder.collectAsStateWithLifecycle()
    val books by viewModel.books.collectAsStateWithLifecycle()
    val allBooks by viewModel.allBooks.collectAsStateWithLifecycle()

    var showAddBookDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(folderId) {
        viewModel.loadFolder(folderId)
    }

    if (showAddBookDialog) {
        AlertDialog(
            onDismissRequest = { showAddBookDialog = false },
            title = { Text("Tambahkan Buku ke Folder") },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(allBooks) { book ->
                        val inFolder = books.any { it.id == book.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleBookInFolder(book.id, !inFolder) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = inFolder,
                                onCheckedChange = { viewModel.toggleBookInFolder(book.id, it) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(book.title, maxLines = 2)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddBookDialog = false }) {
                    Text("Selesai")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(folder?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddBookDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Buku")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (books.isEmpty()) {
                Text("Belum ada buku di folder ini.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(books) { book ->
                        Text(
                            text = book.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToBook(book.googleBookId, book.id) }
                                .padding(16.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

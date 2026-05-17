package com.example.noteai.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noteai.core.util.formatToDisplay
import com.example.noteai.presentation.components.CategoryBadge
import com.example.noteai.presentation.components.EmptyState
import com.example.noteai.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onShare: (String) -> Unit,
    viewModel: NoteDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NoteDetailEvent.NoteDeleted -> onNavigateBack()
                is NoteDetailEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteNote()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Catatan Makanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    val currentState = uiState
                    if (currentState is NoteDetailUiState.Success) {
                        IconButton(onClick = { viewModel.togglePin() }) {
                            Icon(
                                imageVector = if (currentState.note.isPinned) {
                                    Icons.Filled.PushPin
                                } else {
                                    Icons.Outlined.PushPin
                                },
                                contentDescription = if (currentState.note.isPinned) {
                                    "Lepas Pin"
                                } else {
                                    "Pin"
                                }
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.getShareContent()?.let { onShare(it) }
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Bagikan")
                        }

                        IconButton(onClick = { onNavigateToEdit(noteId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }

                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is NoteDetailUiState.Loading -> {
                LoadingIndicator()
            }

            is NoteDetailUiState.Success -> {
                val mealContent = parseMealContent(state.note.content)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = state.note.title.ifBlank { "Makanan tanpa nama" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryBadge(category = state.note.category.displayName)

                        Text(
                            text = state.note.updatedAt.formatToDisplay(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Informasi Makanan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (mealContent.price.isNotBlank()) {
                                Text(
                                    text = "Harga: Rp${mealContent.price}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            } else {
                                Text(
                                    text = "Harga: belum diisi",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (mealContent.note.isNotBlank()) {
                                Text(
                                    text = mealContent.note,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            } else {
                                Text(
                                    text = "Tidak ada catatan tambahan.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            is NoteDetailUiState.NotFound -> {
                EmptyState(
                    title = "Catatan Makanan Tidak Ditemukan",
                    message = "Catatan makanan mungkin sudah dihapus"
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Catatan Makanan") },
        text = { Text("Apakah kamu yakin ingin menghapus catatan makanan ini? Tindakan ini tidak dapat dibatalkan.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun parseMealContent(content: String): MealContent {
    val lines = content.lines()
    val firstLine = lines.firstOrNull().orEmpty()
    val hasPrice = firstLine.startsWith("Harga: Rp")

    return if (hasPrice) {
        MealContent(
            price = firstLine.removePrefix("Harga: Rp"),
            note = lines.drop(1).joinToString("\n").trim()
        )
    } else {
        MealContent(
            price = "",
            note = content.trim()
        )
    }
}

private data class MealContent(
    val price: String,
    val note: String
)
package com.example.foodsaver.presentation.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodsaver.core.utility.formatToDisplay
import com.example.foodsaver.presentation.components.EmptyFoodState
import com.example.foodsaver.presentation.components.LoadingIndicator
import com.example.foodsaver.presentation.components.StatusBadge
import com.example.foodsaver.domain.model.FoodStatus
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
        DeleteNoteDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteNote()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    
    Scaffold(
        modifier = Modifier.testTag("note_detail_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            NoteDetailTopBar(
                uiState = uiState,
                onBackClick = onNavigateBack,
                onPinClick = viewModel::togglePin,
                onShareClick = { viewModel.getShareContent()?.let { onShare(it) } },
                onEditClick = { onNavigateToEdit(noteId) },
                onDeleteClick = { showDeleteDialog = true }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is NoteDetailUiState.Loading -> {
                    LoadingIndicator()
                }
                is NoteDetailUiState.Success -> {
                    NoteContent(note = state.note)
                }
                is NoteDetailUiState.NotFound -> {
                    EmptyFoodState(
                        message = "Catatan Tidak Ditemukan",
                        description = "Catatan mungkin sudah dihapus atau tidak tersedia.",
                        onActionClick = onNavigateBack
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteDetailTopBar(
    uiState: NoteDetailUiState,
    onBackClick: () -> Unit,
    onPinClick: () -> Unit,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Detail Catatan", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
        actions = {
            if (uiState is NoteDetailUiState.Success) {
                IconButton(onClick = onPinClick) {
                    Icon(
                        imageVector = if (uiState.note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        tint = if (uiState.note.isPinned) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
                IconButton(onClick = onShareClick) {
                    Icon(Icons.Default.Share, contentDescription = "Bagikan")
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Ubah", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
private fun NoteContent(note: com.example.foodsaver.domain.model.Note) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (note.title.isNotBlank()) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = note.category.displayName,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Text(
                text = "Diperbarui: ${note.updatedAt.formatToDisplay()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        
        Text(
            text = note.content,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 26.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DeleteNoteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Catatan", fontWeight = FontWeight.Bold) },
        text = { Text("Apakah kamu yakin ingin menghapus catatan ini? Tindakan ini tidak bisa dibatalkan.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

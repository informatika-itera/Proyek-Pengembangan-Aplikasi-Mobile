package com.studymate.presentation.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.studymate.domain.model.Note
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is NoteEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is NoteEvent.NavigateTo -> {} 
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("📝 Smart Notes") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Catatan")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is NotesUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is NotesUiState.Empty -> Text("Belum ada catatan. Tekan + untuk mulai!", modifier = Modifier.align(Alignment.Center))
                is NotesUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is NotesUiState.Success -> {
                    NoteList(
                        notes = state.notes,
                        refiningNoteId = null,
                        onNoteClick = { onNavigateToDetail(it.id) },
                        onRefineClick = { viewModel.refineNote(it) },
                        onDeleteClick = { viewModel.deleteNote(it.id) }
                    )
                }
                is NotesUiState.Refining -> {
                    NoteList(
                        notes = state.notes,
                        refiningNoteId = state.noteBeingRefined,
                        onNoteClick = { onNavigateToDetail(it.id) },
                        onRefineClick = { viewModel.refineNote(it) },
                        onDeleteClick = { viewModel.deleteNote(it.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddNoteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content, subject ->
                viewModel.addNote(title, content, subject)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun NoteList(
    notes: List<Note>,
    refiningNoteId: Long?,
    onNoteClick: (Note) -> Unit,
    onRefineClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notes) { note ->
            NoteCard(
                note = note,
                isRefining = note.id == refiningNoteId,
                onClick = { onNoteClick(note) },
                onRefineClick = { onRefineClick(note) },
                onDeleteClick = { onDeleteClick(note) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: Note,
    isRefining: Boolean,
    onClick: () -> Unit,
    onRefineClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(note.title, style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
            if (note.subject.isNotBlank()) {
                AssistChip(
                    onClick = {},
                    label = { Text(note.subject) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                note.rawContent,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (note.isRefined) {
                    AssistChip(
                        onClick = {},
                        label = { Text("✨ AI Refined") },
                        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                } else {
                    Button(
                        onClick = onRefineClick,
                        enabled = !isRefining,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        if (isRefining) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Memproses AI...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Refine")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddNoteDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Catatan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") })
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Mata Kuliah") })
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Catatan") },
                    minLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, content, subject) },
                enabled = title.isNotBlank() && content.isNotBlank()
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

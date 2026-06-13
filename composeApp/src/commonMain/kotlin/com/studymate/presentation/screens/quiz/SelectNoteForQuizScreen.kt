package com.studymate.presentation.screens.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.studymate.domain.model.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectNoteForQuizScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdvanced: () -> Unit,
    onNoteSelected: (Note) -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    var sortBySubject by remember { mutableStateOf(false) }
    var selectedNoteForDialog by remember { mutableStateOf<Note?>(null) }
    var questionCount by remember { mutableFloatStateOf(5f) }
    
    val sortedNotes = remember(notes, sortBySubject) {
        if (sortBySubject) {
            notes.sortedWith(compareBy({ it.subject }, { -it.updatedAt }))
        } else {
            notes.sortedByDescending { it.updatedAt }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Pilih Catatan untuk Quiz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAdvanced) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Advanced Quiz",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { sortBySubject = !sortBySubject }) {
                        Icon(
                            Icons.Default.Sort, 
                            contentDescription = "Urutkan",
                            tint = if (sortBySubject) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada catatan. Buat catatan terlebih dahulu.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedNotes) { note ->
                    NoteSelectionItem(
                        note = note,
                        onClick = { selectedNoteForDialog = note }
                    )
                }
            }
        }
    }

    if (selectedNoteForDialog != null) {
        AlertDialog(
            onDismissRequest = { selectedNoteForDialog = null },
            title = { Text("Pengaturan Quiz") },
            text = {
                Column {
                    Text("Pilih jumlah soal untuk catatan: ${selectedNoteForDialog?.title}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Jumlah Soal: ${questionCount.toInt()}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = questionCount,
                        onValueChange = { questionCount = it },
                        valueRange = 3f..15f,
                        steps = 11
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val note = selectedNoteForDialog
                    if (note != null) {
                        viewModel.startQuiz(note, questionCount.toInt())
                        onNoteSelected(note)
                    }
                    selectedNoteForDialog = null
                }) {
                    Text("Mulai")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedNoteForDialog = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun NoteSelectionItem(
    note: Note,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (note.subject.isNotBlank()) {
                Text(
                    text = note.subject,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.rawContent,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

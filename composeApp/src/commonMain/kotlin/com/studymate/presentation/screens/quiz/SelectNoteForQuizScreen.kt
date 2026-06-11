package com.studymate.presentation.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onNoteSelected: (Note, Int) -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    var sortBySubject by remember { mutableStateOf(false) }
    
    var selectedNoteForDialog by remember { mutableStateOf<Note?>(null) }
    var questionCount by remember { mutableStateOf(10f) }

    val sortedNotes = remember(notes, sortBySubject) {
        if (sortBySubject) {
            notes.sortedWith(compareBy({ it.subject }, { -it.updatedAt }))
        } else {
            notes.sortedByDescending { it.updatedAt }
        }
    }

    if (selectedNoteForDialog != null) {
        AlertDialog(
            onDismissRequest = { selectedNoteForDialog = null },
            title = { Text("Pengaturan Quiz") },
            text = {
                Column {
                    Text("Jumlah Soal: ${questionCount.toInt()}")
                    Slider(
                        value = questionCount,
                        onValueChange = { questionCount = it },
                        valueRange = 5f..25f,
                        steps = 19
                    )
                    Text(
                        "Minimal 5, Maksimal 25 soal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onNoteSelected(selectedNoteForDialog!!, questionCount.toInt())
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Catatan (AI Refined)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Advanced Quiz Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onNavigateToAdvanced() },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.secondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Advanced Quiz", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("Gabungkan materi & atur jumlah soal lebih banyak", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.Default.ChevronRight, null)
                }
            }

            if (notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Belum ada catatan yang di-Refine AI.\nSilakan Refine catatan Anda terlebih dahulu.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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

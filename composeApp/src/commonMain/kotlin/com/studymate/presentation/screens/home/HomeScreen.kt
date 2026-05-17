package com.studymate.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
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
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("🐬 StudyMate") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Error -> Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.retry() }) { Text("Retry") }
                }
                is HomeUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        GreetingCard(userName = state.userName)
                        StreakCard(streak = state.currentStreak)
                        DailyMantraCard(mantra = state.dailyMantra)
                        QuickSnippetsSection(recentNotes = state.recentNotes)
                    }
                }
            }
        }
    }
}

@Composable
private fun GreetingCard(userName: String) {
    Column {
        Text("Halo, $userName! 👋", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Siap untuk belajar hal baru hari ini?", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥", style = MaterialTheme.typography.displaySmall)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(streak.toString(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Hari Berturut-turut", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun DailyMantraCard(mantra: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.FormatQuote, contentDescription = null)
            Text(mantra, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun QuickSnippetsSection(recentNotes: List<Note>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("📋 Catatan Terbaru", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (recentNotes.isEmpty()) {
            Text("Belum ada catatan terbaru.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recentNotes) { note ->
                    NoteSnippetCard(note = note)
                }
            }
        }
    }
}

@Composable
fun NoteSnippetCard(note: Note) {
    Card(modifier = Modifier.width(200.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(note.subject, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(note.rawContent, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

package com.studymate.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.domain.model.Note
import com.studymate.presentation.theme.GradientEnd
import com.studymate.presentation.theme.GradientStart
import com.studymate.presentation.theme.TextGray
import com.studymate.Res
import com.studymate.app_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = { HomeFAB() }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            when (val state = uiState) {
                is HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is HomeUiState.Error -> ErrorContent(state.message, onRetry = { viewModel.retry() })
                is HomeUiState.Success -> {
                    // Header Section (Z-Index 1)
                    MockupHeaderSection(scrollOffset = scrollState.value)

                    // Scrollable Content (Z-Index 0)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Spacer matched to max header height
                        Spacer(modifier = Modifier.height(300.dp))

                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(28.dp)
                        ) {
                            DailyMantraSection(state.dailyMantra) {
                                viewModel.refreshMantra()
                            }
                            DailyStreakSection(state.currentStreak)
                            QuickSnippetsSection(state.recentNotes)
                            LastNoteSection(state.recentNotes.firstOrNull())
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MockupHeaderSection(scrollOffset: Int) {
    val maxHeight = 300.dp
    val minHeight = 0.dp
    
    // Calculate dynamic height based on scroll
    val headerHeight = (maxHeight - (scrollOffset / 2).dp).coerceAtLeast(minHeight)
    val alpha = (1f - (scrollOffset / 400f)).coerceIn(0f, 1f)

    if (headerHeight > 0.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .clip(RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 45.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF9C27B0), Color(0xFFE91E63))
                    )
                )
                .graphicsLayer {
                    this.alpha = alpha
                }
        ) {
            Image(
                painter = painterResource(Res.drawable.app_logo),
                contentDescription = "StudyMate Logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            
            IconButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 20.dp)
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun HomeFAB() {
    var isExpanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.End) {
        // Quick Menu Bubble
        if (isExpanded) {
            Surface(
                color = Color(0xFF251849),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Text("Teks Cepat", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Text("Catatan", color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        
        // Main FAB
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF9C27B0), Color(0xFFE91E63))))
                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                .clickable { isExpanded = !isExpanded },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add, 
                contentDescription = null, 
                tint = Color.White, 
                modifier = Modifier
                    .size(32.dp)
                    .rotate(if (isExpanded) 45f else 0f)
            )
        }
    }
}

@Composable
private fun DailyMantraSection(mantra: String, onRefreshClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Daily Mantra", 
            color = MaterialTheme.colorScheme.onBackground, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold
        )
        Card(
            modifier = Modifier.fillMaxWidth().border(
                1.dp, 
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), 
                RoundedCornerShape(16.dp)
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    mantra,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(
                        Brush.linearGradient(listOf(GradientStart, GradientEnd))
                    )
                ) {
                    Icon(Icons.Default.Autorenew, contentDescription = "Ganti Mantra", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}


@Composable
private fun DailyStreakSection(streak: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Daily Streak", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Card(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
            ) {
                // Simplified Chart Graphic
                Canvas(modifier = Modifier.fillMaxSize().padding(top = 60.dp)) {
                    val width = size.width
                    val height = size.height
                    repeat(6) { i ->
                        drawRect(
                            color = Color.White.copy(alpha = 0.2f),
                            topLeft = androidx.compose.ui.geometry.Offset(width * (0.1f + i * 0.15f), height * (0.2f + i * 0.1f)),
                            size = androidx.compose.ui.geometry.Size(width * 0.1f, height)
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("$streak Hari!", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                    Text("minimal 1 note daily", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun QuickSnippetsSection(notes: List<Note>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Quick Snippets", 
            color = MaterialTheme.colorScheme.onBackground, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold
        )
        Text("Show the latest unrefined notes", color = TextGray, style = MaterialTheme.typography.labelSmall)
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (notes.isEmpty()) {
                item { Text("No unrefined notes", color = TextGray) }
            } else {
                items(notes) { note ->
                    Card(
                        modifier = Modifier.size(120.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            note.rawContent,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LastNoteSection(note: Note?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Terakhir Kamu Catat", 
            color = MaterialTheme.colorScheme.onBackground, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    note?.title ?: "Untitled", 
                    color = MaterialTheme.colorScheme.onSurface, 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    note?.rawContent ?: "Isi catatan...", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    style = MaterialTheme.typography.bodySmall, 
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

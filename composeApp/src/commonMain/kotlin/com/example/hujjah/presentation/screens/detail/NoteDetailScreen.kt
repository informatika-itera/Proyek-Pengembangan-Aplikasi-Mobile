package com.example.hujjah.presentation.screens.detail

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.hujjah.presentation.theme.LocalHujjahColors
import androidx.compose.ui.text.font.FontStyle
import com.example.hujjah.core.util.formatToDisplay
import com.example.hujjah.presentation.components.CategoryBadge
import com.example.hujjah.presentation.components.EmptyState
import com.example.hujjah.presentation.components.LoadingIndicator
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
                title = { Text("Detail Catatan") },
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
                                imageVector = if (currentState.note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (currentState.note.isPinned) "Lepas Pin" else "Pin"
                            )
                        }
                        
                        IconButton(onClick = { 
                            viewModel.getShareContent()?.let { onShare(it) }
                        }) {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (state.note.title.isNotBlank()) {
                        Text(
                            text = state.note.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryBadge(category = state.note.category)
                        
                        Text(
                            text = state.note.updatedAt.formatToDisplay(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Parse rujukan dalil dari konten
                    val rawContent = state.note.content
                    val regex = Regex("""\n\n\[Rujukan:\s*(Quran|Hadits)\s*(\|\|\||\|)\s*(.*?)\s*\]""")
                    val match = regex.find(rawContent)
                    val cleanText = if (match != null) rawContent.replace(match.value, "") else rawContent
                    
                    Text(
                        text = cleanText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    if (match != null) {
                        val fullTag = match.value
                        val tagContent = fullTag.trim().removeSurrounding("[Rujukan:", "]").trim()
                        val parts = if (tagContent.contains("|||")) {
                            tagContent.split("|||").map { it.trim() }
                        } else {
                            tagContent.split("|").map { it.trim() }
                        }
                        
                        if (parts.size >= 3) {
                            val refType = parts[0]
                            val refSource = parts[1]
                            val refNumber = parts[2]
                            val refArabic = parts.getOrNull(3) ?: ""
                            val refTranslation = parts.getOrNull(4) ?: ""
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // Rujukan Card Apple Premium
                            val colors = LocalHujjahColors.current
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (colors.isDarkTheme) Color.Black else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(colors.goldHighlight.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (refType == "Quran") "📖" else "📚",
                                                fontSize = 20.sp
                                            )
                                        }
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Rujukan Terhubung",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = colors.goldHighlight,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (refType == "Quran") {
                                                    "Surah $refSource: Ayat $refNumber"
                                                } else {
                                                    "Hadits $refSource: No. $refNumber"
                                                },
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                            )
                                        }
                                    }
                                    
                                    // Tampilkan isi teks rujukan jika tersedia
                                    if (refArabic.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = refArabic,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End,
                                            lineHeight = 28.sp,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "\"$refTranslation\"",
                                            fontSize = 13.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            is NoteDetailUiState.NotFound -> {
                EmptyState(
                    title = "Catatan Tidak Ditemukan",
                    message = "Catatan mungkin sudah dihapus"
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
        title = { Text("Hapus Catatan") },
        text = { Text("Apakah Anda yakin ingin menghapus catatan ini? Tindakan ini tidak dapat dibatalkan.") },
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

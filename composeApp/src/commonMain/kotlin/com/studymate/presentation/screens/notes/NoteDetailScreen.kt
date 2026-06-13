package com.studymate.presentation.screens.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.domain.model.Note
import com.studymate.presentation.theme.AIColorLight
import com.studymate.presentation.theme.PrimaryLight
import com.studymate.presentation.theme.ActionFABLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long,
    viewModel: NotesViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Local state for editing
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var currentNote by remember { mutableStateOf<Note?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is NoteEvent.ShowMessage) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is NotesUiState.Success) {
            val notes = (uiState as NotesUiState.Success).notes
            val note = notes.find { it.id == noteId }
            if (note != null) {
                currentNote = note
                title = note.title
                content = if (note.isRefined) note.refinedContent ?: note.rawContent else note.rawContent
                subject = note.subject
            }
        }
    }

    val isRefining = uiState is NotesUiState.Refining

    // AI Refine is available if at least Subject and Title are filled
    val showAIAction = subject.isNotBlank() && title.isNotBlank()
    
    // Get all unique subjects for autocomplete
    val allSubjects = if (uiState is NotesUiState.Success) {
        (uiState as NotesUiState.Success).notes.map { it.subject }.filter { it.isNotBlank() }.distinct().sorted()
    } else {
        emptyList()
    }
    
    // Filter subjects based on user input
    val filteredSubjects = if (subject.isBlank()) {
        emptyList()
    } else {
        allSubjects.filter { it.contains(subject, ignoreCase = true) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (noteId == -1L) "Catatan Baru" else "Edit Catatan",
                        fontWeight = FontWeight.Black
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (noteId != -1L) {
                        IconButton(
                            onClick = {
                                viewModel.deleteNote(noteId)
                                onBack()
                            },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.Red.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (noteId == -1L) {
                                viewModel.addNote(title, content, subject) {
                                    onBack()
                                }
                            } else {
                                currentNote?.let { note ->
                                    viewModel.updateNote(note.copy(title = title, rawContent = content, subject = subject)) {
                                        onBack()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryLight),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (showAIAction) {
                AIActionBar(
                    onRefineClick = {
                        if (noteId == -1L) {
                            viewModel.refineContent(subject, title, content) { refined: String ->
                                content = refined
                            }
                        } else {
                            currentNote?.let { viewModel.refineNote(it) }
                        }
                    },
                    isRefining = isRefining,
                    isNewNote = noteId == -1L
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Subject Tag with Autocomplete Dropdown
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    placeholder = { Text("Mata Kuliah (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryLight,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )
                
                // Suggestions list (tidak block input)
                if (subject.isNotBlank() && filteredSubjects.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            filteredSubjects.forEach { suggestedSubject ->
                                Text(
                                    text = suggestedSubject,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { subject = suggestedSubject }
                                        .padding(12.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title Input
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Judul Materi...", style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Distraction-free Content Editor
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Mulai mencatat di sini...", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 300.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = TextStyle(fontSize = 17.sp, lineHeight = 28.sp, fontWeight = FontWeight.Normal)
            )
            
            if (isRefining) {
                Spacer(modifier = Modifier.height(16.dp))
                ShimmerEffect()
            }
        }
    }
}

@Composable
fun AIActionBar(onRefineClick: () -> Unit, isRefining: Boolean, isNewNote: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(AIColorLight, Color(0xFF9D4EDD))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = !isRefining) { onRefineClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isRefining) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isRefining) "Sedang Memproses..." else if (isNewNote) "✨ Ringkas & Jelaskan dengan AI" else "🪄 Rapikan dengan Gemini AI",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ShimmerEffect() {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = alpha))
            )
        }
    }
}

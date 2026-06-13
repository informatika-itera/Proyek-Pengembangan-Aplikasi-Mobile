package com.example.bookku.presentation.screens.bookdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.bookku.core.util.formatToDisplay
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.Review
import com.example.bookku.domain.model.ReadingStatus
import com.example.bookku.presentation.components.CategoryBadge
import com.example.bookku.presentation.components.EmptyState
import com.example.bookku.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToAiAssistant: (Long) -> Unit,
    onShare: (String) -> Unit,
    viewModel: BookDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val progress by viewModel.readingProgress.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiRecommendation by viewModel.aiRecommendation.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReviewSheet by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }
    
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
                viewModel.deleteBook()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showProgressDialog) {
        ProgressUpdateDialog(
            currentProgress = progress?.currentPage ?: 0,
            onConfirm = { current ->
                viewModel.updateProgress(current)
                showProgressDialog = false
            },
            onDismiss = { showProgressDialog = false }
        )
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Buku") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    val currentState = uiState
                    if (currentState is NoteDetailUiState.Success) {
                        // Tombol Pin tetap ada untuk semua buku (bersifat lokal)
                        IconButton(onClick = { viewModel.togglePin() }) {
                            Icon(
                                imageVector = if (currentState.book.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (currentState.book.isPinned) "Lepas Pin" else "Pin"
                            )
                        }
                        
                        // HANYA tampilkan Edit, Hapus, dan Bagikan jika pemilik buku
                        if (currentState.isOwner) {
                            IconButton(onClick = { 
                                val shareText = "Baca buku '${currentState.book.title}' oleh ${currentState.book.author} di Bookku!"
                                onShare(shareText)
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Bagikan")
                            }
                            IconButton(onClick = { onNavigateToEdit(noteId) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is NoteDetailUiState.Loading -> LoadingIndicator()
            is NoteDetailUiState.Success -> {
                val book = state.book
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .verticalScroll(rememberScrollState())
                ) {
                    // Cover Header
                    Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                        AsyncImage(
                            model = book.coverUrl.ifBlank { "https://via.placeholder.com/400x600?text=No+Cover" },
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, MaterialTheme.colorScheme.surface)
                                )
                            )
                        )
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                            CategoryBadge(category = book.category.displayName)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = book.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            if (book.author.isNotBlank()) {
                                Text(text = "Oleh ${book.author}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Reading Progress Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Progress Membaca", style = MaterialTheme.typography.titleSmall)
                                    val totalPages = book.totalPages
                                    val currentP = progress?.currentPage ?: 0
                                    val percent = if (totalPages > 0) (currentP.toFloat() / totalPages * 100).toInt() else 0
                                    
                                    Text("$percent% Selesai ($currentP/$totalPages Hal)", style = MaterialTheme.typography.bodyMedium)
                                    LinearProgressIndicator(
                                        progress = { if (totalPages > 0) currentP.toFloat() / totalPages else 0f },
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                    )
                                }
                                IconButton(onClick = { showProgressDialog = true }) {
                                    Icon(Icons.Default.Update, contentDescription = "Update Progress")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Tentang Buku", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = book.content, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 8.dp))

                        // AI Recommendation
                        Spacer(modifier = Modifier.height(24.dp))
                        AIRecommendationSection(
                            recommendation = aiRecommendation,
                            isLoading = isAiLoading,
                            onFetch = { viewModel.fetchAiRecommendation() },
                            onChatWithAi = { onNavigateToAiAssistant(book.id) }
                        )

                        // Reviews Section
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Review Pembaca", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            TextButton(onClick = { showReviewSheet = true }) {
                                Text("Tulis Review")
                            }
                        }
                        
                        if (reviews.isEmpty()) {
                            Text("Belum ada review. Jadilah yang pertama!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            reviews.forEach { review ->
                                ReviewItem(review)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
            is NoteDetailUiState.NotFound -> EmptyState(title = "Buku Tidak Ditemukan", message = "Buku mungkin sudah dihapus", modifier = Modifier.padding(paddingValues))
        }
    }

    if (showReviewSheet) {
        ReviewBottomSheet(
            onDismiss = { showReviewSheet = false },
            onSubmit = { rating, text ->
                viewModel.addReview(rating, text)
                showReviewSheet = false
            }
        )
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = review.userName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (index < review.rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline
                )
            }
        }
        Text(text = review.reviewText, style = MaterialTheme.typography.bodyMedium)
        Text(text = review.createdAt.formatToDisplay(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewBottomSheet(onDismiss: () -> Unit, onSubmit: (Float, String) -> Unit) {
    var rating by remember { mutableStateOf(5f) }
    var text by remember { mutableStateOf("") }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
            Text("Berikan Rating", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.padding(vertical = 16.dp)) {
                repeat(5) { index ->
                    IconButton(onClick = { rating = (index + 1).toFloat() }) {
                        Icon(
                            imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (index < rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Tulis pendapatmu...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Button(
                onClick = { onSubmit(rating, text) },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = text.isNotBlank()
            ) {
                Text("Kirim Review")
            }
        }
    }
}

@Composable
fun ProgressUpdateDialog(currentProgress: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var currentStr by remember { mutableStateOf(currentProgress.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Progress") },
        text = {
            Column {
                Text("Halaman yang sudah kamu baca saat ini:")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = currentStr,
                    onValueChange = { currentStr = it },
                    label = { Text("Halaman Saat Ini") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                val current = currentStr.toIntOrNull() ?: 0
                onConfirm(current)
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun AIRecommendationSection(
    recommendation: String?,
    isLoading: Boolean,
    onFetch: () -> Unit,
    onChatWithAi: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Wawasan AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                
                IconButton(onClick = onChatWithAi) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Tanya AI Lebih Lanjut", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else if (recommendation != null) {
                Text(text = recommendation, style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = onChatWithAi, modifier = Modifier.align(Alignment.End)) {
                    Text("Tanya AI Lebih Lanjut")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp).padding(start = 4.dp))
                }
            } else {
                Text(text = "Dapatkan ringkasan atau rekomendasi buku serupa dari AI.", style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onFetch, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Tanya AI")
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Buku") },
        text = { Text("Apakah Anda yakin ingin menghapus catatan buku ini?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

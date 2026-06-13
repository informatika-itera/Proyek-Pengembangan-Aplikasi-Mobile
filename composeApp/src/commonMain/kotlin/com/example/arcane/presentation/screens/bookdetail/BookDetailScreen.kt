package com.example.arcane.presentation.screens.bookdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.ReadingStatus
import com.example.arcane.presentation.components.ErrorState
import com.example.arcane.presentation.components.LoadingIndicator
import com.example.arcane.presentation.components.StatusBadge
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.filled.Check
import com.example.arcane.domain.model.Folder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    googleBookId: String,
    localBookId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToResearch: (String, String) -> Unit,
    viewModel: BookDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allFolders by viewModel.allFolders.collectAsStateWithLifecycle()
    val bookFolders by viewModel.bookFolders.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(googleBookId, localBookId) {
        viewModel.loadBook(googleBookId, localBookId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BookDetailEvent.BookDeleted -> onNavigateBack()
                is BookDetailEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteFromLibrary()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Detail Buku",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Informasi lengkap buku",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    if (uiState is BookDetailUiState.Success) {
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
            is BookDetailUiState.Loading -> LoadingIndicator()
            is BookDetailUiState.Error -> ErrorState(
                message = state.message,
                onRetry = { viewModel.loadBook(googleBookId, localBookId) }
            )
            is BookDetailUiState.NotInLibrary -> {
                NotInLibraryContent(
                    book = state.book,
                    isSaved = state.isSaved,
                    modifier = Modifier.padding(paddingValues),
                    onSaveToLibrary = { viewModel.saveToLibrary(state.book) },
                    onNavigateToResearch = onNavigateToResearch
                )
            }
            is BookDetailUiState.Success -> {
                BookDetailContent(
                    book = state.book,
                    allFolders = allFolders,
                    bookFolders = bookFolders,
                    modifier = Modifier.padding(paddingValues),
                    onStatusChange = viewModel::updateStatus,
                    onSaveNotes = viewModel::saveNotesAndRating,
                    onNavigateToResearch = onNavigateToResearch,
                    onToggleFolder = viewModel::toggleFolder
                )
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun NotInLibraryContent(
    book: Book,
    isSaved: Boolean = false,
    onSaveToLibrary: () -> Unit,
    onNavigateToResearch: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = book.authorsFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    if (book.categories.isNotEmpty()) {
                        val cleanedGenres = book.categories.flatMap { category ->
                            category.split("/").map { it.trim() }
                        }.distinct()

                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            cleanedGenres.forEach { genreString ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = genreString,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    if (isSaved) {
                        Text(
                            text = "✓ Tersimpan di perpustakaan",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Button(
            onClick = onSaveToLibrary,
            enabled = !isSaved,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                if (isSaved) Icons.Default.Check else Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isSaved) "Tersimpan" else "Simpan ke Perpustakaan")
        }

        Button(
            onClick = { onNavigateToResearch(book.title, book.description.stripHtmlAndEntities()) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tanya Asisten AI")
        }

        if (book.description.isNotBlank()) {
            SectionTitle("Deskripsi Buku")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = book.description.stripHtmlAndEntities(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun BookDetailContent(
    book: Book,
    allFolders: List<Folder>,
    bookFolders: List<Folder>,
    onStatusChange: (ReadingStatus) -> Unit,
    onSaveNotes: (String, Int?) -> Unit,
    onNavigateToResearch: (String, String) -> Unit,
    onToggleFolder: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    var notes by remember(book.notes) { mutableStateOf(book.notes) }
    var rating by remember(book.rating) { mutableStateOf(book.rating) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = book.authorsFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    if (book.categories.isNotEmpty()) {
                        val cleanedGenres = book.categories.flatMap { category ->
                            category.split("/").map { it.trim() }
                        }.distinct()

                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            cleanedGenres.forEach { genreString ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = genreString,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    StatusBadge(status = book.readingStatus)

                    book.rating?.let { r ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < r) Icons.Default.Star else Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (index < r) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onNavigateToResearch(book.title, book.description.stripHtmlAndEntities()) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tanya Asisten AI tentang Buku Ini")
        }

        SectionTitle("Status Membaca")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ReadingStatus.entries.forEach { status ->
                val isSelected = book.readingStatus == status
                TextButton(
                    onClick = { onStatusChange(status) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = if (isSelected)
                        ButtonDefaults.filledTonalButtonColors()
                    else
                        ButtonDefaults.textButtonColors()
                ) {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        if (allFolders.isNotEmpty()) {
            SectionTitle("Koleksi Folder")
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                allFolders.forEach { folder ->
                    val isSelected = bookFolders.any { it.id == folder.id }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggleFolder(folder) },
                        label = { Text(folder.name) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }

        SectionTitle("Rating & Catatan")

        RatingSelector(
            rating = rating ?: 0,
            onRatingChange = { rating = it }
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = { Text("Tulis refleksi atau catatan tentang buku ini...") },
            shape = RoundedCornerShape(12.dp)
        )

        Button(
            onClick = {
                onSaveNotes(notes, rating)
                notes = ""
            },
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Catatan")
        }

        if (book.description.isNotBlank()) {
            SectionTitle("Deskripsi Buku")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = book.description.stripHtmlAndEntities(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun RatingSelector(rating: Int, onRatingChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            val pos = index + 1
            IconButton(onClick = { onRatingChange(pos) }) {
                Icon(
                    imageVector = if (pos <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (pos <= rating) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.outline
                )
            }
        }
        if (rating > 0) {
            Text(
                text = "$rating/5",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Hapus dari Perpustakaan",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Apakah kamu yakin ingin menghapus buku ini beserta semua catatannya?")
        },
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

private fun String.stripHtmlAndEntities(): String {
    return this
        .replace(Regex("<[^>]*>"), "")
        .replace("&quot;", "\"")
        .replace("&amp;", "&")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()
}
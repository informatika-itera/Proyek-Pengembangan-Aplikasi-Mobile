package com.example.arcane.presentation.screens.letterbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.arcane.domain.model.Book
import com.example.arcane.domain.model.Folder
import com.example.arcane.presentation.components.EmptyState
import com.example.arcane.presentation.components.ErrorState
import com.example.arcane.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterboxScreen(
    onNavigateToBook: (String) -> Unit,
    onNavigateToBookDetail: (String, Long) -> Unit,
    onNavigateToFolderDetail: (Long) -> Unit,
    viewModel: LetterboxViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var folderToEdit by remember { mutableStateOf<Folder?>(null) }
    var expandedMenuFolderId by remember { mutableStateOf<Long?>(null) }
    val reviewStates by viewModel.reviewStates.collectAsStateWithLifecycle()
    val recommendationState by viewModel.recommendationState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Letterbox",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Jurnal & Rekomendasi Bacaanmu",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 1) {
                FloatingActionButton(onClick = { showCreateFolderDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Folder")
                }
            }
        }
    ) { paddingValues ->
        if (showCreateFolderDialog || folderToEdit != null) {
            val isEditMode = folderToEdit != null
            var folderName by remember { mutableStateOf(folderToEdit?.name ?: "") }
            AlertDialog(
                onDismissRequest = { 
                    showCreateFolderDialog = false
                    folderToEdit = null
                },
                title = { Text(if (isEditMode) "Edit Nama Folder" else "Buat Folder Baru") },
                text = {
                    TextField(
                        value = folderName,
                        onValueChange = { folderName = it },
                        placeholder = { Text("Nama Folder") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val current = folderToEdit
                            if (isEditMode && current != null) {
                                viewModel.updateFolder(current.id, folderName)
                            } else {
                                viewModel.createFolder(folderName)
                            }
                            showCreateFolderDialog = false
                            folderToEdit = null
                        }
                    ) { Text("Simpan") }
                },
                dismissButton = {
                    OutlinedButton(onClick = { 
                        showCreateFolderDialog = false
                        folderToEdit = null
                    }) { Text("Batal") }
                }
            )
        }
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Buku") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Folder") }
                )
            }
            if (selectedTabIndex == 0) {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refreshLetterboxData() },
                    modifier = Modifier.fillMaxSize()
                ) {
            when (val state = uiState) {
                is LetterboxUiState.Loading -> LoadingIndicator()

                is LetterboxUiState.Error -> ErrorState(message = state.message)

                is LetterboxUiState.Empty -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            EmptyState(
                                title = "Belum Ada Buku Selesai",
                                message = "Tandai buku sebagai 'Selesai' untuk melihatnya di Letterbox.",
                                icon = {
                                    Icon(
                                        Icons.Default.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp).padding(bottom = 8.dp),
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    )
                                }
                            )
                        }
                    }
                }

                is LetterboxUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            RecommendationCard(
                                totalBooks = state.books.size,
                                recommendationState = recommendationState,
                                onGetRecommendation = { viewModel.getRecommendations(state.books) },
                                onDismiss = { viewModel.dismissRecommendation() },
                                onBookClick = onNavigateToBook
                            )
                        }

                        item {
                            Text(
                                text = "Buku yang Sudah Dibaca",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(state.books) { book ->
                            LetterboxBookCard(
                                book = book,
                                reviewState = reviewStates[book.googleBookId] ?: ReviewState.Idle,
                                onGenerateReview = { viewModel.generateReview(book) },
                                onBookClick = { onNavigateToBookDetail(book.googleBookId, book.id) }
                            )
                        }
                    }
                }
            }
        }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(folders) { folder ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clickable { onNavigateToFolderDetail(folder.id) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(folder.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                                    IconButton(onClick = { expandedMenuFolderId = folder.id }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Opsi Folder")
                                    }
                                    DropdownMenu(
                                        expanded = expandedMenuFolderId == folder.id,
                                        onDismissRequest = { expandedMenuFolderId = null }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                folderToEdit = folder
                                                expandedMenuFolderId = null
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Hapus") },
                                            onClick = {
                                                viewModel.deleteFolder(folder.id)
                                                expandedMenuFolderId = null
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun RecommendationCard(
    totalBooks: Int,
    recommendationState: RecommendationState,
    onGetRecommendation: () -> Unit,
    onDismiss: () -> Unit,
    onBookClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Rekomendasi AI Terpadu",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                if (recommendationState is RecommendationState.Success) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Tutup",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            when (recommendationState) {
                is RecommendationState.Idle -> {
                    Text(
                        text = "Kamu sudah membaca $totalBooks buku. Mau AI rekomendasikan literatur baru yang setipe dengan seleramu?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Button(
                        onClick = onGetRecommendation,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analisis & Rekomendasikan Buku")
                    }
                }

                is RecommendationState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Membaca selera jurnal... Merangkai katalog buku...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                is RecommendationState.Success -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        recommendationState.recommendations.forEach { recommendedBook ->
                            Card(
                                onClick = { onBookClick(recommendedBook.title) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                // 📝 MURNI FULL TEXT LAYOUT: Mengisi penuh lebar kartu tanpa sisa kotak kosong hantu
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = recommendedBook.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Oleh ${recommendedBook.author}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    if (recommendedBook.genres.isNotEmpty()) {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            recommendedBook.genres.forEach { genre ->
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            color = MaterialTheme.colorScheme.primaryContainer,
                                                            shape = RoundedCornerShape(4.dp)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = genre,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // 🔥 FIX: Karakter huruf Mandarin hantu '激' sudah resmi disapu bersih total!
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )

                                    Text(
                                        text = recommendedBook.shortDescription,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                is RecommendationState.Error -> {
                    Text(
                        text = "Gagal memuat rekomendasi: ${recommendationState.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterboxBookCard(book: Book, reviewState: ReviewState, onGenerateReview: () -> Unit, onBookClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onBookClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp, 90.dp).clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book.authorsFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    if (book.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${book.notes}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (reviewState) {
                is ReviewState.Idle -> {
                    OutlinedButton(
                        onClick = onGenerateReview,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Review AI", style = MaterialTheme.typography.labelMedium)
                    }
                }

                is ReviewState.Loading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Membuat review...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                is ReviewState.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AutoAwesome, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                                Text(text = "Review oleh AI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = reviewState.review, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }

                is ReviewState.Error -> {
                    Text(text = "Gagal: ${reviewState.message}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    OutlinedButton(onClick = onGenerateReview, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                        Text("Coba Lagi")
                    }
                }
            }
        }
    }
}
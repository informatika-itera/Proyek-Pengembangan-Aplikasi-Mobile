package com.example.hujjah.presentation.screens.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hujjah.core.util.formatToDisplay
import com.example.hujjah.presentation.components.LoadingIndicator
import com.example.hujjah.presentation.components.NoteCard
import com.example.hujjah.presentation.components.hujjah.HujjahEmptyState
import com.example.hujjah.presentation.theme.LocalHujjahColors
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddNote: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: NotesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalHujjahColors.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NotesEvent.Error -> snackbarHostState.showSnackbar(event.message)
                is NotesEvent.NotePinnedToggled -> snackbarHostState.showSnackbar("Status pin diubah")
                is NotesEvent.NoteDeleted -> snackbarHostState.showSnackbar("Catatan berhasil dihapus")
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Catatan Harian",
                        fontWeight = FontWeight.Bold,
                        color = colors.goldHighlight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = colors.goldHighlight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddNote,
                containerColor = colors.goldHighlight,
                contentColor = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Catatan")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // ==================== SEARCH BAR WITH iOS FAST DELETE (X) ====================
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Cari Catatan...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.goldHighlight) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus Pencarian",
                                tint = colors.goldHighlight
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.goldHighlight,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ==================== CATEGORIES FILTER CHIPS ROW ====================
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    val isAllSelected = uiState.selectedCategory == null
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { viewModel.onCategorySelected(null) },
                        label = { Text("Semua") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.goldHighlight,
                            selectedLabelColor = MaterialTheme.colorScheme.background
                        )
                    )
                }

                items(uiState.categories) { category ->
                    val isSelected = uiState.selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.goldHighlight,
                            selectedLabelColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==================== LIST OF NOTES ====================
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            } else if (uiState.notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    HujjahEmptyState(
                        title = "Belum Ada Catatan",
                        message = if (uiState.searchQuery.isNotEmpty()) {
                            "Tidak ada catatan yang cocok dengan pencarian \"${uiState.searchQuery}\""
                        } else {
                            "Mulai catat pemikiran Anda atau simpan nasihat spiritual dari AI Hujjah Lens."
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.notes, key = { it.id }) { note ->
                        HujjahNoteCard(
                            note = note,
                            onClick = { onNavigateToDetail(note.id) },
                            onPinClick = { viewModel.togglePin(note.id) },
                            onDeleteClick = { viewModel.deleteNote(note.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HujjahNoteCard(
    note: com.example.hujjah.domain.model.Note,
    onClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val colors = LocalHujjahColors.current
    
    // Parse rujukan dalil dari konten
    val rawContent = note.content
    val regex = Regex("""\n\n\[Rujukan:\s*(Quran|Hadits)\s*(\|\|\||\|)\s*(.*?)\s*\]""")
    val match = regex.find(rawContent)
    val cleanText = if (match != null) rawContent.replace(match.value, "") else rawContent
    
    var refType = ""
    var refSource = ""
    var refNumber = ""
    var refArabic = ""
    var refTranslation = ""

    if (match != null) {
        val fullTag = match.value
        val tagContent = fullTag.trim().removeSurrounding("[Rujukan:", "]").trim()
        val parts = if (tagContent.contains("|||")) {
            tagContent.split("|||").map { it.trim() }
        } else {
            tagContent.split("|").map { it.trim() }
        }
        if (parts.size >= 3) {
            refType = parts[0]
            refSource = parts[1]
            refNumber = parts[2]
            if (parts.size >= 5) {
                refArabic = parts[3]
                refTranslation = parts[4]
            }
        }
    }

    // Pemetaan warna berdasarkan tema monokrom OLED pekat (Warna pastel dihilangkan!)
    val cardBgColor = if (colors.isDarkTheme) {
        Color.Black
    } else {
        Color.White
    }
    
    // Penanda status terpin menggunakan border emas solid. Kartu biasa menggunakan border minimalis.
    val cardBorderColor = if (note.isPinned) {
        colors.goldHighlight
    } else {
        if (colors.isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFE5E5E5)
    }
    
    val borderThickness = if (note.isPinned) 1.5.dp else 1.dp

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = BorderStroke(borderThickness, cardBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Kategori & Aksi Hapus (PushPin/Bintang dihilangkan dari pojok kanan atas!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge Kategori
                    Box(
                        modifier = Modifier
                            .background(
                                color = colors.islamicGreen.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = note.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.islamicGreen
                        )
                    }
                    
                    // Badge Rujukan Dalil jika ada
                    if (refType.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = colors.goldHighlight.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (refType == "Quran") "📖 Al-Qur'an" else "📚 Hadits",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.goldHighlight
                            )
                        }
                    }
                }

                // Hanya aksi Hapus saja di pojok kanan atas. Bintang/Pin dihilangkan!
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body: Judul & Konten
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (colors.isDarkTheme) Color.White else colors.islamicGreen,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = cleanText,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                lineHeight = 18.sp
            )

            // Render isi rujukan dalil (Arab & terjemahan) jika ada langsung di dalam kartu
            if (refType.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) Color(0xFF0F0F0F) else Color(0xFFF7F7F7)
                    ),
                    border = BorderStroke(0.5.dp, colors.goldHighlight.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (refType == "Quran") "📖 Surah $refSource: Ayat $refNumber" else "📚 Hadits $refSource: No. $refNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.goldHighlight
                        )
                        if (refArabic.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = refArabic,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.End,
                                lineHeight = 22.sp,
                                modifier = Modifier.fillMaxWidth(),
                                color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                            )
                        }
                        if (refTranslation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"$refTranslation\"",
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                maxLines = 2,
                                lineHeight = 15.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Waktu modifikasi terakhir
            Text(
                text = "Diperbarui: ${note.updatedAt.formatToDisplay()}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

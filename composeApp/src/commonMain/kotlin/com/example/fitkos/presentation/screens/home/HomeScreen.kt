package com.example.fitkos.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitkos.domain.model.Note
import com.example.fitkos.domain.model.NoteCategory
import com.example.fitkos.domain.usecase.NoteSortBy
import com.example.fitkos.presentation.components.ErrorState
import com.example.fitkos.presentation.components.LoadingIndicator
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddNote: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Catatan Makan",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Urutkan")
                    }

                    SortDropdownMenu(
                        expanded = showSortMenu,
                        currentSortBy = currentSortBy,
                        onSortSelected = {
                            viewModel.onSortByChanged(it)
                            showSortMenu = false
                        },
                        onDismiss = { showSortMenu = false }
                    )

                    IconButton(onClick = onNavigateToAI) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Assistant")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddNote,
                containerColor = MaterialTheme.colorScheme.primary
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
            val query = when (val state = uiState) {
                is HomeUiState.Success -> state.query
                is HomeUiState.Empty -> state.query
                else -> ""
            }

            MealSearchField(
                query = query,
                onQueryChange = viewModel::onSearchQueryChange,
                onClear = viewModel::clearSearch
            )

            Spacer(modifier = Modifier.height(12.dp))

            CategoryFilterRow(
                selectedCategory = when (val state = uiState) {
                    is HomeUiState.Success -> state.category
                    is HomeUiState.Empty -> state.category
                    else -> null
                },
                onCategorySelected = viewModel::onCategorySelected
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingIndicator()
                }

                is HomeUiState.Success -> {
                    MealList(
                        notes = state.notes,
                        onNoteClick = onNavigateToDetail
                    )
                }

                is HomeUiState.Empty -> {
                    EmptyMealState(
                        isFiltered = state.query.isNotBlank() || state.category != null
                    )
                }

                is HomeUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.clearSearch() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MealSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari makanan...") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Hapus")
                }
            }
        },
        shape = RoundedCornerShape(14.dp)
    )
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: NoteCategory?,
    onCategorySelected: (NoteCategory?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Semua") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        items(NoteCategory.entries) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = {
                    onCategorySelected(
                        if (selectedCategory == category) null else category
                    )
                },
                label = { Text(category.shortLabel()) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun MealList(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Hari ini",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(
            items = notes,
            key = { it.id }
        ) { note ->
            MealLogCard(
                note = note,
                onClick = { onNoteClick(note.id) }
            )
        }
    }
}

@Composable
private fun MealLogCard(
    note: Note,
    onClick: () -> Unit
) {
    val mealContent = remember(note.content) {
        parseMealContent(note.content)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = note.category.icon(),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " ${note.category.shortLabel()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = note.title.ifBlank { "Makanan tanpa nama" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (mealContent.note.isNotBlank()) {
                    Text(
                        text = mealContent.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                if (mealContent.price.isNotBlank()) {
                    Text(
                        text = "Rp${mealContent.price.formatRupiah()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = note.updatedAt.formatTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyMealState(
    isFiltered: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Outlined.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            Text(
                text = if (isFiltered) {
                    "Makanan tidak ditemukan"
                } else {
                    "Belum ada catatan makanan"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (isFiltered) {
                    "Coba ubah kata kunci atau filter"
                } else {
                    "Tap + untuk menambah catatan makanan"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SortDropdownMenu(
    expanded: Boolean,
    currentSortBy: NoteSortBy,
    onSortSelected: (NoteSortBy) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        NoteSortBy.entries.forEach { sortBy ->
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(sortBy.displayName)

                        if (sortBy == currentSortBy) {
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                onClick = { onSortSelected(sortBy) }
            )
        }
    }
}

private fun NoteCategory.shortLabel(): String {
    return when (this) {
        NoteCategory.BREAKFAST -> "Pagi"
        NoteCategory.LUNCH -> "Siang"
        NoteCategory.DINNER -> "Malam"
        NoteCategory.SNACK -> "Camilan"
        NoteCategory.DRINK -> "Minum"
        NoteCategory.OTHER -> "Lainnya"
    }
}

private fun NoteCategory.icon(): String {
    return when (this) {
        NoteCategory.BREAKFAST -> "☀️"
        NoteCategory.LUNCH -> "🍽️"
        NoteCategory.DINNER -> "🌙"
        NoteCategory.SNACK -> "🍪"
        NoteCategory.DRINK -> "💧"
        NoteCategory.OTHER -> "🥗"
    }
}

private fun Instant.formatTime(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$hour:$minute"
}

private fun parseMealContent(content: String): MealContent {
    val lines = content.lines()
    val firstLine = lines.firstOrNull().orEmpty()
    val hasPrice = firstLine.startsWith("Harga: Rp")

    return if (hasPrice) {
        MealContent(
            price = firstLine.removePrefix("Harga: Rp").filter { it.isDigit() },
            note = lines.drop(1).joinToString("\n").trim()
        )
    } else {
        MealContent(
            price = "",
            note = content.trim()
        )
    }
}

private fun String.formatRupiah(): String {
    if (this.isBlank()) return this

    return this
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}

private data class MealContent(
    val price: String,
    val note: String
)

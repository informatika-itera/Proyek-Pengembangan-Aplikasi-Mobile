package com.example.synesthesia.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import com.example.synesthesia.presentation.components.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.domain.model.Note
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.domain.usecase.NoteSortBy
import com.example.synesthesia.presentation.components.EmptyState
import com.example.synesthesia.presentation.components.ErrorState
import com.example.synesthesia.presentation.components.LoadingIndicator
import com.example.synesthesia.presentation.components.NoteCard
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
    var showSearch by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    AuroraBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    title = { 
                        if (showSearch) {
                            SearchField(
                                query = when (val state = uiState) {
                                    is HomeUiState.Success -> state.query
                                    is HomeUiState.Empty -> state.query
                                    else -> ""
                                },
                                onQueryChange = viewModel::onSearchQueryChange,
                                onClear = {
                                    viewModel.clearSearch()
                                    showSearch = false
                                }
                            )
                        } else {
                            Text("NoteAI", style = MaterialTheme.typography.headlineMedium)
                        }
                    },
                    actions = {
                        if (!showSearch) {
                            IconButton(onClick = { showSearch = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Cari")
                            }
                            
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(Icons.Outlined.Sort, contentDescription = "Urutkan")
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
                        }
                        
                        IconButton(onClick = onNavigateToAI) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = "AI Assistant")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToAddNote,
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Catatan")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CategoryFilterRow(
                    selectedCategory = when (val state = uiState) {
                        is HomeUiState.Success -> state.category
                        is HomeUiState.Empty -> state.category
                        else -> null
                    },
                    onCategorySelected = viewModel::onCategorySelected
                )
                
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        LoadingIndicator()
                    }
                    
                    is HomeUiState.Success -> {
                        NotesList(
                            notes = state.notes,
                            onNoteClick = onNavigateToDetail,
                            onPinClick = viewModel::togglePin,
                            onDeleteClick = viewModel::deleteNote
                        )
                    }
                    
                    is HomeUiState.Empty -> {
                        EmptyState(
                            title = if (state.query.isNotBlank() || state.category != null) {
                                "Tidak Ditemukan"
                            } else {
                                "Belum Ada Catatan"
                            },
                            message = if (state.query.isNotBlank() || state.category != null) {
                                "Coba ubah kata kunci atau filter"
                            } else {
                                "Tap + untuk membuat catatan baru"
                            },
                            icon = {
                                Icon(
                                    Icons.Outlined.NoteAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                            }
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
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Cari catatan...", color = Color.White.copy(alpha = 0.5f)) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White.copy(alpha = 0.6f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            cursorColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth(),
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
        }
    )
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
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                onClick = { onSortSelected(sortBy) }
            )
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: NoteCategory?,
    onCategorySelected: (NoteCategory?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Semua") },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = Color.White.copy(alpha = 0.6f),
                    selectedLabelColor = Color.Black,
                    selectedContainerColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color.White.copy(alpha = 0.2f),
                    selectedBorderColor = Color.Transparent,
                    enabled = true,
                    selected = selectedCategory == null
                )
            )
        }
        
        items(NoteCategory.entries) { category ->
            val isSelected = selectedCategory == category
            FilterChip(
                selected = isSelected,
                onClick = { 
                    onCategorySelected(
                        if (isSelected) null else category
                    )
                },
                label = { Text(category.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = Color.White.copy(alpha = 0.6f),
                    selectedLabelColor = Color.Black,
                    selectedContainerColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color.White.copy(alpha = 0.2f),
                    selectedBorderColor = Color.Transparent,
                    enabled = true,
                    selected = isSelected
                )
            )
        }
    }
}

@Composable
private fun NotesList(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    onPinClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = notes,
            key = { it.id }
        ) { note ->
            NoteCard(
                note = note,
                onClick = { onNoteClick(note.id) },
                onPinClick = { onPinClick(note.id) },
                onDeleteClick = { onDeleteClick(note.id) }
            )
        }
    }
}

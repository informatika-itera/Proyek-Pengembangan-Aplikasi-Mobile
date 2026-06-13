package com.example.bookku.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookku.domain.model.Book
import com.example.bookku.domain.model.BookGenre
import com.example.bookku.domain.model.NoteSortBy
import com.example.bookku.presentation.components.EmptyState
import com.example.bookku.presentation.components.ErrorState
import com.example.bookku.presentation.components.LoadingIndicator
import com.example.bookku.presentation.components.NoteCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onnavigateToAddBook: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentSortBy by viewModel.sortBy.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    var showSortMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                }) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onnavigateToAddBook,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bagikan Buku")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // Custom Stylish Header
            HomeHeader(
                userName = userName ?: "Pembaca",
                isDarkMode = isDarkMode,
                onToggleTheme = viewModel::toggleDarkMode,
                onLogout = { showLogoutDialog = true }
            )

            // Search and Filter Bar
            SearchBarRow(
                query = when (val state = uiState) {
                    is HomeUiState.Success -> state.query
                    is HomeUiState.Empty -> state.query
                    else -> ""
                },
                onQueryChange = viewModel::onSearchQueryChange,
                onSortClick = { showSortMenu = true }
            )

            CategoryFilterRow(
                selectedCategory = when (val state = uiState) {
                    is HomeUiState.Success -> state.category
                    is HomeUiState.Empty -> state.category
                    else -> null
                },
                onCategorySelected = viewModel::onCategorySelected
            )
            
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is HomeUiState.Loading -> LoadingIndicator()
                    
                    is HomeUiState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Koleksi Bukumu",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            items(state.books, key = { it.id }) { book ->
                                NoteCard(
                                    book = book,
                                    onClick = { onNavigateToDetail(book.id) },
                                    onPinClick = { viewModel.togglePin(book.id) },
                                    onDeleteClick = { viewModel.deleteBook(book.id) },
                                    isOwner = true
                                )
                            }
                        }
                    }
                    
                    is HomeUiState.Empty -> {
                        EmptyState(
                            title = "Belum Ada Buku",
                            message = "Mulai bagikan koleksi bukumu!",
                            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) }
                        )
                    }
                    
                    is HomeUiState.Error -> ErrorState(message = state.message, onRetry = { viewModel.clearSearch() })
                }
            }
        }
    }

    if (showSortMenu) {
        SortBottomSheet(
            currentSortBy = currentSortBy,
            onSortSelected = { 
                viewModel.onSortByChanged(it)
                showSortMenu = false
            },
            onDismiss = { showSortMenu = false }
        )
    }
}

@Composable
fun HomeHeader(
    userName: String,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Halo, $userName! 👋",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Temukan Buku Menarik",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            }
            
            Row {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Theme"
                    )
                }
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBarRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            placeholder = { Text("Cari buku...") },
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(
            onClick = onSortClick,
            modifier = Modifier
                .size(52.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
        ) {
            Icon(Icons.Outlined.Sort, contentDescription = "Sort")
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selectedCategory: BookGenre?,
    onCategorySelected: (BookGenre?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Semua") }
            )
        }
        
        items(BookGenre.entries) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(if (selectedCategory == category) null else category) },
                label = { Text(category.displayName) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortBy: NoteSortBy,
    onSortSelected: (NoteSortBy) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "Urutkan Berdasarkan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            NoteSortBy.entries.forEach { sortBy ->
                val isSelected = sortBy == currentSortBy
                NavigationDrawerItem(
                    label = { Text(sortBy.displayName) },
                    selected = isSelected,
                    onClick = { onSortSelected(sortBy) },
                    icon = {
                        if (isSelected) Icon(Icons.Default.Check, contentDescription = null)
                    },
                    modifier = Modifier.padding(vertical = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

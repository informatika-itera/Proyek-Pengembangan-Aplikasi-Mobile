package com.example.hujjah.presentation.screens.hadith

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hujjah.presentation.components.hujjah.HujjahMenuItem
import com.example.hujjah.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.hujjah.presentation.components.hujjah.HujjahEmptyState
import com.example.hujjah.presentation.components.hujjah.HujjahErrorState
import com.example.hujjah.presentation.components.hujjah.shimmerBrush
import com.example.hujjah.presentation.components.hujjah.ShimmerHadithItem
import com.example.hujjah.presentation.theme.LocalHujjahColors
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.example.hujjah.data.local.datastore.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HadithViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalHujjahColors.current
    
    val userPreferences = koinInject<UserPreferences>()
    val arabicFontSize by userPreferences.arabicFontSize.collectAsStateWithLifecycle(initialValue = 22)

    val isViewingBook = uiState.currentBookId != null

    // Text glow configuration for dark mode
    val textGlow = if (colors.isDarkTheme) {
        Shadow(
            color = colors.goldHighlight.copy(alpha = 0.8f),
            offset = Offset(0f, 0f),
            blurRadius = 8f
        )
    } else {
        Shadow.None
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isViewingBook) uiState.currentBookName.orEmpty() else "Hadits Ensiklopedia",
                        fontWeight = FontWeight.Bold,
                        color = colors.goldHighlight,
                        style = MaterialTheme.typography.titleLarge.copy(
                            shadow = textGlow
                        )
                    )
                },
                navigationIcon = {
                    if (isViewingBook) {
                        IconButton(onClick = { viewModel.selectBook("", "") }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.goldHighlight
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            HujjahSprint2MenuBar(
                currentItem = HujjahMenuItem.HADITH,
                onNavigateToHome = onNavigateToHome,
                onNavigateToLens = onNavigateToLens,
                onNavigateToQuran = onNavigateToQuran,
                onNavigateToHadith = onNavigateToHadith,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (!isViewingBook) {
                // ==================== 1. GRID VIEW OF BOOKS ====================
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "9 Kitab Perawi Hadits",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (colors.isDarkTheme) Color.White else colors.islamicGreen,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    if (uiState.isLoading) {
                        // Grid Shimmer Loading for Books
                        val brush = shimmerBrush()
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(6) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .background(brush, shape = RoundedCornerShape(24.dp))
                                )
                            }
                        }
                    } else if (uiState.error != null) {
                        HujjahErrorState(
                            message = uiState.error ?: "Gagal memuat kitab hadits",
                            onRetry = { viewModel.selectBook("", "") } // Reloads by resetting
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(uiState.books) { book ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .goldGlowShadow(colors.isDarkTheme, colors.goldHighlight, RoundedCornerShape(24.dp))
                                        .clickable {
                                            viewModel.selectBook(book.id, book.name)
                                        },
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.3f)),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (colors.isDarkTheme) Color.Black else Color.White
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "📚",
                                            fontSize = 28.sp
                                        )
                                        Column {
                                            Text(
                                                text = book.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = colors.goldHighlight
                                            )
                                            Text(
                                                text = "${book.totalHadith} Hadits",
                                                fontSize = 11.sp,
                                                color = if (colors.isDarkTheme) Color.White.copy(alpha = 0.6f) else colors.islamicGreen.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ==================== 2. PAGINATED LIST VIEW OF HADITHS ====================
                Column(modifier = Modifier.fillMaxSize()) {
                    // ==================== SEARCH BAR WITH iOS FAST DELETE (X) ====================
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = viewModel::onSearchQueryChanged,
                            placeholder = { Text("Cari Nomor Hadits...", fontSize = 14.sp) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { viewModel.performSearch() }
                            ),
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
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.goldHighlight,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { viewModel.performSearch() },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.goldHighlight),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.background)
                        }
                    }

                    if (uiState.isLoading) {
                        // Shimmer Loading for Hadiths
                        val brush = shimmerBrush()
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(4) {
                                ShimmerHadithItem(brush = brush)
                            }
                        }
                    } else if (uiState.error != null && uiState.hadiths.isEmpty()) {
                        HujjahErrorState(
                            message = uiState.error ?: "Terjadi kesalahan",
                            onRetry = { viewModel.performSearch() }
                        )
                    } else if (uiState.hadiths.isEmpty()) {
                        HujjahEmptyState(
                            title = "Hadits Tidak Ditemukan",
                            message = "Tidak ada nomor hadits yang cocok dengan pencarian \"${uiState.searchQuery}\" di kitab ini."
                        )
                    } else {
                        val listState = rememberLazyListState()

                        // Detect scrolling to bottom for Lazy Loading
                        val shouldLoadMore = remember {
                            derivedStateOf {
                                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                                    ?: return@derivedStateOf false
                                lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
                            }
                        }

                        LaunchedEffect(shouldLoadMore.value) {
                            if (shouldLoadMore.value && uiState.searchQuery.isBlank()) {
                                viewModel.fetchNextPage()
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(uiState.hadiths) { hadith ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .goldGlowShadow(colors.isDarkTheme, colors.goldHighlight, RoundedCornerShape(20.dp)),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (colors.isDarkTheme) Color.Black else Color.White
                                    ),
                                    border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.25f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "No. ${hadith.number}",
                                                fontWeight = FontWeight.Bold,
                                                color = colors.goldHighlight,
                                                fontSize = 12.sp,
                                                style = TextStyle(shadow = textGlow)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = hadith.arab,
                                            fontSize = arabicFontSize.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End,
                                            lineHeight = 34.sp,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = if (colors.isDarkTheme) Color.White else colors.islamicGreen,
                                            style = TextStyle(shadow = textGlow)
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = "\"${hadith.translation}\"",
                                            fontSize = 13.sp,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            if (uiState.isPageLoading) {
                                item {
                                    val brush = shimmerBrush()
                                    ShimmerHadithItem(brush = brush)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== GOLD GLOW SHADOW EXTENSION MODIFIER ====================
private fun Modifier.goldGlowShadow(
    enabled: Boolean,
    color: Color,
    shape: androidx.compose.ui.graphics.Shape
): Modifier {
    return if (enabled) {
        this
            .border(4.dp, color.copy(alpha = 0.08f), shape)
            .border(2.dp, color.copy(alpha = 0.2f), shape)
            .border(0.5.dp, color.copy(alpha = 0.5f), shape)
    } else {
        this
    }
}

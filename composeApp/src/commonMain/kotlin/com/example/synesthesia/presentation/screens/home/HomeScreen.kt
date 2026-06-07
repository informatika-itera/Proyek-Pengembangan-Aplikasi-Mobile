package com.example.synesthesia.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.synesthesia.domain.model.NoteCategory
import com.example.synesthesia.presentation.components.CelestialBackground
import com.example.synesthesia.presentation.components.EmptyStateView
import com.example.synesthesia.presentation.theme.BrightYellow
import com.example.synesthesia.presentation.theme.RoyalBlue
import com.example.synesthesia.presentation.theme.SpaceBlack
import com.example.synesthesia.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    onNavigateToAddNote: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToSettings: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    var isSearchActive by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                title = { 
                    AnimatedVisibility(
                        visible = !isSearchActive,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        Column {
                            Text("SYNESTHESIA", style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ))
                            Text("Welcome, $userName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.animateContentSize()
                    ) {
                        AnimatedVisibility(
                            visible = isSearchActive,
                            enter = expandHorizontally() + fadeIn(),
                            exit = shrinkHorizontally() + fadeOut()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = viewModel::onSearchQueryChange,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(end = Spacing.sm),
                                shape = RoundedCornerShape(50.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
                                    focusedBorderColor = if (MaterialTheme.colorScheme.background == SpaceBlack) BrightYellow else RoyalBlue.copy(alpha = 0.5f),
                                    unfocusedBorderColor = (if (MaterialTheme.colorScheme.background == SpaceBlack) BrightYellow else RoyalBlue).copy(alpha = 0.3f)
                                ),
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = if (MaterialTheme.colorScheme.background == SpaceBlack) BrightYellow else RoyalBlue
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = viewModel::clearSearch) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                placeholder = {
                                    Text(
                                        "Search memories...",
                                        style = TextStyle(fontStyle = FontStyle.Italic)
                                    )
                                },
                                singleLine = true
                            )
                        }

                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Box {
                                Icon(
                                    if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = if (MaterialTheme.colorScheme.background == SpaceBlack) BrightYellow else RoyalBlue
                                )
                                if (!isSearchActive && searchQuery.isNotEmpty()) {
                                    Surface(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .align(Alignment.TopEnd),
                                        color = BrightYellow,
                                        shape = CircleShape
                                    ) {}
                                }
                            }
                        }

                        if (!isSearchActive) {
                            IconButton(onClick = { showFilterSheet = true }) {
                                Icon(
                                    Icons.Default.Tune,
                                    contentDescription = "Filter",
                                    tint = if (MaterialTheme.colorScheme.background == SpaceBlack) BrightYellow else RoyalBlue
                                )
                            }
                            IconButton(onClick = onNavigateToSettings) {
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddNote,
                containerColor = RoyalBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Memory")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (val state = uiState) {
                is HomeUiState.Success -> {
                    val memoryCount = remember(state.notes) { state.notes.size }
                    
                    ConstellationCanvas(
                        notes = state.notes,
                        onNoteClick = onNavigateToDetail,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Small Insight Overlay (Top Left)
                    val infiniteTransition = rememberInfiniteTransition(label = "badge")
                    val badgeScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )

                    Column(modifier = Modifier.padding(Spacing.md).align(Alignment.TopStart)) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp)).scale(badgeScale)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = RoyalBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("$memoryCount Memories in Galaxy", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Empty -> {
                    if (state.query.isNotEmpty()) {
                        EmptyStateView(
                            title = "No matches found",
                            description = "Your galaxy doesn't contain memories matching \"${state.query}\"",
                            icon = Icons.Default.SearchOff
                        )
                    } else {
                        EmptyStateView(
                            title = "Quiet Galaxy",
                            description = "Begin your journey by engraving your first memory.",
                            icon = Icons.Default.AutoAwesome
                        )
                    }
                }
                is HomeUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
            }
            
            // AI Button
            IconButton(
                onClick = onNavigateToAI,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(Spacing.lg)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = RoyalBlue)
            }
        }
        
        if (showFilterSheet) {
            FilterSortBottomSheet(
                selectedCategory = when(val state = uiState) {
                    is HomeUiState.Success -> state.category
                    is HomeUiState.Empty -> state.category
                    else -> null
                },
                selectedSort = when(val state = uiState) {
                    is HomeUiState.Success -> state.sortBy
                    else -> com.example.synesthesia.domain.usecase.NoteSortBy.UPDATED_DESC
                },
                onCategorySelected = {
                    viewModel.onCategorySelected(it)
                    showFilterSheet = false
                },
                onSortByChanged = {
                    viewModel.onSortByChanged(it)
                    showFilterSheet = false
                },
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortBottomSheet(
    selectedCategory: NoteCategory?,
    selectedSort: com.example.synesthesia.domain.usecase.NoteSortBy,
    onCategorySelected: (NoteCategory?) -> Unit,
    onSortByChanged: (com.example.synesthesia.domain.usecase.NoteSortBy) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xl, start = Spacing.lg, end = Spacing.lg)
        ) {
            Text(
                "Filter Memories",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("By Category", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("All") }
                )
                NoteCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category.displayName) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Sort By", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            com.example.synesthesia.domain.usecase.NoteSortBy.entries.forEach { sort ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onSortByChanged(sort) },
                    color = if (selectedSort == sort) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            sort.displayName,
                            modifier = Modifier.weight(1f),
                            fontWeight = if (selectedSort == sort) FontWeight.Bold else FontWeight.Normal
                        )
                        if (selectedSort == sort) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

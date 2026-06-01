package com.example.tabungin.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tabungin.domain.model.Target
import com.example.tabungin.presentation.components.formatRupiah
import com.example.tabungin.presentation.components.parseHexColor
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddEdit: () -> Unit,
    onNavigateToRiwayat: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TabungIn",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddEdit,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Target")
                Spacer(Modifier.width(8.dp))
                Text("Target Baru")
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Beranda",
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    label = { Text("Beranda", style = MaterialTheme.typography.labelMedium) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedTab == 1) Icons.Filled.QueryStats else Icons.Outlined.QueryStats,
                            contentDescription = "Statistik",
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    label = { Text("Statistik", style = MaterialTheme.typography.labelMedium) },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        onNavigateToStatistics()
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedTab == 2) Icons.Filled.SmartToy else Icons.Outlined.SmartToy,
                            contentDescription = "AI Assistant",
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    label = { Text("AI", style = MaterialTheme.typography.labelMedium) },
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        onNavigateToAI()
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profil",
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    label = { Text("Profil", style = MaterialTheme.typography.labelMedium) },
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        onNavigateToSettings()
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.tertiary,
                        selectedTextColor = MaterialTheme.colorScheme.tertiary,
                        indicatorColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Summary Card
            item {
                HomeSummaryCard(
                    totalTerkumpul = uiState.totalTerkumpul,
                    totalTarget = uiState.totalTarget,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Search and Filter Section
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        placeholder = { Text("Cari target...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Cari",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Hapus")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    // Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sort Dropdown
                        var sortExpanded by remember { mutableStateOf(false) }
                        Box {
                            FilterChip(
                                selected = uiState.sortOrder != SortOrder.TERBARU,
                                onClick = { sortExpanded = true },
                                label = {
                                    Text(
                                        when (uiState.sortOrder) {
                                            SortOrder.TERBARU -> "Terbaru"
                                            SortOrder.TERLAMA -> "Terlama"
                                            SortOrder.NAMA_AZ -> "A-Z"
                                            SortOrder.NAMA_ZA -> "Z-A"
                                            SortOrder.PROGRESS_TERTINGGI -> "Progress Tertinggi"
                                            SortOrder.PROGRESS_TERENDAH -> "Progress Terendah"
                                        }
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Sort,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                            DropdownMenu(
                                expanded = sortExpanded,
                                onDismissRequest = { sortExpanded = false }
                            ) {
                                SortOrder.entries.forEach { sortOrder ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                when (sortOrder) {
                                                    SortOrder.TERBARU -> "Terbaru"
                                                    SortOrder.TERLAMA -> "Terlama"
                                                    SortOrder.NAMA_AZ -> "Nama A-Z"
                                                    SortOrder.NAMA_ZA -> "Nama Z-A"
                                                    SortOrder.PROGRESS_TERTINGGI -> "Progress Tertinggi"
                                                    SortOrder.PROGRESS_TERENDAH -> "Progress Terendah"
                                                }
                                            )
                                        },
                                        onClick = {
                                            viewModel.onSortOrderChange(sortOrder)
                                            sortExpanded = false
                                        },
                                        leadingIcon = {
                                            if (uiState.sortOrder == sortOrder) {
                                                Icon(Icons.Default.Check, contentDescription = null)
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        // Filter by Progress
                        FilterChip(
                            selected = uiState.filterType == FilterType.PROGRESS,
                            onClick = {
                                viewModel.onFilterTypeChange(
                                    if (uiState.filterType == FilterType.PROGRESS) FilterType.ALL
                                    else FilterType.PROGRESS
                                )
                            },
                            label = { Text(">50% Progress") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )

                        // Filter by Deadline (within 3 months)
                        FilterChip(
                            selected = uiState.filterType == FilterType.DEADLINE,
                            onClick = {
                                viewModel.onFilterTypeChange(
                                    if (uiState.filterType == FilterType.DEADLINE) FilterType.ALL
                                    else FilterType.DEADLINE
                                )
                            },
                            label = { Text("3 Bulan Ini") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }

            // Active Filters Display
            if (uiState.searchQuery.isNotEmpty() || uiState.filterType != FilterType.ALL || uiState.sortOrder != SortOrder.TERBARU) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${uiState.filteredTargets.size} hasil",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextButton(onClick = viewModel::clearFilters) {
                            Icon(
                                Icons.Default.FilterAltOff,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Hapus Filter", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Target Aktif Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Target Aktif",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.filteredTargets.isNotEmpty()) {
                        Text(
                            "${uiState.filteredTargets.size} target",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                uiState.filteredTargets.isEmpty() && uiState.targets.isEmpty() -> {
                    item {
                        EmptyTargetState(
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }

                uiState.filteredTargets.isEmpty() -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Tidak ada hasil",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Coba ubah kata kunci atau filter",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(16.dp))
                            TextButton(onClick = viewModel::clearFilters) {
                                Text("Hapus Filter")
                            }
                        }
                    }
                }

                else -> {
                    items(
                        uiState.filteredTargets,
                        key = { it.id }
                    ) { target ->
                        HomeTargetCard(
                            target = target,
                            onClick = { onNavigateToDetail(target.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSummaryCard(
    totalTerkumpul: Double,
    totalTarget: Double,
    modifier: Modifier = Modifier
) {
    val progress = if (totalTarget <= 0) 0f
    else (totalTerkumpul / totalTarget).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "Total Tabungan",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatRupiah(totalTerkumpul),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "Target",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatRupiah(totalTarget),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${(progress * 100).toDouble().toInt()}% tercapai",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            "✨ Tetap Semangat!",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTargetCard(
    target: Target,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = parseHexColor(target.warna)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        target.icon,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        target.nama,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${formatRupiah(target.terkumpul)} / ${formatRupiah(target.targetAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${(target.progres * 100).toDouble().toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        target.deadline,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { target.progres.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun EmptyTargetState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(100.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                )
            ) {
                Text("🎯", style = MaterialTheme.typography.displayMedium)
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Text(
                "Mulai journey menabungmu dengan membuat target pertama! 🚀",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Belum Ada Target Tabungan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Tap \"Target Baru\" untuk mulai menabung",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
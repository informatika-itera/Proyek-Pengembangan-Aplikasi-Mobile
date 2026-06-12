package com.example.bridgebit.presentation.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.bridgebit.presentation.components.TranslationCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToWorkspace: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAI: () -> Unit, // Tergantung jika masih dipakai
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    // Status untuk membuka/menutup Dropdown
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedLanguage by remember { mutableStateOf(false) }

    // Daftar statis yang sesuai dengan AI Workspace Anda
    val availableCategories = listOf("Semua Kategori", "Teknologi & IT", "Akademik & Pendidikan", "Keuangan & Kripto", "Hiburan & Hobi", "Traveling & Transportasi", "Bisnis & Profesional", "Umum")
    val availableLanguages = listOf("Semua Bahasa", "Indonesia", "Inggris", "Jepang", "Korea", "Arab", "Jerman")

    val infiniteTransition = rememberInfiniteTransition(label = "fabPulseTransition")
    val fabScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabPulse"
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("BridgeBit History") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWorkspace,
                modifier = Modifier.scale(fabScale)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Terjemahan Baru")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            // SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                placeholder = { Text("Cari kata atau frasa...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // BARIS FILTER (MENDUKUNG SCROLL HORIZONTAL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)

                // 1. Filter Vault
                FilterChip(
                    selected = filterState.isVaultOnly,
                    onClick = { viewModel.toggleVaultFilter() },
                    label = { Text("Vault") }
                )

                // 2. Filter Kategori (Dropdown)
                Box {
                    val categoryRotation by animateFloatAsState(targetValue = if (expandedCategory) 180f else 0f)
                    FilterChip(
                        selected = filterState.selectedCategory != null,
                        onClick = { expandedCategory = true },
                        label = { Text(filterState.selectedCategory ?: "Kategori") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.rotate(categoryRotation)) }
                    )
                    DropdownMenu(expanded = expandedCategory, onDismissRequest = { expandedCategory = false }) {
                        availableCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.setCategoryFilter(if (cat == "Semua Kategori") null else cat)
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                // 3. Filter Bahasa (Dropdown)
                Box {
                    val languageRotation by animateFloatAsState(targetValue = if (expandedLanguage) 180f else 0f)
                    FilterChip(
                        selected = filterState.selectedLanguage != null,
                        onClick = { expandedLanguage = true },
                        label = { Text(filterState.selectedLanguage ?: "Bahasa") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.rotate(languageRotation)) }
                    )
                    DropdownMenu(expanded = expandedLanguage, onDismissRequest = { expandedLanguage = false }) {
                        availableLanguages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    viewModel.setLanguageFilter(if (lang == "Semua Bahasa") null else lang)
                                    expandedLanguage = false
                                }
                            )
                        }
                    }
                }

                // 4. Tombol Reset (Muncul hanya jika ada filter aktif)
                AnimatedVisibility(
                    visible = filterState.isVaultOnly || filterState.selectedCategory != null || filterState.selectedLanguage != null,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    TextButton(onClick = { viewModel.resetFilters() }) {
                        Text("Reset")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

            // DAFTAR RIWAYAT
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                when (state) {
                    is DashboardUiState.Loading -> CircularProgressIndicator()
                    is DashboardUiState.Empty -> {
                        val isSearch = searchQuery.isNotBlank() || filterState.selectedCategory != null || filterState.selectedLanguage != null || filterState.isVaultOnly
                        com.example.bridgebit.presentation.components.AnimatedEmptyState(
                            title = if (isSearch) "Data tidak ditemukan" else "Riwayat Kosong",
                            subtitle = if (isSearch) "Coba ubah kata kunci atau filter pencarian Anda." else "Belum ada riwayat terjemahan. Mulai terjemahkan frasa baru!"
                        )
                    }
                    is DashboardUiState.Success -> {
                        val historyList = (state as DashboardUiState.Success).history
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = historyList, key = { it.id }) { item ->
                                TranslationCard(
                                    translation = item,
                                    onClick = { onNavigateToDetail(item.id) },
                                    onVaultClick = { viewModel.toggleVaultStatus(item.id) },
                                    onDeleteClick = { viewModel.deleteTranslation(item.id) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                    is DashboardUiState.Error -> Text("Terjadi kesalahan memuat data.")
                }
            }
        }
    }
}
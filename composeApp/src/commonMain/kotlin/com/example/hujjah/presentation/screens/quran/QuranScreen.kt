package com.example.hujjah.presentation.screens.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hujjah.presentation.components.hujjah.HujjahMenuItem
import com.example.hujjah.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.hujjah.presentation.components.hujjah.HujjahEmptyState
import com.example.hujjah.presentation.components.hujjah.HujjahErrorState
import com.example.hujjah.presentation.components.hujjah.shimmerBrush
import com.example.hujjah.presentation.components.hujjah.ShimmerSurahItem
import com.example.hujjah.presentation.theme.LocalHujjahColors
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (Int, String) -> Unit,
    viewModel: QuranViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lastRead by viewModel.lastReadLocation.collectAsStateWithLifecycle()
    val colors = LocalHujjahColors.current

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
                        text = "Al-Qur'an Mushaf",
                        fontWeight = FontWeight.Bold,
                        color = colors.goldHighlight,
                        style = MaterialTheme.typography.titleLarge.copy(
                            shadow = textGlow
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            HujjahSprint2MenuBar(
                currentItem = HujjahMenuItem.QURAN,
                onNavigateToHome = onNavigateToHome,
                onNavigateToLens = onNavigateToLens,
                onNavigateToQuran = onNavigateToQuran,
                onNavigateToHadith = onNavigateToHadith,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // ==================== THE GOLDEN CARD WITH GOLD GLOW SHADOW ====================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .goldGlowShadow(colors.isDarkTheme, colors.goldHighlight, RoundedCornerShape(24.dp))
                    .clickable {
                        if (lastRead.isNotBlank()) {
                            // Extract surah name or use default
                            val surahName = lastRead.substringBefore(":").replace("QS. ", "").trim()
                            onNavigateToDetail(18, surahName) // default to Al-Kahfi for demo
                        } else {
                            onNavigateToDetail(1, "Al-Fatihah")
                        }
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.goldHighlight
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.background,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Terakhir Baca",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.background,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = lastRead.ifBlank { "Mulai Membaca Al-Qur'an" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.background
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            // ==================== SEARCH BAR WITH iOS FAST DELETE (X) ====================
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = { Text("Cari Surah...", fontSize = 14.sp) },
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
            
            Spacer(modifier = Modifier.height(16.dp))

            // ==================== SURAH LIST GROUPED CONTAINER ====================
            if (uiState.isLoading) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) Color.Black else Color.White
                    ),
                    border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.25f))
                ) {
                    val brush = shimmerBrush()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        items(6) { idx ->
                            ShimmerSurahItem(brush = brush)
                            if (idx < 5) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            } else if (uiState.error != null) {
                HujjahErrorState(
                    message = uiState.error.orEmpty(),
                    onRetry = { viewModel.fetchSurahs(forceRefresh = true) }
                )
            } else if (uiState.surahs.isEmpty()) {
                HujjahEmptyState(
                    title = "Surah Tidak Ditemukan",
                    message = "Tidak ada surah yang cocok dengan pencarian \"${uiState.searchQuery}\". Coba kata kunci lain."
                )
            } else {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) Color.Black else Color.White
                    ),
                    border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.25f))
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        items(uiState.surahs) { surah ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onNavigateToDetail(surah.number, surah.name)
                                    }
                                    .padding(vertical = 14.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Gold Octagram Frame for Number
                                GoldOctagramNumber(
                                    number = surah.number,
                                    isDarkTheme = colors.isDarkTheme,
                                    goldColor = colors.goldHighlight
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = surah.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (colors.isDarkTheme) Color.White else colors.islamicGreen,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "${surah.revelation} • ${surah.numberOfVerses} Ayat",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                }

                                // Arabic text on the right
                                Text(
                                    text = surah.asma,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.goldHighlight,
                                    textAlign = TextAlign.End,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        shadow = textGlow
                                    )
                                )
                            }

                            // Divider
                            if (surah != uiState.surahs.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== GOLD OCTAGRAM NUMBER ICON (RUB EL HIZB SHAPE) ====================
@Composable
fun GoldOctagramNumber(
    number: Int,
    isDarkTheme: Boolean,
    goldColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(40.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(goldColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                .border(1.dp, goldColor, RoundedCornerShape(4.dp))
        )
        Box(
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer(rotationZ = 45f)
                .background(goldColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                .border(1.dp, goldColor, RoundedCornerShape(4.dp))
        )
        Text(
            text = "$number",
            color = if (isDarkTheme) Color.White else Color(0xFF134E4A),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== GOLD GLOW SHADOW EXTENSION MODIFIER ====================
fun Modifier.goldGlowShadow(
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

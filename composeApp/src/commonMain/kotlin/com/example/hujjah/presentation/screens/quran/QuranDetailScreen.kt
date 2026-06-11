package com.example.hujjah.presentation.screens.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hujjah.presentation.theme.LocalHujjahColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import com.example.hujjah.data.local.datastore.UserPreferences
import com.example.hujjah.domain.repository.hujjah.BookmarkRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranDetailScreen(
    surahNumber: Int,
    surahName: String,
    verseNumber: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: QuranViewModel = koinViewModel()
) {
    val detailUiState by viewModel.detailUiState.collectAsStateWithLifecycle()
    val lastRead by viewModel.lastReadLocation.collectAsStateWithLifecycle()
    val colors = LocalHujjahColors.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val userPreferences = koinInject<UserPreferences>()
    val bookmarkRepository = koinInject<BookmarkRepository>()
    val arabicFontSize by userPreferences.arabicFontSize.collectAsStateWithLifecycle(initialValue = 22)

    // Background sync timer variables
    var activeSeconds by remember { mutableStateOf(0) }

    // Start timer while reading
    LaunchedEffect(Unit) {
        viewModel.fetchSurahDetail(surahNumber, surahName)
        while (true) {
            delay(1000)
            activeSeconds++
        }
    }

    // Sync back reading duration to home analytics when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            if (activeSeconds > 0) {
                viewModel.addReadingTime(activeSeconds)
            }
        }
    }

    // Auto-scroll to specific verse if requested
    LaunchedEffect(detailUiState.verses, verseNumber) {
        if (detailUiState.verses.isNotEmpty() && verseNumber != null) {
            val index = detailUiState.verses.indexOfFirst { it.number == verseNumber }
            if (index != -1) {
                // Small delay to ensure layout is ready
                delay(300)
                listState.animateScrollToItem(index)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = surahName,
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
        }
    ) { paddingValues ->
        if (detailUiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.goldHighlight)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Info header card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) colors.islamicGreen else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = surahName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.goldHighlight
                        )
                        Text(
                            text = "Surah ke-$surahNumber",
                            fontSize = 14.sp,
                            color = if (colors.isDarkTheme) Color.White.copy(alpha = 0.7f) else colors.islamicGreen.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                        )
                    }
                }

                // Verses List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(detailUiState.verses) { verse ->
                        val isLastReadLoc = lastRead == "QS. $surahName: Ayat ${verse.number}"

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isLastReadLoc) {
                                        colors.goldHighlight.copy(alpha = 0.08f)
                                    } else Color.Transparent
                                )
                                .padding(12.dp)
                        ) {
                            // Verse Number and Last Read action bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text(
                                        text = "۞",
                                        color = colors.goldHighlight,
                                        fontSize = 24.sp
                                    )
                                    Text(
                                        text = "${verse.number}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // 1. Tombol Terakhir Baca
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            viewModel.saveLastRead("QS. $surahName: Ayat ${verse.number}")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isLastReadLoc) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Tandai Terakhir Baca",
                                            tint = colors.goldHighlight,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isLastReadLoc) "Terakhir Baca" else "Tandai",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.goldHighlight
                                        )
                                    }

                                    // 2. Tombol Simpan ke Khazanah Dalil
                                    val referenceId = "quran-$surahNumber-${verse.number}"
                                    val isBookmarked by bookmarkRepository.getBookmarkByReferenceId(referenceId)
                                        .collectAsStateWithLifecycle(initialValue = null)

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            coroutineScope.launch {
                                                if (isBookmarked != null) {
                                                    bookmarkRepository.deleteBookmark(referenceId)
                                                } else {
                                                    val ref = com.example.hujjah.domain.model.islamic.IslamicReference(
                                                        id = referenceId,
                                                        sourceType = com.example.hujjah.domain.model.islamic.SourceType.QURAN,
                                                        title = "QS. $surahName [$surahNumber]: Ayat ${verse.number}",
                                                        sourceName = "QS. $surahName:${verse.number}",
                                                        arabicText = verse.arabic,
                                                        translation = verse.translation,
                                                        explanation = "",
                                                        topicId = "quran",
                                                        topicTitle = "Al-Qur'an mushaf",
                                                        surahNumber = surahNumber,
                                                        verseNumber = verse.number
                                                    )
                                                    bookmarkRepository.saveBookmark(ref, "")
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked != null) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Simpan ke Khazanah Dalil",
                                            tint = if (isBookmarked != null) colors.goldHighlight else colors.islamicGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isBookmarked != null) "Tersimpan" else "Simpan Dalil",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isBookmarked != null) colors.goldHighlight else colors.islamicGreen
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Arabic Verse Text
                            Text(
                                text = verse.arabic,
                                fontSize = arabicFontSize.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.End,
                                lineHeight = 36.sp,
                                modifier = Modifier.fillMaxWidth(),
                                color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Translation
                            Text(
                                text = verse.translation,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

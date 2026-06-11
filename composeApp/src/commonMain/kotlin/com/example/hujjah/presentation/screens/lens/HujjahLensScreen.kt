package com.example.hujjah.presentation.screens.lens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hujjah.domain.model.islamic.IslamicReference
import com.example.hujjah.domain.model.islamic.ChatMessage
import com.example.hujjah.domain.model.islamic.Sender
import com.example.hujjah.presentation.components.hujjah.HujjahMenuItem
import com.example.hujjah.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.hujjah.presentation.theme.LocalHujjahColors
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HujjahLensScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToQuranDetail: (surahNumber: Int, surahName: String, verseNumber: Int?) -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToResult: (String) -> Unit,
    onNavigateToAddNote: (Long?, String?) -> Unit,
    viewModel: HujjahLensViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LocalHujjahColors.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    var selectedMessageForAction by remember { mutableStateOf<ChatMessage?>(null) }
    var showActionDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hujjah Lens",
                            fontWeight = FontWeight.Bold,
                            color = colors.goldHighlight
                        )
                        Text(
                            text = "AI Spiritual Counselor",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            HujjahSprint2MenuBar(
                currentItem = HujjahMenuItem.LENS,
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
        ) {
            if (uiState.isOffline) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "☁️ Mode Luring: Anda hanya dapat membaca riwayat chat.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ==================== CHAT HISTORY AREA ====================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (uiState.messages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.goldHighlight)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            ChatBubble(
                                message = message,
                                colors = colors,
                                onLongPressMessage = {
                                    selectedMessageForAction = message
                                    showActionDialog = true
                                },
                                onClickReference = { ref ->
                                    if (ref.sourceType == com.example.hujjah.domain.model.islamic.SourceType.QURAN && ref.surahNumber != null) {
                                        onNavigateToQuranDetail(ref.surahNumber, ref.sourceName.substringBefore(":").trim(), ref.verseNumber)
                                    }
                                }
                            )
                        }

                        if (uiState.isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(20.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        modifier = Modifier.padding(end = 40.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = colors.goldHighlight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==================== BOTTOM PANEL (FILTERS + INPUT) ====================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(bottom = 8.dp)
            ) {
                // CHAT INPUT FIELD
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.inputText,
                        onValueChange = viewModel::onInputTextChanged,
                        placeholder = {
                            Text(
                                if (uiState.isOffline) "Fitur chat tidak tersedia dalam mode luring" else "Curhat di sini...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.goldHighlight,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        maxLines = 3,
                        singleLine = false,
                        enabled = !uiState.isOffline
                    )

                    IconButton(
                        onClick = {
                            if (uiState.inputText.isNotBlank()) {
                                viewModel.sendUserMessage(uiState.inputText)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isOffline) colors.goldHighlight.copy(alpha = 0.5f) else colors.goldHighlight),
                        enabled = uiState.inputText.isNotBlank() && !uiState.isLoading && !uiState.isOffline
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }
    }

    // ==================== ACTIONS DIALOG (Long press popup) ====================
    if (showActionDialog && selectedMessageForAction != null) {
        AlertDialog(
            onDismissRequest = {
                showActionDialog = false
            },
            title = {
                Text(
                    text = "Opsi Pesan",
                    fontWeight = FontWeight.Bold,
                    color = colors.goldHighlight
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Pilih tindakan yang ingin Anda lakukan untuk pesan ini:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Opsi Simpan ke Catatan
                    Surface(
                        onClick = {
                            showActionDialog = false
                            onNavigateToAddNote(null, selectedMessageForAction!!.text)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = colors.goldHighlight.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = colors.goldHighlight
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Simpan ke Catatan Saya",
                                fontWeight = FontWeight.SemiBold,
                                color = colors.goldHighlight
                            )
                        }
                    }
                    
                    // Opsi Hapus Pesan
                    Surface(
                        onClick = {
                            viewModel.deleteMessage(selectedMessageForAction!!.id)
                            showActionDialog = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Hapus Pesan",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showActionDialog = false
                }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatBubble(
    message: ChatMessage,
    colors: com.example.hujjah.presentation.theme.HujjahColors,
    onLongPressMessage: () -> Unit,
    onClickReference: (IslamicReference) -> Unit
) {
    val isUser = message.sender == Sender.USER

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            // The main bubble
            Card(
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) {
                        if (colors.isDarkTheme) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                    } else {
                        if (colors.isDarkTheme) colors.islamicGreen else colors.islamicGreen.copy(alpha = 0.08f)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = if (!isUser) {
                    BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.3f))
                } else {
                    if (!colors.isDarkTheme) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null
                },
                modifier = Modifier.combinedClickable(
                    onLongClick = onLongPressMessage,
                    onClick = {}
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            if (colors.isDarkTheme) Color.White else colors.islamicGreen
                        },
                        textAlign = TextAlign.Justify,
                        lineHeight = 22.sp
                    )

                    // Render "Solusi Berdalil" if present
                    if (message.solutions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Solusi Berdalil:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = colors.goldHighlight
                        )
                        message.solutions.forEachIndexed { index, solution ->
                            Text(
                                text = "${index + 1}. $solution",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (colors.isDarkTheme) Color.White.copy(alpha = 0.9f) else colors.islamicGreen.copy(alpha = 0.85f),
                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                            )
                        }
                    }
                }
            }

            // Render Dalil References below the main bubble
            if (message.references.isNotEmpty()) {
                message.references.forEach { reference ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (colors.isDarkTheme) {
                                Color.Black.copy(alpha = 0.3f)
                            } else {
                                colors.goldHighlight.copy(alpha = 0.08f)
                            }
                        ),
                        border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .clickable { onClickReference(reference) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(colors.goldHighlight)
                            )
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.BookmarkBorder,
                                    contentDescription = null,
                                    tint = colors.goldHighlight,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "📖 Baca Surah Penuh: ${reference.sourceName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getSurahNumber(sourceName: String): Int? {
    val clean = sourceName.uppercase()
    return when {
        clean.contains("ALI 'IMRAN") || clean.contains("ALI IMRAN") -> 3
        clean.contains("AR-RA'D") || clean.contains("AR RAD") -> 13
        clean.contains("AL-INSYIRAH") || clean.contains("AL INSYIRAH") -> 94
        clean.contains("AL-BAQARAH") || clean.contains("AL BAQARAH") -> 2
        clean.contains("AZ-ZUMAR") || clean.contains("AZ ZUMAR") -> 39
        clean.contains("AT-TAHRIM") || clean.contains("AT TAHRIM") -> 66
        clean.contains("IBRAHIM") -> 14
        clean.contains("AL-MA'UN") || clean.contains("AL MAUN") -> 107
        clean.contains("ATH-THALAQ") || clean.contains("ATH THALAQ") -> 65
        clean.contains("AL-MUJADILAH") || clean.contains("AL MUJADILAH") -> 58
        clean.contains("AL-ISRA") || clean.contains("AL ISRA") -> 17
        clean.contains("HUD") -> 11
        else -> null
    }
}

private fun extractSurahName(sourceName: String): String {
    val start = sourceName.indexOf("QS. ")
    val end = sourceName.indexOf(":")
    if (start != -1 && end != -1 && end > start + 4) {
        return sourceName.substring(start + 4, end).trim()
    }
    return sourceName.replace("QS. ", "").substringBefore(":").trim()
}


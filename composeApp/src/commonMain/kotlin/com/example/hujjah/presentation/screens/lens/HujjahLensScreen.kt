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
    onNavigateToHadith: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToResult: (String) -> Unit,
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
    var selectedReferenceForAction by remember { mutableStateOf<IslamicReference?>(null) }
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
                actions = {
                    IconButton(onClick = onNavigateToBookmarks) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = "Tersimpan",
                            tint = colors.goldHighlight
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
                                onLongPressReference = { ref ->
                                    selectedReferenceForAction = ref
                                    selectedMessageForAction = message
                                    showActionDialog = true
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
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = "Hujjah sedang menelaah dalil...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
                // FLOATING CONTEXT FILTER (Capsules)
                val emotionFilters = listOf(
                    EmotionFilter("Cemas 😟", "Saya merasa cemas dan takut akan masa depan saya."),
                    EmotionFilter("Sedih 😢", "Saya merasa sedih dan hampa saat ini."),
                    EmotionFilter("Marah 😡", "Saya sedang marah dan sulit mengendalikan emosi saya."),
                    EmotionFilter("Ujian 🤲", "Saya sedang menghadapi ujian hidup yang berat."),
                    EmotionFilter("Dosa 😔", "Saya menyesal atas dosa saya dan ingin bertaubat."),
                    EmotionFilter("Syukur ☀️", "Saya sangat bersyukur atas nikmat yang didapatkan hari ini."),
                    EmotionFilter("Malas Shalat 🕌", "Saya merasa malas mendirikan shalat tepat waktu."),
                    EmotionFilter("Malas Belajar 📚", "Saya sedang malas belajar dan menuntut ilmu.")
                )

                Text(
                    text = "Ada apa hari ini?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.goldHighlight,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    letterSpacing = 1.sp
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(emotionFilters) { filter ->
                        Card(
                            shape = RoundedCornerShape(50),
                            colors = CardDefaults.cardColors(
                                containerColor = if (colors.isDarkTheme) colors.islamicGreen else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendUserMessage(filter.messagePrompt)
                                }
                        ) {
                            Text(
                                text = filter.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (colors.isDarkTheme) Color.White else colors.islamicGreen,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                                "Curhat di sini...",
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
                        singleLine = false
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
                            .background(colors.goldHighlight),
                        enabled = uiState.inputText.isNotBlank() && !uiState.isLoading
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
                selectedReferenceForAction = null
            },
            title = {
                Text(
                    text = "Aksi Hujjah",
                    fontWeight = FontWeight.Bold,
                    color = colors.islamicGreen
                )
            },
            text = {
                Text(
                    if (selectedReferenceForAction != null) {
                        "Simpan dalil \"${selectedReferenceForAction?.sourceName}\" ke Khazanah Bookmark?"
                    } else {
                        "Hapus pesan ini dari riwayat obrolan lokal?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ref = selectedReferenceForAction
                        if (ref != null) {
                            viewModel.saveBookmark(ref)
                        } else {
                            viewModel.deleteMessage(selectedMessageForAction!!.id)
                        }
                        showActionDialog = false
                        selectedReferenceForAction = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.goldHighlight)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (selectedReferenceForAction != null) Icons.Default.Bookmark else Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (selectedReferenceForAction != null) "Simpan" else "Hapus",
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showActionDialog = false
                    selectedReferenceForAction = null
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
    onLongPressReference: (IslamicReference) -> Unit
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
                            .fillMaxWidth()
                            .combinedClickable(
                                onLongClick = { onLongPressReference(reference) },
                                onClick = {}
                            )
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
                            Column(modifier = Modifier.padding(14.dp).weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (reference.sourceType == com.example.hujjah.domain.model.islamic.SourceType.QURAN) "Al-Qur'an" else "Hadis",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.goldHighlight,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(colors.goldHighlight.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )

                                    Text(
                                        text = reference.sourceName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = reference.arabicText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.End,
                                    lineHeight = 30.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "\"${reference.translation}\"",
                                    fontSize = 13.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = reference.explanation,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class EmotionFilter(
    val displayName: String,
    val messagePrompt: String
)

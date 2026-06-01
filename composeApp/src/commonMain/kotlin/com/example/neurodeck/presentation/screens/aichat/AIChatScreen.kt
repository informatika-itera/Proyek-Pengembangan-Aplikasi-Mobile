package com.example.neurodeck.presentation.screens.aichat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neurodeck.domain.model.ChatMessage
import com.example.neurodeck.domain.model.MessageRole
import com.example.neurodeck.presentation.components.ConfirmDialog
import org.koin.compose.viewmodel.koinViewModel

// ════════════════════════════════════════════════════════════════════════════
// AIChatScreen.kt — Sprint 2 P3f.4
//
// 💬 AI Chat Tab — Tutor AI conversational.
//
// Layout (bottom-up):
//   1. Chat input row di paling bawah (sticky)
//   2. Typing indicator (kalau AI sedang reply) di atas chat input
//   3. LazyColumn message bubbles (auto-scroll ke bottom saat ada new msg)
//   4. Welcome state kalau chat kosong: greeting + suggestion chips
//
// Note: TopBar action button "clear chat" tidak ditambahkan ke AppTopBar
// supaya AppNavHost tetap simple (chrome generic). Sebagai gantinya, tombol
// clear di-letakkan inline di body (header chat) — accessible via scroll up.
// Trade-off acceptable Sprint 2.
// ════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    viewModel: AIChatViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showClearConfirm by remember { mutableStateOf(false) }

    // Auto-scroll ke pesan terbaru saat list bertambah atau saat AI typing.
    // LaunchedEffect key = messages.size + isAITyping supaya re-trigger.
    LaunchedEffect(uiState.messages.size, uiState.isAITyping) {
        if (uiState.messages.isNotEmpty()) {
            // animateScrollToItem ke index terakhir (+1 kalau ada typing indicator)
            listState.animateScrollToItem(uiState.messages.size)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ════════════════════════════════════════════════════════════════
            // MAIN CONTENT — chat list OR welcome state
            // ════════════════════════════════════════════════════════════════
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isEmpty) {
                    EmptyChatWelcome(
                        suggestions = uiState.suggestedQuestions,
                        onSuggestionClick = viewModel::sendSuggestion,
                    )
                } else {
                    ChatMessageList(
                        messages = uiState.messages,
                        isAITyping = uiState.isAITyping,
                        listState = listState,
                        onClearChat = { showClearConfirm = true },
                    )
                }
            }

            // ════════════════════════════════════════════════════════════════
            // INPUT ROW — sticky di bottom
            // ════════════════════════════════════════════════════════════════
            ChatInputRow(
                text = uiState.inputText,
                canSend = uiState.canSend,
                onTextChange = viewModel::onInputChange,
                onSend = viewModel::send,
            )
        }

        // Error snackbar overlay
        uiState.errorSnackbar?.let { msg ->
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(3500)
                viewModel.consumeSnackbar()
            }
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp),
            ) {
                Text(text = msg)
            }
        }
    }

    // Clear chat confirmation dialog
    if (showClearConfirm) {
        ConfirmDialog(
            title = "Hapus Riwayat Chat?",
            message = "Semua percakapan dengan AI Tutor akan dihapus permanen.",
            confirmLabel = "Hapus",
            isDestructive = true,
            onConfirm = {
                showClearConfirm = false
                viewModel.clearHistory()
            },
            onDismiss = { showClearConfirm = false },
        )
    }
}

// ════════════════════════════════════════════════════════════════════════════
// EMPTY STATE — Welcome + suggestion chips
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun EmptyChatWelcome(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "✨",
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "NeuroDeck Tutor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tanyakan apa saja tentang konsep yang sedang kamu pelajari.\n" +
                    "AI akan jelaskan dengan analogi sederhana.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "COBA TANYAKAN",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Vertical list of suggestion chips (each on its own row, full width)
        suggestions.forEach { suggestion ->
            Spacer(modifier = Modifier.height(8.dp))
            SuggestionChip(
                text = suggestion,
                onClick = { onSuggestionClick(suggestion) },
            )
        }
    }
}

@Composable
private fun SuggestionChip(
    text: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// CHAT MESSAGE LIST
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChatMessageList(
    messages: List<ChatMessage>,
    isAITyping: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onClearChat: () -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Header inline dengan tombol Clear (karena TopBar shared global tidak
        // bisa di-customize per-screen di P3a setup).
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                AssistChip(
                    onClick = onClearChat,
                    label = { Text("Hapus Riwayat") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
            }
        }

        items(items = messages, key = { it.id }) { msg ->
            MessageBubble(message = msg)
        }

        if (isAITyping) {
            item {
                TypingIndicatorBubble()
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// MESSAGE BUBBLE — alternating left/right alignment by role
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.User

    val backgroundColor = when {
        message.isError -> MaterialTheme.colorScheme.errorContainer
        isUser -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        message.isError -> MaterialTheme.colorScheme.onErrorContainer
        isUser -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        val bubbleShape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = if (isUser) 16.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 16.dp,
        )
        Box(
            modifier = Modifier
                .padding(
                    start = if (isUser) 48.dp else 0.dp,
                    end = if (isUser) 0.dp else 48.dp,
                )
                .clip(bubbleShape)
                .background(backgroundColor)
                .then(
                    // AI/error bubble dapat border tegas (Vivid Logic); user bubble tidak
                    if (!isUser) {
                        Modifier.border(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = bubbleShape,
                        )
                    } else Modifier,
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// TYPING INDICATOR — animated 3 dots
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun TypingIndicatorBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .padding(end = 48.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 16.dp,
                    ),
                )
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TypingDot(delayMillis = 0)
                TypingDot(delayMillis = 150)
                TypingDot(delayMillis = 300)
            }
        }
    }
}

@Composable
private fun TypingDot(delayMillis: Int) {
    val transition = rememberInfiniteTransition(label = "typing_dot")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = delayMillis),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha)),
    )
}

// ════════════════════════════════════════════════════════════════════════════
// CHAT INPUT ROW — sticky bottom
// ════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChatInputRow(
    text: String,
    canSend: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Tanya AI Tutor...") },
            modifier = Modifier.weight(1f),
            maxLines = 4,
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSend,
            enabled = canSend,
            modifier = Modifier
                .size(48.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(
                    if (canSend) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = "Kirim",
                tint = if (canSend) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
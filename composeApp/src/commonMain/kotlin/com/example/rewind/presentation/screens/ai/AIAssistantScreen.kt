package com.example.rewind.presentation.screens.ai

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rewind.presentation.theme.BorderGold
import com.example.rewind.presentation.theme.GoldAmber
import com.example.rewind.presentation.theme.GoldAmberDim
import com.example.rewind.presentation.theme.LocalRewindColors
import com.example.rewind.presentation.theme.TheaterRed
import org.koin.compose.viewmodel.koinViewModel

private fun String.stripMarkdown(): String {
    return this
        .replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
        .replace(Regex("__(.+?)__"), "$1")
        .replace(Regex("\\*(.+?)\\*"), "$1")
        .replace(Regex("_(.+?)_"), "$1")
        .replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
        .replace(Regex("`{1,3}(.+?)`{1,3}"), "$1")
        .trim()
}

@Composable
fun AIAssistantScreen(
    noteId: Long? = null,
    initialText: String? = null,
    onNavigateBack: () -> Unit,
    onApplyResult: ((String) -> Unit)? = null,
    viewModel: AIAssistantViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(initialText) {
        viewModel.setInitialText(initialText)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AIAssistantEvent.CopyToClipboard -> {
                    clipboardManager.setText(AnnotatedString(event.text))
                    snackbarHostState.showSnackbar("Disalin ke clipboard")
                }
                is AIAssistantEvent.ApplyToNote -> {
                    onApplyResult?.invoke(event.text)
                    snackbarHostState.showSnackbar("Berhasil diterapkan")
                    onNavigateBack()
                }
            }
        }
    }

    val bg = MaterialTheme.colorScheme.background
    val rewindColors = LocalRewindColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-80).dp)
                .blur(90.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(GoldAmber.copy(alpha = glowPulse * 0.18f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = 40.dp)
                .blur(70.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(TheaterRed.copy(alpha = glowPulse * 0.12f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            AIHeader(onNavigateBack = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                AIWelcomeBanner()

                SectionLabel("PILIH AKSI")
                ActionChips(
                    selectedAction = uiState.selectedAction,
                    onActionSelected = viewModel::onActionSelected
                )

                ActionDescriptionBox(description = uiState.selectedAction.description)

                SectionLabel("INPUT KAMU")
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = viewModel::onInputTextChange,
                    placeholder = {
                        Text(
                            "Ketik teks atau pertanyaanmu di sini...",
                            color = rewindColors.textMuted,
                            fontSize = 14.sp
                        )
                    },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAmber.copy(alpha = 0.7f),
                        unfocusedBorderColor = rewindColors.borderSubtle,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = GoldAmber,
                        focusedContainerColor = rewindColors.surfaceElevated,
                        unfocusedContainerColor = rewindColors.surfaceElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                )

                if (uiState.error != null) {
                    ErrorBox(message = uiState.error ?: "")
                }

                RunButton(
                    isLoading = uiState.isLoading,
                    canExecute = uiState.canExecute,
                    onClick = { viewModel.executeAction() }
                )

                AnimatedVisibility(
                    visible = uiState.result != null,
                    enter = fadeIn(tween(400)) + slideInVertically(
                        animationSpec = tween(400),
                        initialOffsetY = { it / 2 }
                    )
                ) {
                    ResultCard(
                        result = uiState.result ?: "",
                        noteId = noteId,
                        onCopy = { viewModel.copyResult() },
                        onApply = { viewModel.applyToNote() }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AIHeader(onNavigateBack: () -> Unit) {
    val rewindColors = LocalRewindColors.current
    val surface = MaterialTheme.colorScheme.surface
    val onBg = MaterialTheme.colorScheme.onBackground

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "BackBtnScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            BorderGold.copy(alpha = 0.3f),
                            GoldAmber.copy(alpha = 0.2f),
                            BorderGold.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clip(CircleShape)
                    .background(rewindColors.surfaceElevated)
                    .border(BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.55f)), CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = onNavigateBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = GoldAmber,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ASISTEN AI",
                    color = GoldAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                )
                Text(
                    text = "Ngobrol dengan Echo",
                    color = onBg,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp,
                    lineHeight = 24.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(rewindColors.surfaceElevated)
                    .border(BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.55f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun AIWelcomeBanner() {
    val rewindColors = LocalRewindColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(rewindColors.surfaceElevated)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            rewindColors.borderGold.copy(alpha = 0.55f),
                            rewindColors.borderSubtle.copy(alpha = 0.4f)
                        )
                    )
                ),
                RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .blur(30.dp)
                .background(
                    Brush.radialGradient(listOf(GoldAmber.copy(alpha = 0.18f), Color.Transparent)),
                    shape = CircleShape
                )
        )

        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(GoldAmberDim.copy(alpha = 0.25f), GoldAmber.copy(alpha = 0.12f))
                        )
                    )
                    .border(
                        BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.5f)),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SmartToy,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Didukung oleh Gemini AI",
                    color = GoldAmber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                )
                Text(
                    text = "Tanya apa saja tentang film dan series, atau biarkan Echo membantumu menulis ulasan yang lebih baik.",
                    color = rewindColors.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ActionDescriptionBox(description: String) {
    val rewindColors = LocalRewindColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rewindColors.surfaceElevated)
            .border(
                BorderStroke(1.dp, rewindColors.borderSubtle.copy(alpha = 0.7f)),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = rewindColors.textMuted,
                modifier = Modifier
                    .size(15.dp)
                    .padding(top = 1.dp)
            )
            AnimatedContent(
                targetState = description,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                label = "ActionDescriptionAnim"
            ) { desc ->
                Text(
                    text = desc,
                    color = rewindColors.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    letterSpacing = 0.1.sp
                )
            }
        }
    }
}

@Composable
private fun ErrorBox(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TheaterRed.copy(alpha = 0.08f))
            .border(BorderStroke(1.dp, TheaterRed.copy(alpha = 0.4f)), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = TheaterRed,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = message,
                color = TheaterRed,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun ActionChips(selectedAction: AIAction, onActionSelected: (AIAction) -> Unit) {
    val rewindColors = LocalRewindColors.current
    val bg = MaterialTheme.colorScheme.background

    LazyRow(
        contentPadding = PaddingValues(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AIAction.entries) { action ->
            val isSelected = selectedAction == action
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.92f else 1f,
                animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                label = "ChipScale"
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber)))
                        .border(BorderStroke(1.dp, GoldAmber.copy(alpha = 0.6f)), RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = LocalIndication.current
                        ) { onActionSelected(action) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = action.displayName,
                        color = bg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clip(RoundedCornerShape(20.dp))
                        .background(rewindColors.surfaceElevated)
                        .border(
                            BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.4f)),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = LocalIndication.current
                        ) { onActionSelected(action) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = action.displayName,
                        color = rewindColors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun RunButton(isLoading: Boolean, canExecute: Boolean, onClick: () -> Unit) {
    val bg = MaterialTheme.colorScheme.background
    val rewindColors = LocalRewindColors.current

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && canExecute && !isLoading) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "RunBtnScale"
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (canExecute) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .blur(40.dp)
                    .background(
                        Brush.radialGradient(listOf(GoldAmber.copy(alpha = 0.18f), Color.Transparent)),
                        shape = CircleShape
                    )
            )
        }
        Box(
            modifier = Modifier
                .graphicsLayer(scaleX = scale, scaleY = scale)
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (canExecute)
                        Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber))
                    else
                        Brush.horizontalGradient(
                            listOf(rewindColors.surfaceElevated, rewindColors.surfaceElevated)
                        )
                )
                .border(
                    BorderStroke(
                        1.dp,
                        if (canExecute) GoldAmber.copy(alpha = 0.5f) else rewindColors.borderSubtle
                    ),
                    RoundedCornerShape(14.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                    enabled = canExecute && !isLoading,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = bg,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Memproses...",
                        color = bg,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = null,
                        tint = if (canExecute) bg else rewindColors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Jalankan",
                        color = if (canExecute) bg else rewindColors.textMuted,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultCard(
    result: String,
    noteId: Long?,
    onCopy: () -> Unit,
    onApply: () -> Unit
) {
    val rewindColors = LocalRewindColors.current
    val bg = MaterialTheme.colorScheme.background
    val onBg = MaterialTheme.colorScheme.onBackground

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionLabel("HASIL")
            Box(
                modifier = Modifier
                    .background(GoldAmber.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                    .border(
                        BorderStroke(0.5.dp, rewindColors.borderGold.copy(alpha = 0.55f)),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Dibuat oleh AI",
                    color = GoldAmber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(rewindColors.surfaceElevated)
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                rewindColors.borderGold.copy(alpha = 0.5f),
                                rewindColors.borderSubtle.copy(alpha = 0.55f)
                            )
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopStart)
                    .offset(x = (-20).dp, y = (-20).dp)
                    .blur(30.dp)
                    .background(
                        Brush.radialGradient(listOf(GoldAmber.copy(alpha = 0.1f), Color.Transparent)),
                        shape = CircleShape
                    )
            )

            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 3.dp, height = 20.dp)
                            .background(GoldAmber, RoundedCornerShape(2.dp))
                    )
                    Icon(
                        imageVector = Icons.Filled.FormatQuote,
                        contentDescription = null,
                        tint = GoldAmber.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = result.stripMarkdown(),
                    color = onBg,
                    fontSize = 14.sp,
                    lineHeight = 23.sp,
                    letterSpacing = 0.1.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    rewindColors.borderGold.copy(alpha = 0.4f),
                                    rewindColors.borderSubtle.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val copyInteractionSource = remember { MutableInteractionSource() }
                    val copyPressed by copyInteractionSource.collectIsPressedAsState()
                    val copyScale by animateFloatAsState(
                        targetValue = if (copyPressed) 0.94f else 1f,
                        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                        label = "CopyScale"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .graphicsLayer(scaleX = copyScale, scaleY = copyScale)
                            .clip(RoundedCornerShape(10.dp))
                            .background(rewindColors.surfaceElevated)
                            .border(
                                BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.45f)),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(
                                interactionSource = copyInteractionSource,
                                indication = LocalIndication.current,
                                onClick = onCopy
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                                tint = rewindColors.textSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Salin",
                                color = rewindColors.textSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (noteId != null) {
                        val applyInteractionSource = remember { MutableInteractionSource() }
                        val applyPressed by applyInteractionSource.collectIsPressedAsState()
                        val applyScale by animateFloatAsState(
                            targetValue = if (applyPressed) 0.94f else 1f,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                            label = "ApplyScale"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .graphicsLayer(scaleX = applyScale, scaleY = applyScale)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber)))
                                .border(
                                    BorderStroke(1.dp, GoldAmber.copy(alpha = 0.5f)),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    interactionSource = applyInteractionSource,
                                    indication = LocalIndication.current,
                                    onClick = onApply
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = bg,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Terapkan",
                                    color = bg,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    val rewindColors = LocalRewindColors.current
    Text(
        text = text,
        color = rewindColors.textMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.5.sp
    )
}
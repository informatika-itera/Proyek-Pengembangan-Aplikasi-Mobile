package com.example.rewind.presentation.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rewind.presentation.theme.BackgroundDark
import com.example.rewind.presentation.theme.BorderGold
import com.example.rewind.presentation.theme.BorderSubtle
import com.example.rewind.presentation.theme.GoldAmber
import com.example.rewind.presentation.theme.GoldAmberDim
import com.example.rewind.presentation.theme.SurfaceDark
import com.example.rewind.presentation.theme.SurfaceElevated
import com.example.rewind.presentation.theme.TextMuted
import com.example.rewind.presentation.theme.TextSecondary
import com.example.rewind.presentation.theme.TextWarm
import com.example.rewind.presentation.theme.TheaterRed
import com.example.rewind.presentation.theme.VelvetRed
import org.koin.compose.viewmodel.koinViewModel

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
                    snackbarHostState.showSnackbar("Copied to clipboard")
                }
                is AIAssistantEvent.ApplyToNote -> {
                    onApplyResult?.invoke(event.text)
                    snackbarHostState.showSnackbar("Applied successfully")
                    onNavigateBack()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
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
                        colors = listOf(TheaterRed.copy(alpha = glowPulse * 0.15f), Color.Transparent)
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
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                AIWelcomeBanner()

                SectionLabel("SELECT ACTION")
                ActionChips(
                    selectedAction = uiState.selectedAction,
                    onActionSelected = viewModel::onActionSelected
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    GoldAmber.copy(alpha = 0.06f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(BorderStroke(1.dp, BorderGold.copy(alpha = 0.2f)), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = uiState.selectedAction.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 18.sp
                    )
                }

                SectionLabel("YOUR INPUT")
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = viewModel::onInputTextChange,
                    placeholder = {
                        Text(
                            "Type your text or question here...",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAmber.copy(alpha = 0.7f),
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextWarm,
                        unfocusedTextColor = TextWarm,
                        cursorColor = GoldAmber,
                        focusedContainerColor = SurfaceElevated,
                        unfocusedContainerColor = SurfaceElevated
                    ),
                    shape = RoundedCornerShape(14.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = TextWarm,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                )

                if (uiState.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TheaterRed.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .border(BorderStroke(1.dp, TheaterRed.copy(alpha = 0.3f)), RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "",
                            color = TheaterRed,
                            fontSize = 13.sp
                        )
                    }
                }

                RunButton(
                    isLoading = uiState.isLoading,
                    canExecute = uiState.canExecute,
                    onClick = { viewModel.executeAction() }
                )

                AnimatedVisibility(
                    visible = uiState.result != null,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    ResultCard(
                        result = uiState.result ?: "",
                        noteId = noteId,
                        onCopy = { viewModel.copyResult() },
                        onApply = { viewModel.applyToNote() }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to SurfaceDark,
                        0.7f to SurfaceDark.copy(alpha = 0.8f),
                        1f to BackgroundDark
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
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
                            BorderGold.copy(alpha = 0.4f),
                            GoldAmber.copy(alpha = 0.25f),
                            BorderGold.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceElevated)
                    .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(10.dp))
                    .clickable(onClick = onNavigateBack),
                contentAlignment = Alignment.Center
            ) {
                Text("←", color = GoldAmber, fontSize = 17.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = "AI ASSISTANT",
                    color = GoldAmber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "Talk with Echo",
                    color = TextWarm,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(listOf(GoldAmberDim.copy(alpha = 0.2f), GoldAmber.copy(alpha = 0.1f)))
                    )
                    .border(BorderStroke(1.dp, BorderGold.copy(alpha = 0.4f)), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🦉", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun AIWelcomeBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to VelvetRed.copy(alpha = 0.3f),
                        0.5f to SurfaceElevated,
                        1f to SurfaceDark
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(GoldAmber.copy(alpha = 0.3f), BorderSubtle, Color.Transparent))
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .blur(25.dp)
                .background(
                    Brush.radialGradient(colors = listOf(GoldAmber.copy(alpha = 0.2f), Color.Transparent)),
                    shape = CircleShape
                )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("🤖", fontSize = 32.sp)
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "Powered by Gemini AI",
                    color = GoldAmber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp
                )
                Text(
                    text = "Ask anything about movies, series, or let the Echo help you write better reviews.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ActionChips(selectedAction: AIAction, onActionSelected: (AIAction) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(AIAction.entries) { action ->
            val isSelected = selectedAction == action
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber)))
                        .clickable { onActionSelected(action) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = action.displayName,
                        color = BackgroundDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(20.dp))
                        .clickable { onActionSelected(action) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = action.displayName,
                        color = TextMuted,
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
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (canExecute) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .blur(35.dp)
                    .background(
                        Brush.radialGradient(colors = listOf(GoldAmber.copy(alpha = 0.15f), Color.Transparent)),
                        shape = CircleShape
                    )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (canExecute)
                        Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber))
                    else
                        Brush.horizontalGradient(
                            listOf(GoldAmberDim.copy(alpha = 0.3f), GoldAmber.copy(alpha = 0.3f))
                        )
                )
                .clickable(enabled = canExecute && !isLoading, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = BackgroundDark,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Processing...",
                        color = BackgroundDark,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✦", color = BackgroundDark, fontSize = 14.sp)
                    Text(
                        text = "Run",
                        color = BackgroundDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
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
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "RESULT",
                color = GoldAmber,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.5.sp
            )
            Box(
                modifier = Modifier
                    .background(GoldAmber.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                    .border(BorderStroke(0.5.dp, BorderGold.copy(alpha = 0.4f)), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "AI Generated",
                    color = GoldAmber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceElevated)
                .border(
                    BorderStroke(
                        1.dp,
                        Brush.verticalGradient(listOf(BorderGold.copy(alpha = 0.35f), BorderSubtle))
                    ),
                    RoundedCornerShape(16.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = (-10).dp, y = (-10).dp)
                    .blur(24.dp)
                    .background(
                        Brush.radialGradient(colors = listOf(GoldAmber.copy(alpha = 0.1f), Color.Transparent)),
                        shape = CircleShape
                    )
            )
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "❝",
                    color = GoldAmber.copy(alpha = 0.35f),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result,
                    color = TextWarm,
                    fontSize = 14.sp,
                    lineHeight = 23.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceDark)
                            .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(10.dp))
                            .clickable(onClick = onCopy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Copy",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (noteId != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber)))
                                .clickable(onClick = onApply),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Apply",
                                color = BackgroundDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 2.5.sp
    )
}
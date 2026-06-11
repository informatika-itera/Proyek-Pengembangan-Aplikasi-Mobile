package com.example.sholatyuk.presentation.screens.islamai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.domain.model.ChatMessage
import com.example.sholatyuk.domain.model.MessageRole
import com.example.sholatyuk.presentation.screens.home.BottomNavigationBar
import com.example.sholatyuk.presentation.theme.*
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamAIScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToShalat: () -> Unit = {},
    onNavigateToDoa: () -> Unit = {},
    viewModel: IslamAIViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLightModeEnabled by profileViewModel.isLightModeEnabled.collectAsState()
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "islamAI",
                onHomeClick = onNavigateToHome,
                onShalatClick = onNavigateToShalat,
                onDoaClick = onNavigateToDoa,
                onIslamAIClick = {},
                isLightMode = isLightModeEnabled
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = if (isLightModeEnabled) {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE0F2F1), Color(0xFFF5F5F5)),
                            startY = 0f,
                            endY = 1200f
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(DarkTeal, DeepBlue),
                            startY = 0f,
                            endY = 1200f
                        )
                    }
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header (Consistently styled with Home and Doa)
                Column(
                    modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "TANYA",
                        color = (if (isLightModeEnabled) Color.Black else TextWhite).copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "IslamAI",
                        color = if (isLightModeEnabled) Color.Black else TextWhite,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.messages) { msg ->
                        ChatBubble(message = msg, isLightMode = isLightModeEnabled)
                    }

                    if (uiState.isLoading) {
                        item {
                            Text(
                                text = "IslamAI sedang mengetik...",
                                color = (if (isLightModeEnabled) Color.Black else TextWhite).copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Tanya seputar Islam...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isLightModeEnabled) DeepBlue else AccentYellow,
                            unfocusedBorderColor = (if (isLightModeEnabled) Color.Black else Color.White).copy(alpha = 0.1f),
                            focusedContainerColor = if (isLightModeEnabled) Color.White else CardBackground,
                            unfocusedContainerColor = if (isLightModeEnabled) Color.White else CardBackground,
                            focusedTextColor = if (isLightModeEnabled) Color.Black else TextWhite,
                            unfocusedTextColor = if (isLightModeEnabled) Color.Black else TextWhite,
                            cursorColor = if (isLightModeEnabled) DeepBlue else AccentYellow
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank() && !uiState.isLoading) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isLightModeEnabled) DeepBlue else AccentYellow)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            tint = if (isLightModeEnabled) Color.White else DeepBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isLightMode: Boolean) {
    val isAi = message.role == MessageRole.ASSISTANT
    val backgroundColor = if (isAi) {
        (if (isLightMode) Color.Black else Color.White).copy(alpha = 0.08f)
    } else {
        if (isLightMode) DeepBlue else AccentYellow
    }
    val alignment = if (isAi) Alignment.CenterStart else Alignment.CenterEnd
    val shape = if (isAi) {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    }

    val formattedText = buildAnnotatedString {
        val parts = message.content.split("**")
        for ((index, part) in parts.withIndex()) {
            if (index % 2 == 0) {
                append(part)
            } else {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(part)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Text(
                text = formattedText,
                color = if (message.isError) {
                    Color.Red.copy(alpha = 0.8f)
                } else if (!isAi) {
                    if (isLightMode) Color.White else DeepBlue
                } else {
                    if (isLightMode) Color.Black else TextWhite
                },
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

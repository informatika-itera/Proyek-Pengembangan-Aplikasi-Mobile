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
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamAIScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToShalat: () -> Unit = {},
    onNavigateToDoa: () -> Unit = {},
    viewModel: IslamAIViewModel = koinViewModel() // Menyuntikkan ViewModel menggunakan Koin
) {
    // Memantau perubahan State dari ViewModel
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "IslamAI",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DeepBlue
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "islamAI",
                onHomeClick = onNavigateToHome,
                onShalatClick = onNavigateToShalat,
                onDoaClick = onNavigateToDoa,
                onIslamAIClick = {}
            )
        },
        containerColor = DeepBlue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Area Daftar Obrolan
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                reverseLayout = false // Auto-scroll ke bawah bisa ditambahkan nanti jika perlu
            ) {
                // Menampilkan daftar pesan dari database/state
                items(uiState.messages) { msg ->
                    ChatBubble(message = msg)
                }

                // Indikator Loading saat AI sedang merespons
                if (uiState.isLoading) {
                    item {
                        Text(
                            text = "IslamAI sedang mengetik...",
                            color = TextWhite.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            // Area Input Teks & Tombol Kirim
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepBlue)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Tanya seputar Islam...", color = TextWhite.copy(alpha = 0.5f)) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LightTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = AccentYellow
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank() && !uiState.isLoading) {
                            viewModel.sendMessage(messageText) // Mengirim pesan ke server
                            messageText = "" // Mengosongkan kolom teks setelah dikirim
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(LightTeal)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Komponen balon pesan yang menyesuaikan pengirimnya (User / AI)
@Composable
fun ChatBubble(message: ChatMessage) {
    val isAi = message.role == MessageRole.ASSISTANT
    val backgroundColor = if (isAi) Color.White.copy(alpha = 0.1f) else LightTeal
    val alignment = if (isAi) Alignment.CenterStart else Alignment.CenterEnd
    val shape = if (isAi) {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    }

    // Logika untuk mengubah **teks** menjadi teks tebal (bold)
    val formattedText = buildAnnotatedString {
        val parts = message.content.split("**")
        for ((index, part) in parts.withIndex()) {
            if (index % 2 == 0) {
                // Teks biasa (tidak diapit bintang)
                append(part)
            } else {
                // Teks yang diapit bintang, berikan gaya Bold
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
                color = if (message.isError) Color.Red.copy(alpha = 0.8f) else TextWhite,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}
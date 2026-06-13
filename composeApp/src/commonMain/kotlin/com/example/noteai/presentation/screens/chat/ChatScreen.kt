package com.example.noteai.presentation.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }
    LazyColumn( /* ... */ ) {
        items(uiState.messages) { message ->
            // Tambahkan parameter callback untuk tombol simpan resep
            ChatBubble(message = message, onSaveRecipeClick = {
                viewModel.saveAiRecipe(message.text)
            })
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Chef Assistant") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages) { message ->
                    ChatBubble(
                        message = message,
                        onSaveRecipeClick = {
                            viewModel.saveAiRecipe(message.text)
                        }
                    )
                }
                if (uiState.isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally)
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
                    value = uiState.inputText,
                    onValueChange = viewModel::onInputTextChanged,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mau masak apa hari ini?") },
                    maxLines = 3
                )
                IconButton(
                    onClick = viewModel::sendMessage,
                    enabled = uiState.inputText.isNotBlank() && !uiState.isLoading
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onSaveRecipeClick: () -> Unit // Parameter ini wajib ada
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val containerColor = if (message.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerColor,
            shape = MaterialTheme.shapes.medium
        ) {
            Column {
                Text(
                    text = formatMarkdownToAnnotatedString(message.text), // Pastikan helper ini ada
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )

                // Menampilkan tombol jika pesan dari AI dan berformat resep
                if (!message.isUser && message.canBeSavedAsRecipe) {
                    HorizontalDivider()
                    TextButton(
                        onClick = onSaveRecipeClick,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(end = 8.dp)
                    ) {
                        Text("Simpan ke Resep")
                    }
                }
            }
        }
    }
}

fun formatMarkdownToAnnotatedString(text: String): AnnotatedString {
    return buildAnnotatedString {
        // Regex untuk mencari teks yang diapit ** (contoh: **teks tebal**)
        val pattern = Regex("\\*\\*(.*?)\\*\\*")
        var currentIndex = 0
        val matches = pattern.findAll(text)

        matches.forEach { matchResult ->
            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1
            val matchText = matchResult.groupValues[1] // Mengambil teks di dalam **

            // Tambahkan teks biasa yang berada sebelum tanda **
            if (startIndex > currentIndex) {
                append(text.substring(currentIndex, startIndex))
            }

            // Terapkan gaya tebal (Bold) pada teks yang cocok
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(matchText)
            }

            currentIndex = endIndex
        }

        // Tambahkan sisa teks biasa setelah tanda ** terakhir
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
}

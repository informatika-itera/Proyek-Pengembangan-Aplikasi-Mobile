package com.example.bridgebit.presentation.screens.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationDetailScreen(
    translationId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TranslationDetailViewModel = koinViewModel()
) {
    LaunchedEffect(translationId) {
        viewModel.loadTranslationDetails(translationId)
    }

    val state by viewModel.uiState.collectAsState()

    // Tools untuk Extra Feature: Copy to Clipboard
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Untuk notif copy
        topBar = {
            TopAppBar(
                title = { Text("Detail Terjemahan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Tombol Salin
                    if (state is DetailUiState.Success) {
                        val translation = (state as DetailUiState.Success).translation
                        var isCopied by remember { mutableStateOf(false) }

                        IconButton(onClick = {
                            val textToCopy = "Terjemahan (${translation.sourceLanguage} ➔ ${translation.targetLanguage}):\n${translation.sourceText}\n\nArtinya:\n${translation.translatedText}"
                            clipboardManager.setText(buildAnnotatedString { append(textToCopy) })

                            isCopied = true

                            // Munculkan notifikasi
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Berhasil disalin ke Clipboard")
                            }
                            coroutineScope.launch {
                                delay(2000)
                                isCopied = false
                            }
                        }) {
                            AnimatedContent(
                                targetState = isCopied,
                                transitionSpec = {
                                    (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
                                            .togetherWith(fadeOut(animationSpec = tween(90)))
                                },
                                label = "copy_icon_animation"
                            ) { copied ->
                                if (copied) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Disalin",
                                        tint = Color(0xFF4CAF50)
                                    )
                                } else {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Salin")
                                }
                            }
                        }
                    }
                    // Tombol Edit
                    IconButton(onClick = { onNavigateToEdit(translationId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Terjemahan")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (state) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Error -> {
                    Text(
                        text = (state as DetailUiState.Error).message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is DetailUiState.Success -> {
                    val translation = (state as DetailUiState.Success).translation
                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        isVisible = true
                    }
                    
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Dari: ${translation.sourceLanguage}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(translation.sourceText, style = MaterialTheme.typography.bodyLarge)
                                }
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Ke: ${translation.targetLanguage}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(translation.translatedText, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
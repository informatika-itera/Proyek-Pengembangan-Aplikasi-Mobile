package com.example.bridgebit.presentation.screens.workspace

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bridgebit.core.util.copyToClipboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    translationId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: WorkspaceViewModel = koinViewModel()
) {
    LaunchedEffect(translationId) {
        if (translationId != null) {
            viewModel.loadTranslation(translationId)
        }
    }

    var expandedSource by remember { mutableStateOf(false) }
    var expandedTarget by remember { mutableStateOf(false) }

    // State untuk animasi icon Copy → Check
    var isCopied by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val availableLanguages = listOf("Indonesia", "Inggris", "Jepang", "Korea", "Arab", "Jerman")

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(if (translationId == null) "Workspace Terjemahan" else "Edit Terjemahan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Baris Pemilihan Bahasa
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box {
                    val sourceRotation by animateFloatAsState(targetValue = if (expandedSource) 180f else 0f, label = "sourceRot")
                    Row(modifier = Modifier.clickable { expandedSource = true }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(viewModel.sourceLanguage.value, style = MaterialTheme.typography.bodyLarge)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.rotate(sourceRotation))
                    }
                    DropdownMenu(expanded = expandedSource, onDismissRequest = { expandedSource = false }) {
                        availableLanguages.forEach { lang ->
                            DropdownMenuItem(text = { Text(lang) }, onClick = { viewModel.sourceLanguage.value = lang; expandedSource = false })
                        }
                    }
                }
                
                var swapRotation by remember { mutableStateOf(0f) }
                val animatedSwapRotation by animateFloatAsState(
                    targetValue = swapRotation,
                    animationSpec = tween(durationMillis = 300),
                    label = "SwapRotation"
                )

                IconButton(onClick = {
                    swapRotation += 180f
                    // Swap language
                    val tempLang = viewModel.sourceLanguage.value
                    viewModel.sourceLanguage.value = viewModel.targetLanguage.value
                    viewModel.targetLanguage.value = tempLang
                    
                    // Swap text
                    val tempText = viewModel.sourceText.value
                    viewModel.sourceText.value = viewModel.translatedText.value
                    viewModel.translatedText.value = tempText
                }) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Tukar Bahasa",
                        modifier = Modifier.rotate(animatedSwapRotation),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Box {
                    val targetRotation by animateFloatAsState(targetValue = if (expandedTarget) 180f else 0f, label = "targetRot")
                    Row(modifier = Modifier.clickable { expandedTarget = true }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(viewModel.targetLanguage.value, style = MaterialTheme.typography.bodyLarge)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.rotate(targetRotation))
                    }
                    DropdownMenu(expanded = expandedTarget, onDismissRequest = { expandedTarget = false }) {
                        availableLanguages.forEach { lang ->
                            DropdownMenuItem(text = { Text(lang) }, onClick = { viewModel.targetLanguage.value = lang; expandedTarget = false })
                        }
                    }
                }
            }


            val maxCharCount = 500
            OutlinedTextField(
                value = viewModel.sourceText.value,
                onValueChange = {
                    if (it.length <= maxCharCount) {
                        viewModel.sourceText.value = it
                        if (it.isBlank()) viewModel.translatedText.value = ""
                    }
                },
                label = { Text("Ketik teks asli di sini...") },
                supportingText = {
                    val count = viewModel.sourceText.value.length
                    val progress = count / maxCharCount.toFloat()
                    val animatedProgress by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(durationMillis = 300),
                        label = "charCountProgress"
                    )
                    val color = if (count > 450) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.size(14.dp),
                            color = color,
                            strokeWidth = 2.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$count / $maxCharCount",
                            color = color,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )


            Button(
                onClick = { viewModel.translateText() },
                enabled = !viewModel.isLoading.value && viewModel.sourceText.value.isNotBlank(),
                modifier = Modifier.fillMaxWidth().animateContentSize()
            ) {
                AnimatedContent(
                    targetState = viewModel.isLoading.value,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "TranslateButtonAnimation"
                ) { isLoading ->
                    if (isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI sedang memproses...")
                        }
                    } else {
                        Text("Terjemahkan")
                    }
                }
            }

            AnimatedVisibility(
                visible = viewModel.errorMessage.value != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(text = viewModel.errorMessage.value ?: "", color = MaterialTheme.colorScheme.error)
            }


            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = viewModel.translatedText.value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.TopStart)
                    )


                    if (viewModel.translatedText.value.isNotBlank()) {
                        IconButton(
                            onClick = {
                                if (!isCopied) {
                                    copyToClipboard(viewModel.translatedText.value)
                                    isCopied = true
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "✓ Terjemahan disalin ke clipboard",
                                            duration = SnackbarDuration.Short
                                        )
                                        delay(2000L)
                                        isCopied = false
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            AnimatedContent(
                                targetState = isCopied,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(250)) togetherWith
                                        fadeOut(animationSpec = tween(250))
                                },
                                label = "CopyIconAnimation"
                            ) { copied ->
                                if (copied) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Berhasil disalin",
                                        tint = Color(0xFF4CAF50) // Hijau Material
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Salin terjemahan",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
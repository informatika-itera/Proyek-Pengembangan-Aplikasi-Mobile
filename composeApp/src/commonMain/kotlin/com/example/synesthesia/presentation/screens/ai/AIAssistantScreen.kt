package com.example.synesthesia.presentation.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.example.synesthesia.presentation.components.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    noteId: Long?,
    initialText: String?,
    onNavigateBack: () -> Unit,
    onApplyResult: ((String) -> Unit)? = null,
    viewModel: AIAssistantViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(initialText) {
        viewModel.setInitialText(initialText)
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AIAssistantEvent.CopyToClipboard -> {
                    snackbarHostState.showSnackbar("Disalin ke clipboard")
                }
                is AIAssistantEvent.ApplyToNote -> {
                    onApplyResult?.invoke(event.text)
                    snackbarHostState.showSnackbar("Diterapkan ke catatan")
                    onNavigateBack()
                }
            }
        }
    }
    
    AuroraBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    title = { Text("AI Assistant", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Pilih Aksi",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(AIAction.entries) { action ->
                        val isSelected = uiState.selectedAction == action
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onActionSelected(action) },
                            label = { Text(action.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = Color.White.copy(alpha = 0.8f),
                                selectedLabelColor = Color.Black,
                                selectedContainerColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = Color.White.copy(alpha = 0.3f),
                                enabled = true,
                                selected = isSelected
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = uiState.selectedAction.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.inputText,
                        onValueChange = viewModel::onInputTextChange,
                        placeholder = { Text("Teks yang ingin diproses...", color = Color.White.copy(alpha = 0.4f)) },
                        minLines = 4,
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { viewModel.executeAction() },
                    enabled = uiState.canExecute,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp).padding(end = 8.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                        Text("Memproses...")
                    } else {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp).padding(end = 8.dp)
                        )
                        Text("Jalankan")
                    }
                }
                
                AnimatedVisibility(visible = uiState.result != null) {
                    Column {
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(
                            text = "Hasil AI",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderOpacity = 0.25f
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = uiState.result ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.copyResult() },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color.White
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                                        )
                                        Text("Salin")
                                    }
                                    
                                    if (noteId != null) {
                                        Button(
                                            onClick = { viewModel.applyToNote() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.White.copy(alpha = 0.15f),
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                Icons.Default.Done,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp).padding(end = 4.dp)
                                            )
                                            Text("Terapkan")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.pocketguard.presentation.screens.ai

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
    initialText: String?, // Teks awal (misal: data transaksi yang ingin dianalisis)
    onNavigateBack: () -> Unit,
    onApplyResult: ((String) -> Unit)? = null, // Digunakan jika ingin menerapkan saran ke deskripsi transaksi
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
                    snackbarHostState.showSnackbar("Saran disalin")
                }
                is AIAssistantEvent.ApplyToTransaction -> {
                    onApplyResult?.invoke(event.text)
                    snackbarHostState.showSnackbar("Saran diterapkan")
                    onNavigateBack()
                }
                is AIAssistantEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Asisten Keuangan AI") },
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
                text = "Pilih Analisis AI",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            // List Aksi (misal: Analisis Pengeluaran, Tips Hemat, Budgeting)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AIAction.entries) { action ->
                    FilterChip(
                        selected = uiState.selectedAction == action,
                        onClick = { viewModel.onActionSelected(action) },
                        label = { Text(action.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.selectedAction.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = viewModel::onInputTextChange,
                label = { Text("Konteks / Pertanyaan") },
                placeholder = { Text("Contoh: Analisis pengeluaran makan saya minggu ini...") },
                minLines = 4,
                maxLines = 8,
                isError = uiState.error != null,
                supportingText = uiState.error?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.executeAction() },
                enabled = uiState.canExecute,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Text("Menganalisis...")
                } else {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Tanyakan AI")
                }
            }

            AnimatedVisibility(visible = uiState.result != null) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Saran & Analisis AI",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = uiState.result ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.copyResult() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp).padding(end = 4.dp)
                                    )
                                    Text("Salin")
                                }

                                // Jika ada saran deskripsi yang bisa diterapkan ke transaksi baru
                                if (onApplyResult != null) {
                                    Button(
                                        onClick = { viewModel.applyToTransaction() },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.Done,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp).padding(end = 4.dp)
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
package com.example.bridgebit.presentation.screens.workspace

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    val availableLanguages = listOf("Indonesia", "Inggris", "Jepang", "Korea", "Arab", "Jerman")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (translationId == null) "Workspace Terjemahan" else "Edit Terjemahan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                }
                // BLOK ACTIONS (TOMBOL SAVE) SUDAH SEPENUHNYA DIHAPUS DARI SINI
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
                    Row(modifier = Modifier.clickable { expandedSource = true }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(viewModel.sourceLanguage.value, style = MaterialTheme.typography.bodyLarge)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(expanded = expandedSource, onDismissRequest = { expandedSource = false }) {
                        availableLanguages.forEach { lang ->
                            DropdownMenuItem(text = { Text(lang) }, onClick = { viewModel.sourceLanguage.value = lang; expandedSource = false })
                        }
                    }
                }
                Text("➔", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Box {
                    Row(modifier = Modifier.clickable { expandedTarget = true }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(viewModel.targetLanguage.value, style = MaterialTheme.typography.bodyLarge)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(expanded = expandedTarget, onDismissRequest = { expandedTarget = false }) {
                        availableLanguages.forEach { lang ->
                            DropdownMenuItem(text = { Text(lang) }, onClick = { viewModel.targetLanguage.value = lang; expandedTarget = false })
                        }
                    }
                }
            }

            // Input Teks Asal
            OutlinedTextField(
                value = viewModel.sourceText.value,
                onValueChange = {
                    viewModel.sourceText.value = it
                    if (it.isBlank()) viewModel.translatedText.value = ""
                },
                label = { Text("Ketik teks asli di sini...") },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )

            // Tombol Terjemahkan
            Button(
                onClick = { viewModel.translateText() },
                enabled = !viewModel.isLoading.value && viewModel.sourceText.value.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI sedang memproses...")
                } else {
                    Text("Terjemahkan")
                }
            }

            if (viewModel.errorMessage.value != null) {
                Text(text = viewModel.errorMessage.value ?: "", color = MaterialTheme.colorScheme.error)
            }

            // Output Teks Hasil Terjemahan
            Card(modifier = Modifier.fillMaxWidth().weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = viewModel.translatedText.value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
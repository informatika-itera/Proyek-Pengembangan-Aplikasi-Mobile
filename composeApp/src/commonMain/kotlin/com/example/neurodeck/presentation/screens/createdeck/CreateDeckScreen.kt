package com.example.neurodeck.presentation.screens.createdeck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckScreen(
    onBack: () -> Unit,
    onSavedManual: (deckId: Long) -> Unit,
    onSavedAIGenerate: (deckId: Long) -> Unit,
    viewModel: CreateDeckViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedMethod by remember { mutableStateOf(GenerationMethod.Manual) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Deck Baru") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // JUDUL
            Text(
                text = "Judul Deck",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Tulis judul deck di sini") },
                placeholder = { Text("Contoh: Kalkulus UAS, Bahasa Inggris TOEFL") },
                singleLine = true,
                supportingText = {
                    Text(
                        text = "${uiState.title.length} / ${CreateDeckUiState.MAX_TITLE_LENGTH}" +
                                if (uiState.title.length < CreateDeckUiState.MIN_TITLE_LENGTH) {
                                    " · minimal ${CreateDeckUiState.MIN_TITLE_LENGTH} karakter"
                                } else "",
                    )
                },
                isError = uiState.title.isNotEmpty() &&
                        uiState.title.trim().length < CreateDeckUiState.MIN_TITLE_LENGTH,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
            )

            // DESKRIPSI
            Text(
                text = "Deskripsi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp),
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Deskripsi (opsional)") },
                placeholder = { Text("Contoh: Materi turunan & integral untuk UAS minggu depan") },
                minLines = 2,
                maxLines = 4,
                supportingText = {
                    Text("${uiState.description.length} / ${CreateDeckUiState.MAX_DESCRIPTION_LENGTH}")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
            )

            // CARA BUAT KARTU
            Text(
                text = "Cara Buat Kartu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MethodOptionCard(
                    title = "Manual",
                    description = "Buat kartu satu per satu (front + back) sendiri.",
                    icon = Icons.Outlined.Edit,
                    selected = selectedMethod == GenerationMethod.Manual,
                    onClick = { selectedMethod = GenerationMethod.Manual },
                )
                MethodOptionCard(
                    title = "AI Generate ✨",
                    description = "Paste teks materi → AI bikin 5-15 kartu otomatis.",
                    icon = Icons.Outlined.AutoAwesome,
                    selected = selectedMethod == GenerationMethod.AIGenerate,
                    onClick = { selectedMethod = GenerationMethod.AIGenerate },
                )
            }

            // ERROR MESSAGE
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            // SAVE BUTTON
            Button(
                onClick = {
                    viewModel.saveDeck { deckId ->
                        // Snackbar jalan paralel (tidak blok navigasi)
                        scope.launch {
                            snackbarHostState.showSnackbar("✅ Deck berhasil dibuat!")
                        }
                        // Navigasi setelah jeda singkat biar popup sempat kelihatan
                        scope.launch {
                            delay(600)
                            when (selectedMethod) {
                                GenerationMethod.Manual -> onSavedManual(deckId)
                                GenerationMethod.AIGenerate -> onSavedAIGenerate(deckId)
                            }
                        }
                    }
                },
                enabled = uiState.canSave,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(end = 8.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text("Menyimpan...")
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Text(
                        text = "Lanjutkan",
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private enum class GenerationMethod { Manual, AIGenerate }

@Composable
private fun MethodOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp).padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
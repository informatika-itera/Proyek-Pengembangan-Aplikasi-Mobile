package com.example.neurodeck.presentation.screens.importgenerate

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.SectionTitle
import com.example.neurodeck.presentation.navigation.AppTopBar
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


/**
 * @param deckId       Target deck. Pass dari nav argument.
 * @param onBack       Pop back stack (cancel).
 * @param onCompleted  Dipanggil saat semua cards selesai di-save.
 *                     Caller harus pop or navigate ke CardList.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportGenerateScreen(
    deckId: Long,
    onBack: () -> Unit,
    onCompleted: (deckId: Long, savedCount: Int) -> Unit,
    viewModel: ImportGenerateViewModel = koinViewModel(
        parameters = { parametersOf(deckId) },
    ),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.phase) {
        if (uiState.phase == GeneratePhase.Done) {
            onCompleted(deckId, uiState.savedCardsCount)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "AI Generate Kartu",
                canNavigateBack = true,
                onNavigationClick = onBack,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (uiState.phase) {
                GeneratePhase.Input -> InputPhase(
                    state = uiState,
                    onMaterialChange = viewModel::onMaterialChange,
                    onCardCountChange = viewModel::onCardCountChange,
                    onGenerate = viewModel::generate,
                )

                GeneratePhase.Generating -> LoadingPhase(
                    message = "AI sedang membaca materi & bikin kartu...\nMohon tunggu 5-15 detik.",
                )

                GeneratePhase.Preview -> PreviewPhase(
                    state = uiState,
                    onFrontChange = viewModel::onDraftFrontChange,
                    onBackChange = viewModel::onDraftBackChange,
                    onDelete = viewModel::deleteDraft,
                    onSaveAll = viewModel::saveAll,
                )

                GeneratePhase.Saving -> LoadingPhase(
                    message = "Menyimpan ${uiState.drafts.size} kartu ke deck...",
                )

                GeneratePhase.Done -> {
                    // Empty UI — LaunchedEffect di atas akan trigger navigation.
                    // Show brief success state biar tidak flash empty screen.
                    LoadingPhase(message = "✅ Selesai!")
                }

                GeneratePhase.Error -> ErrorMessage(
                    message = uiState.errorMessage ?: "Terjadi kesalahan",
                    onRetry = viewModel::retryFromInput,
                )
            }
        }
    }
}

@Composable
private fun InputPhase(
    state: ImportGenerateUiState,
    onMaterialChange: (String) -> Unit,
    onCardCountChange: (Int) -> Unit,
    onGenerate: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Paste materi belajarmu (catatan, ringkasan, slide).\n" +
                    "AI akan otomatis bikin flashcard dari teks ini.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.material,
            onValueChange = onMaterialChange,
            label = { Text("Teks Materi") },
            placeholder = {
                Text(
                    "Contoh: Fotosintesis adalah proses tumbuhan mengubah cahaya matahari menjadi energi kimia...",
                )
            },
            minLines = 8,
            maxLines = 12,
            supportingText = {
                val chars = state.material.length
                val minOK = chars >= ImportGenerateUiState.MIN_MATERIAL_LENGTH
                Text(
                    text = if (minOK) {
                        "$chars / ${ImportGenerateUiState.MAX_MATERIAL_LENGTH}"
                    } else {
                        "$chars / ${ImportGenerateUiState.MAX_MATERIAL_LENGTH} · " +
                                "minimal ${ImportGenerateUiState.MIN_MATERIAL_LENGTH} karakter"
                    },
                    color = if (minOK) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Jumlah Kartu: ${state.cardCount}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Slider(
            value = state.cardCount.toFloat(),
            onValueChange = { onCardCountChange(it.toInt()) },
            valueRange = ImportGenerateUiState.MIN_CARD_COUNT.toFloat()..
                    ImportGenerateUiState.MAX_CARD_COUNT.toFloat(),
            steps = ImportGenerateUiState.MAX_CARD_COUNT -
                    ImportGenerateUiState.MIN_CARD_COUNT - 1,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Min ${ImportGenerateUiState.MIN_CARD_COUNT}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Max ${ImportGenerateUiState.MAX_CARD_COUNT}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGenerate,
            enabled = state.canGenerate,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generate Flashcards")
        }
    }
}

@Composable
private fun LoadingPhase(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PreviewPhase(
    state: ImportGenerateUiState,
    onFrontChange: (Long, String) -> Unit,
    onBackChange: (Long, String) -> Unit,
    onDelete: (Long) -> Unit,
    onSaveAll: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "✨ ${state.drafts.size} Kartu Ter-generate",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Edit atau hapus kartu yang kurang tepat sebelum simpan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }

        state.errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Drafts list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = state.drafts, key = { it.id }) { draft ->
                DraftCard(
                    draft = draft,
                    onFrontChange = { onFrontChange(draft.id, it) },
                    onBackChange = { onBackChange(draft.id, it) },
                    onDelete = { onDelete(draft.id) },
                )
            }
        }

        // Bottom save button
        Button(
            onClick = onSaveAll,
            enabled = state.canSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Text("Simpan ${state.drafts.size} Kartu ke Deck")
        }
    }
}

@Composable
private fun DraftCard(
    draft: CardDraft,
    onFrontChange: (String) -> Unit,
    onBackChange: (String) -> Unit,
    onDelete: () -> Unit,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Kartu #${draft.id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Hapus kartu",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = draft.front,
                onValueChange = onFrontChange,
                label = { Text("Pertanyaan / Front") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 1,
                maxLines = 3,
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = draft.back,
                onValueChange = onBackChange,
                label = { Text("Jawaban / Back") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 1,
                maxLines = 4,
            )
        }
    }
}

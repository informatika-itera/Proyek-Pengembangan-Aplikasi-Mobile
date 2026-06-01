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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neurodeck.presentation.components.SectionTitle
import com.example.neurodeck.presentation.navigation.AppTopBar
import org.koin.compose.viewmodel.koinViewModel

// ════════════════════════════════════════════════════════════════════════════
// CreateDeckScreen.kt — Sprint 2 P3d.2
//
// Form 2-step utk bikin deck baru:
//   1. Input nama (required, min 3 char) + deskripsi (opsional)
//   2. Pilih cara fill kartu: Manual atau AI Generate
//   3. Setelah "Lanjutkan" → save deck → navigate sesuai pilihan
//
// UX pattern: user pilih METODE generation DULU sebelum input nama? Atau
// nama dulu? Saya pilih NAMA DULU karena:
//   - Nama deck adalah informasi inti (mandatory), generation method adalah
//     workflow choice (bisa di-defer).
//   - Kalau user pilih AI tapi ganti pikiran ke Manual, dia tidak perlu
//     re-input nama.
//   - Pattern sama dengan Notion/Anki: nama dulu, content method kemudian.
// ════════════════════════════════════════════════════════════════════════════

/**
 * CreateDeck screen — form bikin deck baru + pilih metode generation.
 *
 * Navigation contract:
 *   @param onBack            User tap back arrow → popBackStack.
 *   @param onSavedManual     Deck saved + user pilih MANUAL → navigate ke
 *                            CardList(deckId) untuk add card manually.
 *   @param onSavedAIGenerate Deck saved + user pilih AI → navigate ke
 *                            ImportGenerate(deckId).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeckScreen(
    onBack: () -> Unit,
    onSavedManual: (deckId: Long) -> Unit,
    onSavedAIGenerate: (deckId: Long) -> Unit,
    viewModel: CreateDeckViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Local state: pilihan metode generation. Default Manual.
    // Bisa pakai ViewModel field tapi overkill — ini pure UI state, tidak
    // perlu survive process death.
    var selectedMethod by remember { mutableStateOf(GenerationMethod.Manual) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Buat Deck Baru",
                canNavigateBack = true,
                onNavigationClick = onBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // ════════════════════════════════════════════════════════════════
            // STEP 1: Form Input
            // ════════════════════════════════════════════════════════════════
            SectionTitle(text = "Informasi Deck")

            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Judul Deck *") },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ════════════════════════════════════════════════════════════════
            // STEP 2: Pilih Metode Generation
            // ════════════════════════════════════════════════════════════════
            SectionTitle(text = "Cara Buat Kartu")

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
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

            Spacer(modifier = Modifier.height(24.dp))

            // ════════════════════════════════════════════════════════════════
            // ERROR MESSAGE (kalau ada)
            // ════════════════════════════════════════════════════════════════
            uiState.errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ════════════════════════════════════════════════════════════════
            // SUBMIT BUTTON
            // ════════════════════════════════════════════════════════════════
            Button(
                onClick = {
                    viewModel.saveDeck { deckId ->
                        when (selectedMethod) {
                            GenerationMethod.Manual -> onSavedManual(deckId)
                            GenerationMethod.AIGenerate -> onSavedAIGenerate(deckId)
                        }
                    }
                },
                enabled = uiState.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...")
                } else {
                    Text("Lanjutkan")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// SUPPORTING TYPES & COMPOSABLES
// ════════════════════════════════════════════════════════════════════════════

/**
 * Pilihan metode generation. Pakai enum supaya:
 *   - Compile-time exhaustive when di Screen
 *   - Mudah extend kalau tambah method baru (e.g. Import from CSV)
 */
private enum class GenerationMethod {
    Manual,
    AIGenerate,
}

/**
 * Selectable card untuk pilih metode generation.
 * Selected state ditandai via border tebal + primaryContainer background.
 */
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
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}
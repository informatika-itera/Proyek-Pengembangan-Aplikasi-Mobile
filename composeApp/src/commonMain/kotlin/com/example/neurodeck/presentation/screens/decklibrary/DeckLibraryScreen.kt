package com.example.neurodeck.presentation.screens.decklibrary

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.presentation.components.EmptyState
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.LoadingIndicator
import com.example.neurodeck.presentation.theme.NeurodeckTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 *   @param onDeckClick           User tap deck card → navigate ke CardList.
 *   @param onCreateDeck          User tap FAB "Buat Deck" → CreateDeck screen.
 *   @param onImportGenerate      User tap "AI Generate" → ImportGenerate (deckId=0).
 */
@Composable
fun DeckLibraryScreen(
    onDeckClick: (deckId: Long) -> Unit,
    onCreateDeck: () -> Unit = {},
    onImportGenerate: () -> Unit = {},
    viewModel: DeckLibraryViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Edit dialog state (lokal)
    var deckToEdit by remember { mutableStateOf<Deck?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // SEARCH BAR
            if (uiState !is DeckLibraryUiState.Empty &&
                uiState !is DeckLibraryUiState.Loading
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onClear = viewModel::clearSearch,
                )

                AIGenerateBanner(onClick = onImportGenerate)
            }

            // CONTENT
            when (val state = uiState) {
                DeckLibraryUiState.Loading -> LoadingIndicator()

                DeckLibraryUiState.Empty -> EmptyState(
                    emoji = "🃏",
                    title = "Belum Ada Deck",
                    description = "Mulai belajar dengan membuat deck flashcard pertama Anda.\n" +
                            "Bisa manual atau AI Generate dari teks materi.",
                    primaryActionLabel = "Buat Deck Pertama",
                    onPrimaryAction = onCreateDeck,
                )

                is DeckLibraryUiState.NoSearchResults -> NoSearchResultsState(
                    query = state.query,
                    onClearSearch = viewModel::clearSearch,
                )

                is DeckLibraryUiState.Success -> DeckList(
                    decks = state.decks,
                    onDeckClick = onDeckClick,
                    onEditClick = { deck -> deckToEdit = deck },
                    onDeleteClick = viewModel::deleteDeck,
                )

                is DeckLibraryUiState.Error -> ErrorMessage(message = state.message)
            }
        }

        // FAB Create Deck
        ExtendedFloatingActionButton(
            onClick = onCreateDeck,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Buat Deck") },
        )
    }

    // EDIT DIALOG
    deckToEdit?.let { deck ->
        DeckFormDialog(
            initialDeck = deck,
            onDismiss = { deckToEdit = null },
            onConfirm = { title, description ->
                viewModel.updateDeck(deck, title, description)
                deckToEdit = null
            },
        )
    }
}

// PRIVATE COMPOSABLES

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Cari deck...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Cari",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Hapus pencarian",
                    )
                }
            }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
        ),
    )
}

@Composable
private fun AIGenerateBanner(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(NeurodeckTheme.extras.heroBrush)
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Generate Kartu",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Text(
                    text = "Paste materi → AI bikin flashcard otomatis",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
        }
    }
}

@Composable
private fun NoSearchResultsState(
    query: String,
    onClearSearch: () -> Unit,
) {
    EmptyState(
        emoji = "🔍",
        title = "Tidak Ditemukan",
        description = "Tidak ada deck yang cocok dengan \"$query\".\nCoba kata kunci lain atau hapus pencarian.",
        primaryActionLabel = "Hapus Pencarian",
        onPrimaryAction = onClearSearch,
    )
}

@Composable
private fun DeckList(
    decks: List<Deck>,
    onDeckClick: (Long) -> Unit,
    onEditClick: (Deck) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = decks, key = { it.id }) { deck ->
            DeckCard(
                deck = deck,
                onClick = { onDeckClick(deck.id) },
                onEdit = { onEditClick(deck) },
                onDelete = { onDeleteClick(deck.id) },
            )
        }
    }
}

@Composable
private fun DeckCard(
    deck: Deck,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val iconBrush = if (deck.id % 2L == 0L) {
        NeurodeckTheme.extras.deckBrushPrimary
    } else {
        NeurodeckTheme.extras.deckBrushPink
    }

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBrush),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Style,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = deck.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                if (deck.description.isNotBlank()) {
                    Text(
                        text = deck.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${deck.cardCount} kartu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Deck",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus Deck",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Deck?") },
            text = {
                Text(
                    "Deck \"${deck.title}\" dan semua kartunya akan dihapus permanen. " +
                            "Tindakan ini tidak bisa di-undo.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal")
                }
            },
        )
    }
}

@Composable
private fun DeckFormDialog(
    initialDeck: Deck?,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String) -> Unit,
) {
    val isEditMode = initialDeck != null
    var title by remember { mutableStateOf(initialDeck?.title ?: "") }
    var description by remember { mutableStateOf(initialDeck?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditMode) "Edit Deck" else "Deck Baru") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, description) },
                enabled = title.isNotBlank(),
            ) {
                Text(if (isEditMode) "Simpan" else "Buat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
    )
}

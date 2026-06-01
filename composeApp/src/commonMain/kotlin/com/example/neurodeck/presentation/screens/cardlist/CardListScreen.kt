package com.example.neurodeck.presentation.screens.cardlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.presentation.components.EmptyState
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.LoadingIndicator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.compose.material.icons.filled.Edit

/**
 * CardList screen — kelola kartu dalam 1 deck.
 *
 * User flow:
 * 1. Lihat list semua kartu di deck (front + back snippet)
 * 2. Tap FAB → AddCardScreen
 * 3. Tap "Mulai Belajar" → StudySessionScreen
 * 4. Long-press / icon delete kartu → confirm dialog → delete
 *
 * Navigation contract:
 * - [onAddCard]: navigate ke AddCardScreen dengan deckId
 * - [onStartStudy]: navigate ke StudySessionScreen dengan deckId
 * - [onBack]: pop back stack
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(
    deckId: Long,
    onAddCard: (Long) -> Unit,
    onEditCard: (Long) -> Unit,
    onStartStudy: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: CardListViewModel = koinViewModel { parametersOf(deckId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (val state = uiState) {
                            is CardListUiState.Success -> state.deck.title
                            else -> "Memuat..."
                        },
                    )
                },
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
        floatingActionButton = {
            // FAB hanya muncul saat state Success (tidak Loading/Error)
            if (uiState is CardListUiState.Success) {
                FloatingActionButton(onClick = { onAddCard(deckId) }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Kartu")
                }
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                CardListUiState.Loading -> LoadingIndicator()

                is CardListUiState.Error -> ErrorMessage(message = state.message)

                is CardListUiState.Success -> {
                    if (state.cards.isEmpty()) {
                        EmptyState(
                            emoji = "✏️",
                            title = "Belum Ada Kartu",
                            description = "Tambahkan kartu pertama untuk deck \"${state.deck.title}\".",
                            primaryActionLabel = "Tambah Kartu",
                            onPrimaryAction = { onAddCard(deckId) },
                        )
                    } else {
                        CardListContent(
                            cards = state.cards,
                            onStartStudy = { onStartStudy(deckId) },
                            onEditCard = onEditCard,
                            onDeleteCard = viewModel::deleteCard,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardListContent(
    cards: List<Card>,
    onStartStudy: () -> Unit,
    onEditCard: (Long) -> Unit,
    onDeleteCard: (Long) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header: tombol Mulai Belajar (full width, prominent)
        ExtendedFloatingActionButton(
            onClick = onStartStudy,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Text(
                text = "Mulai Belajar (${cards.size} kartu)",
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.SemiBold,
            )
        }

        HorizontalDivider()

        // List kartu
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = cards, key = { it.id }) { card ->
                CardItem(
                    card = card,
                    onEdit = { onEditCard(card.id) },
                    onDelete = { onDeleteCard(card.id) },
                )
            }
        }
    }
}

/**
 * Card item: tampilkan front (besar) dan back (preview, smaller).
 * Tombol delete di pojok kanan.
 */
@Composable
private fun CardItem(
    card: Card,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = card.back,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2,
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Kartu",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus Kartu",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Hapus Kartu?") },
            text = {
                Text("Kartu ini akan dihapus permanen beserta riwayat reviewnya.")
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
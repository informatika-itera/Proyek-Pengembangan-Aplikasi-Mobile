package com.example.fitkos.presentation.screens.detail

import androidx.compose.foundation.layout.Arrangement
import com.example.fitkos.presentation.components.FitKosTopBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fitkos.domain.model.Note
import com.example.fitkos.presentation.components.EmptyState
import com.example.fitkos.presentation.components.LoadingIndicator
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onShare: (String) -> Unit,
    viewModel: NoteDetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is NoteDetailEvent.NoteDeleted -> onNavigateBack()
                is NoteDetailEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteNote()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            FitKosTopBar(
                title = "Detail Catatan Makanan",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is NoteDetailUiState.Loading -> {
                LoadingIndicator()
            }

            is NoteDetailUiState.Success -> {
                MealDetailContent(
                    note = state.note,
                    onEditClick = { onNavigateToEdit(noteId) },
                    onDeleteClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            is NoteDetailUiState.NotFound -> {
                EmptyState(
                    title = "Catatan Makanan Tidak Ditemukan",
                    message = "Catatan makanan mungkin sudah dihapus"
                )
            }
        }
    }
}

@Composable
private fun MealDetailContent(
    note: Note,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mealContent = remember(note.content) {
        parseMealContent(note.content)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderMealCard(note = note)

        DetailInfoCard(
            title = "Harga",
            value = if (mealContent.price.isNotBlank()) {
                "Rp${mealContent.price.formatRupiah()}"
            } else {
                "Belum diisi"
            }
        )

        DetailInfoCard(
            title = "Jenis Makan",
            value = note.category.displayName
        )

        NoteContentCard(
            content = mealContent.note
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onEditClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )

            Text(
                text = "Edit Catatan",
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )

            Text(
                text = "Hapus Catatan",
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun HeaderMealCard(
    note: Note
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = note.category.icon(),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = note.category.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = note.updatedAt.formatDateTime(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = note.title.ifBlank { "Makanan tanpa nama" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DetailInfoCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NoteContentCard(
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Catatan Tambahan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = content.ifBlank { "Tidak ada catatan tambahan." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Hapus Catatan Makanan")
        },
        text = {
            Text("Apakah kamu yakin ingin menghapus catatan makanan ini? Data yang sudah dihapus tidak bisa dikembalikan.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Hapus",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun parseMealContent(content: String): MealContent {
    val lines = content.lines()
    val firstLine = lines.firstOrNull().orEmpty()
    val hasPrice = firstLine.startsWith("Harga: Rp")

    return if (hasPrice) {
        MealContent(
            price = firstLine.removePrefix("Harga: Rp").filter { it.isDigit() },
            note = lines.drop(1).joinToString("\n").trim()
        )
    } else {
        MealContent(
            price = "",
            note = content.trim()
        )
    }
}

private fun String.formatRupiah(): String {
    if (isBlank()) return this

    return reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}

private fun Instant.formatDateTime(): String {
    val localDateTime = toLocalDateTime(TimeZone.currentSystemDefault())
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val month = localDateTime.monthNumber.toString().padStart(2, '0')
    val year = localDateTime.year
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')

    return "$day/$month/$year • $hour:$minute"
}

private fun com.example.fitkos.domain.model.NoteCategory.icon(): String {
    return when (this) {
        com.example.fitkos.domain.model.NoteCategory.BREAKFAST -> "☀️"
        com.example.fitkos.domain.model.NoteCategory.LUNCH -> "🍽️"
        com.example.fitkos.domain.model.NoteCategory.DINNER -> "🌙"
        com.example.fitkos.domain.model.NoteCategory.SNACK -> "🍪"
        com.example.fitkos.domain.model.NoteCategory.DRINK -> "💧"
        com.example.fitkos.domain.model.NoteCategory.OTHER -> "🥗"
    }
}

private data class MealContent(
    val price: String,
    val note: String
)
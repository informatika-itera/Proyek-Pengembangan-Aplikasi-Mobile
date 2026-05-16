package com.example.mybawanggacha.presentation.screens.library.list

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mybawanggacha.domain.library.model.LibraryEntry
import com.example.mybawanggacha.domain.library.model.LibraryStatus
import com.example.mybawanggacha.domain.library.model.MediaType
import com.example.mybawanggacha.presentation.components.ConfirmationDialog
import com.example.mybawanggacha.presentation.components.EmptyState
import com.example.mybawanggacha.presentation.components.ErrorState
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGMainRailKey
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import com.example.mybawanggacha.presentation.screens.library.LibraryUiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyListScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAnimeList: () -> Unit,
    onNavigateToMangaList: () -> Unit,
    onNavigateToDetail: (Int, MediaType) -> Unit,
    onEditEntry: (LibraryEntry) -> Unit,
    viewModel: LibraryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<LibraryEntry?>(null) }

    deleteTarget?.let { entry ->
        ConfirmationDialog(
            title = "Hapus dari My Library?",
            message = "${entry.title} akan dihapus dari daftar lokal.",
            confirmText = "Hapus",
            onConfirm = {
                viewModel.deleteEntry(entry)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null },
            isDestructive = true
        )
    }

    MBGSideRailScaffold(
        selectedRailKey = MBGMainRailKey.MyLibrary,
        onRailItemClick = { key ->
            when (key) {
                MBGMainRailKey.Home -> onNavigateHome()
                MBGMainRailKey.MyLibrary -> Unit
                MBGMainRailKey.AnimeList -> onNavigateToAnimeList()
                MBGMainRailKey.MangaList -> onNavigateToMangaList()
            }
        },
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp, top = 32.dp, end = 18.dp)
        ) {
            Text(
                text = "My Library",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Daftar yang kamu simpan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            LibraryStatusFilterRow(
                selectedStatus = selectedStatus,
                onStatusSelected = viewModel::selectStatus
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    LibraryUiState.Loading -> LoadingIndicator()
                    is LibraryUiState.Empty -> LibraryEmptyState(selectedStatus = state.selectedStatus)
                    is LibraryUiState.Error -> ErrorState(message = state.message)
                    is LibraryUiState.Success -> LibraryEntryList(
                        entries = state.entries,
                        onOpenEntry = onNavigateToDetail,
                        onEditEntry = onEditEntry,
                        onDeleteEntry = { deleteTarget = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryStatusFilterRow(
    selectedStatus: LibraryStatus?,
    onStatusSelected: (LibraryStatus?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 20.dp)
    ) {
        item(key = "all") {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusSelected(null) },
                label = { Text("Semua") }
            )
        }

        items(
            items = LibraryStatus.entries,
            key = { it.storageKey }
        ) { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                label = { Text(status.defaultLabel) }
            )
        }
    }
}

@Composable
private fun LibraryEmptyState(selectedStatus: LibraryStatus?) {
    EmptyState(
        title = if (selectedStatus == null) "My Library masih kosong" else "Belum ada item ${selectedStatus.defaultLabel}",
        message = "Tambahkan anime dari halaman detail, lalu atur status, progress, score, dan catatan di sini."
    )
}

@Composable
private fun LibraryEntryList(
    entries: List<LibraryEntry>,
    onOpenEntry: (Int, MediaType) -> Unit,
    onEditEntry: (LibraryEntry) -> Unit,
    onDeleteEntry: (LibraryEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = entries,
            key = { it.id }
        ) { entry ->
            LibraryEntryCard(
                entry = entry,
                onOpen = { onOpenEntry(entry.mediaId, entry.mediaType) },
                onEdit = { onEditEntry(entry) },
                onDelete = { onDeleteEntry(entry) }
            )
        }
    }
}

@Composable
private fun LibraryEntryCard(
    entry: LibraryEntry,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(92.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!entry.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = entry.imageUrl,
                        contentDescription = entry.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = entry.mediaType.displayName.take(1),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallPill(text = entry.mediaType.displayName)
                    SmallPill(text = entry.status.labelFor(entry.mediaType))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = buildString {
                        append("Progress: ${entry.progress.format()}")
                        entry.userScore?.let { append(" • Score: ${it.value}/10") }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(onClick = onEdit) {
                        Text("Edit")
                    }
                    TextButton(onClick = onDelete) {
                        Text(
                            text = "Hapus",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SmallPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

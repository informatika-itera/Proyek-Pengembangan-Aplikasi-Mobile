package com.example.mybawanggacha.presentation.screens.library

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mybawanggacha.domain.model.LibraryStatus
import com.example.mybawanggacha.domain.model.MediaType
import com.example.mybawanggacha.presentation.components.LoadingIndicator
import com.example.mybawanggacha.presentation.components.MBGRailBackButton
import com.example.mybawanggacha.presentation.components.MBGSideRailScaffold
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryEntryEditorScreen(
    mediaId: Int,
    mediaType: MediaType,
    title: String,
    imageUrl: String?,
    totalCount: Int?,
    entryId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: LibraryEntryEditorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(mediaId, mediaType, entryId) {
        viewModel.start(
            mediaId = mediaId,
            mediaType = mediaType,
            title = title,
            imageUrl = imageUrl,
            totalCount = totalCount,
            entryId = entryId
        )
    }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onNavigateBack()
    }

    MBGSideRailScaffold(
        selectedRailKey = "library_editor",
        railItems = emptyList(),
        onRailItemClick = {},
        topAction = {
            MBGRailBackButton(onClick = onNavigateBack)
        }
    ) {
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            LibraryEntryEditorContent(
                state = uiState,
                onStatusChange = viewModel::updateStatus,
                onProgressChange = viewModel::updateProgress,
                onTotalChange = viewModel::updateTotal,
                onScoreChange = viewModel::updateScore,
                onNotesChange = viewModel::updateNotes,
                onCancel = onNavigateBack,
                onSave = viewModel::save
            )
        }
    }
}

@Composable
private fun LibraryEntryEditorContent(
    state: LibraryEntryEditorUiState,
    onStatusChange: (LibraryStatus) -> Unit,
    onProgressChange: (String) -> Unit,
    onTotalChange: (String) -> Unit,
    onScoreChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 4.dp, top = 32.dp, end = 20.dp, bottom = 32.dp)
    ) {
        Text(
            text = if (state.entryId == null) "Tambah ke My Library" else "Edit My Library",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        LibraryEntryHeader(state)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Status",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        StatusChipRow(
            mediaType = state.mediaType,
            selectedStatus = state.status,
            onStatusChange = onStatusChange
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.progressText,
                onValueChange = onProgressChange,
                label = { Text("Progress episode") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = state.totalText,
                onValueChange = onTotalChange,
                label = { Text("Total episode") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.scoreText,
            onValueChange = onScoreChange,
            label = { Text("Score pribadi (1-10)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.notes,
            onValueChange = onNotesChange,
            label = { Text("Catatan opsional") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        state.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Batal")
            }

            Button(
                onClick = onSave,
                enabled = !state.isSaving,
                modifier = Modifier.weight(1f)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan")
                }
            }
        }
    }
}

@Composable
private fun LibraryEntryHeader(state: LibraryEntryEditorUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(68.dp)
                .height(96.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (!state.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = state.imageUrl,
                    contentDescription = state.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = state.mediaType.displayName.take(1),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = state.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.mediaType.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatusChipRow(
    mediaType: MediaType,
    selectedStatus: LibraryStatus,
    onStatusChange: (LibraryStatus) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 20.dp)
    ) {
        items(
            items = LibraryStatus.entries,
            key = { it.storageKey }
        ) { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusChange(status) },
                label = {
                    Text(
                        text = status.labelFor(mediaType),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

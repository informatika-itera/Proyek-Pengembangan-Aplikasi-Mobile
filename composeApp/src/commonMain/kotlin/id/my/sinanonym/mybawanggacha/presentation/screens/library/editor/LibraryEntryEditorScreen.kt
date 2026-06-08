package id.my.sinanonym.mybawanggacha.presentation.screens.library.editor

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
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryStatus
import id.my.sinanonym.mybawanggacha.domain.library.model.MediaType
import id.my.sinanonym.mybawanggacha.presentation.components.LoadingIndicator
import id.my.sinanonym.mybawanggacha.presentation.components.MBGRailBackButton
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailScaffold
import id.my.sinanonym.mybawanggacha.presentation.screens.library.LibraryEntryEditorUiState
import org.koin.compose.viewmodel.koinViewModel
import id.my.sinanonym.mybawanggacha.presentation.screens.library.editor.components.LibraryEntryEditorContent

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

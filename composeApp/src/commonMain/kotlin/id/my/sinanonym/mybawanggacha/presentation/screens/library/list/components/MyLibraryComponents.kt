package id.my.sinanonym.mybawanggacha.presentation.screens.library.list.components

import id.my.sinanonym.mybawanggacha.presentation.screens.library.list.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryEntry
import id.my.sinanonym.mybawanggacha.domain.library.model.LibraryStatus
import id.my.sinanonym.mybawanggacha.domain.library.model.MediaType
import id.my.sinanonym.mybawanggacha.presentation.components.ConfirmationDialog
import id.my.sinanonym.mybawanggacha.presentation.components.EmptyState
import id.my.sinanonym.mybawanggacha.presentation.components.ErrorState
import id.my.sinanonym.mybawanggacha.presentation.components.LoadingIndicator
import id.my.sinanonym.mybawanggacha.presentation.components.MBGMainRailKey
import id.my.sinanonym.mybawanggacha.presentation.components.MBGRailBackButton
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailScaffold
import id.my.sinanonym.mybawanggacha.presentation.components.PullRefreshContainer
import id.my.sinanonym.mybawanggacha.presentation.components.ScreenHeader
import id.my.sinanonym.mybawanggacha.presentation.screens.library.LibraryUiState
import org.koin.compose.viewmodel.koinViewModel

private const val LibrarySwipeThresholdPx = 96f
private const val LibrarySwipeMaxPx = 180f

@Composable
internal fun LibraryStatusFilterRow(
    selectedStatus: LibraryStatus?,
    onStatusSelected: (LibraryStatus?) -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndex = selectedStatus
        ?.let { LibraryStatus.entries.indexOf(it) + 1 }
        ?: 0

    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex.coerceAtLeast(0))
    }

    LazyRow(
        state = listState,
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
internal fun LibraryEmptyState(selectedStatus: LibraryStatus?) {
    EmptyState(
        title = if (selectedStatus == null) "My Library masih kosong" else "Belum ada item ${selectedStatus.defaultLabel}",
        message = "Tambahkan anime dari halaman detail, lalu atur status, progress, score, dan catatan di sini."
    )
}


@Composable
internal fun LibraryListSkeleton() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = 6,
            key = { index -> "library_skeleton_$index" },
            contentType = { "library_entry_skeleton" }
        ) {
            LibraryEntrySkeletonCard()
        }
    }
}

@Composable
private fun LibraryEntrySkeletonCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
            contentColor = MaterialTheme.colorScheme.onSurface
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
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f))
            )

            Column(modifier = Modifier.weight(1f)) {
                LibrarySkeletonLine(widthFraction = 0.82f, height = 16.dp)
                Spacer(modifier = Modifier.height(8.dp))
                LibrarySkeletonLine(widthFraction = 0.48f, height = 12.dp)
                Spacer(modifier = Modifier.height(10.dp))
                LibrarySkeletonLine(widthFraction = 0.68f, height = 12.dp)
            }
        }
    }
}

@Composable
private fun LibrarySkeletonLine(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.78f))
    )
}

@Composable
internal fun LibraryEntryList(
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
            key = { it.id },
            contentType = { "library_entry" }
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
    var swipeOffset by remember(entry.id) { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(entry.id) {
                detectHorizontalDragGestures(
                    onDragCancel = { swipeOffset = 0f },
                    onDragEnd = {
                        when {
                            swipeOffset <= -LibrarySwipeThresholdPx -> onDelete()
                            swipeOffset >= LibrarySwipeThresholdPx -> onEdit()
                        }
                        swipeOffset = 0f
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        swipeOffset = (swipeOffset + dragAmount)
                            .coerceIn(-LibrarySwipeMaxPx, LibrarySwipeMaxPx)
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationX = swipeOffset * 0.16f
                }
                .clickable(onClick = onOpen),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
                contentColor = MaterialTheme.colorScheme.onSurface
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
                }
            }
        }

        LibrarySwipeActionOverlay(swipeOffset = swipeOffset)
    }
}

@Composable
private fun BoxScope.LibrarySwipeActionOverlay(
    swipeOffset: Float
) {
    val progress = (abs(swipeOffset) / LibrarySwipeThresholdPx).coerceIn(0f, 1f)
    if (progress <= 0f) return

    val isEditSwipe = swipeOffset > 0f
    val label = if (isEditSwipe) "Edit" else "Delete"
    val icon = if (isEditSwipe) Icons.Default.Edit else Icons.Default.Delete
    val contentAlignment = if (isEditSwipe) Alignment.CenterStart else Alignment.CenterEnd
    val actionColor = if (isEditSwipe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    val gradient = if (isEditSwipe) {
        Brush.horizontalGradient(
            colors = listOf(
                actionColor.copy(alpha = 0.30f * progress),
                actionColor.copy(alpha = 0.12f * progress),
                Color.Transparent
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                actionColor.copy(alpha = 0.12f * progress),
                actionColor.copy(alpha = 0.30f * progress)
            )
        )
    }

    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = contentAlignment
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f * progress))
                .padding(horizontal = 11.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = actionColor.copy(alpha = 0.92f * progress),
                modifier = Modifier.width(16.dp).height(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = actionColor.copy(alpha = 0.92f * progress),
                fontWeight = FontWeight.Bold
            )
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

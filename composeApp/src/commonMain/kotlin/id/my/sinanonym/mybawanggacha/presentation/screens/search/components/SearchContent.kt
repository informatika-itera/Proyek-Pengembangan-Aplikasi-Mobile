package id.my.sinanonym.mybawanggacha.presentation.screens.search.components

import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchFilterMetadataUiState
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchUiState
import id.my.sinanonym.mybawanggacha.presentation.screens.search.buildActiveFilterLabels
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchItem
import id.my.sinanonym.mybawanggacha.presentation.components.EmptyState
import id.my.sinanonym.mybawanggacha.presentation.components.ErrorState
import id.my.sinanonym.mybawanggacha.presentation.components.LoadingIndicator
import kotlinx.coroutines.flow.distinctUntilChanged
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchContent(
    filters: MediaSearchFilters,
    uiState: SearchUiState,
    filterMetadata: SearchFilterMetadataUiState,
    onFiltersChange: (MediaSearchFilters) -> Unit,
    onSearch: () -> Unit,
    onReset: () -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onItemClick: (MediaSearchItem) -> Unit
) {
    var showFilters by remember { mutableStateOf(false) }
    val activeLabels = remember(filters, filterMetadata) {
        buildActiveFilterLabels(filters = filters, filterMetadata = filterMetadata)
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    val latestUiState by rememberUpdatedState(uiState)
    val latestOnLoadMore by rememberUpdatedState(onLoadMore)
    var loadMoreArmed by remember { mutableStateOf(true) }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 && lastVisibleIndex >= totalItems - 4
        }
            .distinctUntilChanged()
            .collect { isNearEnd ->
                if (!isNearEnd) {
                    loadMoreArmed = true
                    return@collect
                }

                val state = latestUiState
                if (
                    loadMoreArmed &&
                    state is SearchUiState.Success &&
                    state.canLoadMore &&
                    !state.isLoadingMore
                ) {
                    loadMoreArmed = false
                    latestOnLoadMore()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 4.dp, top = 18.dp, end = 18.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(contentType = "search_panel") {
            SearchPanel(
                filters = filters,
                activeFilterCount = activeLabels.size,
                onFiltersChange = onFiltersChange,
                onOpenFilters = { showFilters = true },
                onSearch = onSearch
            )
        }

        if (activeLabels.isNotEmpty()) {
            item(contentType = "active_filters") {
                ActiveFilterRow(
                    labels = activeLabels,
                    onReset = onReset
                )
            }
        }

        when (uiState) {
            SearchUiState.Idle -> item {
                EmptyState(
                    title = SearchText.emptyIdleTitle,
                    message = SearchText.emptyIdleMessage,
                    modifier = Modifier.height(360.dp)
                )
            }

            SearchUiState.Loading -> item(contentType = "search_loading") {
                LoadingIndicator(modifier = Modifier.height(360.dp))
            }

            is SearchUiState.Error -> item(contentType = "search_error") {
                ErrorState(
                    message = uiState.message,
                    onRetry = onRetry,
                    modifier = Modifier.height(360.dp)
                )
            }

            is SearchUiState.Success -> {
                if (uiState.items.isEmpty()) {
                    item(contentType = "search_empty") {
                        EmptyState(
                            title = SearchText.emptyResultTitle,
                            message = SearchText.emptyResultMessage,
                            modifier = Modifier.height(360.dp)
                        )
                    }
                } else {
                    items(
                        items = uiState.items,
                        key = { item -> "${item.mediaType}:${item.malId}" },
                        contentType = { "search_result" }
                    ) { item ->
                        ResultCard(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                    }

                    if (uiState.isLoadingMore) {
                        item(contentType = "search_loading_more") { LoadingMoreRow() }
                    }
                }
            }
        }
    }

    if (showFilters) {
        var draft by remember(showFilters) { mutableStateOf(filters) }

        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = sheetState
        ) {
            SearchFilterSheet(
                filters = draft,
                filterMetadata = filterMetadata,
                onFiltersChange = { draft = it },
                onReset = { draft = MediaSearchFilters(mediaType = draft.mediaType) },
                onApply = {
                    onFiltersChange(draft)
                    showFilters = false
                },
                onApplyAndSearch = {
                    onFiltersChange(draft)
                    showFilters = false
                    onSearch()
                }
            )
        }
    }
}

@Composable
private fun LoadingMoreRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = SearchText.loadingMore,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

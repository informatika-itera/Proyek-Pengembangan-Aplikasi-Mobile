package id.my.sinanonym.mybawanggacha.presentation.screens.search.components

import id.my.sinanonym.mybawanggacha.presentation.screens.search.ANIME_TYPES
import id.my.sinanonym.mybawanggacha.presentation.screens.search.MANGA_TYPES
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SORT_OPTIONS
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchTestTags
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType
import id.my.sinanonym.mybawanggacha.presentation.screens.search.ANIME_STATUSES
import id.my.sinanonym.mybawanggacha.presentation.screens.search.MANGA_STATUSES

@Composable
internal fun SearchPanel(
    filters: MediaSearchFilters,
    activeFilterCount: Int,
    onFiltersChange: (MediaSearchFilters) -> Unit,
    onOpenFilters: () -> Unit,
    onSearch: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = filters.query,
                onValueChange = { value -> onFiltersChange(filters.copy(query = value)) },
                modifier = Modifier
                    .weight(1f)
                    .testTag(SearchTestTags.queryField),
                singleLine = true,
                placeholder = {
                    Text(
                        text = SearchText.queryPlaceholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )
            FilledIconButton(
                onClick = onSearch,
                modifier = Modifier
                    .size(50.dp)
                    .testTag(SearchTestTags.searchButton)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = SearchText.searchButton,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediaTypeDropdown(
                selected = filters.mediaType,
                modifier = Modifier.weight(1f),
                onSelected = { mediaType ->
                    onFiltersChange(
                        filters.copy(
                            mediaType = mediaType,
                            type = null,
                            status = null,
                            rating = null,
                            orderBy = null,
                            sort = null
                        )
                    )
                }
            )
            OutlinedButton(
                onClick = onOpenFilters,
                modifier = Modifier
                    .weight(1f)
                    .testTag(SearchTestTags.filterButton)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = SearchText.filterButton(activeFilterCount),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                QuickFilterChip(
                    label = SearchText.nsfwLabel,
                    selected = !filters.sfw,
                    onClick = { onFiltersChange(filters.copy(sfw = !filters.sfw)) }
                )
            }
            item {
                DropdownChip(
                    label = SearchText.typeLabel,
                    value = filters.type,
                    options = if (filters.mediaType == SearchMediaType.Anime) ANIME_TYPES else MANGA_TYPES,
                    onSelected = { value -> onFiltersChange(filters.copy(type = value)) }
                )
            }
            item {
                DropdownChip(
                    label = SearchText.statusLabel,
                    value = filters.status,
                    options = if (filters.mediaType == SearchMediaType.Anime) ANIME_STATUSES else MANGA_STATUSES,
                    onSelected = { value -> onFiltersChange(filters.copy(status = value)) }
                )
            }
            item {
                DropdownChip(
                    label = SearchText.sortLabel,
                    value = filters.sort,
                    options = SORT_OPTIONS,
                    onSelected = { value -> onFiltersChange(filters.copy(sort = value)) }
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f))
    }
}

@Composable
internal fun ActiveFilterRow(
    labels: List<String>,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(labels) { label ->
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        TextButton(onClick = onReset) {
            Text(SearchText.resetButton)
        }
    }
}
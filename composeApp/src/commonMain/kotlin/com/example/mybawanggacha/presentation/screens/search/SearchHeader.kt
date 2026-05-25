package com.example.mybawanggacha.presentation.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.SearchMediaType

@Composable
internal fun SearchHeader() {
    Column {
        Text(
            text = SearchText.screenTitle,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = SearchText.screenSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
internal fun SearchCompactPanel(
    filters: MediaSearchFilters,
    activeFilterCount: Int,
    onFiltersChange: (MediaSearchFilters) -> Unit,
    onOpenFilters: () -> Unit,
    onSearch: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchMediaTypeDropdown(
                selected = filters.mediaType,
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

            OutlinedTextField(
                value = filters.query,
                onValueChange = { value -> onFiltersChange(filters.copy(query = value)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(SearchTestTags.queryField),
                singleLine = true,
                label = { Text(SearchText.queryLabel) },
                placeholder = { Text(SearchText.queryPlaceholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    QuickFilterChip(
                        label = SearchText.sfwChip,
                        selected = filters.sfw,
                        onClick = { onFiltersChange(filters.copy(sfw = !filters.sfw)) }
                    )
                }
                item {
                    SearchDropdownChip(
                        label = SearchText.typeLabel,
                        value = filters.type,
                        options = if (filters.mediaType == SearchMediaType.Anime) ANIME_TYPES else MANGA_TYPES,
                        onSelected = { value -> onFiltersChange(filters.copy(type = value)) }
                    )
                }
                item {
                    SearchDropdownChip(
                        label = SearchText.statusLabel,
                        value = filters.status,
                        options = if (filters.mediaType == SearchMediaType.Anime) ANIME_STATUSES else MANGA_STATUSES,
                        onSelected = { value -> onFiltersChange(filters.copy(status = value)) }
                    )
                }
                item {
                    SearchDropdownChip(
                        label = SearchText.sortLabel,
                        value = filters.sort,
                        options = SORT_OPTIONS,
                        onSelected = { value -> onFiltersChange(filters.copy(sort = value)) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = SearchText.filterButton(activeFilterCount),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Button(
                    onClick = onSearch,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(SearchTestTags.searchButton)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(SearchText.searchButton)
                }
            }
        }
    }
}

@Composable
internal fun ActiveFilterRow(
    labels: List<String>,
    onReset: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = SearchText.activeFiltersTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onReset) {
                Text(SearchText.resetButton)
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
    }
}

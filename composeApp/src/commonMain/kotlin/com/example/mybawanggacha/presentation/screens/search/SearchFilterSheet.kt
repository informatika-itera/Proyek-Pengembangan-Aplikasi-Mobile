package com.example.mybawanggacha.presentation.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.SearchMediaType

@Composable
internal fun SearchFilterSheet(
    filters: MediaSearchFilters,
    onFiltersChange: (MediaSearchFilters) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    onApplyAndSearch: () -> Unit
) {
    var generalExpanded by remember { mutableStateOf(true) }
    var classificationExpanded by remember { mutableStateOf(true) }
    var scoreExpanded by remember { mutableStateOf(false) }
    var metadataExpanded by remember { mutableStateOf(false) }
    var sortingExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SearchFilterSheetHeader() }

        item {
            FilterSection(
                title = SearchText.generalSectionTitle,
                subtitle = SearchText.generalSectionSubtitle,
                expanded = generalExpanded,
                onToggle = { generalExpanded = !generalExpanded }
            ) {
                SmallSearchTextField(
                    label = SearchText.limitLabel,
                    value = filters.limit,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(limit = value)) }
                )
                SmallSearchTextField(
                    label = SearchText.letterLabel,
                    value = filters.letter,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(letter = value)) }
                )
                ToggleRow(
                    label = SearchText.sfwOnlyLabel,
                    checked = filters.sfw,
                    onCheckedChange = { value -> onFiltersChange(filters.copy(sfw = value)) }
                )
                ToggleRow(
                    label = SearchText.unapprovedLabel,
                    checked = filters.unapproved,
                    onCheckedChange = { value -> onFiltersChange(filters.copy(unapproved = value)) }
                )
            }
        }

        item {
            FilterSection(
                title = SearchText.classificationSectionTitle,
                subtitle = SearchText.classificationSectionSubtitle,
                expanded = classificationExpanded,
                onToggle = { classificationExpanded = !classificationExpanded }
            ) {
                SearchDropdown(
                    label = SearchText.typeLabel,
                    value = filters.type,
                    options = if (filters.mediaType == SearchMediaType.Anime) ANIME_TYPES else MANGA_TYPES,
                    modifier = Modifier.fillMaxWidth(),
                    onSelected = { value -> onFiltersChange(filters.copy(type = value)) }
                )
                SearchDropdown(
                    label = SearchText.statusLabel,
                    value = filters.status,
                    options = if (filters.mediaType == SearchMediaType.Anime) ANIME_STATUSES else MANGA_STATUSES,
                    modifier = Modifier.fillMaxWidth(),
                    onSelected = { value -> onFiltersChange(filters.copy(status = value)) }
                )
                if (filters.mediaType == SearchMediaType.Anime) {
                    SearchDropdown(
                        label = SearchText.ratingLabel,
                        value = filters.rating,
                        options = ANIME_RATINGS,
                        modifier = Modifier.fillMaxWidth(),
                        onSelected = { value -> onFiltersChange(filters.copy(rating = value)) }
                    )
                }
            }
        }

        item {
            FilterSection(
                title = SearchText.scoreDateSectionTitle,
                subtitle = SearchText.scoreDateSectionSubtitle,
                expanded = scoreExpanded,
                onToggle = { scoreExpanded = !scoreExpanded }
            ) {
                SmallSearchTextField(
                    label = SearchText.exactScoreLabel,
                    value = filters.score,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(score = value)) }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SmallSearchTextField(
                        label = SearchText.minScoreLabel,
                        value = filters.minScore,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(minScore = value)) }
                    )
                    SmallSearchTextField(
                        label = SearchText.maxScoreLabel,
                        value = filters.maxScore,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(maxScore = value)) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SmallSearchTextField(
                        label = SearchText.startDateLabel,
                        value = filters.startDate,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(startDate = value)) }
                    )
                    SmallSearchTextField(
                        label = SearchText.endDateLabel,
                        value = filters.endDate,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(endDate = value)) }
                    )
                }
            }
        }

        item {
            FilterSection(
                title = SearchText.metadataSectionTitle,
                subtitle = if (filters.mediaType == SearchMediaType.Anime) {
                    SearchText.metadataAnimeSubtitle
                } else {
                    SearchText.metadataMangaSubtitle
                },
                expanded = metadataExpanded,
                onToggle = { metadataExpanded = !metadataExpanded }
            ) {
                SmallSearchTextField(
                    label = SearchText.genreIdsLabel,
                    value = filters.genres,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(genres = value)) }
                )
                SmallSearchTextField(
                    label = SearchText.excludedGenreIdsLabel,
                    value = filters.genresExclude,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(genresExclude = value)) }
                )
                SmallSearchTextField(
                    label = if (filters.mediaType == SearchMediaType.Anime) {
                        SearchText.producerIdsLabel
                    } else {
                        SearchText.magazineIdsLabel
                    },
                    value = if (filters.mediaType == SearchMediaType.Anime) {
                        filters.producers
                    } else {
                        filters.magazines
                    },
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value ->
                        if (filters.mediaType == SearchMediaType.Anime) {
                            onFiltersChange(filters.copy(producers = value))
                        } else {
                            onFiltersChange(filters.copy(magazines = value))
                        }
                    }
                )
            }
        }

        item {
            FilterSection(
                title = SearchText.sortingSectionTitle,
                subtitle = SearchText.sortingSectionSubtitle,
                expanded = sortingExpanded,
                onToggle = { sortingExpanded = !sortingExpanded }
            ) {
                SearchDropdown(
                    label = SearchText.orderByLabel,
                    value = filters.orderBy,
                    options = if (filters.mediaType == SearchMediaType.Anime) ANIME_ORDER_BY else MANGA_ORDER_BY,
                    modifier = Modifier.fillMaxWidth(),
                    onSelected = { value -> onFiltersChange(filters.copy(orderBy = value)) }
                )
                SearchDropdown(
                    label = SearchText.sortLabel,
                    value = filters.sort,
                    options = SORT_OPTIONS,
                    modifier = Modifier.fillMaxWidth(),
                    onSelected = { value -> onFiltersChange(filters.copy(sort = value)) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(SearchText.resetButton)
                }
                OutlinedButton(
                    onClick = onApply,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(SearchText.applyButton)
                }
                Button(
                    onClick = onApplyAndSearch,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(SearchText.searchButton)
                }
            }
        }
    }
}

@Composable
private fun SearchFilterSheetHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = SearchText.advancedFiltersTitle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = SearchText.advancedFiltersSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun FilterSection(
    title: String,
    subtitle: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.64f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f)
                    )
                    content()
                }
            }
        }
    }
}

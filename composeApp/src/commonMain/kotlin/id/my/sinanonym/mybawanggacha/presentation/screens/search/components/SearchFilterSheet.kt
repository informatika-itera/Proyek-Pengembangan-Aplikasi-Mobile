package id.my.sinanonym.mybawanggacha.presentation.screens.search.components

import id.my.sinanonym.mybawanggacha.presentation.screens.search.ANIME_ORDER_BY
import id.my.sinanonym.mybawanggacha.presentation.screens.search.ANIME_RATINGS
import id.my.sinanonym.mybawanggacha.presentation.screens.search.ANIME_STATUSES
import id.my.sinanonym.mybawanggacha.presentation.screens.search.ANIME_TYPES
import id.my.sinanonym.mybawanggacha.presentation.screens.search.MANGA_ORDER_BY
import id.my.sinanonym.mybawanggacha.presentation.screens.search.MANGA_STATUSES
import id.my.sinanonym.mybawanggacha.presentation.screens.search.MANGA_TYPES
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SORT_OPTIONS
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchFilterMetadataUiState
import id.my.sinanonym.mybawanggacha.presentation.screens.search.SearchText
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import id.my.sinanonym.mybawanggacha.domain.search.model.MediaSearchFilters
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterOption
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchMediaType

@Composable
internal fun SearchFilterSheet(
    filters: MediaSearchFilters,
    filterMetadata: SearchFilterMetadataUiState,
    onFiltersChange: (MediaSearchFilters) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    onApplyAndSearch: () -> Unit
) {
    var generalExpanded by remember { mutableStateOf(false) }
    var classificationExpanded by remember { mutableStateOf(false) }
    var scoreExpanded by remember { mutableStateOf(false) }
    var metadataExpanded by remember { mutableStateOf(true) }
    var sortingExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item { FilterSheetHeader() }

        item {
            FilterSection(
                title = SearchText.generalSectionTitle,
                subtitle = SearchText.generalSectionSubtitle,
                expanded = generalExpanded,
                onToggle = { generalExpanded = !generalExpanded }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchTextField(
                        label = SearchText.limitLabel,
                        value = filters.limit,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(limit = value)) }
                    )
                    SearchTextField(
                        label = SearchText.letterLabel,
                        value = filters.letter,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(letter = value)) }
                    )
                }
                ToggleRow(
                    label = SearchText.allowNsfwLabel,
                    checked = !filters.sfw,
                    onCheckedChange = { value -> onFiltersChange(filters.copy(sfw = !value)) }
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Dropdown(
                        label = SearchText.typeLabel,
                        value = filters.type,
                        options = if (filters.mediaType == SearchMediaType.Anime) ANIME_TYPES else MANGA_TYPES,
                        modifier = Modifier.weight(1f),
                        onSelected = { value -> onFiltersChange(filters.copy(type = value)) }
                    )
                    Dropdown(
                        label = SearchText.statusLabel,
                        value = filters.status,
                        options = if (filters.mediaType == SearchMediaType.Anime) ANIME_STATUSES else MANGA_STATUSES,
                        modifier = Modifier.weight(1f),
                        onSelected = { value -> onFiltersChange(filters.copy(status = value)) }
                    )
                }
                if (filters.mediaType == SearchMediaType.Anime) {
                    Dropdown(
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
                SearchTextField(
                    label = SearchText.exactScoreLabel,
                    value = filters.score,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { value -> onFiltersChange(filters.copy(score = value)) }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchTextField(
                        label = SearchText.minScoreLabel,
                        value = filters.minScore,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(minScore = value)) }
                    )
                    SearchTextField(
                        label = SearchText.maxScoreLabel,
                        value = filters.maxScore,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(maxScore = value)) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SearchTextField(
                        label = SearchText.startDateLabel,
                        value = filters.startDate,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value -> onFiltersChange(filters.copy(startDate = value)) }
                    )
                    SearchTextField(
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
                MetadataStatusMessage(filterMetadata = filterMetadata)

                if (filterMetadata.genres.isNotEmpty()) {
                    MetadataTriStateSelector(
                        title = SearchText.genreSelectorTitle,
                        hint = SearchText.genreSelectorHint,
                        options = filterMetadata.genres,
                        includedValues = filters.genres,
                        excludedValues = filters.genresExclude,
                        onValuesChange = { included, excluded ->
                            onFiltersChange(
                                filters.copy(
                                    genres = included,
                                    genresExclude = excluded
                                )
                            )
                        }
                    )
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SearchTextField(
                            label = SearchText.genreIdsLabel,
                            value = filters.genres,
                            modifier = Modifier.weight(1f),
                            onValueChange = { value -> onFiltersChange(filters.copy(genres = value)) }
                        )
                        SearchTextField(
                            label = SearchText.excludedGenreIdsLabel,
                            value = filters.genresExclude,
                            modifier = Modifier.weight(1f),
                            onValueChange = { value -> onFiltersChange(filters.copy(genresExclude = value)) }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f))

                if (filterMetadata.related.isNotEmpty()) {
                    MetadataIncludeSelector(
                        title = if (filters.mediaType == SearchMediaType.Anime) {
                            SearchText.producerIdsLabel
                        } else {
                            SearchText.magazineIdsLabel
                        },
                        hint = if (filters.mediaType == SearchMediaType.Anime) {
                            SearchText.producerSelectorHint
                        } else {
                            SearchText.magazineSelectorHint
                        },
                        options = filterMetadata.related,
                        selectedValues = if (filters.mediaType == SearchMediaType.Anime) {
                            filters.producers
                        } else {
                            filters.magazines
                        },
                        onValuesChange = { values ->
                            if (filters.mediaType == SearchMediaType.Anime) {
                                onFiltersChange(filters.copy(producers = values))
                            } else {
                                onFiltersChange(filters.copy(magazines = values))
                            }
                        }
                    )
                } else {
                    SearchTextField(
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
        }

        item {
            FilterSection(
                title = SearchText.sortingSectionTitle,
                subtitle = SearchText.sortingSectionSubtitle,
                expanded = sortingExpanded,
                onToggle = { sortingExpanded = !sortingExpanded }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Dropdown(
                        label = SearchText.orderByLabel,
                        value = filters.orderBy,
                        options = if (filters.mediaType == SearchMediaType.Anime) ANIME_ORDER_BY else MANGA_ORDER_BY,
                        modifier = Modifier.weight(1f),
                        onSelected = { value -> onFiltersChange(filters.copy(orderBy = value)) }
                    )
                    Dropdown(
                        label = SearchText.sortLabel,
                        value = filters.sort,
                        options = SORT_OPTIONS,
                        modifier = Modifier.weight(1f),
                        onSelected = { value -> onFiltersChange(filters.copy(sort = value)) }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.weight(0.9f)
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
private fun FilterSheetHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = SearchText.advancedFiltersTitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = subtitle.takeIf { it.isNotBlank() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f))
    }
}

@Composable
private fun MetadataStatusMessage(filterMetadata: SearchFilterMetadataUiState) {
    when {
        filterMetadata.isLoading -> Text(
            text = SearchText.metadataLoading,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        filterMetadata.errorMessage != null -> Text(
            text = SearchText.metadataLoadFailed,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MetadataTriStateSelector(
    title: String,
    hint: String,
    options: List<SearchFilterOption>,
    includedValues: String,
    excludedValues: String,
    onValuesChange: (includedValues: String, excludedValues: String) -> Unit
) {
    val included = includedValues.toMetadataValueSet()
    val excluded = excludedValues.toMetadataValueSet()
    var query by remember(options) { mutableStateOf("") }
    var showAll by remember(options) { mutableStateOf(false) }
    val activeValues = (included + excluded).toSet()
    val visibleOptions = remember(options, included, excluded, query, showAll) {
        options.visibleMetadataOptions(
            query = query,
            activeValues = activeValues,
            showAll = showAll
        )
    }

    MetadataSelectorContainer(
        title = title,
        hint = hint,
        query = query,
        queryLabel = SearchText.genreSearchLabel,
        totalCount = options.size,
        visibleCount = visibleOptions.size,
        showAll = showAll,
        onQueryChange = { value ->
            query = value
            showAll = false
        },
        onToggleShowAll = { showAll = !showAll }
    ) {
        MetadataSelectionSummary(
            includedCount = included.size,
            excludedCount = excluded.size
        )

        if (visibleOptions.isEmpty()) {
            MetadataEmptyMessage()
        } else {
            visibleOptions.forEach { option ->
                val id = option.id.toString()
                val state = when (id) {
                    in included -> MetadataSelectionState.Included
                    in excluded -> MetadataSelectionState.Excluded
                    else -> MetadataSelectionState.Neutral
                }

                MetadataOptionRow(
                    option = option,
                    state = state,
                    onStateClick = {
                        val newIncluded = included.toMutableSet()
                        val newExcluded = excluded.toMutableSet()

                        when (state) {
                            MetadataSelectionState.Neutral -> {
                                newIncluded.add(id)
                                newExcluded.remove(id)
                            }

                            MetadataSelectionState.Included -> {
                                newIncluded.remove(id)
                                newExcluded.add(id)
                            }

                            MetadataSelectionState.Excluded -> {
                                newIncluded.remove(id)
                                newExcluded.remove(id)
                            }
                        }

                        onValuesChange(
                            newIncluded.toMetadataValueString(options),
                            newExcluded.toMetadataValueString(options)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MetadataIncludeSelector(
    title: String,
    hint: String,
    options: List<SearchFilterOption>,
    selectedValues: String,
    onValuesChange: (String) -> Unit
) {
    val selected = selectedValues.toMetadataValueSet()
    var query by remember(options) { mutableStateOf("") }
    var showAll by remember(options) { mutableStateOf(false) }
    val visibleOptions = remember(options, selected, query, showAll) {
        options.visibleMetadataOptions(
            query = query,
            activeValues = selected,
            showAll = showAll
        )
    }

    MetadataSelectorContainer(
        title = title,
        hint = hint,
        query = query,
        queryLabel = SearchText.metadataSearchLabel,
        totalCount = options.size,
        visibleCount = visibleOptions.size,
        showAll = showAll,
        onQueryChange = { value ->
            query = value
            showAll = false
        },
        onToggleShowAll = { showAll = !showAll }
    ) {
        if (selected.isNotEmpty()) {
            MetadataSelectionSummary(
                includedCount = selected.size,
                excludedCount = 0
            )
        }

        if (visibleOptions.isEmpty()) {
            MetadataEmptyMessage()
        } else {
            visibleOptions.forEach { option ->
                val id = option.id.toString()
                val state = if (id in selected) {
                    MetadataSelectionState.Included
                } else {
                    MetadataSelectionState.Neutral
                }

                MetadataOptionRow(
                    option = option,
                    state = state,
                    onStateClick = {
                        val newSelected = selected.toMutableSet()

                        if (!newSelected.add(id)) {
                            newSelected.remove(id)
                        }

                        onValuesChange(newSelected.toMetadataValueString(options))
                    }
                )
            }
        }
    }
}

@Composable
private fun MetadataSelectorContainer(
    title: String,
    hint: String,
    query: String,
    queryLabel: String,
    totalCount: Int,
    visibleCount: Int,
    showAll: Boolean,
    onQueryChange: (String) -> Unit,
    onToggleShowAll: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = SearchText.metadataCount(visibleCount = visibleCount, totalCount = totalCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        SearchTextField(
            label = queryLabel,
            value = query,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onQueryChange
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            content()
        }

        if (query.isBlank() && totalCount > METADATA_COLLAPSED_LIMIT) {
            TextButton(
                onClick = onToggleShowAll,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = if (showAll) {
                        SearchText.showLessMetadata
                    } else {
                        SearchText.showAllMetadata(totalCount)
                    }
                )
            }
        }
    }
}

@Composable
private fun MetadataSelectionSummary(
    includedCount: Int,
    excludedCount: Int
) {
    if (includedCount == 0 && excludedCount == 0) return

    Text(
        text = SearchText.metadataSelectionSummary(
            includedCount = includedCount,
            excludedCount = excludedCount
        ),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun MetadataEmptyMessage() {
    Text(
        text = SearchText.metadataNoMatch,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun MetadataOptionRow(
    option: SearchFilterOption,
    state: MetadataSelectionState,
    onStateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStateClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetadataSelectionMark(state = state)

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = option.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(
                text = "#${option.id}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MetadataSelectionMark(
    state: MetadataSelectionState
) {
    val tint = when (state) {
        MetadataSelectionState.Included -> MaterialTheme.colorScheme.primary
        MetadataSelectionState.Excluded -> MaterialTheme.colorScheme.error
        MetadataSelectionState.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.48f)
    }

    Box(
        modifier = Modifier.size(22.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            MetadataSelectionState.Included -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = SearchText.includeChip,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )

            MetadataSelectionState.Excluded -> Icon(
                imageVector = Icons.Default.Close,
                contentDescription = SearchText.excludeChip,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )

            MetadataSelectionState.Neutral -> Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                color = tint,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private const val METADATA_COLLAPSED_LIMIT = 18

private fun List<SearchFilterOption>.visibleMetadataOptions(
    query: String,
    activeValues: Set<String>,
    showAll: Boolean
): List<SearchFilterOption> {
    val normalizedQuery = query.trim()
    val filtered = if (normalizedQuery.isBlank()) {
        this
    } else {
        filter { option ->
            option.name.contains(normalizedQuery, ignoreCase = true) ||
                option.id.toString().contains(normalizedQuery)
        }
    }
    val activeOptions = filter { option -> option.id.toString() in activeValues }
    val limitedOptions = if (normalizedQuery.isBlank() && !showAll) {
        filtered.take(METADATA_COLLAPSED_LIMIT)
    } else {
        filtered
    }

    return (activeOptions + limitedOptions).distinctBy { option -> option.id }
}

private enum class MetadataSelectionState {
    Neutral,
    Included,
    Excluded
}

private fun String.toMetadataValueSet(): Set<String> {
    return split(",")
        .map { value -> value.trim() }
        .filter { value -> value.isNotBlank() }
        .toSet()
}

private fun Set<String>.toMetadataValueString(options: List<SearchFilterOption>): String {
    val orderedKnownValues = options
        .map { option -> option.id.toString() }
        .filter { value -> value in this }
    val unknownValues = filterNot { value -> value in orderedKnownValues }
        .sortedBy { value -> value.toIntOrNull() ?: Int.MAX_VALUE }

    return (orderedKnownValues + unknownValues).joinToString(",")
}

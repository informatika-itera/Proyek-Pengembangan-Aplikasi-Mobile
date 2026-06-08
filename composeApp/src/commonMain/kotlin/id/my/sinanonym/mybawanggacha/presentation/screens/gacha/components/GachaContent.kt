package id.my.sinanonym.mybawanggacha.presentation.screens.gacha.components

import id.my.sinanonym.mybawanggacha.presentation.screens.gacha.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaHistoryEntry
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaMediaFormat
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaMediaPool
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaPreference
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaResultItem
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaResultMediaType
import id.my.sinanonym.mybawanggacha.domain.gacha.model.GachaStatusFilter
import id.my.sinanonym.mybawanggacha.domain.search.model.SearchFilterOption
import id.my.sinanonym.mybawanggacha.presentation.components.EmptyState
import id.my.sinanonym.mybawanggacha.presentation.components.MBGMainRailKey
import id.my.sinanonym.mybawanggacha.presentation.components.MBGRailBackButton
import id.my.sinanonym.mybawanggacha.presentation.components.MBGSideRailScaffold
import id.my.sinanonym.mybawanggacha.presentation.components.ScreenHeader
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun Content(
    uiState: GachaUiState,
    onPreferenceChange: ((GachaPreference) -> GachaPreference) -> Unit,
    onRunGacha: () -> Unit,
    onReroll: () -> Unit,
    onAddToLibrary: () -> Unit,
    onClearHistory: () -> Unit,
    onOpenDetail: (GachaResultItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 4.dp, top = 26.dp, end = 18.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(contentType = "gacha_header") {
            Header()
        }

        item(contentType = "gacha_preferences") {
            PreferencePanel(
                preference = uiState.preference,
                availableGenres = uiState.availableGenres,
                isGenreLoading = uiState.isGenreLoading,
                genreErrorMessage = uiState.genreErrorMessage,
                isLoading = uiState.isLoading,
                onPreferenceChange = onPreferenceChange,
                onRunGacha = onRunGacha
            )
        }

        uiState.errorMessage?.let { message ->
            item(contentType = "gacha_error") {
                InlineMessage(
                    text = message,
                    isError = true
                )
            }
        }

        uiState.infoMessage?.let { message ->
            item(contentType = "gacha_info") {
                InlineMessage(
                    text = message,
                    isError = false
                )
            }
        }

        item(contentType = "gacha_result") {
            val result = uiState.result
            if (result == null) {
                EmptyState(
                    title = "Belum ada hasil",
                    message = "Atur filter seperlunya, lalu tekan Gacha.",
                    modifier = Modifier.height(190.dp)
                )
            } else {
                ResultScreen(
                    item = result,
                    isLoading = uiState.isLoading,
                    onReroll = onReroll,
                    onAddToLibrary = onAddToLibrary,
                    onOpenDetail = { onOpenDetail(result) }
                )
            }
        }

        if (uiState.history.isNotEmpty()) {
            item(contentType = "gacha_history_header") {
                HistoryHeader(
                    count = uiState.history.size,
                    onClearHistory = onClearHistory
                )
            }

            items(
                items = uiState.history.take(8),
                key = { history -> "${history.item.mediaType}:${history.item.malId}:${history.pickedAtEpochMillis}" },
                contentType = { "gacha_history_item" }
            ) { history ->
                HistoryRow(
                    history = history,
                    onClick = { onOpenDetail(history.item) }
                )
            }
        }
    }
}

@Composable
private fun Header() {
    ScreenHeader(
        icon = Icons.Default.Star,
        title = "Gacha",
        subtitle = "Random pick dari search Jikan."
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferencePanel(
    preference: GachaPreference,
    availableGenres: List<SearchFilterOption>,
    isGenreLoading: Boolean,
    genreErrorMessage: String?,
    isLoading: Boolean,
    onPreferenceChange: ((GachaPreference) -> GachaPreference) -> Unit,
    onRunGacha: () -> Unit
) {
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val activeFilterLabels = remember(preference, availableGenres) {
        preference.activeFilterLabels(availableGenres)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MediaPoolRow(
            selected = preference.mediaPool,
            onSelected = { mediaPool ->
                onPreferenceChange { current -> current.copy(mediaPool = mediaPool) }
            }
        )

        ActiveFilterRow(
            labels = activeFilterLabels,
            onOpenFilters = { showFilters = true }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { showFilters = true },
                modifier = Modifier.weight(0.42f),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (activeFilterLabels.isEmpty()) "Filters" else "Filters ${activeFilterLabels.size}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onRunGacha,
                enabled = !isLoading,
                modifier = Modifier.weight(0.58f),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isLoading) "Loading" else "Gacha",
                    maxLines = 1
                )
            }
        }
    }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            FilterSheetContent(
                preference = preference,
                availableGenres = availableGenres,
                isGenreLoading = isGenreLoading,
                genreErrorMessage = genreErrorMessage,
                onPreferenceChange = onPreferenceChange,
                onClose = { showFilters = false }
            )
        }
    }
}

@Composable
private fun ActiveFilterRow(
    labels: List<String>,
    onOpenFilters: () -> Unit
) {
    if (labels.isEmpty()) {
        Text(
            text = "Default filter",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 8.dp)
    ) {
        items(
            items = labels,
            key = { label -> label }
        ) { label ->
            FilterChip(
                selected = true,
                onClick = onOpenFilters,
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

@Composable
private fun FilterSheetContent(
    preference: GachaPreference,
    availableGenres: List<SearchFilterOption>,
    isGenreLoading: Boolean,
    genreErrorMessage: String?,
    onPreferenceChange: ((GachaPreference) -> GachaPreference) -> Unit,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(contentType = "sheet_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Gacha filters",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        onPreferenceChange { current ->
                            current.copy(
                                selectedGenreIds = emptyList(),
                                excludedGenreIds = emptyList(),
                                minScore = "",
                                status = GachaStatusFilter.Any,
                                format = GachaMediaFormat.Any,
                                includeKnownItems = false,
                                allowNsfw = false
                            )
                        }
                    }
                ) {
                    Text("Reset")
                }
            }
        }

        item(contentType = "sheet_genres") {
            GenreSelector(
                availableGenres = availableGenres,
                selectedGenreIds = preference.selectedGenreIds,
                excludedGenreIds = preference.excludedGenreIds,
                allowNsfw = preference.allowNsfw,
                isLoading = isGenreLoading,
                errorMessage = genreErrorMessage,
                onCycleGenre = { genreId ->
                    onPreferenceChange { current -> current.cycleGenre(genreId) }
                },
                onClearGenres = {
                    onPreferenceChange { current ->
                        current.copy(
                            selectedGenreIds = emptyList(),
                            excludedGenreIds = emptyList()
                        )
                    }
                }
            )
        }

        item(contentType = "sheet_core_filters") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterTextField(
                    label = "Min score",
                    value = preference.minScore,
                    modifier = Modifier.weight(1f),
                    onValueChange = { value ->
                        onPreferenceChange { current -> current.copy(minScore = value) }
                    }
                )
                GachaDropdown(
                    label = "Type",
                    value = preference.format.label,
                    options = GachaMediaFormat.availableFor(preference.mediaPool),
                    optionLabel = { it.label },
                    modifier = Modifier.weight(1f),
                    onSelected = { format ->
                        onPreferenceChange { current -> current.copy(format = format) }
                    }
                )
            }
        }

        item(contentType = "sheet_status") {
            GachaDropdown(
                label = "Status",
                value = preference.status.label,
                options = GachaStatusFilter.entries.toList(),
                optionLabel = { it.label },
                onSelected = { status ->
                    onPreferenceChange { current -> current.copy(status = status) }
                }
            )
        }

        item(contentType = "sheet_flags") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SwitchRow(
                    title = "Include library",
                    subtitle = "Izinkan item yang sudah ada di My Library",
                    checked = preference.includeKnownItems,
                    onCheckedChange = { checked ->
                        onPreferenceChange { current -> current.copy(includeKnownItems = checked) }
                    }
                )
                SwitchRow(
                    title = "Allow NSFW",
                    subtitle = "Matikan safe filter Jikan untuk genre explicit",
                    checked = preference.allowNsfw,
                    onCheckedChange = { checked ->
                        onPreferenceChange { current -> current.copy(allowNsfw = checked) }
                    }
                )
            }
        }

        item(contentType = "sheet_done") {
            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreSelector(
    availableGenres: List<SearchFilterOption>,
    selectedGenreIds: List<Int>,
    excludedGenreIds: List<Int>,
    allowNsfw: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onCycleGenre: (Int) -> Unit,
    onClearGenres: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var showAll by rememberSaveable { mutableStateOf(false) }
    val activeGenreIds = remember(selectedGenreIds, excludedGenreIds) {
        (selectedGenreIds + excludedGenreIds).toSet()
    }

    val selectedGenres = remember(availableGenres, selectedGenreIds) {
        availableGenres
            .filter { option -> option.id in selectedGenreIds }
            .sortedBy { option -> option.name.lowercase() }
    }
    val excludedGenres = remember(availableGenres, excludedGenreIds) {
        availableGenres
            .filter { option -> option.id in excludedGenreIds }
            .sortedBy { option -> option.name.lowercase() }
    }
    val filteredGenres = remember(availableGenres, activeGenreIds, query) {
        availableGenres
            .filterNot { option -> option.id in activeGenreIds }
            .filter { option ->
                query.isBlank() || option.name.contains(query, ignoreCase = true)
            }
            .sortedBy { option -> option.name.lowercase() }
    }
    val visibleGenres = if (showAll) {
        filteredGenres
    } else {
        filteredGenres.take(18)
    }
    val hasExplicitGenreIncluded = selectedGenres.any { genre ->
        genre.name.lowercase() in EXPLICIT_GENRE_NAMES
    }
    val activeCount = selectedGenreIds.size + excludedGenreIds.size

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Genre",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = when {
                        activeCount == 0 -> "Tap chip: include → exclude → off"
                        excludedGenreIds.isEmpty() -> "${selectedGenreIds.size} include"
                        selectedGenreIds.isEmpty() -> "${excludedGenreIds.size} exclude"
                        else -> "${selectedGenreIds.size} include • ${excludedGenreIds.size} exclude"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (activeCount > 0) {
                TextButton(onClick = onClearGenres) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear")
                }
            }
        }

        if (selectedGenres.isNotEmpty() || excludedGenres.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = selectedGenres,
                    key = { genre -> "include:${genre.id}" }
                ) { genre ->
                    FilterChip(
                        selected = true,
                        onClick = { onCycleGenre(genre.id) },
                        label = {
                            Text(
                                text = "+ ${genre.name}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }

                items(
                    items = excludedGenres,
                    key = { genre -> "exclude:${genre.id}" }
                ) { genre ->
                    FilterChip(
                        selected = true,
                        onClick = { onCycleGenre(genre.id) },
                        label = {
                            Text(
                                text = "− ${genre.name}",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Cari genre") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
        )

        when {
            isLoading -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Memuat genre...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (hasExplicitGenreIncluded && !allowNsfw) {
            Text(
                text = "Genre explicit butuh Allow NSFW agar tidak kosong.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        if (visibleGenres.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                visibleGenres.forEach { genre ->
                    FilterChip(
                        selected = false,
                        onClick = { onCycleGenre(genre.id) },
                        label = {
                            Text(
                                text = genre.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        } else if (!isLoading && errorMessage == null) {
            Text(
                text = "Genre tidak ditemukan.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (filteredGenres.size > 18) {
            TextButton(onClick = { showAll = !showAll }) {
                Text(if (showAll) "Tampilkan lebih sedikit" else "Tampilkan semua genre")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MediaPoolRow(
    selected: GachaMediaPool,
    onSelected: (GachaMediaPool) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GachaMediaPool.entries.forEach { pool ->
            FilterChip(
                selected = pool == selected,
                onClick = { onSelected(pool) },
                label = { Text(pool.label) }
            )
        }
    }
}

@Composable
private fun <T> GachaDropdown(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = true,
        label = { Text(label) }
    )
}

@Composable
private fun InlineMessage(
    text: String,
    isError: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isError) {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
                } else {
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                },
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun ResultScreen(
    item: GachaResultItem,
    isLoading: Boolean,
    onReroll: () -> Unit,
    onAddToLibrary: () -> Unit,
    onOpenDetail: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(width = 96.dp, height = 136.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = true,
                        onClick = {},
                        label = { Text(item.mediaType.label) }
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.metadataLine(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReroll,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reroll")
                }
                OutlinedButton(
                    onClick = onOpenDetail,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Detail")
                }
            }

            Button(
                onClick = onAddToLibrary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah ke My Library")
            }
        }
    }
}

@Composable
private fun HistoryHeader(
    count: Int,
    onClearHistory: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$count hasil terakhir",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(onClick = onClearHistory) {
            Text("Clear")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryRow(
    history: GachaHistoryEntry,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = history.item.imageUrl,
                contentDescription = history.item.title,
                modifier = Modifier
                    .size(width = 48.dp, height = 64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = history.item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = history.item.metadataLine(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
    }
}

private fun GachaResultItem.metadataLine(): String {
    return buildList {
        type?.takeIf { it.isNotBlank() }?.let { add(it) }
        status?.takeIf { it.isNotBlank() }?.let { add(it) }
        score?.let { add("★ ${it.toString().take(4)}") }
        episodes?.let { add("$it eps") }
        chapters?.let { add("$it ch") }
        volumes?.let { add("$it vol") }
    }.joinToString(" • ").ifBlank { "No metadata" }
}

private fun GachaPreference.activeFilterLabels(
    availableGenres: List<SearchFilterOption>
): List<String> {
    val genresById = availableGenres.associateBy { option -> option.id }

    return buildList {
        if (mediaPool != GachaMediaPool.Both) {
            add(mediaPool.label)
        }
        if (selectedGenreIds.isNotEmpty()) {
            add("Genre: ${selectedGenreIds.toGenreSummary(genresById)}")
        }
        if (excludedGenreIds.isNotEmpty()) {
            add("Exclude: ${excludedGenreIds.toGenreSummary(genresById)}")
        }
        minScore.trim().takeIf { it.isNotBlank() }?.let { score ->
            add("Score ≥ $score")
        }
        if (status != GachaStatusFilter.Any) {
            add(status.label)
        }
        if (format != GachaMediaFormat.Any) {
            add(format.label)
        }
        if (includeKnownItems) {
            add("Include library")
        }
        if (allowNsfw) {
            add("NSFW")
        }
    }
}

private fun List<Int>.toGenreSummary(
    genresById: Map<Int, SearchFilterOption>
): String {
    val names = mapNotNull { genreId -> genresById[genreId]?.name }
        .take(2)

    return if (names.isEmpty()) {
        "$size genre"
    } else {
        buildString {
            append(names.joinToString(", "))
            val remaining = size - names.size
            if (remaining > 0) append(" +$remaining")
        }
    }
}

private fun GachaPreference.cycleGenre(genreId: Int): GachaPreference {
    return when {
        genreId in selectedGenreIds -> copy(
            selectedGenreIds = selectedGenreIds.filterNot { item -> item == genreId },
            excludedGenreIds = excludedGenreIds + genreId
        )
        genreId in excludedGenreIds -> copy(
            excludedGenreIds = excludedGenreIds.filterNot { item -> item == genreId }
        )
        else -> copy(
            selectedGenreIds = selectedGenreIds + genreId
        )
    }
}

private val EXPLICIT_GENRE_NAMES = setOf(
    "ecchi",
    "erotica",
    "hentai"
)

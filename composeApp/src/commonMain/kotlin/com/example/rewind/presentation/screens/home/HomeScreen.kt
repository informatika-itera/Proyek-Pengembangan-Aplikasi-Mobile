package com.example.rewind.presentation.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onMovieClick: (Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val tmdbState by viewModel.tmdbState.collectAsState()
    val trendingState by viewModel.trendingState.collectAsState()
    val addMessage by viewModel.addMessage.collectAsState()
    var selectedFilter by remember { mutableStateOf<WatchStatus?>(null) }
    var isSearchFocused by remember { mutableStateOf(false) }
    var selectedTmdbItem by remember { mutableStateOf<TmdbMovieDto?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val showSearchContent = isSearchFocused || searchQuery.isNotEmpty()

    LaunchedEffect(addMessage) {
        addMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAddMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(
            modifier = Modifier.size(300.dp).offset(x = (-60).dp, y = (-40).dp).blur(100.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f), Color.Transparent)
                    ), shape = CircleShape
                )
        )
        Box(
            modifier = Modifier.size(200.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = 20.dp).blur(80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), Color.Transparent)
                    ), shape = CircleShape
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader()
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                onFocusChange = { isSearchFocused = it }
            )

            AnimatedContent(
                targetState = showSearchContent,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                label = "SearchContentAnimation"
            ) { isSearching ->
                if (isSearching) {
                    if (searchQuery.isEmpty()) {
                        TrendingSection(
                            state = trendingState,
                            onItemClick = { selectedTmdbItem = it }
                        )
                    } else {
                        SearchCombinedResults(
                            uiState = uiState,
                            tmdbState = tmdbState,
                            onMovieClick = onMovieClick,
                            onTmdbClick = { selectedTmdbItem = it }
                        )
                    }
                } else {
                    Column {
                        FilterRow(selected = selectedFilter, onSelect = { selectedFilter = it })

                        AnimatedContent(
                            targetState = uiState,
                            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                            label = "UiStateAnimation"
                        ) { state ->
                            when (state) {
                                is HomeUiState.Loading -> LoadingState()
                                is HomeUiState.Empty -> EmptyState()
                                is HomeUiState.NoResults -> NoResultsState(query = state.query)
                                is HomeUiState.Success -> {
                                    val displayed = if (selectedFilter != null) {
                                        state.movies.filter { it.status == selectedFilter }
                                    } else state.movies

                                    if (displayed.isEmpty()) EmptyFilterState()
                                    else MovieList(movies = displayed, onMovieClick = onMovieClick)
                                }
                                is HomeUiState.Error -> ErrorState(message = state.message)
                            }
                        }
                    }
                }
            }
        }

        if (!showSearchContent) {
            Box(modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)) {
                Box(
                    modifier = Modifier.size(70.dp).blur(20.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), Color.Transparent)
                            ), shape = CircleShape
                        )
                )
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(12.dp)
                ) {
                    Text("+", fontSize = 28.sp, fontWeight = FontWeight.Light)
                }
            }
        }

        selectedTmdbItem?.let { item ->
            TmdbDetailDialog(
                item = item,
                onDismiss = { selectedTmdbItem = null },
                onConfirmAdd = { status ->
                    viewModel.addTmdbToCollection(item, status)
                    selectedTmdbItem = null
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }
}

@Composable
private fun TrendingSection(
    state: TmdbSearchState,
    onItemClick: (TmdbMovieDto) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔥", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Lagi Trending Minggu Ini",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        when (state) {
            is TmdbSearchState.Loading -> LoadingState()
            is TmdbSearchState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(state.results, key = { it.id }) { item ->
                        TmdbResultCard(
                            item = item,
                            onClick = { onItemClick(item) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
            is TmdbSearchState.Error -> ErrorState(state.message)
            else -> Unit
        }
    }
}

@Composable
private fun SearchCombinedResults(
    uiState: HomeUiState,
    tmdbState: TmdbSearchState,
    onMovieClick: (Long) -> Unit,
    onTmdbClick: (TmdbMovieDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📌", fontSize = 14.sp)
                Text(
                    text = "Di Koleksi Kamu",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        when (uiState) {
            is HomeUiState.Success -> {
                items(uiState.movies, key = { "local_${it.id}" }) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
            is HomeUiState.NoResults -> {
                item {
                    Text(
                        "Tidak ada di koleksi lokal",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                }
            }
            is HomeUiState.Loading -> {
                item {
                    Box(Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
            }
            else -> {}
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🌐", fontSize = 14.sp)
                Text(
                    text = "Hasil dari TMDB",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        when (tmdbState) {
            is TmdbSearchState.Loading -> {
                item {
                    Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 1.5.dp)
                    }
                }
            }
            is TmdbSearchState.Success -> {
                items(tmdbState.results.take(10), key = { "tmdb_${it.id}" }) { item ->
                    TmdbResultCard(
                        item = item,
                        onClick = { onTmdbClick(item) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
            is TmdbSearchState.Empty -> {
                item {
                    Text(
                        "Tidak ditemukan di TMDB",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                }
            }
            is TmdbSearchState.Error -> {
                item {
                    Text(
                        "Gagal memuat hasil TMDB",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            else -> {}
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
private fun TmdbResultCard(
    item: TmdbMovieDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "TmdbCardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.posterUrl("w200"),
                contentDescription = item.displayTitle,
                modifier = Modifier.width(70.dp).fillMaxHeight().clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp).weight(1f)) {
                Text(
                    text = item.displayTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (item.isTvSeries) "Series" else "Film",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (item.voteAverage > 0) {
                        val roundedRating = (item.voteAverage * 10).roundToInt() / 10.0
                        Text(
                            text = "★ $roundedRating",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAmber
                        )
                    }
                    item.displayDate?.take(4)?.let { year ->
                        Text(
                            text = year,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = item.overview ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TmdbDetailDialog(
    item: TmdbMovieDto,
    onDismiss: () -> Unit,
    onConfirmAdd: (WatchStatus) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(WatchStatus.PLAN_TO_WATCH) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.9f).clip(RoundedCornerShape(24.dp)),
        content = {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        AsyncImage(
                            model = item.backdropUrl() ?: item.posterUrl(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
                        Text(
                            item.displayTitle,
                            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            item.overview ?: "Tidak ada deskripsi tersedia.",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Tambahkan ke Koleksi Sebagai:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        val statuses = listOf(
                            WatchStatus.WATCHING to "Sedang Ditonton",
                            WatchStatus.PLAN_TO_WATCH to "Direncanakan",
                            WatchStatus.COMPLETED to "Sudah Selesai",
                            WatchStatus.ON_HOLD to "Ditunda",
                            WatchStatus.DROPPED to "Berhenti"
                        )

                        statuses.forEach { (status, label) ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedStatus = status }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedStatus == status, onClick = { selectedStatus = status })
                                Text(label, style = MaterialTheme.typography.bodyLarge)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = onDismiss) { Text("Batal") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onConfirmAdd(selectedStatus) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Tambah")
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to MaterialTheme.colorScheme.surface,
                        0.6f to MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        1f to MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 22.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(1.dp).align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BorderGold.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            BorderGold.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("REWIND", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 5.sp)
            Text("My Collection", color = MaterialTheme.colorScheme.onBackground, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search collection or find on TMDB...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) },
        leadingIcon = { Text("🔍", fontSize = 15.sp, modifier = Modifier.padding(start = 4.dp)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable {
                    onQueryChange("")
                    focusManager.clearFocus()
                }.padding(4.dp)) {
                    Text("✕", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp).heightIn(min = 48.dp)
            .onFocusChanged { onFocusChange(it.isFocused) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        ),
        textStyle = TextStyle(fontSize = 13.sp)
    )
}

@Composable
private fun FilterRow(selected: WatchStatus?, onSelect: (WatchStatus?) -> Unit) {
    val filters = listOf(null to "All", WatchStatus.WATCHING to "Watching", WatchStatus.COMPLETED to "Completed", WatchStatus.PLAN_TO_WATCH to "Planned", WatchStatus.ON_HOLD to "On Hold", WatchStatus.DROPPED to "Dropped")
    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { (status, label) ->
            val isSelected = selected == status
            val scale by animateFloatAsState(targetValue = if (isSelected) 1.05f else 1f, label = "FilterScale")

            Crossfade(
                targetState = isSelected,
                modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                label = "FilterCrossfade"
            ) { selectedState ->
                if (selectedState) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber))).clickable { onSelect(status) }.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(text = label, color = MaterialTheme.colorScheme.background, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                    }
                } else {
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(MaterialTheme.colorScheme.surface).border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(20.dp)).clickable { onSelect(status) }.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.3.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieList(movies: List<Movie>, onMovieClick: (Long) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(movies, key = { "movie_${it.id}" }) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie.id) },
                modifier = Modifier.animateItem()
            )
        }
        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusLabel) = when (movie.status) {
        WatchStatus.COMPLETED -> StatusFinished to "Completed"
        WatchStatus.WATCHING -> StatusWatching to "Watching"
        WatchStatus.PLAN_TO_WATCH -> StatusWantToWatch to "Planned"
        WatchStatus.ON_HOLD -> StatusOnHold to "On Hold"
        WatchStatus.DROPPED -> StatusDropped to "Dropped"
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "LocalCardScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(colorStops = arrayOf(0f to MaterialTheme.colorScheme.surfaceVariant, 1f to MaterialTheme.colorScheme.surface)))
            .border(BorderStroke(1.dp, Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.8f), MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), Color.Transparent))), RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
    ) {
        Box(modifier = Modifier.size(80.dp).offset(x = (-10).dp, y = (-10).dp).blur(30.dp).background(Brush.radialGradient(colors = listOf(statusColor.copy(alpha = 0.15f), Color.Transparent)), shape = CircleShape))
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                if (movie.posterUrl != null) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(13.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(13.dp))
                            .background(Brush.linearGradient(colorStops = arrayOf(0f to MaterialTheme.colorScheme.tertiary, 1f to MaterialTheme.colorScheme.secondary)))
                            .border(BorderStroke(1.dp, Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), Color.Transparent))), RoundedCornerShape(13.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = movie.title.take(1).uppercase(), color = MaterialTheme.colorScheme.onBackground, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = movie.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, letterSpacing = 0.1.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Box(modifier = Modifier.background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(5.dp)).border(BorderStroke(0.5.dp, statusColor.copy(alpha = 0.35f)), RoundedCornerShape(5.dp)).padding(horizontal = 7.dp, vertical = 3.dp)) {
                        Text(text = statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp)
                    }
                    if (movie.rating != null && movie.rating > 0f) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("★", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                            Text(text = movie.rating.toString(), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Text(text = "${movie.type.displayName}  ·  ${movie.genre.displayName}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, letterSpacing = 0.2.sp)
                if (movie.status == WatchStatus.WATCHING && movie.totalEpisodes != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LinearProgressIndicator(progress = { movie.progressPercent / 100f }, modifier = Modifier.weight(1f).height(3.dp).clip(RoundedCornerShape(2.dp)), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f))
                        Text(text = "${movie.watchedEpisodes}/${movie.totalEpisodes}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("›", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 20.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 1.5.dp, modifier = Modifier.size(32.dp))
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(horizontal = 40.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(100.dp).blur(30.dp).background(Brush.radialGradient(colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), Color.Transparent)), shape = CircleShape))
                Text("🎞️", fontSize = 52.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Your collection is empty", color = MaterialTheme.colorScheme.onBackground, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.1.sp)
            Text(text = "Tap + to add your first title", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}

@Composable
private fun EmptyFilterState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🔍", fontSize = 36.sp)
            Text(text = "No titles in this category", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}

@Composable
private fun NoResultsState(query: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🎬", fontSize = 40.sp)
            Text(text = "No results for \"$query\"", color = MaterialTheme.colorScheme.onBackground, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Try a different title or genre", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
    }
}
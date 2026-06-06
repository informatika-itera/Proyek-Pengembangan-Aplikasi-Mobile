package com.example.rewind.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import com.example.rewind.data.remote.dto.TmdbMovieDto
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.ui.text.TextStyle

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Ambient background glow ──────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(GoldAmber.copy(alpha = 0.07f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.12f),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.15f, size.height * 0.12f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(TheaterRed.copy(alpha = 0.05f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.35f),
                    radius = size.width * 0.4f
                ),
                radius = size.width * 0.4f,
                center = Offset(size.width * 0.85f, size.height * 0.35f)
            )
        }

        // ── Main Content ─────────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = showSearchContent,
                transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
                label = "MainContentSwitch"
            ) { isSearching ->
                if (isSearching) {
                    SearchOverlay(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onFocusChange = { isSearchFocused = it },
                        uiState = uiState,
                        tmdbState = tmdbState,
                        trendingState = trendingState,
                        onMovieClick = onMovieClick,
                        onTmdbClick = { selectedTmdbItem = it }
                    )
                } else {
                    MainFeed(
                        uiState = uiState,
                        trendingState = trendingState,
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onFocusChange = { isSearchFocused = it },
                        selectedFilter = selectedFilter,
                        onFilterSelect = { selectedFilter = it },
                        onMovieClick = onMovieClick,
                        onTmdbClick = { selectedTmdbItem = it },
                        onAddClick = onAddClick
                    )
                }
            }
        }

        // ── TMDB Detail Dialog ───────────────────────────────────────────────
        selectedTmdbItem?.let { item ->
            TmdbDetailDialog(
                item = item,
                onDismiss = { selectedTmdbItem = null },
                onConfirmAdd = { status, rating, review ->
                    viewModel.addTmdbToCollection(item, status, rating, review)
                    selectedTmdbItem = null
                }
            )
        }

        // ── Snackbar ─────────────────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 110.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MAIN FEED
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MainFeed(
    uiState: HomeUiState,
    trendingState: TmdbSearchState,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    selectedFilter: WatchStatus?,
    onFilterSelect: (WatchStatus?) -> Unit,
    onMovieClick: (Long) -> Unit,
    onTmdbClick: (TmdbMovieDto) -> Unit,
    onAddClick: () -> Unit // Tambahkan parameter ini
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // ── Greeting Header ──────────────────────────────────────────────────
            item { GreetingHeader() }

            // ── Featured Hero Carousel ───────────────────────────────────────────
            if (trendingState is TmdbSearchState.Success && trendingState.results.isNotEmpty()) {
                item {
                    HeroCarousel(
                        items = trendingState.results.take(5),
                        onItemClick = onTmdbClick
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            // ── Search Bar ───────────────────────────────────────────────────────
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onFocusChange = onFocusChange
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── Trending Now Section ─────────────────────────────────────────────
            if (trendingState is TmdbSearchState.Success && trendingState.results.isNotEmpty()) {
                item {
                    TrendingHorizontalSection(
                        items = trendingState.results,
                        onItemClick = onTmdbClick
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            // ── My Collection ────────────────────────────────────────────────────
            item {
                SectionHeader(
                    emoji = "🎞️",
                    title = "Koleksi Saya"
                )
                Spacer(modifier = Modifier.height(12.dp))
                FilterRow(selected = selectedFilter, onSelect = onFilterSelect)
                Spacer(modifier = Modifier.height(12.dp))
            }

            when (uiState) {
                is HomeUiState.Loading -> item { LoadingState() }
                is HomeUiState.Empty -> item { EmptyState() }
                is HomeUiState.NoResults -> item { NoResultsState(query = uiState.query) }
                is HomeUiState.Error -> item { ErrorState(message = uiState.message) }
                is HomeUiState.Success -> {
                    val displayed = if (selectedFilter != null)
                        uiState.movies.filter { it.status == selectedFilter }
                    else uiState.movies

                    if (displayed.isEmpty()) {
                        item { EmptyFilterState() }
                    } else {
                        items(displayed, key = { "movie_${it.id}" }) { movie ->
                            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                                MovieCard(
                                    movie = movie,
                                    onClick = { onMovieClick(movie.id) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // ── FAB (Sekarang ada di dalam Box MainFeed, menempel di pojok kanan bawah)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
        ) {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = GoldAmber,
                contentColor = BackgroundDark,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Text("+", fontSize = 26.sp, fontWeight = FontWeight.Light)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GREETING HEADER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun GreetingHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 52.dp, bottom = 20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Sub label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(GoldAmber, CircleShape)
                )
                Text(
                    text = "REWIND",
                    color = GoldAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 5.sp
                )
            }
            // Main greeting
            Text(
                text = "Hallo🍿",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.8).sp
            )
            Text(
                text = "Siap untuk rewind hari ini?",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.1.sp
            )
        }

        // Decorative pill top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(35.dp))
                .background(GoldAmber.copy(alpha = 0.12f))
                .border(
                    BorderStroke(1.dp, GoldAmber.copy(alpha = 0.3f)),
                    RoundedCornerShape(35.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✦ Hari ini",
                color = GoldAmber,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HERO CAROUSEL
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroCarousel(
    items: List<TmdbMovieDto>,
    onItemClick: (TmdbMovieDto) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // Auto-advance setiap 4 detik
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentIndex = (currentIndex + 1) % items.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(300.dp)
            .clip(RoundedCornerShape(35.dp))
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < -30) currentIndex = (currentIndex + 1) % items.size
                    else if (dragAmount > 30) currentIndex = (currentIndex - 1 + items.size) % items.size
                }
            }
    ) {
        // Background poster
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = { fadeIn(tween(600)) togetherWith fadeOut(tween(600)) },
            label = "HeroPosterSwitch"
        ) { idx ->
            val item = items[idx]
            AsyncImage(
                model = item.backdropUrl() ?: item.posterUrl("w500"),
                contentDescription = item.displayTitle,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Black.copy(alpha = 0.1f),
                            0.45f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.88f)
                        )
                    )
                )
        )

        // Side glow accent
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            GoldAmber.copy(alpha = 0.08f),
                            Color.Transparent,
                            TheaterRed.copy(alpha = 0.06f)
                        )
                    )
                )
        )

        // Bottom content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(22.dp)
        ) {
            // Type badge
            val currentItem = items[currentIndex]
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(35.dp))
                    .background(GoldAmber.copy(alpha = 0.2f))
                    .border(BorderStroke(1.dp, GoldAmber.copy(alpha = 0.5f)), RoundedCornerShape(35.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (currentItem.isTvSeries) "● SERIES" else "● FILM",
                    color = GoldAmber,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            // Title
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = {
                    (fadeIn(tween(400)) + slideInVertically { it / 4 }) togetherWith
                            (fadeOut(tween(200)) + slideOutVertically { -it / 4 })
                },
                label = "HeroTitleSwitch"
            ) { idx ->
                Text(
                    text = items[idx].displayTitle,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating
                if (currentItem.voteAverage > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("★", color = GoldAmber, fontSize = 12.sp)
                        Text(
                            text = ((currentItem.voteAverage * 10).roundToInt() / 10.0).toString(),
                            color = TextWarm,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Add to collection button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(35.dp))
                        .background(
                            Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber))
                        )
                        .clickable { onItemClick(currentItem) }
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "＋ Tambah ke Koleksi",
                        color = BackgroundDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }

        // Dot indicators
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items.forEachIndexed { index, _ ->
                val isActive = index == currentIndex
                val width by animateDpAsState(
                    targetValue = if (isActive) 20.dp else 5.dp,
                    animationSpec = tween(300),
                    label = "DotWidth"
                )
                Box(
                    modifier = Modifier
                        .height(5.dp)
                        .width(width)
                        .clip(RoundedCornerShape(35.dp))
                        .background(if (isActive) GoldAmber else Color.White.copy(alpha = 0.4f))
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TRENDING HORIZONTAL SECTION
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TrendingHorizontalSection(
    items: List<TmdbMovieDto>,
    onItemClick: (TmdbMovieDto) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🔥", fontSize = 16.sp)
                Text(
                    text = "Sedang Trending",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp
                )
            }
            Text(
                text = "Lihat Semua →",
                color = GoldAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items.take(10), key = { "trend_${it.id}" }) { item ->
                TrendingPosterCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
private fun TrendingPosterCard(
    item: TmdbMovieDto,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "PosterScale"
    )

    Column(
        modifier = Modifier
            .width(110.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(155.dp)
                .clip(RoundedCornerShape(26.dp))
        ) {
            AsyncImage(
                model = item.posterUrl("w300"),
                contentDescription = item.displayTitle,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Subtle overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f))
                        )
                    )
            )
            // Rating badge
            if (item.voteAverage > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(35.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "★ ${((item.voteAverage * 10).roundToInt() / 10.0)}",
                        color = GoldAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = item.displayTitle,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
        )
        item.displayDate?.take(4)?.let { year ->
            Text(
                text = year,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION HEADER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(emoji: String, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(emoji, fontSize = 17.sp)
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.3).sp
        )
        // Decorative line
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SEARCH OVERLAY
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SearchOverlay(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    uiState: HomeUiState,
    tmdbState: TmdbSearchState,
    trendingState: TmdbSearchState,
    onMovieClick: (Long) -> Unit,
    onTmdbClick: (TmdbMovieDto) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(52.dp))
        SearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onFocusChange = onFocusChange
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (searchQuery.isEmpty()) {
            TrendingSection(state = trendingState, onItemClick = onTmdbClick)
        } else {
            SearchCombinedResults(
                uiState = uiState,
                tmdbState = tmdbState,
                onMovieClick = onMovieClick,
                onTmdbClick = onTmdbClick
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SEARCH BAR
// ═══════════════════════════════════════════════════════════════════════════════

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
        placeholder = {
            Text(
                "Cari koleksi atau temukan di TMDB...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        },
        leadingIcon = {
            Text("🔍", fontSize = 15.sp, modifier = Modifier.padding(start = 4.dp))
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(35.dp))
                        .clickable {
                            onQueryChange("")
                            focusManager.clearFocus()
                        }
                        .padding(4.dp)
                ) {
                    Text("✕", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        },
        modifier = Modifier
            .testTag("search_bar")
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .heightIn(min = 52.dp)
            .onFocusChanged { onFocusChange(it.isFocused) },
        singleLine = true,
        shape = RoundedCornerShape(35.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldAmber.copy(alpha = 0.7f),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = GoldAmber,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        ),
        textStyle = TextStyle(fontSize = 13.sp)
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// FILTER ROW
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FilterRow(selected: WatchStatus?, onSelect: (WatchStatus?) -> Unit) {
    val filters = listOf(
        null to "Semua",
        WatchStatus.WATCHING to "Sedang Ditonton",
        WatchStatus.COMPLETED to "Selesai",
        WatchStatus.PLAN_TO_WATCH to "Rencana",
        WatchStatus.ON_HOLD to "Ditunda",
        WatchStatus.DROPPED to "Berhenti"
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (status, label) ->
            val isSelected = selected == status
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                label = "FilterScale"
            )
            Box(
                modifier = Modifier
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clip(RoundedCornerShape(35.dp))
                    .background(
                        if (isSelected)
                            Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber))
                        else
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isSelected) Color.Transparent
                            else MaterialTheme.colorScheme.outline
                        ),
                        RoundedCornerShape(35.dp)
                    )
                    .clickable { onSelect(status) }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) BackgroundDark else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MOVIE CARD — Redesigned
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusLabel) = when (movie.status) {
        WatchStatus.COMPLETED  -> StatusFinished    to "Completed"
        WatchStatus.WATCHING   -> StatusWatching    to "Watching"
        WatchStatus.PLAN_TO_WATCH -> StatusWantToWatch to "Planned"
        WatchStatus.ON_HOLD    -> StatusOnHold      to "On Hold"
        WatchStatus.DROPPED    -> StatusDropped     to "Dropped"
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "CardScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to MaterialTheme.colorScheme.surfaceVariant,
                        1f to MaterialTheme.colorScheme.surface
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                ),
                RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
    ) {
        // Status color ambient
        Box(
            modifier = Modifier
                .size(90.dp)
                .offset(x = (-15).dp, y = (-15).dp)
                .blur(35.dp)
                .background(
                    Brush.radialGradient(
                        listOf(statusColor.copy(alpha = 0.18f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster / Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (movie.posterUrl != null) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = movie.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colorStops = arrayOf(
                                        0f to MaterialTheme.colorScheme.tertiary,
                                        1f to MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = movie.title.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Title
                Text(
                    text = movie.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.1.sp
                )

                // Status + rating row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(35.dp))
                            .background(statusColor.copy(alpha = 0.14f))
                            .border(
                                BorderStroke(0.5.dp, statusColor.copy(alpha = 0.4f)),
                                RoundedCornerShape(35.dp)
                            )
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.3.sp
                        )
                    }

                    // Rating
                    if (movie.rating != null && movie.rating > 0f) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text("★", color = GoldAmber, fontSize = 11.sp)
                            Text(
                                text = movie.rating.toString(),
                                color = GoldAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Genre · Type
                Text(
                    text = "${movie.type.displayName}  ·  ${movie.genre.displayName}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.2.sp
                )

                // Progress bar for WATCHING
                if (movie.status == WatchStatus.WATCHING && movie.totalEpisodes != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { movie.progressPercent / 100f },
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(35.dp)),
                            color = GoldAmber,
                            trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        )
                        Text(
                            text = "${movie.watchedEpisodes}/${movie.totalEpisodes}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "›",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TRENDING SECTION (search overlay – list vertikal)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TrendingSection(
    state: TmdbSearchState,
    onItemClick: (TmdbMovieDto) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🔥", fontSize = 17.sp)
            Text(
                "Lagi Trending Minggu Ini",
                style = MaterialTheme.typography.titleMedium,
                color = GoldAmber,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

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

// ═══════════════════════════════════════════════════════════════════════════════
// SEARCH COMBINED RESULTS
// ═══════════════════════════════════════════════════════════════════════════════

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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📌", fontSize = 14.sp)
                Text(
                    "Di Koleksi Kamu",
                    color = GoldAmber,
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
            is HomeUiState.NoResults -> item {
                Text(
                    "Tidak ada di koleksi lokal",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
            }
            is HomeUiState.Loading -> item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = GoldAmber
                    )
                }
            }
            else -> {}
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🌐", fontSize = 14.sp)
                Text(
                    "Hasil dari TMDB",
                    color = GoldAmber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        when (tmdbState) {
            is TmdbSearchState.Loading -> item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 1.5.dp,
                        color = GoldAmber
                    )
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
            is TmdbSearchState.Empty -> item {
                Text(
                    "Tidak ditemukan di TMDB",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
            }
            is TmdbSearchState.Error -> item {
                Text(
                    "Gagal memuat hasil TMDB",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            else -> {}
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TMDB RESULT CARD
// ═══════════════════════════════════════════════════════════════════════════════

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
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.posterUrl("w200"),
                contentDescription = item.displayTitle,
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp, bottom = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.displayTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(35.dp))
                            .background(GoldAmber.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.isTvSeries) "Series" else "Film",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (item.voteAverage > 0) {
                        Text(
                            text = "★ ${(item.voteAverage * 10).roundToInt() / 10.0}",
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

// ═══════════════════════════════════════════════════════════════════════════════
// TMDB DETAIL DIALOG
// ═══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TmdbDetailDialog(
    item: TmdbMovieDto,
    onDismiss: () -> Unit,
    onConfirmAdd: (WatchStatus, Float?, String) -> Unit  // ← tambah parameter rating & review
) {
    var selectedStatus by remember { mutableStateOf(WatchStatus.PLAN_TO_WATCH) }
    var rating by remember { mutableStateOf(0f) }
    var userReview by remember { mutableStateOf("") }

    // Tampilkan field rating & review hanya untuk status Selesai atau Berhenti
    val showRatingReview = selectedStatus == WatchStatus.COMPLETED ||
            selectedStatus == WatchStatus.DROPPED

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(35.dp)),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        content = {
            Surface(
                shape = RoundedCornerShape(35.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())  // ← scroll supaya tidak overflow
                ) {
                    // ── Poster Header ──────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    ) {
                        AsyncImage(
                            model = item.backdropUrl() ?: item.posterUrl(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                    )
                                )
                        )
                        Text(
                            item.displayTitle,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(18.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        // ── Sinopsis (dari TMDB, bukan review pribadi) ──
                        Text(
                            "Sinopsis",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldAmber,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            item.overview ?: "Tidak ada deskripsi.",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // ── Status Selector ─────────────────────────────
                        Text(
                            "Tambah ke Koleksi Sebagai:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val statuses = listOf(
                            WatchStatus.WATCHING      to "Sedang Ditonton",
                            WatchStatus.PLAN_TO_WATCH to "Direncanakan",
                            WatchStatus.COMPLETED     to "Sudah Selesai",
                            WatchStatus.ON_HOLD       to "Ditunda",
                            WatchStatus.DROPPED       to "Berhenti"
                        )
                        statuses.forEach { (status, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedStatus = status }
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedStatus == status,
                                    onClick = { selectedStatus = status },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldAmber)
                                )
                                Text(
                                    label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        // ── Rating & Review (kondisional) ───────────────
                        AnimatedVisibility(
                            visible = showRatingReview,
                            enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                            exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Rating
                                Text(
                                    "Rating",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "★".repeat(rating.toInt()) + "☆".repeat(5 - rating.toInt()),
                                        color = GoldAmber,
                                        fontSize = 20.sp,
                                        letterSpacing = 2.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(GoldAmber.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            "${rating.toInt()} / 5",
                                            color = GoldAmber,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Slider(
                                    value = rating,
                                    onValueChange = { rating = it },
                                    valueRange = 0f..5f,
                                    steps = 4,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = GoldAmber,
                                        activeTrackColor = GoldAmber,
                                        inactiveTrackColor = MaterialTheme.colorScheme.outline,
                                        activeTickColor = Color.Transparent,
                                        inactiveTickColor = Color.Transparent
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Review / Notes
                                Text(
                                    "Catatan Pribadi",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = userReview,
                                    onValueChange = { userReview = it },
                                    placeholder = {
                                        Text(
                                            "Tulis pendapatmu tentang film/series ini...",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    maxLines = 4,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldAmber.copy(alpha = 0.7f),
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                        cursorColor = GoldAmber,
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    ),
                                    textStyle = TextStyle(fontSize = 13.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // ── Buttons ─────────────────────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onConfirmAdd(
                                        selectedStatus,
                                        if (showRatingReview && rating > 0f) rating else null,
                                        if (showRatingReview) userReview else ""
                                    )
                                },
                                shape = RoundedCornerShape(35.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldAmber,
                                    contentColor = BackgroundDark
                                )
                            ) {
                                Text("Tambah", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// STATE COMPOSABLES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = GoldAmber,
            strokeWidth = 1.5.dp,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .blur(35.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(GoldAmber.copy(alpha = 0.25f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
                Text("🎞️", fontSize = 54.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Koleksi masih kosong",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Tap + untuk tambah judul pertamamu",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun EmptyFilterState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🔍", fontSize = 38.sp)
            Text(
                text = "Tidak ada judul di kategori ini",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun NoResultsState(query: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🎬", fontSize = 42.sp)
            Text(
                text = "Tidak ada hasil untuk \"$query\"",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Coba judul atau genre lain",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("⚠️", fontSize = 28.sp)
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp
            )
        }
    }
}
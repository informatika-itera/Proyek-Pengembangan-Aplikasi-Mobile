package com.example.rewind.presentation.screens.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val background = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Box(
            modifier = Modifier
                .size(350.dp)
                .offset(x = (-100).dp, y = (-80).dp)
                .blur(130.dp)
                .background(
                    Brush.radialGradient(listOf(TheaterRed.copy(alpha = 0.22f), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(x = 70.dp, y = 120.dp)
                .blur(110.dp)
                .background(
                    Brush.radialGradient(listOf(GoldAmber.copy(alpha = 0.13f), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 60.dp)
                .blur(120.dp)
                .background(
                    Brush.radialGradient(listOf(VelvetRed.copy(alpha = 0.12f), Color.Transparent)),
                    CircleShape
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            ProfileHeader(onNavigateBack = onNavigateBack)

            AnimatedContent(
                targetState = uiState,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                label = "ProfileStateAnimation"
            ) { state ->
                when (state) {
                    is ProfileUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 1.5.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    is ProfileUiState.Success -> ProfileContent(
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
@Composable
private fun ProfileHeader(onNavigateBack: () -> Unit) {
    val rewindColors = LocalRewindColors.current
    val surface = MaterialTheme.colorScheme.surface
    val onBg = MaterialTheme.colorScheme.onBackground

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "BackBtnScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            BorderGold.copy(alpha = 0.3f),
                            GoldAmber.copy(alpha = 0.2f),
                            BorderGold.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clip(CircleShape)
                    .background(rewindColors.surfaceElevated)
                    .border(BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.55f)), CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = onNavigateBack
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = GoldAmber,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "PROFILE SAYA",
                    color = GoldAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                )
                Text(
                    text = "Statistik & Koleksi",
                    color = onBg,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.3).sp,
                    lineHeight = 24.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(rewindColors.surfaceElevated)
                    .border(BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.55f)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun RewindHeroSection(state: ProfileUiState.Success) {
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to VelvetRed.copy(alpha = 0.35f),
                        0.5f to SurfaceElevated,
                        1f to GoldAmberDim.copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            GoldAmber.copy(alpha = 0.5f),
                            BorderSubtle.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                ),
                RoundedCornerShape(28.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .blur(60.dp)
                .background(
                    Brush.radialGradient(
                        listOf(GoldAmber.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Label "2026 REWIND"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(GoldAmber)
                )
                Text(
                    "2026  REWIND",
                    color = GoldAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            // Nama user
            Text(
                "${state.userName}'s",
                color = TextWarm.copy(alpha = 0.7f),  // ← hardcode TextWarm
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.3).sp
            )

            // "Rewind" judul besar
            Text(
                "Rewind",
                color = TextWarm,  // ← hardcode TextWarm biar selalu terang
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-2).sp,
                lineHeight = 44.sp
            )

            Spacer(Modifier.height(16.dp))

            // Total movies — fix layout supaya "Movies Watched" gak kepotong
            Row(
                verticalAlignment = Alignment.CenterVertically,  // ← ganti Bottom → CenterVertically
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedCounter(
                    target = state.totalMovies,
                    style = TextStyle(
                        color = GoldAmber,
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-2).sp,
                        lineHeight = 52.sp,
                    )
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Film",
                        color = TextWarm,  // ← hardcode
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Ditonton",
                        color = TextWarm.copy(alpha = 0.5f),  // ← hardcode
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(GoldAmber.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "Dicatat sejak Jan 2026",
                color = TextWarm.copy(alpha = 0.35f),  // ← hardcode
                fontSize = 11.sp,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
private fun ProfileContent(
    state: ProfileUiState.Success,
    viewModel: ProfileViewModel
) {
    val isEditMode by viewModel.isEditMode.collectAsState()
    val editName by viewModel.editName.collectAsState()
    val editBio by viewModel.editBio.collectAsState()

    // State untuk trigger animasi
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Tiap section pakai delay berbeda
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(
                tween(400), initialOffsetY = { it / 3 }
            )
        ) {
            RewindHeroSection(state = state)
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400, delayMillis = 80)) + slideInVertically(
                tween(400, delayMillis = 80), initialOffsetY = { it / 3 }
            )
        ) {
            IdentityCard(
                state = state,
                isEditMode = isEditMode,
                editName = editName,
                editBio = editBio,
                onNameChange = { viewModel.editName.value = it },
                onBioChange = { viewModel.editBio.value = it },
                onEditClick = { viewModel.startEdit() },
                onSaveClick = { viewModel.saveEdit() },
                onCancelClick = { viewModel.cancelEdit() }
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400, delayMillis = 160)) + slideInVertically(
                tween(400, delayMillis = 160), initialOffsetY = { it / 3 }
            )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                RewindStatsSection(state = state)
                SectionTitle("STATUS TONTONAN")
                StatusBreakdown(state = state)
                if (state.topGenres.isNotEmpty()) {
                    SectionTitle("GENRE FAVORIT")
                    GenreBreakdown(state = state)
                }
            }
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400, delayMillis = 240)) + slideInVertically(
                tween(400, delayMillis = 240), initialOffsetY = { it / 3 }
            )
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                SectionTitle("PENCAPAIAN")
                AchievementsGrid(achievements = state.achievements)
                if (state.recentMovies.isNotEmpty()) {
                    SectionTitle("BARU DITONTON")
                    RecentPosterScroll(movies = state.recentMovies)
                }
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun RecentPosterScroll(movies: List<com.example.rewind.domain.model.Movie>) {
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onBackground = MaterialTheme.colorScheme.onBackground
    val outline = MaterialTheme.colorScheme.outline

    // Warna poster placeholder berdasarkan index
    val posterColors = listOf(
        listOf(VelvetRed, TheaterRed),
        listOf(Color(0xFF1a3a5c), Color(0xFF2d6091)),
        listOf(Color(0xFF3d1a4a), GoldAmberDim),
        listOf(Color(0xFF1a3a20), Color(0xFF2d7040)),
        listOf(SurfaceElevated, TheaterRed.copy(alpha = 0.6f))
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        itemsIndexed(movies) { index, movie ->
            val colors = posterColors[index % posterColors.size]
            val (statusColor, _) = when (movie.status) {
                WatchStatus.COMPLETED -> StatusFinished to "Selesai"
                WatchStatus.WATCHING  -> StatusWatching to "Sedang Ditonton"
                WatchStatus.PLAN_TO_WATCH -> StatusWantToWatch to "Rencana"
                WatchStatus.ON_HOLD   -> StatusOnHold to "Ditunda"
                WatchStatus.DROPPED   -> StatusDropped to "Berhenti"
            }

            Box(
                modifier = Modifier
                    .width(105.dp)
                    .height(152.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colorStops = arrayOf(0f to colors[0], 1f to colors[1])
                        )
                    )
                    .border(
                        BorderStroke(1.dp, outline.copy(alpha = 0.2f)),
                        RoundedCornerShape(14.dp)
                    )
            ) {
                // Overlay gradient bawah
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.55f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )

                // Status dot kanan atas
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(7.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(statusColor)
                )

                // Info bawah
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        movie.title,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp
                    )
                    if ((movie.rating ?: 0f) > 0f) {
                        Text(
                            "★ ${movie.rating}",
                            color = GoldAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewindStatsSection(state: ProfileUiState.Success) {
    val topGenre = state.topGenres.firstOrNull()?.first ?: "—"
    val topMovie = state.recentMovies
        .filter { it.status == WatchStatus.COMPLETED }
        .maxByOrNull { it.rating ?: 0f }
        ?.title ?: "—"

    val statItems = listOf(
        RewindStat("🔥", "${state.currentStreak}", "Hari Beruntun", TheaterRed),
        RewindStat("⭐", if (state.averageRating > 0f) "${state.averageRating}" else "—", "Rata-rata Rating", GoldAmber),
        RewindStat("🎭", topGenre, "Genre Terfavorit", StatusOnHold),
        RewindStat("🎬", topMovie, "Film Terbaik", StatusFinished)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        statItems.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { stat ->
                    RewindStatCard(stat = stat, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

data class RewindStat(
    val emoji: String,
    val value: String,
    val label: String,
    val accentColor: Color
)

@Composable
private fun RewindStatCard(stat: RewindStat, modifier: Modifier) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val onBackground = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(surfaceVariant)
            .border(
                BorderStroke(1.dp, stat.accentColor.copy(alpha = 0.25f)),
                RoundedCornerShape(20.dp)
            )
    ) {
        // Accent line atas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(stat.accentColor, stat.accentColor.copy(alpha = 0.3f))
                    )
                )
        )
        // Ambient glow sudut kiri atas
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopStart)
                .offset(x = (-10).dp, y = (-10).dp)
                .blur(25.dp)
                .background(
                    Brush.radialGradient(
                        listOf(stat.accentColor.copy(alpha = 0.25f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stat.emoji, fontSize = 22.sp)

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    stat.value,
                    color = onBackground,
                    fontSize = if (stat.value.length > 8) 14.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    stat.label,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 10.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

@Composable
fun AnimatedCounter(
    target: Int,
    durationMs: Int = 1200,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var display by remember { mutableIntStateOf(0) }

    LaunchedEffect(target) {
        val steps = 40
        val stepDelay = durationMs / steps
        for (i in 1..steps) {
            delay(stepDelay.toLong())
            display = (target * i / steps)
        }
        display = target
    }

    Text(text = "$display", style = style, modifier = modifier)
}

@Composable
private fun IdentityCard(
    state: ProfileUiState.Success,
    isEditMode: Boolean,
    editName: String,
    editBio: String,
    onNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val primary = MaterialTheme.colorScheme.primary
    val onBackground = MaterialTheme.colorScheme.onBackground
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surface = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(surfaceVariant)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(outline.copy(alpha = 0.5f), outline.copy(alpha = 0.2f), Color.Transparent)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = (-20).dp, y = (-20).dp)
                .blur(40.dp)
                .background(
                    Brush.radialGradient(listOf(primary.copy(alpha = 0.12f), Color.Transparent)),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .blur(20.dp)
                            .background(
                                Brush.radialGradient(listOf(primary.copy(alpha = 0.3f), Color.Transparent)),
                                CircleShape
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colorStops = arrayOf(0f to VelvetRed, 1f to TheaterRed)
                                )
                            )
                            .border(
                                BorderStroke(
                                    2.dp,
                                    Brush.linearGradient(
                                        listOf(primary.copy(alpha = 0.6f), GoldAmberDim.copy(alpha = 0.3f))
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎬", fontSize = 30.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(StatusFinished)
                            .border(BorderStroke(2.dp, surfaceVariant), CircleShape)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isEditMode) {
                        BasicTextField(
                            value = editName,
                            onValueChange = onNameChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = onBackground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(1.dp, primary.copy(alpha = 0.5f)),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) { innerTextField() }
                            }
                        )
                    } else {
                        Text(
                            state.userName,
                            color = onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(primary.copy(alpha = 0.12f))
                                .border(BorderStroke(0.5.dp, primary.copy(alpha = 0.35f)), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "✦ cinephile",
                                color = primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    if (isEditMode) {
                        BasicTextField(
                            value = editBio,
                            onValueChange = onBioChange,
                            maxLines = 3,
                            textStyle = TextStyle(
                                color = onSurfaceVariant,
                                fontSize = 12.sp
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            BorderStroke(1.dp, outline.copy(alpha = 0.4f)),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) { innerTextField() }
                            }
                        )
                    } else {
                        Text(
                            state.userBio,
                            color = onSurfaceVariant,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 17.sp
                        )
                    }
                }

                if (isEditMode) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        val saveInteractionSource = remember { MutableInteractionSource() }
                        val savePressed by saveInteractionSource.collectIsPressedAsState()
                        val saveScale by animateFloatAsState(
                            targetValue = if (savePressed) 0.92f else 1f,
                            label = "SaveScale"
                        )

                        val cancelInteractionSource = remember { MutableInteractionSource() }
                        val cancelPressed by cancelInteractionSource.collectIsPressedAsState()
                        val cancelScale by animateFloatAsState(
                            targetValue = if (cancelPressed) 0.92f else 1f,
                            label = "CancelScale"
                        )

                        Box(
                            modifier = Modifier
                                .graphicsLayer(scaleX = saveScale, scaleY = saveScale)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber)))
                                .clickable(
                                    interactionSource = saveInteractionSource,
                                    indication = LocalIndication.current,
                                    onClick = onSaveClick
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Save", color = BackgroundDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .graphicsLayer(scaleX = cancelScale, scaleY = cancelScale)
                                .clip(RoundedCornerShape(8.dp))
                                .background(surface)
                                .border(BorderStroke(1.dp, outline.copy(alpha = 0.4f)), RoundedCornerShape(8.dp))
                                .clickable(
                                    interactionSource = cancelInteractionSource,
                                    indication = LocalIndication.current,
                                    onClick = onCancelClick
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Cancel", color = onSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                } else {
                    val editInteractionSource = remember { MutableInteractionSource() }
                    val editPressed by editInteractionSource.collectIsPressedAsState()
                    val editScale by animateFloatAsState(
                        targetValue = if (editPressed) 0.9f else 1f,
                        label = "EditScale"
                    )

                    Box(
                        modifier = Modifier
                            .graphicsLayer(scaleX = editScale, scaleY = editScale)
                            .clip(RoundedCornerShape(8.dp))
                            .background(primary.copy(alpha = 0.1f))
                            .border(
                                BorderStroke(1.dp, primary.copy(alpha = 0.35f)),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable(
                                interactionSource = editInteractionSource,
                                indication = LocalIndication.current,
                                onClick = onEditClick
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("✏️", fontSize = 13.sp)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(outline.copy(alpha = 0.3f), Color.Transparent)
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat("${state.totalMovies}", "Film")
                StatDivider()
                MiniStat("${state.statusCounts[WatchStatus.COMPLETED] ?: 0}", "Selesai")
                StatDivider()
                MiniStat(if (state.averageRating > 0f) "${state.averageRating}★" else "—", "Rating")
                StatDivider()
                MiniStat("${state.favoriteCount}", "Favorit")
            }
        }
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            value,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.3).sp
        )
        Text(
            label,
            color = MaterialTheme.colorScheme.outline,
            fontSize = 9.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    )
}

@Composable
private fun QuickStatsRow(state: ProfileUiState.Success) {
    val completionRate = if (state.totalMovies > 0)
        ((state.statusCounts[WatchStatus.COMPLETED] ?: 0).toFloat() / state.totalMovies * 100).toInt()
    else 0

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickStatCard(
            modifier = Modifier.weight(1f),
            emoji = "🏆",
            value = "$completionRate%",
            label = "Completion",
            color = StatusFinished
        )
        QuickStatCard(
            modifier = Modifier.weight(1f),
            emoji = "📺",
            value = "${state.statusCounts[WatchStatus.WATCHING] ?: 0}",
            label = "In Progress",
            color = StatusWatching
        )
        QuickStatCard(
            modifier = Modifier.weight(1f),
            emoji = "🔖",
            value = "${state.statusCounts[WatchStatus.PLAN_TO_WATCH] ?: 0}",
            label = "Watchlist",
            color = StatusWantToWatch
        )
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier,
    emoji: String,
    value: String,
    label: String,
    color: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.08f))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.25f)), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(emoji, fontSize = 20.sp)
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
        }
    }
}

@Composable
private fun StatusBreakdown(state: ProfileUiState.Success) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val onBackground = MaterialTheme.colorScheme.onBackground

    val statusItems = listOf(
        WatchStatus.COMPLETED to (StatusFinished to "Completed"),
        WatchStatus.WATCHING to (StatusWatching to "Watching"),
        WatchStatus.PLAN_TO_WATCH to (StatusWantToWatch to "Planned"),
        WatchStatus.ON_HOLD to (StatusOnHold to "On Hold"),
        WatchStatus.DROPPED to (StatusDropped to "Dropped")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceVariant)
            .border(BorderStroke(1.dp, outline.copy(alpha = 0.3f)), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            statusItems.forEach { (status, pair) ->
                val (color, label) = pair
                val count = state.statusCounts[status] ?: 0
                val targetProgress = if (state.totalMovies > 0) count.toFloat() / state.totalMovies else 0f
                val animatedProgress by animateFloatAsState(
                    targetValue = targetProgress,
                    animationSpec = tween(1000),
                    label = "StatusProgressAnim"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .background(color.copy(alpha = 0.12f), RoundedCornerShape(5.dp))
                            .border(BorderStroke(0.5.dp, color.copy(alpha = 0.3f)), RoundedCornerShape(5.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = color,
                        trackColor = color.copy(alpha = 0.08f)
                    )

                    Text(
                        "$count",
                        color = onBackground,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreBreakdown(state: ProfileUiState.Success) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceVariant)
            .border(BorderStroke(1.dp, outline.copy(alpha = 0.3f)), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            state.topGenres.take(5).forEachIndexed { index, (genre, count) ->
                val targetProgress = if (state.totalMovies > 0) count.toFloat() / state.totalMovies else 0f
                val animatedProgress by animateFloatAsState(
                    targetValue = targetProgress,
                    animationSpec = tween(1000),
                    label = "GenreProgressAnim"
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("${index + 1}", color = outline, fontSize = 11.sp, modifier = Modifier.width(14.dp))
                    Text(
                        genre,
                        color = onSurfaceVariant,
                        fontSize = 12.sp,
                        modifier = Modifier.width(80.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = primary,
                        trackColor = primary.copy(alpha = 0.08f)
                    )
                    Text("$count", color = outline, fontSize = 12.sp, modifier = Modifier.width(20.dp))
                }
            }
        }
    }
}

@Composable
private fun AchievementsGrid(achievements: List<Achievement>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        achievements.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { achievement ->
                    AchievementCard(achievement = achievement, modifier = Modifier.weight(1f))
                }
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement, modifier: Modifier) {
    val unlocked = achievement.unlocked
    val primary = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val surface = MaterialTheme.colorScheme.surface
    val outline = MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .height(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (unlocked)
                    Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to GoldAmberDim.copy(alpha = 0.25f),
                            0.6f to surfaceVariant,
                            1f to VelvetRed.copy(alpha = 0.08f)
                        )
                    )
                else
                    Brush.linearGradient(listOf(surface, surface))
            )
            .border(
                BorderStroke(
                    if (unlocked) 1.dp else 0.5.dp,
                    if (unlocked)
                        Brush.linearGradient(listOf(primary.copy(alpha = 0.6f), outline.copy(alpha = 0.2f)))
                    else
                        Brush.linearGradient(listOf(outline.copy(alpha = 0.3f), outline.copy(alpha = 0.1f)))
                ),
                RoundedCornerShape(18.dp)
            )
    ) {
        if (unlocked) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-20).dp)
                    .blur(30.dp)
                    .background(
                        Brush.radialGradient(listOf(primary.copy(alpha = 0.25f), Color.Transparent)),
                        CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (unlocked) primary.copy(alpha = 0.12f)
                            else outline.copy(alpha = 0.08f)
                        )
                        .border(
                            BorderStroke(
                                0.5.dp,
                                if (unlocked) primary.copy(alpha = 0.3f)
                                else outline.copy(alpha = 0.2f)
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.emoji,
                        fontSize = 22.sp,
                        modifier = if (!unlocked) Modifier.blur(2.dp) else Modifier
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (unlocked) StatusFinished.copy(alpha = 0.15f)
                            else outline.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (unlocked) "✓" else "🔒",
                        color = if (unlocked) StatusFinished else outline.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = achievement.title,
                    color = if (unlocked) primary else outline.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = achievement.description,
                    color = if (unlocked) MaterialTheme.colorScheme.onSurfaceVariant else outline.copy(alpha = 0.35f),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RecentActivity(movies: List<com.example.rewind.domain.model.Movie>) {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val onBackground = MaterialTheme.colorScheme.onBackground
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceVariant)
            .border(BorderStroke(1.dp, outline.copy(alpha = 0.3f)), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            movies.forEachIndexed { index, movie ->
                val (statusColor, statusLabel) = when (movie.status) {
                    WatchStatus.COMPLETED -> StatusFinished to "Done"
                    WatchStatus.WATCHING -> StatusWatching to "Watching"
                    WatchStatus.PLAN_TO_WATCH -> StatusWantToWatch to "Planned"
                    WatchStatus.ON_HOLD -> StatusOnHold to "On Hold"
                    WatchStatus.DROPPED -> StatusDropped to "Dropped"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(surfaceVariant)
                            .border(BorderStroke(1.dp, outline.copy(alpha = 0.3f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = outline, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            movie.title,
                            color = onBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${movie.type.displayName} · ${movie.genre.displayName}",
                            color = onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(5.dp))
                            .border(BorderStroke(0.5.dp, statusColor.copy(alpha = 0.3f)), RoundedCornerShape(5.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(statusLabel, color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (index < movies.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(outline.copy(alpha = 0.2f))
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Text(
                text,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.5.sp
            )
        }
    }
}
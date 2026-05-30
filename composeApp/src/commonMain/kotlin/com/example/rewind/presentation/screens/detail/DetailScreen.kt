package com.example.rewind.presentation.screens.detail

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.presentation.theme.GoldAmber
import com.example.rewind.presentation.theme.GoldAmberDim
import com.example.rewind.presentation.theme.LocalRewindColors
import com.example.rewind.presentation.theme.StatusDropped
import com.example.rewind.presentation.theme.StatusFinished
import com.example.rewind.presentation.theme.StatusOnHold
import com.example.rewind.presentation.theme.StatusWantToWatch
import com.example.rewind.presentation.theme.StatusWatching
import com.example.rewind.presentation.theme.TheaterRed
import com.example.rewind.presentation.theme.VelvetRed
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DetailScreen(
    movieId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: DetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val rewindColors = LocalRewindColors.current
    val bg = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = rewindColors.surfaceElevated,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Remove from Collection?",
                    color = onBg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "This title will be permanently deleted.",
                    color = rewindColors.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                val removeInteractionSource = remember { MutableInteractionSource() }
                val removePressed by removeInteractionSource.collectIsPressedAsState()
                val removeScale by animateFloatAsState(
                    targetValue = if (removePressed) 0.9f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "removeDialogScale"
                )
                Box(
                    modifier = Modifier
                        .graphicsLayer(scaleX = removeScale, scaleY = removeScale)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TheaterRed.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, TheaterRed.copy(alpha = 0.4f)), RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = removeInteractionSource,
                            indication = LocalIndication.current
                        ) {
                            viewModel.deleteMovie(movieId) { onNavigateBack() }
                            showDeleteDialog = false
                        }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text("Remove", color = TheaterRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            dismissButton = {
                val cancelInteractionSource = remember { MutableInteractionSource() }
                val cancelPressed by cancelInteractionSource.collectIsPressedAsState()
                val cancelScale by animateFloatAsState(
                    targetValue = if (cancelPressed) 0.9f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "cancelDialogScale"
                )
                Box(
                    modifier = Modifier
                        .graphicsLayer(scaleX = cancelScale, scaleY = cancelScale)
                        .clip(RoundedCornerShape(10.dp))
                        .background(surface)
                        .border(BorderStroke(1.dp, rewindColors.borderSubtle), RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = cancelInteractionSource,
                            indication = LocalIndication.current
                        ) { showDeleteDialog = false }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text("Cancel", color = rewindColors.textMuted, fontSize = 13.sp)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                val infiniteTransition = rememberInfiniteTransition(label = "loadingTransition")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "loadingAlpha"
                )
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = GoldAmber,
                        strokeWidth = 1.5.dp,
                        modifier = Modifier.size(32.dp).graphicsLayer(alpha = alpha)
                    )
                }
            }
            is DetailUiState.NotFound -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🎞️", fontSize = 44.sp)
                        Text("Title not found", color = rewindColors.textMuted, fontSize = 14.sp)
                    }
                }
            }
            is DetailUiState.Success -> {
                MovieDetail(
                    movie = state.movie,
                    onBack = onNavigateBack,
                    onDelete = { showDeleteDialog = true },
                    onEdit = { onNavigateToEdit(movieId) }
                )
            }
        }
    }
}

@Composable
private fun MovieDetail(movie: Movie, onBack: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    val rewindColors = LocalRewindColors.current
    val bg = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg = MaterialTheme.colorScheme.onBackground

    val (statusColor, statusLabel) = when (movie.status) {
        WatchStatus.COMPLETED -> StatusFinished to "Completed"
        WatchStatus.WATCHING -> StatusWatching to "Watching"
        WatchStatus.PLAN_TO_WATCH -> StatusWantToWatch to "Planned"
        WatchStatus.ON_HOLD -> StatusOnHold to "On Hold"
        WatchStatus.DROPPED -> StatusDropped to "Dropped"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colorStops = arrayOf(
                                0f to VelvetRed,
                                0.5f to TheaterRed,
                                1f to surface
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.5f to bg.copy(alpha = 0.3f),
                                1f to bg
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-20).dp)
                    .blur(60.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GoldAmber.copy(alpha = 0.2f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-30).dp, y = 30.dp)
                    .blur(50.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(statusColor.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            Text(
                text = movie.title.take(2).uppercase(),
                color = onBg.copy(alpha = 0.07f),
                fontSize = 110.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Center)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val backInteractionSource = remember { MutableInteractionSource() }
                val backPressed by backInteractionSource.collectIsPressedAsState()
                val backScale by animateFloatAsState(
                    targetValue = if (backPressed) 0.85f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "backBtnScale"
                )
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .graphicsLayer(scaleX = backScale, scaleY = backScale)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg.copy(alpha = 0.55f))
                        .border(
                            BorderStroke(1.dp, rewindColors.borderSubtle.copy(alpha = 0.5f)),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable(
                            interactionSource = backInteractionSource,
                            indication = LocalIndication.current,
                            onClick = onBack
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", color = onBg, fontSize = 17.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val editInteractionSource = remember { MutableInteractionSource() }
                    val editPressed by editInteractionSource.collectIsPressedAsState()
                    val editScale by animateFloatAsState(
                        targetValue = if (editPressed) 0.9f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "editBtnScale"
                    )
                    Box(
                        modifier = Modifier
                            .graphicsLayer(scaleX = editScale, scaleY = editScale)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldAmber.copy(alpha = 0.15f))
                            .border(BorderStroke(1.dp, GoldAmber.copy(alpha = 0.35f)), RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = editInteractionSource,
                                indication = LocalIndication.current,
                                onClick = onEdit
                            )
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Text(
                            "Edit",
                            color = GoldAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp
                        )
                    }

                    val deleteInteractionSource = remember { MutableInteractionSource() }
                    val deletePressed by deleteInteractionSource.collectIsPressedAsState()
                    val deleteScale by animateFloatAsState(
                        targetValue = if (deletePressed) 0.9f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "deleteBtnScale"
                    )
                    Box(
                        modifier = Modifier
                            .graphicsLayer(scaleX = deleteScale, scaleY = deleteScale)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TheaterRed.copy(alpha = 0.18f))
                            .border(BorderStroke(1.dp, TheaterRed.copy(alpha = 0.35f)), RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = deleteInteractionSource,
                                indication = LocalIndication.current,
                                onClick = onDelete
                            )
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                    ) {
                        Text(
                            "Remove",
                            color = TheaterRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .border(BorderStroke(0.5.dp, statusColor.copy(alpha = 0.4f)), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel.uppercase(),
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = movie.title,
                color = onBg,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp,
                letterSpacing = (-0.3).sp
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(rewindColors.surfaceElevated, RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, rewindColors.borderSubtle), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = movie.type.displayName,
                        color = rewindColors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .background(rewindColors.surfaceElevated, RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, rewindColors.borderSubtle), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = movie.genre.displayName,
                        color = rewindColors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (movie.rating != null && movie.rating > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(GoldAmberDim.copy(alpha = 0.12f), Color.Transparent)
                            )
                        )
                        .border(
                            BorderStroke(1.dp, Brush.horizontalGradient(listOf(rewindColors.borderGold.copy(alpha = 0.5f), Color.Transparent))),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "★".repeat(movie.rating.toInt()),
                            color = GoldAmber,
                            fontSize = 18.sp,
                            letterSpacing = 2.sp
                        )
                        Column {
                            Text(
                                text = "${movie.rating} / 5",
                                color = GoldAmber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your Rating",
                                color = rewindColors.textMuted,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            if (movie.totalEpisodes != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(rewindColors.surfaceElevated)
                        .border(BorderStroke(1.dp, rewindColors.borderSubtle), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EPISODES",
                                color = rewindColors.textMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "${movie.watchedEpisodes} / ${movie.totalEpisodes}",
                                color = onBg,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        var startProgress by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { startProgress = true }
                        val animatedProgress by animateFloatAsState(
                            targetValue = if (startProgress) movie.progressPercent / 100f else 0f,
                            animationSpec = tween(1200, easing = FastOutSlowInEasing),
                            label = "episodeProgress"
                        )

                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = GoldAmber,
                            trackColor = VelvetRed.copy(alpha = 0.2f)
                        )
                        Text(
                            text = "${movie.progressPercent.toInt()}% completed",
                            color = rewindColors.textMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, rewindColors.borderSubtle, rewindColors.borderSubtle, Color.Transparent)
                        )
                    )
            )

            if (movie.review.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "REVIEW",
                        color = GoldAmber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(rewindColors.surfaceElevated)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    Brush.verticalGradient(listOf(rewindColors.borderGold.copy(alpha = 0.3f), rewindColors.borderSubtle))
                                ),
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .offset(x = (-10).dp, y = (-10).dp)
                                .blur(20.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(GoldAmber.copy(alpha = 0.1f), Color.Transparent)
                                    ),
                                    shape = CircleShape
                                )
                        )
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "❝",
                                color = GoldAmber.copy(alpha = 0.4f),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = movie.review,
                                color = onBg,
                                fontSize = 14.sp,
                                lineHeight = 23.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }

            if (movie.isFavorite) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(GoldAmberDim.copy(alpha = 0.1f), Color.Transparent)
                            )
                        )
                        .border(
                            BorderStroke(1.dp, Brush.horizontalGradient(listOf(rewindColors.borderGold.copy(alpha = 0.5f), Color.Transparent))),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("⭐", fontSize = 18.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text(
                                text = "Favorite Title",
                                color = GoldAmber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Rating above 8.0",
                                color = rewindColors.textMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
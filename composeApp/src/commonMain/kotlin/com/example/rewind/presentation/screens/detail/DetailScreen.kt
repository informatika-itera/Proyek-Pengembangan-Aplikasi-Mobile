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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.presentation.theme.BorderGold
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
import kotlin.math.round
import org.koin.compose.viewmodel.koinViewModel

private fun Float.toRatingString(): String {
    val rounded = round(this * 10).toInt()
    val whole = rounded / 10
    val decimal = rounded % 10
    return "$whole.$decimal"
}

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
                    "Hapus dari Koleksi?",
                    color = onBg,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "Judul ini akan dihapus secara permanen dari koleksimu.",
                    color = rewindColors.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 21.sp
                )
            },
            confirmButton = {
                val interSrc = remember { MutableInteractionSource() }
                val pressed by interSrc.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (pressed) 0.92f else 1f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                    label = "removeScale"
                )
                Box(
                    modifier = Modifier
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TheaterRed.copy(alpha = 0.12f))
                        .border(BorderStroke(1.dp, TheaterRed.copy(alpha = 0.5f)), RoundedCornerShape(12.dp))
                        .clickable(interactionSource = interSrc, indication = LocalIndication.current) {
                            viewModel.deleteMovie(movieId) { onNavigateBack() }
                            showDeleteDialog = false
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Hapus", color = TheaterRed, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            },
            dismissButton = {
                val interSrc = remember { MutableInteractionSource() }
                val pressed by interSrc.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (pressed) 0.92f else 1f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
                    label = "cancelScale"
                )
                Box(
                    modifier = Modifier
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clip(RoundedCornerShape(12.dp))
                        .background(surface)
                        .border(BorderStroke(1.dp, rewindColors.borderSubtle), RoundedCornerShape(12.dp))
                        .clickable(interactionSource = interSrc, indication = LocalIndication.current) {
                            showDeleteDialog = false
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Batal", color = rewindColors.textSecondary, fontSize = 14.sp)
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
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer(alpha = alpha)
                    )
                }
            }

            is DetailUiState.NotFound -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = rewindColors.textMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Judul tidak ditemukan",
                            color = rewindColors.textMuted,
                            fontSize = 15.sp,
                            letterSpacing = 0.2.sp
                        )
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
private fun DetailHeader(
    title: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val rewindColors = LocalRewindColors.current
    val surface = MaterialTheme.colorScheme.surface
    val onBg = MaterialTheme.colorScheme.onBackground

    val backSrc = remember { MutableInteractionSource() }
    val backPressed by backSrc.collectIsPressedAsState()
    val backScale by animateFloatAsState(
        targetValue = if (backPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "backScale"
    )

    val editSrc = remember { MutableInteractionSource() }
    val editPressed by editSrc.collectIsPressedAsState()
    val editScale by animateFloatAsState(
        targetValue = if (editPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "editScale"
    )

    val deleteSrc = remember { MutableInteractionSource() }
    val deletePressed by deleteSrc.collectIsPressedAsState()
    val deleteScale by animateFloatAsState(
        targetValue = if (deletePressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "deleteScale"
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
                    .graphicsLayer(scaleX = backScale, scaleY = backScale)
                    .clip(CircleShape)
                    .background(rewindColors.surfaceElevated)
                    .border(BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.55f)), CircleShape)
                    .clickable(interactionSource = backSrc, indication = LocalIndication.current, onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = GoldAmber,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "DETAIL FILM",
                    color = GoldAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                )
                Text(
                    text = title,
                    color = onBg,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp,
                    lineHeight = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer(scaleX = editScale, scaleY = editScale)
                        .clip(CircleShape)
                        .background(rewindColors.surfaceElevated)
                        .border(BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.55f)), CircleShape)
                        .clickable(interactionSource = editSrc, indication = LocalIndication.current, onClick = onEdit),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = GoldAmber,
                        modifier = Modifier.size(17.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer(scaleX = deleteScale, scaleY = deleteScale)
                        .clip(CircleShape)
                        .background(rewindColors.surfaceElevated)
                        .border(BorderStroke(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.55f)), CircleShape)
                        .clickable(interactionSource = deleteSrc, indication = LocalIndication.current, onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Hapus",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieHero(movie: Movie, bg: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        if (movie.posterUrl != null) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(22.dp),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colorStops = arrayOf(
                                0f to VelvetRed,
                                0.6f to TheaterRed,
                                1f to bg
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(80.dp)
                .background(Brush.verticalGradient(listOf(bg.copy(alpha = 0.7f), Color.Transparent)))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, bg.copy(alpha = 0.8f), bg)))
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(72.dp)
                .background(Brush.horizontalGradient(listOf(bg.copy(alpha = 0.8f), Color.Transparent)))
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(72.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, bg.copy(alpha = 0.8f))))
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .blur(55.dp)
                .background(
                    Brush.radialGradient(listOf(GoldAmber.copy(alpha = 0.22f), Color.Transparent)),
                    shape = CircleShape
                )
        )

        if (movie.posterUrl == null) {
            Text(
                text = movie.title.take(2).uppercase(),
                color = Color.White.copy(alpha = 0.06f),
                fontSize = 110.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun StatusCard(statusColor: Color, statusLabel: String) {
    val rewindColors = LocalRewindColors.current
    val onBg = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(rewindColors.surfaceElevated)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            statusColor.copy(alpha = 0.5f),
                            rewindColors.borderSubtle.copy(alpha = 0.5f)
                        )
                    )
                ),
                RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(statusColor.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "STATUS TONTONAN",
                    color = rewindColors.textMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = statusLabel,
                    color = onBg,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.1.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                    .border(BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusLabel.uppercase(),
                    color = statusColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

@Composable
private fun MovieDetail(
    movie: Movie,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val rewindColors = LocalRewindColors.current
    val bg = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg = MaterialTheme.colorScheme.onBackground

    val (statusColor, statusLabel) = when (movie.status) {
        WatchStatus.COMPLETED     -> StatusFinished    to "Selesai"
        WatchStatus.WATCHING      -> StatusWatching    to "Sedang Ditonton"
        WatchStatus.PLAN_TO_WATCH -> StatusWantToWatch to "Rencana Ditonton"
        WatchStatus.ON_HOLD       -> StatusOnHold      to "Ditunda"
        WatchStatus.DROPPED       -> StatusDropped     to "Berhenti"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        DetailHeader(
            title = movie.title,
            onBack = onBack,
            onEdit = onEdit,
            onDelete = onDelete
        )

        MovieHero(movie = movie, bg = bg)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = movie.title,
                color = onBg,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 30.sp,
                letterSpacing = (-0.2).sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaChip(label = movie.type.displayName)
                MetaChip(label = movie.genre.displayName)
            }

            Spacer(modifier = Modifier.height(2.dp))

            StatusCard(statusColor = statusColor, statusLabel = statusLabel)

            if (movie.rating != null && movie.rating > 0f) {
                SectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            SectionLabel(text = "RATING KAMU")
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = movie.rating.toRatingString(),
                                    color = GoldAmber,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 36.sp
                                )
                                Text(
                                    text = "/ 5",
                                    color = rewindColors.textMuted,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < movie.rating.toInt()) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        tint = if (index < movie.rating.toInt()) GoldAmber else rewindColors.borderGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = when {
                                    movie.rating >= 4.5f -> "Luar biasa"
                                    movie.rating >= 3.5f -> "Sangat bagus"
                                    movie.rating >= 2.5f -> "Cukup bagus"
                                    else                 -> "Kurang bagus"
                                },
                                color = rewindColors.textSecondary,
                                fontSize = 11.sp,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            }

            if (movie.totalEpisodes != null) {
                SectionCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionLabel(text = "PROGRESS EPISODE")
                            Text(
                                text = "${movie.watchedEpisodes} / ${movie.totalEpisodes} ep",
                                color = onBg,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        var startProgress by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { startProgress = true }
                        val animatedProgress by animateFloatAsState(
                            targetValue = if (startProgress) movie.progressPercent / 100f else 0f,
                            animationSpec = tween(1200, easing = FastOutSlowInEasing),
                            label = "episodeProgress"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(rewindColors.borderSubtle)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = animatedProgress)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber))
                                    )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${movie.progressPercent.toInt()}% selesai",
                                color = rewindColors.textMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${movie.totalEpisodes!! - movie.watchedEpisodes} ep tersisa",
                                color = rewindColors.textMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            if (movie.review.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionLabel(text = "ULASANKU")
                    SectionCard(glowColor = GoldAmber.copy(alpha = 0.08f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 3.dp, height = 18.dp)
                                        .background(GoldAmber, RoundedCornerShape(2.dp))
                                )
                                Text(
                                    text = "Catatan Pribadi",
                                    color = GoldAmber,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = movie.review,
                                color = onBg,
                                fontSize = 14.sp,
                                lineHeight = 24.sp,
                                letterSpacing = 0.1.sp
                            )
                        }
                    }
                }
            }

            if (movie.isFavorite) {
                SectionCard(glowColor = GoldAmber.copy(alpha = 0.08f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldAmber.copy(alpha = 0.12f))
                                .border(BorderStroke(1.dp, GoldAmber.copy(alpha = 0.3f)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = GoldAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Judul Favorit",
                                color = GoldAmber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Rating di atas 8.0 — masuk daftar favoritmu",
                                color = rewindColors.textMuted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun SectionCard(
    glowColor: Color = Color.Transparent,
    content: @Composable () -> Unit
) {
    val rewindColors = LocalRewindColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(rewindColors.surfaceElevated)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            rewindColors.borderGold.copy(alpha = 0.45f),
                            rewindColors.borderSubtle.copy(alpha = 0.6f)
                        )
                    )
                ),
                RoundedCornerShape(16.dp)
            )
    ) {
        if (glowColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Brush.verticalGradient(listOf(glowColor, Color.Transparent)))
            )
        }
        Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            content()
        }
    }
}

@Composable
private fun MetaChip(label: String) {
    val rewindColors = LocalRewindColors.current
    Box(
        modifier = Modifier
            .background(rewindColors.surfaceElevated, RoundedCornerShape(20.dp))
            .border(BorderStroke(1.dp, rewindColors.borderGold.copy(alpha = 0.35f)), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = rewindColors.textSecondary,
            fontSize = 12.sp,
            letterSpacing = 0.2.sp
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    val rewindColors = LocalRewindColors.current
    Text(
        text = text,
        color = rewindColors.textMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp
    )
}
package com.example.rewind.presentation.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rewind.domain.model.Movie
import com.example.rewind.domain.model.WatchStatus
import com.example.rewind.presentation.theme.BackgroundDark
import com.example.rewind.presentation.theme.BorderGold
import com.example.rewind.presentation.theme.BorderSubtle
import com.example.rewind.presentation.theme.GoldAmber
import com.example.rewind.presentation.theme.GoldAmberDim
import com.example.rewind.presentation.theme.StatusDropped
import com.example.rewind.presentation.theme.StatusFinished
import com.example.rewind.presentation.theme.StatusOnHold
import com.example.rewind.presentation.theme.StatusWantToWatch
import com.example.rewind.presentation.theme.StatusWatching
import com.example.rewind.presentation.theme.SurfaceDark
import com.example.rewind.presentation.theme.SurfaceElevated
import com.example.rewind.presentation.theme.TextMuted
import com.example.rewind.presentation.theme.TextSecondary
import com.example.rewind.presentation.theme.TextWarm
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

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = SurfaceElevated,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Remove from Collection?",
                    color = TextWarm,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    "This title will be permanently deleted.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(TheaterRed.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, TheaterRed.copy(alpha = 0.4f)), RoundedCornerShape(10.dp))
                        .clickable {
                            viewModel.deleteMovie(movieId) { onNavigateBack() }
                            showDeleteDialog = false
                        }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text("Remove", color = TheaterRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceDark)
                        .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(10.dp))
                        .clickable { showDeleteDialog = false }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text("Cancel", color = TextMuted, fontSize = 13.sp)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GoldAmber, strokeWidth = 1.5.dp, modifier = Modifier.size(32.dp))
                }
            }
            is DetailUiState.NotFound -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🎞️", fontSize = 44.sp)
                        Text("Title not found", color = TextMuted, fontSize = 14.sp)
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
                                1f to SurfaceDark
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
                                0.5f to BackgroundDark.copy(alpha = 0.3f),
                                1f to BackgroundDark
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
                color = TextWarm.copy(alpha = 0.07f),
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
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BackgroundDark.copy(alpha = 0.55f))
                        .border(
                            BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.5f)),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", color = TextWarm, fontSize = 17.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tombol Edit — BARU
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldAmber.copy(alpha = 0.15f))
                            .border(BorderStroke(1.dp, GoldAmber.copy(alpha = 0.35f)), RoundedCornerShape(10.dp))
                            .clickable(onClick = onEdit)
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

                    // Tombol Remove — sama seperti sebelumnya
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(TheaterRed.copy(alpha = 0.18f))
                            .border(BorderStroke(1.dp, TheaterRed.copy(alpha = 0.35f)), RoundedCornerShape(10.dp))
                            .clickable(onClick = onDelete)
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
                color = TextWarm,
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
                        .background(SurfaceElevated, RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = movie.type.displayName,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .background(SurfaceElevated, RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = movie.genre.displayName,
                        color = TextSecondary,
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
                            BorderStroke(1.dp, Brush.horizontalGradient(listOf(BorderGold.copy(alpha = 0.5f), Color.Transparent))),
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
                                color = TextMuted,
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
                        .background(SurfaceElevated)
                        .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(12.dp))
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
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "${movie.watchedEpisodes} / ${movie.totalEpisodes}",
                                color = TextWarm,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { movie.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = GoldAmber,
                            trackColor = VelvetRed.copy(alpha = 0.2f)
                        )
                        Text(
                            text = "${movie.progressPercent.toInt()}% completed",
                            color = TextMuted,
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
                            listOf(Color.Transparent, BorderSubtle, BorderSubtle, Color.Transparent)
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
                            .background(SurfaceElevated)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    Brush.verticalGradient(listOf(BorderGold.copy(alpha = 0.3f), BorderSubtle))
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
                                color = TextWarm,
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
                            BorderStroke(1.dp, Brush.horizontalGradient(listOf(BorderGold.copy(alpha = 0.5f), Color.Transparent))),
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
                                color = TextMuted,
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
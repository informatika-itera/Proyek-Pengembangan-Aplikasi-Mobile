package com.example.rewind.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
fun HomeScreen(
    onAddClick: () -> Unit,
    onMovieClick: (Long) -> Unit,
    onAIClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf<WatchStatus?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .blur(100.dp)
                .background(
                    Brush.radialGradient(colors = listOf(TheaterRed.copy(alpha = 0.18f), Color.Transparent)),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = 20.dp)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(colors = listOf(GoldAmber.copy(alpha = 0.12f), Color.Transparent)),
                    shape = CircleShape
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader(onAIClick = onAIClick)
            FilterRow(selected = selectedFilter, onSelect = { selectedFilter = it })

            when (val state = uiState) {
                is HomeUiState.Loading -> LoadingState()
                is HomeUiState.Empty -> EmptyState()
                is HomeUiState.Success -> {
                    val displayed = if (selectedFilter != null) {
                        state.movies.filter { it.status == selectedFilter }
                    } else {
                        state.movies
                    }
                    if (displayed.isEmpty()) {
                        EmptyFilterState()
                    } else {
                        MovieList(movies = displayed, onMovieClick = onMovieClick)
                    }
                }
                is HomeUiState.Error -> ErrorState(message = state.message)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .blur(20.dp)
                    .background(
                        Brush.radialGradient(colors = listOf(GoldAmber.copy(alpha = 0.5f), Color.Transparent)),
                        shape = CircleShape
                    )
            )
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = GoldAmber,
                contentColor = BackgroundDark,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(12.dp)
            ) {
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Light)
            }
        }
    }
}

@Composable
private fun HomeHeader(onAIClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to SurfaceDark,
                        0.6f to SurfaceDark.copy(alpha = 0.8f),
                        1f to BackgroundDark
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 22.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BorderGold.copy(alpha = 0.4f),
                            GoldAmber.copy(alpha = 0.3f),
                            BorderGold.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "REWIND",
                    color = GoldAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 5.sp
                )
                Text(
                    text = "My Collection",
                    color = TextWarm,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            }

            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .blur(16.dp)
                        .background(
                            Brush.radialGradient(colors = listOf(GoldAmber.copy(alpha = 0.35f), Color.Transparent)),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(GoldAmberDim.copy(alpha = 0.2f), GoldAmber.copy(alpha = 0.1f))
                            )
                        )
                        .border(
                            BorderStroke(1.dp, BorderGold.copy(alpha = 0.5f)),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onAIClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🦉", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
private fun FilterRow(selected: WatchStatus?, onSelect: (WatchStatus?) -> Unit) {
    val filters = listOf(
        null to "All",
        WatchStatus.WATCHING to "Watching",
        WatchStatus.COMPLETED to "Completed",
        WatchStatus.PLAN_TO_WATCH to "Planned",
        WatchStatus.ON_HOLD to "On Hold",
        WatchStatus.DROPPED to "Dropped"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (status, label) ->
            val isSelected = selected == status
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(GoldAmberDim, GoldAmber)))
                        .clickable { onSelect(status) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = BackgroundDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(BorderStroke(1.dp, BorderSubtle), RoundedCornerShape(20.dp))
                        .clickable { onSelect(status) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieList(movies: List<Movie>, onMovieClick: (Long) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
        }
        item { Spacer(modifier = Modifier.height(96.dp)) }
    }
}

@Composable
private fun MovieCard(movie: Movie, onClick: () -> Unit) {
    val (statusColor, statusLabel) = when (movie.status) {
        WatchStatus.COMPLETED -> StatusFinished to "Completed"
        WatchStatus.WATCHING -> StatusWatching to "Watching"
        WatchStatus.PLAN_TO_WATCH -> StatusWantToWatch to "Planned"
        WatchStatus.ON_HOLD -> StatusOnHold to "On Hold"
        WatchStatus.DROPPED -> StatusDropped to "Dropped"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(0f to SurfaceElevated, 1f to SurfaceDark)
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            BorderSubtle.copy(alpha = 0.8f),
                            BorderSubtle.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = (-10).dp, y = (-10).dp)
                .blur(30.dp)
                .background(
                    Brush.radialGradient(colors = listOf(statusColor.copy(alpha = 0.15f), Color.Transparent)),
                    shape = CircleShape
                )
        )

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .blur(12.dp)
                        .background(
                            Brush.radialGradient(colors = listOf(TheaterRed.copy(alpha = 0.4f), Color.Transparent)),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            Brush.linearGradient(
                                colorStops = arrayOf(0f to VelvetRed, 1f to TheaterRed)
                            )
                        )
                        .border(
                            BorderStroke(
                                1.dp,
                                Brush.linearGradient(listOf(GoldAmber.copy(alpha = 0.3f), Color.Transparent))
                            ),
                            RoundedCornerShape(13.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = movie.title.take(1).uppercase(),
                        color = TextWarm,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = movie.title,
                    color = TextWarm,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.1.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(5.dp))
                            .border(BorderStroke(0.5.dp, statusColor.copy(alpha = 0.35f)), RoundedCornerShape(5.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        )
                    }

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

                Text(
                    text = "${movie.type.displayName}  ·  ${movie.genre.displayName}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.2.sp
                )

                if (movie.status == WatchStatus.WATCHING && movie.totalEpisodes != null) {
                    Spacer(modifier = Modifier.height(2.dp))
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
                                .clip(RoundedCornerShape(2.dp)),
                            color = GoldAmber,
                            trackColor = VelvetRed.copy(alpha = 0.25f)
                        )
                        Text(
                            text = "${movie.watchedEpisodes}/${movie.totalEpisodes}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))
            Text("›", color = TextMuted, fontSize = 20.sp, fontWeight = FontWeight.Light)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = GoldAmber,
            strokeWidth = 1.5.dp,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .blur(30.dp)
                        .background(
                            Brush.radialGradient(colors = listOf(GoldAmber.copy(alpha = 0.2f), Color.Transparent)),
                            shape = CircleShape
                        )
                )
                Text("🎞️", fontSize = 52.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your collection is empty",
                color = TextWarm,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp
            )
            Text(
                text = "Tap + to add your first title",
                color = TextMuted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun EmptyFilterState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🔍", fontSize = 36.sp)
            Text(
                text = "No titles in this category",
                color = TextMuted,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = TheaterRed, fontSize = 13.sp)
    }
}
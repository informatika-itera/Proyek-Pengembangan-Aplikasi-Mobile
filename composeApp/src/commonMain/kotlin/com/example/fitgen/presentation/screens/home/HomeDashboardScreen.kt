package com.example.fitgen.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.runtime.remember
import com.preat.peekaboo.image.picker.toImageBitmap
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import noteai.composeapp.generated.resources.Res
import noteai.composeapp.generated.resources.challenge_full_body
import noteai.composeapp.generated.resources.challenge_core
import noteai.composeapp.generated.resources.challenge_upper_body
import org.jetbrains.compose.resources.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.fitgen.data.remote.dto.ExerciseDto
import com.example.fitgen.domain.model.WorkoutLog
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun HomeDashboardScreen(
    onNavigateToWorkout        : () -> Unit        = {},
    onNavigateToNutrition      : () -> Unit      = {},
    onNavigateToDynamicWorkout : () -> Unit = {},
    onNavigateToExerciseDetail : (name: String, bodyPart: String, gifUrl: String, instructions: String) -> Unit = { _, _, _, _ -> },
    onNavigateToChallengeDetail: (String) -> Unit = {},
    viewModel: HomeDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ─── 1. Greeting Header ───────────────────────────────────────────
            item {
                GreetingHeader(
                    userName          = uiState.userName,
                    profileImageBytes = uiState.profileImageBytes,
                    activeStreak      = uiState.activeStreakDays
                )
            }

            // ─── 2. Popular Challenges ────────────────────────────────────────
            item {
                SectionTitle(
                    text = "Popular Challenges",
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
                )
            }
            item {
                if (uiState.isChallengesLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.5.dp
                        )
                    }
                } else if (uiState.challenges.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tidak dapat memuat challenge.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
                        pageCount = { Int.MAX_VALUE },
                        initialPage = Int.MAX_VALUE / 2
                    )
                    androidx.compose.foundation.pager.HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        pageSpacing = 14.dp,
                        modifier = Modifier.fillMaxWidth().height(220.dp)
                    ) { page ->
                        val actualPage = page % uiState.challenges.size
                        val exercise = uiState.challenges[actualPage]
                        ChallengeCard(
                            exercise = exercise,
                            onStartClick = {
                                onNavigateToChallengeDetail(exercise.id ?: "")
                            },
                            modifier = Modifier.fillMaxWidth().height(220.dp)
                        )
                    }
                }
            }

            // ─── 3. AI Workout Banner ─────────────────────────────────────────
            item {
                AiWorkoutBanner(
                    onClick = onNavigateToDynamicWorkout,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            // ─── 4. Classic Workouts ──────────────────────────────────────────
            item {
                SectionTitle(
                    text = "Classic Workouts",
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                )
            }
            item {
                ClassicWorkoutsRow(
                    selectedCategory = uiState.selectedClassicCategory,
                    onCategoryClick  = { label -> viewModel.selectClassicCategory(label) },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            // ─── 4b. Classic Workout List ─────
            item {
                AnimatedVisibility(
                    visible = uiState.selectedClassicCategory != null,
                    enter   = expandVertically() + fadeIn(),
                    exit    = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (uiState.isClassicLoading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(28.dp),
                                    color       = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.5.dp
                                )
                            }
                        } else {
                            uiState.classicWorkouts.forEach { exercise ->
                                ClassicWorkoutItem(
                                    exercise     = exercise,
                                    onStartClick = {
                                        val instructions = exercise.instructions?.joinToString("\n") ?: "Tidak ada instruksi tersedia."
                                        onNavigateToExerciseDetail(
                                            exercise.name ?: "Unknown",
                                            exercise.bodyPart ?: "General",
                                            exercise.gifUrl ?: "",
                                            instructions
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ─── 5. Latihan Terakhir ──────────────────────────────────────────
            item {
                LastWorkoutCard(
                    lastWorkout = uiState.lastWorkout,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// GREETING HEADER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun GreetingHeader(
    userName: String,
    profileImageBytes: ByteArray?,
    activeStreak: Int
) {
    val displayName = if (userName.isBlank()) "there" else userName.split(" ").first()
    val initial     = displayName.first().uppercaseChar().toString()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Avatar + teks sambutan
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar lingkaran dengan inisial
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF800000), Color(0xFFAF3A50))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageBytes != null) {
                    val imageBitmap = remember(profileImageBytes) {
                        profileImageBytes.toImageBitmap()
                    }
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Text(
                        text       = initial,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                }
            }
            Column {
                Text(
                    text  = "Halo, $displayName! 👋",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "Tetap semangat hari ini 💪",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        }

        // Streak badge
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Color(0xFFFF9800).copy(alpha = 0.13f),
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = null,
                    tint     = Color(0xFFE65100),
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text       = "$activeStreak",
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color(0xFFE65100)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f))
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// CHALLENGE CARD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ChallengeCard(
    exercise: ExerciseDto,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onStartClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val resource = when (exercise.gifUrl) {
                "challenge_full_body" -> Res.drawable.challenge_full_body
                "challenge_core" -> Res.drawable.challenge_core
                "challenge_upper_body" -> Res.drawable.challenge_upper_body
                else -> null
            }
            if (resource != null) {
                Image(
                    painter = painterResource(resource),
                    contentDescription = exercise.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
            }

            // Gradient overlay at the bottom for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                            startY = 250f
                        )
                    )
            )

            // Text at bottom left
            Text(
                text = exercise.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AI WORKOUT BANNER
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AiWorkoutBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color  = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape  = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = "Generate Workout dengan AI",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text  = "Buat program latihan personal sesuai tujuanmu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                    lineHeight = 16.sp
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CLASSIC WORKOUTS ROW
// ─────────────────────────────────────────────────────────────────────────────

private data class WorkoutCategory(val label: String)

@Composable
private fun ClassicWorkoutsRow(
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        WorkoutCategory("Push"),
        WorkoutCategory("Pull"),
        WorkoutCategory("Legs"),
        WorkoutCategory("Core"),
        WorkoutCategory("Cardio"),
        WorkoutCategory("Shoulders")
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(categories) { category ->
            ClassicCategoryChip(
                label      = category.label,
                isSelected = selectedCategory == category.label,
                onClick    = { onCategoryClick(category.label) }
            )
        }
    }
}

@Composable
private fun ClassicCategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor  = if (isSelected) MaterialTheme.colorScheme.primary
                   else MaterialTheme.colorScheme.surfaceVariant
    val txtColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                   else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick   = onClick,
        shape     = RoundedCornerShape(50.dp),
        color     = bgColor,
        tonalElevation = if (isSelected) 0.dp else 1.dp,
        modifier  = Modifier.width(90.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text      = label,
                style     = MaterialTheme.typography.labelMedium,
                color     = txtColor,
                maxLines  = 1
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CLASSIC WORKOUT ITEM (horizontal card)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ClassicWorkoutItem(
    exercise: ExerciseDto,
    onStartClick: () -> Unit
) {
    val displayName = exercise.name
        ?.split(" ")
        ?.joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
        ?: "Exercise"

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail gambar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val coverImage = exercise.imageUrls.firstOrNull() ?: exercise.gifUrl
                if (!coverImage.isNullOrBlank()) {
                    coil3.compose.SubcomposeAsyncImage(
                        model              = coverImage,
                        contentDescription = displayName,
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize(),
                        loading = {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        },
                        error = {
                            Icon(
                                imageVector        = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier           = Modifier.size(28.dp)
                            )
                        }
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }

            // Nama + badge kategori
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = displayName,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis
                )
                if (!exercise.bodyPart.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text       = exercise.bodyPart,
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color      = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Tombol Mulai
            Button(
                onClick        = onStartClick,
                shape          = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                colors         = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector        = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier           = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text       = "Mulai",
                    style      = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LAST WORKOUT CARD
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LastWorkoutCard(
    lastWorkout: WorkoutLog?,
    modifier: Modifier = Modifier
) {
    val marun       = Color(0xFF7B1E2E)
    val marunLight  = Color(0xFFAF3A50)
    val maroonBrush = Brush.horizontalGradient(
        colors = listOf(marun, marunLight)
    )

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = maroonBrush)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon + judul
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint               = Color.White.copy(alpha = 0.90f),
                        modifier           = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = "Latihan Terakhir",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }

                // Divider tipis
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.30f))
                )

                if (lastWorkout == null) {
                    Text(
                        text      = "Belum ada latihan yang tercatat.\nYuk mulai hari ini! 💪",
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = Color.White.copy(alpha = 0.85f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 22.sp
                    )
                } else {
                    val volumeFormatted = (lastWorkout.totalVolume * 10).roundToInt() / 10.0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LastWorkoutStat(label = "Gerakan", value = "${lastWorkout.jumlahGerakan}")
                        LastWorkoutStat(label = "Set",     value = "${lastWorkout.totalSets}")
                        LastWorkoutStat(label = "Volume",  value = "${volumeFormatted} kg")
                    }
                    if (lastWorkout.catatan.isNotBlank()) {
                        Text(
                            text      = lastWorkout.catatan,
                            style     = MaterialTheme.typography.bodySmall,
                            color     = Color.White.copy(alpha = 0.75f),
                            maxLines  = 2,
                            overflow  = TextOverflow.Ellipsis,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LastWorkoutStat(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text       = value,
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color      = Color.White
        )
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.70f)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// UTILITIES
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Accent bar marun
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF800000), Color(0xFFAF3A50))
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Text(
            text       = text,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface
        )
    }
}
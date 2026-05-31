package com.example.travelplanner.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.travelplanner.core.service.CityImageService
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.presentation.screens.planner.PantaiAnimatedScene
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// ── Vibe localizer (used across screens) ─────────────────────────────
fun String.localizeVibe(): String {
    val s = com.example.travelplanner.core.util.LocalStrings
    // Map stored Indonesian vibe keys → current language label
    return this
}

// ── Gradient fallback (deterministic, no network) ────────────────────
fun getDestinationGradient(destination: String): Pair<Color, Color> {
    return when (destination.hashCode() % 5) {
        0    -> Pair(Color(0xFF0096C7), Color(0xFF48CAE4))
        1    -> Pair(Color(0xFFE07A5F), Color(0xFFF2CC8F))
        2    -> Pair(Color(0xFF2D6A4F), Color(0xFF52B788))
        3    -> Pair(Color(0xFF6A0572), Color(0xFFAB47BC))
        else -> Pair(Color(0xFF1B3A5C), Color(0xFF3D7A6F))
    }
}


// ══════════════════════════════════════════════════════════════════════
//  DATA MODEL
// ══════════════════════════════════════════════════════════════════════

data class DummyTrip(
    val id: String,
    val destination: String,
    val country: String,
    val dateRange: String,
    val vibe: String,
    val photoUrl: String,
    val gradientStart: Color,
    val gradientEnd: Color
)

// ══════════════════════════════════════════════════════════════════════
//  SCREEN
// ══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGenerateTrip: () -> Unit,
    onNavigateToTripDetail:   (String) -> Unit,
    onNavigateToMyTrips:      () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    LaunchedEffect(Unit) { viewModel.loadRecentTrips() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            val infiniteTransition = rememberInfiniteTransition(label = "FABPulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.98f,
                targetValue = 1.04f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "fabScale"
            )
            val borderGlow by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 0.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "borderGlow"
            )

            Surface(
                onClick = onNavigateToGenerateTrip,
                modifier = Modifier
                    .scale(scale)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = borderGlow)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = borderGlow),
                                MaterialTheme.colorScheme.primary.copy(alpha = borderGlow)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = s.planButton.uppercase(),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            item { HeroSection() }

            item {
                Button(
                    onClick = onNavigateToGenerateTrip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (s.seeAll == "See All") "Create New Travel Plan" else "Buat Rencana Liburan",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 20.dp, bottom = 12.dp),
                    verticalAlignment   = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(s.recentTripsTitle, style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                        Text(s.recentTripsSubtitle(uiState.recentTrips.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (uiState.recentTrips.isNotEmpty()) {
                        TextButton(onClick = onNavigateToMyTrips) {
                            Text(s.seeAll, style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (uiState.recentTrips.isEmpty()) {
                item { EmptyState() }
            } else {
                items(uiState.recentTrips.take(3)) { trip ->
                    val (gradStart, gradEnd) = remember(trip.destination) {
                        getDestinationGradient(trip.destination)
                    }
                    // ── Gambar kota diambil dari ViewModel state (sudah terisi instan dari cache/loremflickr)
                    val photoUrl = uiState.cityImages[trip.destination] ?: ""

                    val visualTrip = remember(trip, photoUrl) {
                        DummyTrip(
                            id            = trip.id,
                            destination   = trip.destination,
                            country       = "Indonesia",
                            dateRange     = "${trip.startDate} (${trip.duration.substringAfter("|")})",
                            vibe          = trip.vibe,
                            photoUrl      = photoUrl,
                            gradientStart = gradStart,
                            gradientEnd   = gradEnd
                        )
                    }
                    TripCard(
                        trip    = visualTrip,
                        onClick = { onNavigateToTripDetail(trip.id) },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                    )
                }
            }

            item {
                InsightBanner(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
            }
        }
    }
}

@Composable
fun HeroSection() {
    val s = LocalStrings.current
    
    val currentHour = remember {
        try {
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .hour
        } catch (e: Exception) {
            12
        }
    }

    val isEn = s.seeAll == "See All"

    val greeting = when {
        currentHour in 5..11 -> if (isEn) "Good Morning," else "Selamat Pagi,"
        currentHour in 12..14 -> if (isEn) "Good Afternoon," else "Selamat Siang,"
        currentHour in 15..17 -> if (isEn) "Good Afternoon," else "Selamat Sore,"
        else -> if (isEn) "Good Evening," else "Selamat Malam,"
    }

    Box(modifier = Modifier.fillMaxWidth().height(270.dp)) {
        PantaiAnimatedScene(modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(
                Color(0xFF1B3A5C).copy(alpha = 0.40f),
                Color(0xFF1B3A5C).copy(alpha = 0.20f),
                Color(0xFF1B3A5C).copy(alpha = 0.70f)
            ))
        ))
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "$greeting\nTraveler.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = s.heroSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: DummyTrip, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalPlatformContext.current
    Box(modifier = modifier.fillMaxWidth().height(155.dp)
        .shadow(6.dp, RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp))
        .clickable { onClick() }
    ) {
        // Gradient fallback
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.horizontalGradient(listOf(trip.gradientStart, trip.gradientEnd))
        ))
        // Foto kota — langsung dari Wikipedia via CityImageService, fallback loremflickr
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(trip.photoUrl)
                .crossfade(true)
                .build(),
            contentDescription = trip.destination,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(Modifier.fillMaxSize().background(
            Brush.horizontalGradient(listOf(
                Color.Black.copy(alpha = 0.72f), Color.Black.copy(alpha = 0.15f)
            ))
        ))
        Column(modifier = Modifier.align(Alignment.CenterStart)
            .padding(horizontal = 20.dp, vertical = 18.dp)) {
            Surface(shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.88f),
                modifier = Modifier.padding(bottom = 8.dp)) {
                Text(trip.vibe.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary, letterSpacing = 1.sp)
            }
            Text(trip.destination, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold, color = Color.White, letterSpacing = (-0.3).sp)
            Text(trip.country, style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f))
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null,
                    tint = Color.White.copy(alpha = 0.72f), modifier = Modifier.size(13.dp))
                Text(trip.dateRange, style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f))
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = Color.White.copy(alpha = 0.55f),
            modifier = Modifier.align(Alignment.CenterEnd).padding(18.dp).size(22.dp))
    }
}

@Composable
fun EmptyState() {
    val s = LocalStrings.current
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.ExploreOff, contentDescription = null, modifier = Modifier.size(52.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        Text(s.emptyStateTitle, style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        Text(s.emptyStateBody, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun InsightBanner(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer) {
        Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(Icons.Default.Lightbulb, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Column {
                Text(s.tipTitle, style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.height(2.dp))
                Text(s.tipBody, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.80f))
            }
        }
    }
}

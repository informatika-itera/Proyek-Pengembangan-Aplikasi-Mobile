package com.example.travelplanner.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.presentation.screens.planner.PantaiAnimatedScene
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun String.localizeVibe(): String {
    val s = LocalStrings.current
    return when (this.lowercase()) {
        "alam", "nature" -> s.vibeNature
        "pantai", "beach" -> s.vibeBeach
        "kuliner", "culinary" -> s.vibeCulinary
        "sejarah", "history" -> s.vibeHistory
        "santai", "relaxed", "relax" -> s.vibeRelax
        "petualangan", "adventure" -> s.vibeAdventure
        "formal", "bisnis", "business" -> s.vibeFormal
        "romantis", "romantic", "love" -> s.vibeRomantic
        else -> this
    }
}

fun getDestinationGradient(destination: String): Pair<Color, Color> =
    when (destination.hashCode() % 5) {
        0    -> Pair(Color(0xFF0096C7), Color(0xFF48CAE4))
        1    -> Pair(Color(0xFFE07A5F), Color(0xFFF2CC8F))
        2    -> Pair(Color(0xFF2D6A4F), Color(0xFF52B788))
        3    -> Pair(Color(0xFF6A0572), Color(0xFFAB47BC))
        else -> Pair(Color(0xFF1B3A5C), Color(0xFF3D7A6F))
    }

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

    // ── Tidak ada floatingActionButton ────────────────────────────────
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {

            // ── 1. HERO ───────────────────────────────────────────────
            item { HeroSection() }

            // ── 2. TOMBOL RENCANAKAN — tepat di bawah hero ────────────
            item {
                Button(
                    onClick = onNavigateToGenerateTrip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector   = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint          = MaterialTheme.colorScheme.secondary,
                        modifier      = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = if (s.isEnglish) "Create New Travel Plan"
                                     else "Buat Rencana Liburan",
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // ── 3. SECTION HEADER ─────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 16.dp),
                    verticalAlignment     = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(s.recentTripsTitle,
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onBackground)
                        Text(s.recentTripsSubtitle(uiState.recentTrips.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (uiState.recentTrips.isNotEmpty()) {
                        TextButton(onClick = onNavigateToMyTrips) {
                            Text(s.seeAll,
                                style      = MaterialTheme.typography.labelMedium,
                                color      = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // ── 4. TRIP CARDS ─────────────────────────────────────────
            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 48.dp), Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (uiState.errorMessage != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = uiState.errorMessage ?: s.failedLoad,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadRecentTrips() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (s.isEnglish) "Retry" else "Coba Lagi")
                        }
                    }
                }
            } else if (uiState.recentTrips.isEmpty()) {
                item { EmptyState() }
            } else {
                items(uiState.recentTrips.take(3)) { trip ->
                    val (gradStart, gradEnd) = remember(trip.destination) {
                        getDestinationGradient(trip.destination)
                    }
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
                        trip     = visualTrip,
                        onClick  = { onNavigateToTripDetail(trip.id) },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }

            // ── 5. INSIGHT BANNER ─────────────────────────────────────
            item {
                InsightBanner(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  HERO SECTION — bersih, elegan, vibes liburan & santai
//  Tidak ada pill berkedip. Teks langsung di atas animasi pantai.
// ══════════════════════════════════════════════════════════════════════

@Composable
fun HeroSection() {
    val s = LocalStrings.current
    val isEn = s.isEnglish

    val currentHour = remember {
        try { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour }
        catch (_: Exception) { 12 }
    }
    val greeting = when (currentHour) {
        in 5..11  -> if (isEn) "Good Morning," else "Selamat Pagi,"
        in 12..14 -> if (isEn) "Good Afternoon," else "Selamat Siang,"
        in 15..17 -> if (isEn) "Good Afternoon," else "Selamat Sore,"
        else       -> if (isEn) "Good Evening," else "Selamat Malam,"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(272.dp)
    ) {
        // Animasi pantai — full bleed, terlihat penuh
        PantaiAnimatedScene(modifier = Modifier.fillMaxSize())

        // Gradient: transparan di atas → gelap natural di bawah
        // Tidak ada box atau container apapun yang menutup animasi
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.40f to Color.Transparent,
                            0.72f to Color(0xFF091523).copy(alpha = 0.55f),
                            1.00f to Color(0xFF091523).copy(alpha = 0.90f)
                        )
                    )
                )
        )

        // Teks — duduk di atas gradient tanpa container
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
                .padding(bottom = 26.dp)
        ) {
            // Label tipis tanpa background
            Text(
                text       = "AI TRAVEL PLANNER",
                style      = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.secondary.copy(alpha = 0.90f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Salam + nama
            Text(
                text       = greeting,
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Normal,
                color      = Color.White.copy(alpha = 0.82f),
                letterSpacing = 0.sp
            )
            Text(
                text       = "Traveler.",
                style      = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color      = Color.White,
                letterSpacing = (-1).sp,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle — satu baris tipis, santai
            Text(
                text       = s.heroSubtitle,
                style      = MaterialTheme.typography.bodyMedium,
                color      = Color.White.copy(alpha = 0.58f),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  TRIP CARD
// ══════════════════════════════════════════════════════════════════════

@Composable
fun TripCard(trip: DummyTrip, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalPlatformContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(155.dp)
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.horizontalGradient(listOf(trip.gradientStart, trip.gradientEnd))
        ))
        AsyncImage(
            model = ImageRequest.Builder(context).data(trip.photoUrl).crossfade(true).build(),
            contentDescription = trip.destination,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(Modifier.fillMaxSize().background(
            Brush.horizontalGradient(listOf(Color.Black.copy(alpha = 0.72f), Color.Black.copy(alpha = 0.15f)))
        ))
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.88f),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(trip.vibe.localizeVibe().uppercase(),
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

// ══════════════════════════════════════════════════════════════════════
//  EMPTY STATE
// ══════════════════════════════════════════════════════════════════════

@Composable
fun EmptyState() {
    val s = LocalStrings.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

// ══════════════════════════════════════════════════════════════════════
//  INSIGHT BANNER
// ══════════════════════════════════════════════════════════════════════

@Composable
fun InsightBanner(modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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

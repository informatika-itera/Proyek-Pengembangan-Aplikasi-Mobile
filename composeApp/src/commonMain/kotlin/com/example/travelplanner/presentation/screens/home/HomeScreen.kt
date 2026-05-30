package com.example.travelplanner.presentation.screens.home

import androidx.compose.animation.*
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
import com.example.travelplanner.core.service.CityImageService
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.presentation.screens.planner.PantaiAnimatedScene
import org.koin.compose.viewmodel.koinViewModel

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

// ── Immediate loremflickr placeholder (synchronous, no suspend needed) ─
fun getDestinationPhotoUrl(destination: String): String {
    val q = destination.lowercase().trim()
    val seed = (q.hashCode().and(0x7FFFFFFF) % 500) + 1
    val keywords = when {
        q.contains("bali")                            -> "bali,temple,rice,terrace"
        q.contains("yogya") || q.contains("jogja")   -> "borobudur,yogyakarta,java"
        q.contains("jakarta")                         -> "jakarta,monas,city"
        q.contains("bandung")                         -> "bandung,tangkuban,java"
        q.contains("lombok")                          -> "lombok,rinjani,beach"
        q.contains("raja ampat")                      -> "raja,ampat,island,sea"
        q.contains("labuan bajo") || q.contains("komodo") -> "komodo,dragon,island"
        q.contains("bromo")                           -> "mount,bromo,volcano,sunrise"
        q.contains("toba")                            -> "lake,toba,sumatra"
        q.contains("manado") || q.contains("bunaken") -> "bunaken,coral,reef"
        q.contains("toraja")                          -> "toraja,tongkonan,sulawesi"
        q.contains("singapore") || q.contains("singapura") -> "singapore,marina,bay"
        q.contains("kuala lumpur")                    -> "kuala,lumpur,petronas"
        q.contains("bangkok")                         -> "bangkok,temple,thailand"
        q.contains("tokyo")                           -> "tokyo,shibuya,japan"
        q.contains("paris")                           -> "paris,eiffel,france"
        q.contains("london")                          -> "london,tower,bridge"
        q.contains("dubai")                           -> "dubai,burj,khalifa"
        q.contains("sydney")                          -> "sydney,opera,house"
        q.contains("new york")                        -> "new,york,times,square"
        else -> "${q.replace(" ", ",").take(30)},travel,city,landmark"
    }
    return "https://loremflickr.com/800/500/$keywords/all?lock=$seed"
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToGenerateTrip,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor   = MaterialTheme.colorScheme.onPrimary,
                shape          = RoundedCornerShape(14.dp),
                modifier       = Modifier.shadow(10.dp, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment   = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(s.planButton, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp,
                        style = MaterialTheme.typography.labelLarge)
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
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 12.dp),
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
                    // ── Prioritas foto: Wikipedia (via CityImageService) → loremflickr placeholder
                    val photoUrl = uiState.cityImages[trip.destination]
                        ?: getDestinationPhotoUrl(trip.destination)

                    val visualTrip = remember(trip, photoUrl) {
                        DummyTrip(
                            id            = trip.id,
                            destination   = trip.destination,
                            country       = "Indonesia",
                            dateRange     = "${trip.startDate} (${trip.duration})",
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
    Box(modifier = Modifier.fillMaxWidth().height(230.dp)) {
        PantaiAnimatedScene(modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(
                Color(0xFF1B3A5C).copy(alpha = 0.55f),
                Color(0xFF1B3A5C).copy(alpha = 0.28f),
                Color(0xFF1B3A5C).copy(alpha = 0.68f)
            ))
        ))
        Column(modifier = Modifier.align(Alignment.BottomStart)
            .padding(horizontal = 24.dp, vertical = 24.dp)) {
            Surface(shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.88f),
                modifier = Modifier.padding(bottom = 8.dp)) {
                Text("AI TRAVEL PLANNER",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary, letterSpacing = 1.5.sp)
            }
            Text(s.heroTitle, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold, color = Color.White, lineHeight = 36.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(s.heroSubtitle, style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.80f))
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

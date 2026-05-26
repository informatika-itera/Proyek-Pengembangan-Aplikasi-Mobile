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
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.presentation.screens.planner.PantaiAnimatedScene
import org.koin.compose.viewmodel.koinViewModel

// Elegant visual card model mapped dynamically from DB
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToGenerateTrip: () -> Unit,
    onNavigateToTripDetail: (String) -> Unit,
    onNavigateToMyTrips: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Trigger db refresh whenever home screen comes back into focus
    LaunchedEffect(Unit) {
        viewModel.loadRecentTrips()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToGenerateTrip,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.shadow(10.dp, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        "Rencanakan",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp,
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Perjalanan Terbaru",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "${uiState.recentTrips.size} rencana liburan aktif",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (uiState.recentTrips.isNotEmpty()) {
                        TextButton(onClick = onNavigateToMyTrips) {
                            Text(
                                "Lihat Semua",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (uiState.recentTrips.isEmpty()) {
                item { EmptyState() }
            } else {
                // Map only the 3 most recent trips in dashboard
                items(uiState.recentTrips.take(3)) { trip ->
                    // Map real Trip object to high fidelity DummyTrip layout dynamically
                    val seed = trip.destination.hashCode()
                    val visualTrip = remember(trip) {
                        val startCol = when (seed % 3) {
                            0 -> Color(0xFF0096C7)
                            1 -> Color(0xFFE07A5F)
                            else -> Color(0xFF2D6A4F)
                        }
                        val endCol = when (seed % 3) {
                            0 -> Color(0xFF48CAE4)
                            1 -> Color(0xFFF2CC8F)
                            else -> Color(0xFF52B788)
                        }
                        DummyTrip(
                            id = trip.id,
                            destination = trip.destination,
                            country = "Indonesia",
                            dateRange = "${trip.startDate} (${trip.duration})",
                            vibe = trip.vibe,
                            photoUrl = when (seed % 3) {
                                0 -> "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=600&q=80"
                                1 -> "https://images.unsplash.com/photo-1596402184320-417e7178b2cd?w=600&q=80"
                                else -> "https://images.unsplash.com/photo-1516690561799-46d8f74f9abf?w=600&q=80"
                            },
                            gradientStart = startCol,
                            gradientEnd = endCol
                        )
                    }

                    TripCard(
                        trip = visualTrip,
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

// ══════════════════════════════════════════════════════════════════════
//  SUB-COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        PantaiAnimatedScene(modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1B3A5C).copy(alpha = 0.55f),
                            Color(0xFF1B3A5C).copy(alpha = 0.28f),
                            Color(0xFF1B3A5C).copy(alpha = 0.68f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.88f),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    "AI TRAVEL PLANNER",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                    letterSpacing = 1.5.sp
                )
            }
            Text(
                "Selamat Datang,\nTraveler.",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Ke mana kita pergi selanjutnya?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.80f)
            )
        }
    }
}

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(trip.gradientStart, trip.gradientEnd)
                    )
                )
        )

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(trip.photoUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Foto ${trip.destination}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.72f),
                            Color.Black.copy(alpha = 0.15f)
                        )
                    )
                )
        )

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
                Text(
                    trip.vibe.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                    letterSpacing = 1.sp
                )
            }
            Text(
                trip.destination,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                letterSpacing = (-0.3).sp
            )
            Text(
                trip.country,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.72f),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    trip.dateRange,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.55f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(18.dp)
                .size(22.dp)
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.ExploreOff, contentDescription = null,
            modifier = Modifier.size(52.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Belum Ada Rencana Perjalanan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Mulai rencanakan perjalanan impianmu\nbersama AI kami.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun InsightBanner(modifier: Modifier = Modifier) {
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
            Icon(
                Icons.Default.Lightbulb, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    "Tips Perjalanan Cerdas",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Isi detail perjalanan selengkap mungkin agar AI dapat menyusun itinerary yang lebih personal.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.80f)
                )
            }
        }
    }
}

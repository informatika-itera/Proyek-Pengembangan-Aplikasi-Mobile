package com.example.travelplanner.presentation.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.presentation.screens.planner.PantaiAnimatedScene

// ══════════════════════════════════════════════════════════════════════
//  DATA MODEL
// ══════════════════════════════════════════════════════════════════════

data class ItineraryDay(val dayLabel: String, val date: String, val items: List<ItineraryItem>)
data class ItineraryItem(
    val time: String,
    val activity: String,
    val location: String,
    val category: String,
    val estimatedCost: String = ""
)

private val categoryColor = mapOf(
    "Kuliner"     to Color(0xFFE63946),
    "Wisata"      to Color(0xFF0096C7),
    "Akomodasi"   to Color(0xFF3D7A6F),
    "Transportasi" to Color(0xFFB8893A),
    "Hiburan"     to Color(0xFF6A0572)
)

// ══════════════════════════════════════════════════════════════════════
//  SCREEN
// ══════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripResultScreen(
    tripId: String,
    onNavigateBack: () -> Unit,
    onNavigateToExpenseTracker: () -> Unit
) {
    val destination = "Bali"; val country = "Indonesia"
    val vibe = "Santai & Pantai"; val dateRange = "10–14 Jul 2026"

    val itineraryDays = listOf(
        ItineraryDay("Hari 1", "Selasa, 10 Juli", listOf(
            ItineraryItem("09.00", "Tiba di Ngurah Rai, transfer ke hotel", "Bandara Ngurah Rai", "Transportasi", "Rp 150k"),
            ItineraryItem("12.00", "Makan siang nasi campur Bali", "Warung Babi Guling Ibu Oka", "Kuliner", "Rp 80k"),
            ItineraryItem("15.00", "Check-in & istirahat", "Villa Seminyak", "Akomodasi"),
            ItineraryItem("18.00", "Sunset di Tanah Lot", "Pura Tanah Lot", "Wisata", "Rp 60k")
        )),
        ItineraryDay("Hari 2", "Rabu, 11 Juli", listOf(
            ItineraryItem("08.00", "Sarapan di tepi sawah", "Ubud Rice Terrace", "Kuliner", "Rp 95k"),
            ItineraryItem("10.00", "Mengunjungi Pura Tirta Empul", "Tampaksiring, Gianyar", "Wisata", "Rp 50k"),
            ItineraryItem("14.00", "Belanja di Pasar Seni Ubud", "Jl. Raya Ubud", "Hiburan"),
            ItineraryItem("20.00", "Dinner & Kecak Dance", "GWK Cultural Park", "Hiburan", "Rp 120k")
        ))
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Itinerary",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToExpenseTracker,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet,
                        contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Catat Biaya",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── HERO CARD ────────────────────────────────────────────
            item {
                HeroTripCard(destination, country, vibe, dateRange,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
            }

            // ── STATS ROW ────────────────────────────────────────────
            item {
                StatsRow(modifier = Modifier.padding(horizontal = 24.dp))
            }

            // ── HARI PER HARI ────────────────────────────────────────
            itineraryDays.forEach { day ->
                item {
                    DayHeader(day.dayLabel, day.date,
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp))
                }
                itemsIndexed(day.items) { idx, item ->
                    TimelineItem(
                        item = item,
                        isLast = idx == day.items.lastIndex,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  HERO CARD — animated ocean backdrop
// ══════════════════════════════════════════════════════════════════════

@Composable
fun HeroTripCard(
    destination: String,
    country: String,
    vibe: String,
    dateRange: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(8.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
    ) {
        PantaiAnimatedScene(Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF1B3A5C).copy(alpha = 0.45f), Color(0xFF1B3A5C).copy(alpha = 0.80f))
            )
        ))
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.90f)
            ) {
                Text(vibe.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                    letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("$destination, $country",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null,
                    tint = Color.White.copy(alpha = 0.75f), modifier = Modifier.size(13.dp))
                Text(dateRange, style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  STATS ROW
// ══════════════════════════════════════════════════════════════════════

@Composable
fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf(
            Triple(Icons.Default.NightsStay, "4", "Malam"),
            Triple(Icons.Default.Place, "8", "Destinasi"),
            Triple(Icons.Default.Restaurant, "6", "Kuliner")
        ).forEach { (icon, val_, label) ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.height(4.dp))
                    Text(val_, style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(label, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  DAY HEADER
// ══════════════════════════════════════════════════════════════════════

@Composable
fun DayHeader(dayLabel: String, date: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(dayLabel,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    letterSpacing = 0.3.sp)
            }
            Text(date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(
            modifier = Modifier.weight(1f).padding(start = 12.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.20f)
        )
    }
}

// ══════════════════════════════════════════════════════════════════════
//  TIMELINE ITEM
// ══════════════════════════════════════════════════════════════════════

@Composable
fun TimelineItem(item: ItineraryItem, isLast: Boolean, modifier: Modifier = Modifier) {
    val dotColor = categoryColor[item.category] ?: MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 3.dp)
    ) {
        // Timeline left column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(52.dp).padding(top = 4.dp)
        ) {
            // Time label
            Text(item.time,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            // Dot
            Box(
                modifier = Modifier.size(10.dp).clip(CircleShape)
                    .background(dotColor)
            )
            // Connector line
            if (!isLast) {
                Box(
                    modifier = Modifier.width(1.5.dp).height(40.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(dotColor.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // Card content
        Card(
            modifier = Modifier.weight(1f)
                .padding(bottom = if (isLast) 0.dp else 6.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.activity,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(11.dp))
                        Text(item.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = dotColor.copy(alpha = 0.12f)
                    ) {
                        Text(item.category,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = dotColor,
                            fontWeight = FontWeight.SemiBold)
                    }
                    if (item.estimatedCost.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(item.estimatedCost,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

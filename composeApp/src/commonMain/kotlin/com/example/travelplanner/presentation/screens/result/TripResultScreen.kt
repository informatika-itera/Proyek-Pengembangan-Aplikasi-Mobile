package com.example.travelplanner.presentation.screens.result

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import coil3.compose.AsyncImagePainter
import com.example.travelplanner.presentation.screens.home.localizeVibe
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.domain.model.ItineraryItem
import com.example.travelplanner.presentation.screens.planner.PantaiAnimatedScene
import com.example.travelplanner.presentation.screens.expenses.formatRupiah
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.daysUntil
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripResultScreen(
    tripId: String,
    onNavigateBack: () -> Unit,
    onNavigateToExpenseTracker: () -> Unit,
    onNavigateToTripSummary: (String) -> Unit,
    viewModel: TripResultViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    LaunchedEffect(tripId) { viewModel.loadTripDetails(tripId) }
    
    LaunchedEffect(uiState.trip, s.isEnglish) {
        val trip = uiState.trip
        if (trip != null) {
            viewModel.checkAndTranslateItinerary(trip, s.isEnglish)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(s.itineraryTitle, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
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
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(s.recordCost, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.trip == null -> {
                Box(Modifier.fillMaxSize().padding(padding).padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(
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
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadTripDetails(tripId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (s.isEnglish) "Retry" else "Coba Lagi")
                        }
                    }
                }
            }
            else -> {
                val trip = uiState.trip!!
                val departureCity = remember(trip.duration) {
                    if (trip.duration.contains("|")) trip.duration.substringBefore("|") else "Jakarta"
                }
                val cleanDuration = remember(trip.duration) {
                    trip.duration.substringAfter("|")
                }
                // Stagger visibility: each item becomes visible with a delay
                val itemVisibility = remember(trip.itineraryItems.size) {
                    List(trip.itineraryItems.size) { false }.toMutableStateList()
                }
                // Trigger sequential reveal after the screen loads
                LaunchedEffect(trip.id) {
                    trip.itineraryItems.forEachIndexed { index, _ ->
                        delay(120L * index) // 120 ms between each item
                        if (index < itemVisibility.size) itemVisibility[index] = true
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    // ── HERO ─────────────────────────────────────────────
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -40 }
                        ) {
                        HeroTripCard(
                                destination = trip.destination,
                                vibe        = trip.vibe,
                                dateRange   = "${trip.startDate} ($cleanDuration)",
                                photoUrl    = uiState.cityPhotoUrl,
                                modifier    = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                            )
                        }
                    }

                    // ── STATS ─────────────────────────────────────────────
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(500, delayMillis = 150)) +
                                    slideInVertically(tween(500, delayMillis = 150)) { -30 }
                        ) {
                            StatsRow(
                                duration       = cleanDuration,
                                activityCount  = trip.itineraryItems.size,
                                totalExpenses  = uiState.totalExpenses,
                                modifier       = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }

                    // ── TRANSIT CARD (Design B) ───────────────────────────
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(450, delayMillis = 200)) +
                                    slideInVertically(tween(450, delayMillis = 200)) { -25 }
                        ) {
                            TransitHeroCard(
                                departureCity = departureCity,
                                destination = trip.destination,
                                startDate = trip.startDate,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // ── LODGING CARD ──────────────────────────────────────
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(450, delayMillis = 250)) +
                                    slideInVertically(tween(450, delayMillis = 250)) { -25 }
                        ) {
                            LodgingHeroCard(
                                destination = trip.destination,
                                startDate = trip.startDate,
                                endDate = trip.endDate,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // ── TIMELINE TITLE ────────────────────────────────────
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(400, delayMillis = 300)) +
                                    slideInVertically(tween(400, delayMillis = 300)) { -20 }
                        ) {
                            Text(
                                text      = s.dailyPlan,
                                style     = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier  = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                                color     = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // ── TIMELINE ITEMS with stagger ───────────────────────
                    itemsIndexed(trip.itineraryItems, key = { idx, item -> "${item.time}_${item.activity}_$idx" }) { idx, item ->
                        val visible = idx < itemVisibility.size && itemVisibility[idx]
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(350)) +
                                    slideInVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness    = Spring.StiffnessLow
                                        )
                                    ) { 60 }  // slides UP from 60px below
                        ) {
                            TimelineItem(
                                item     = item,
                                isLast   = idx == trip.itineraryItems.lastIndex,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { onNavigateToTripSummary(trip.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (s.isEnglish) "Finish Trip" else "Selesaikan Perjalanan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  SUB-COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun HeroTripCard(
    destination: String,
    vibe: String,
    dateRange: String,
    photoUrl: String?,
    modifier: Modifier = Modifier
) {
    // Track loading state to show a shimmer/placeholder while image loads
    var imageState by remember(photoUrl) { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(8.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
    ) {
        // Always try to show image; if photoUrl is null show animated fallback
        if (photoUrl != null) {
            // Shimmer placeholder while loading
            if (imageState !is AsyncImagePainter.State.Success) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        )
                )
            }
            AsyncImage(
                model = photoUrl,
                contentDescription = destination,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onState = { imageState = it }
            )
        } else {
            PantaiAnimatedScene(Modifier.fillMaxSize())
        }
        // Dark gradient overlay so text is always readable
        Box(Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color.Transparent, Color(0xFF0D1B2A).copy(alpha = 0.85f))
            )
        ))
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.90f)) {
                Text(vibe.localizeVibe().uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(destination, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null,
                    tint = Color.White.copy(alpha = 0.75f), modifier = Modifier.size(13.dp))
                Text(dateRange, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
fun StatsRow(duration: String, activityCount: Int, totalExpenses: Double, modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant) {
            Column(modifier = Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.NightsStay, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.height(4.dp))
                Text(duration, style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                Text(s.statTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant) {
            Column(modifier = Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Place, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.height(4.dp))
                Text("$activityCount", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(s.statDestination, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Surface(modifier = Modifier.weight(1.2f), shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant) {
            Column(modifier = Modifier.padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Wallet, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.height(4.dp))
                Text(formatRupiah(totalExpenses), style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center)
                Text(s.statCost, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun TimelineItem(item: ItineraryItem, isLast: Boolean, modifier: Modifier = Modifier) {
    val s = LocalStrings.current
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    val annotatedText = buildAnnotatedString {
        val activityText = if (s.isEnglish && item.activityEn.isNotBlank()) item.activityEn else item.activity
        val place = if (s.isEnglish && item.placeNameEn.isNotBlank()) item.placeNameEn else item.placeName
        val url = item.mapsUrl
        if (place.isNotBlank() && url.isNotBlank()) {
            val startIndex = activityText.indexOf(place, ignoreCase = true)
            if (startIndex != -1) {
                val endIndex = startIndex + place.length
                append(activityText.substring(0, startIndex))
                pushStringAnnotation(tag = "URL", annotation = url)
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold))
                append(activityText.substring(startIndex, endIndex))
                pop(); pop()
                append(activityText.substring(endIndex))
            } else { append(activityText) }
        } else { append(activityText) }
    }

    Row(modifier = modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(56.dp).padding(top = 4.dp)) {
            Text(item.time, style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
            Spacer(Modifier.height(6.dp))
            Box(modifier = Modifier.size(28.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center) {
                Text(item.icon, fontSize = 14.sp)
            }
            if (!isLast) {
                Box(modifier = Modifier.width(1.5.dp).height(48.dp).background(
                    Brush.verticalGradient(listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), Color.Transparent
                    ))
                ))
            }
        }
        Spacer(Modifier.width(12.dp))
        Card(modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 10.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    ClickableText(
                        text = annotatedText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                .firstOrNull()?.let { uriHandler.openUri(it.item) }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    if (item.priceRange.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Icon(Icons.Default.LocalOffer, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(10.dp))
                                Text(item.priceRange, style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }
                }
                if (item.mapsUrl.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            .clickable { uriHandler.openUri(item.mapsUrl) }
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Icon(Icons.Default.Map, contentDescription = s.openMaps,
                            tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(12.dp))
                        Text(s.openMaps, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  TRANSIT & TICKETING COMPONENT (Design B & C)
// ══════════════════════════════════════════════════════════════════════

@Composable
fun TransitHeroCard(
    departureCity: String,
    destination: String,
    startDate: String,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    var showSheet by remember { mutableStateOf(false) }

    val isFlightRecommended = remember(destination, departureCity) {
        val q = destination.lowercase().trim()
        val dep = departureCity.lowercase().trim()
        
        val isLandRoute = (dep.contains("jakarta") && q.contains("bandung")) ||
                (dep.contains("bandung") && q.contains("jakarta")) ||
                (dep.contains("surabaya") && q.contains("malang")) ||
                (dep.contains("solo") && q.contains("yogya")) ||
                (dep.contains("yogyakarta") && q.contains("solo")) ||
                (dep.contains("semarang") && q.contains("yogya"))
        
        !isLandRoute && (
            q.contains("bali") || q.contains("lombok") || q.contains("raja ampat") ||
            q.contains("singapore") || q.contains("tokyo") || q.contains("paris") ||
            q.contains("london") || q.contains("new york") || q.contains("phuket") ||
            q.contains("bangkok") || q.contains("seoul") || q.contains("dubai") ||
            q.contains("manado") || q.contains("raja ampat") || q.contains("labuan bajo") ||
            q.contains("komodo") || q.contains("medan") || q.contains("padang") ||
            q.contains("palembang") || q.contains("balikpapan") || q.contains("makassar")
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFlightRecommended) Icons.Default.FlightTakeoff else Icons.Default.Train,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isFlightRecommended) s.flightRecommendation else s.trainBusRecommendation,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${departureCity.trim()} ➔ ${destination.trim()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${s.departPrefix}: $startDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showSheet = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(s.searchTicket, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSheet) {
        TransitBookingBottomSheet(
            departureCity = departureCity,
            destination = destination,
            startDate = startDate,
            isFlight = isFlightRecommended,
            onDismiss = { showSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransitBookingBottomSheet(
    departureCity: String,
    destination: String,
    startDate: String,
    isFlight: Boolean,
    onDismiss: () -> Unit
) {
    val s = LocalStrings.current
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    
    val originEncoded = departureCity.trim().replace(" ", "%20")
    val destEncoded = destination.trim().replace(" ", "%20")
    
    val parsedDate = remember(startDate) { parseToLocalDate(startDate) }
    val depDateTiket = remember(parsedDate) { formatToYyyyMmDd(parsedDate) }
    val depDateTraveloka = remember(parsedDate) { formatToDdMmYyyy(parsedDate) }
    
    val originIata = remember(departureCity) { getAirportCode(departureCity) }
    val destIata = remember(destination) { getAirportCode(destination) }
    
    val tiketPesawatUrl = "https://www.tiket.com/pesawat/search?d=$originIata&a=$destIata&dDate=$depDateTiket&adult=1"
    val tiketKeretaUrl = "https://www.tiket.com/kereta-api/search?d=$originEncoded&a=$destEncoded&dDate=$depDateTiket"
    
    val travelokaPesawatUrl = "https://www.traveloka.com/id-id/flight/full-search?ap=$originIata.$destIata&dt=$depDateTraveloka.NA&ps=1.0.0&sc=ECONOMY"
    val travelokaBusUrl = "https://www.traveloka.com/id-id/tiket-bus-travel"
    
    val bookingPesawatUrl = "https://flights.booking.com/flights/$originIata-$destIata/?type=oneWay&adults=1&cabinClass=ECONOMY&depart=$depDateTiket"
    val redbusUrl = "https://www.redbus.id"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = s.compareAndBook,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isFlight) s.compareFlightTitle(departureCity, destination) else s.compareTrainTitle(departureCity, destination),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (isFlight) {
                OtaItem(
                    name = "Traveloka Flights",
                    subtitle = s.biggestPlatformID,
                    icon = "✈️",
                    onClick = {
                        uriHandler.openUri(travelokaPesawatUrl)
                        onDismiss()
                    }
                )
                OtaItem(
                    name = "Tiket.com Pesawat",
                    subtitle = s.lotsOfPromos,
                    icon = "🎫",
                    onClick = {
                        uriHandler.openUri(tiketPesawatUrl)
                        onDismiss()
                    }
                )
                OtaItem(
                    name = "Booking.com Flights",
                    subtitle = s.goodForInternational,
                    icon = "✈️",
                    onClick = {
                        uriHandler.openUri(bookingPesawatUrl)
                        onDismiss()
                    }
                )
            } else {
                OtaItem(
                    name = "Tiket.com Kereta Api",
                    subtitle = s.instantKAITicket,
                    icon = "🚆",
                    onClick = {
                        uriHandler.openUri(tiketKeretaUrl)
                        onDismiss()
                    }
                )
                OtaItem(
                    name = "Traveloka Kereta & Bus",
                    subtitle = s.completeTransport,
                    icon = "🚌",
                    onClick = {
                        uriHandler.openUri(travelokaBusUrl)
                        onDismiss()
                    }
                )
                OtaItem(
                    name = "RedBus Indonesia",
                    subtitle = s.bestBusTravel,
                    icon = "🚍",
                    onClick = {
                        uriHandler.openUri(redbusUrl)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun OtaItem(
    name: String,
    subtitle: String,
    icon: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 18.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun LodgingHeroCard(
    destination: String,
    startDate: String,
    endDate: String,
    modifier: Modifier = Modifier
) {
    val s = LocalStrings.current
    var showSheet by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Hotel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = s.lodgingRecommendation,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = destination.trim(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${s.checkInPrefix}: $startDate • ${s.checkOutPrefix}: $endDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { showSheet = true },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(s.searchHotel, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSheet) {
        LodgingBookingBottomSheet(
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            onDismiss = { showSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LodgingBookingBottomSheet(
    destination: String,
    startDate: String,
    endDate: String,
    onDismiss: () -> Unit
) {
    val s = LocalStrings.current
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    val cityEncoded = destination.trim().replace(" ", "%20")

    val checkinLocalDate = remember(startDate) { parseToLocalDate(startDate) }
    val checkoutLocalDate = remember(endDate) { parseToLocalDate(endDate) }
    val nights = remember(checkinLocalDate, checkoutLocalDate) {
        checkinLocalDate.daysUntil(checkoutLocalDate).coerceAtLeast(1)
    }

    val checkinYyyyMmDd = remember(checkinLocalDate) { formatToYyyyMmDd(checkinLocalDate) }
    val checkoutYyyyMmDd = remember(checkoutLocalDate) { formatToYyyyMmDd(checkoutLocalDate) }

    val checkinDdMmYyyy = remember(checkinLocalDate) { formatToDdMmYyyy(checkinLocalDate) }
    val checkoutDdMmYyyy = remember(checkoutLocalDate) { formatToDdMmYyyy(checkoutLocalDate) }

    val travelokaHotelUrl = "https://www.traveloka.com/id-id/hotel/search?spec=$checkinDdMmYyyy.$checkoutDdMmYyyy.$nights.1.HOTEL_GEOLOCATION..$cityEncoded..1"
    val tiketHotelUrl = "https://www.tiket.com/hotel/search?q=$cityEncoded&checkin=$checkinYyyyMmDd&checkout=$checkoutYyyyMmDd&room=1&adult=1"
    val bookingHotelUrl = "https://www.booking.com/searchresults.html?ss=$cityEncoded&checkin=$checkinYyyyMmDd&checkout=$checkoutYyyyMmDd&group_adults=1&no_rooms=1"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = s.compareAndBookHotel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = s.compareLodgingTitle(destination),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            OtaItem(
                name = "Traveloka Hotels",
                subtitle = s.biggestPlatformID,
                icon = "🏨",
                onClick = {
                    uriHandler.openUri(travelokaHotelUrl)
                    onDismiss()
                }
            )
            OtaItem(
                name = "Tiket.com Hotels",
                subtitle = s.lotsOfPromos,
                icon = "🏨",
                onClick = {
                    uriHandler.openUri(tiketHotelUrl)
                    onDismiss()
                }
            )
            OtaItem(
                name = "Booking.com Hotels",
                subtitle = s.bookingPartnerHotel,
                icon = "🏨",
                onClick = {
                    uriHandler.openUri(bookingHotelUrl)
                    onDismiss()
                }
            )
        }
    }
}

private fun getAirportCode(city: String): String {
    val q = city.lowercase().trim()
    return when {
        q.contains("jakarta") || q.contains("tangerang") -> "CGK"
        q.contains("bali") || q.contains("denpasar") -> "DPS"
        q.contains("yogya") || q.contains("jogja") || q.contains("yogyakarta") -> "YIA"
        q.contains("surabaya") -> "SUB"
        q.contains("bandung") -> "BDO"
        q.contains("medan") || q.contains("kualanamu") -> "KNO"
        q.contains("makassar") || q.contains("ujung pandang") -> "UPG"
        q.contains("lombok") || q.contains("praya") -> "LOP"
        q.contains("balikpapan") -> "BPN"
        q.contains("palembang") -> "PLM"
        q.contains("padang") -> "PDG"
        q.contains("manado") -> "MDC"
        q.contains("labuan bajo") -> "LBJ"
        q.contains("semarang") -> "SRG"
        q.contains("solo") || q.contains("surakarta") -> "SOC"
        q.contains("malang") -> "MLG"
        q.contains("singapore") || q.contains("singapura") -> "SIN"
        q.contains("kuala lumpur") -> "KUL"
        q.contains("phuket") -> "HKT"
        q.contains("bangkok") -> "BKK"
        q.contains("tokyo") -> "HND"
        q.contains("seoul") -> "ICN"
        q.contains("london") -> "LHR"
        q.contains("paris") -> "CDG"
        q.contains("new york") -> "JFK"
        q.contains("dubai") -> "DXB"
        else -> {
            val clean = q.filter { it.isLetter() }.trim()
            if (clean.length >= 3) clean.take(3).uppercase() else "CGK"
        }
    }
}

private fun parseToLocalDate(dateStr: String): kotlinx.datetime.LocalDate {
    val parts = dateStr.trim().split(" ", "-", "/")
    if (parts.size < 3) {
        return kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
    }
    val day = parts[0].toIntOrNull() ?: 1
    val monthStr = parts[1].lowercase()
    val year = parts[2].toIntOrNull() ?: 2026
    
    val monthNumber = when (monthStr) {
        "jan", "januari", "january" -> 1
        "feb", "februari", "february" -> 2
        "mar", "maret", "march" -> 3
        "apr", "april" -> 4
        "mei", "may" -> 5
        "jun", "juni", "june" -> 6
        "jul", "juli", "july" -> 7
        "agt", "aug", "agustus", "august" -> 8
        "sep", "september" -> 9
        "okt", "oct", "oktober", "october" -> 10
        "nov", "november" -> 11
        "des", "dec", "desember", "december" -> 12
        else -> monthStr.toIntOrNull() ?: 1
    }
    return try {
        kotlinx.datetime.LocalDate(year, monthNumber, day)
    } catch (e: Exception) {
        kotlinx.datetime.Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
    }
}

private fun formatToYyyyMmDd(date: kotlinx.datetime.LocalDate): String {
    val y = date.year
    val m = date.monthNumber.toString().padStart(2, '0')
    val d = date.dayOfMonth.toString().padStart(2, '0')
    return "$y-$m-$d"
}

private fun formatToDdMmYyyy(date: kotlinx.datetime.LocalDate): String {
    val y = date.year
    val m = date.monthNumber.toString().padStart(2, '0')
    val d = date.dayOfMonth.toString().padStart(2, '0')
    return "$d-$m-$y"
}

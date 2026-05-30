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
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripResultScreen(
    tripId: String,
    onNavigateBack: () -> Unit,
    onNavigateToExpenseTracker: () -> Unit,
    viewModel: TripResultViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current

    LaunchedEffect(tripId) { viewModel.loadTripDetails(tripId) }

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
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.errorMessage ?: s.failedLoad,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                val trip = uiState.trip!!
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
                                dateRange   = "${trip.startDate} (${trip.duration})",
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
                                duration       = trip.duration,
                                activityCount  = trip.itineraryItems.size,
                                totalExpenses  = uiState.totalExpenses,
                                modifier       = Modifier.padding(horizontal = 24.dp)
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
                    itemsIndexed(trip.itineraryItems) { idx, item ->
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
        val activityText = item.activity
        val place = item.placeName
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

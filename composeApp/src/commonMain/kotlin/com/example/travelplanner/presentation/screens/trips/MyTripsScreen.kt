package com.example.travelplanner.presentation.screens.trips

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.core.util.LocalStrings
import com.example.travelplanner.presentation.screens.home.TripCard
import com.example.travelplanner.presentation.screens.home.DummyTrip
import com.example.travelplanner.presentation.screens.home.getDestinationGradient

import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(
    onNavigateToTripDetail: (String) -> Unit,
    viewModel: MyTripsViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val s = LocalStrings.current
    var searchQuery by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadTrips() }

    val filteredTrips = remember(uiState.trips, searchQuery) {
        if (searchQuery.isBlank()) uiState.trips
        else uiState.trips.filter {
            it.destination.contains(searchQuery, ignoreCase = true) ||
            it.vibe.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(s.myTripsTitle, fontWeight = FontWeight.SemiBold, letterSpacing = 0.3.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(modifier = modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(s.searchPlaceholder, style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = s.cancel)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                )
            )

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadTrips() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (s.isEnglish) "Retry" else "Coba Lagi")
                        }
                    }
                }
            } else if (filteredTrips.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isBlank()) s.noTripsTitle else s.noSearchResultTitle,
                            style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isBlank()) s.noTripsBody else s.noSearchResultBody,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredTrips, key = { it.id }) { trip ->
                        var showConfirmDelete by rememberSaveable(trip.id) { mutableStateOf(false) }
                        val (gradStart, gradEnd) = remember(trip.destination) { getDestinationGradient(trip.destination) }
                        // Gambar kota diambil dari ViewModel state (sudah terisi instan dari cache/loremflickr)
                        val photoUrl: String = uiState.cityImages[trip.destination] ?: ""
                        val visualTrip = remember(trip, photoUrl) {
                            DummyTrip(
                                id = trip.id,
                                destination = trip.destination,
                                country = "Indonesia",
                                dateRange = "${trip.startDate} (${trip.duration.substringAfter("|")})",
                                vibe = trip.vibe,
                                photoUrl = photoUrl,
                                gradientStart = gradStart,
                                gradientEnd = gradEnd
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                            Box {
                                TripCard(trip = visualTrip, onClick = { onNavigateToTripDetail(trip.id) })
                                IconButton(
                                    onClick = { showConfirmDelete = true },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                                        .size(36.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = s.delete,
                                        tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            AnimatedVisibility(visible = showConfirmDelete,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()) {
                                Surface(modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.errorContainer) {
                                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(s.deleteThisTrip, style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onErrorContainer)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            TextButton(onClick = { showConfirmDelete = false },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f))) {
                                                Text(s.cancel)
                                            }
                                            Button(
                                                onClick = { viewModel.deleteTrip(trip.id); showConfirmDelete = false },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.error,
                                                    contentColor = MaterialTheme.colorScheme.onError),
                                                shape = RoundedCornerShape(8.dp)) { Text(s.deleteConfirm) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

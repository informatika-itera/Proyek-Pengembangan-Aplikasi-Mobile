package com.example.travelplanner.presentation.screens.trips

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travelplanner.domain.model.Trip
import com.example.travelplanner.presentation.screens.home.TripCard
import com.example.travelplanner.presentation.screens.home.DummyTrip
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(
    onNavigateToTripDetail: (String) -> Unit,
    viewModel: MyTripsViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Filter trips dynamically in UI based on search query
    val filteredTrips = remember(uiState.trips, searchQuery) {
        if (searchQuery.isBlank()) {
            uiState.trips
        } else {
            uiState.trips.filter {
                it.destination.contains(searchQuery, ignoreCase = true) ||
                it.vibe.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Semua Perjalanan",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Cari kota tujuan atau vibe...", style = MaterialTheme.typography.bodyMedium)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
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
            } else if (filteredTrips.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "Belum Ada Perjalanan Tersimpan" else "Perjalanan Tidak Ditemukan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "Mulailah membuat rencana baru dengan AI dari halaman Utama." else "Coba cari dengan kata kunci lain.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredTrips, key = { it.id }) { trip ->
                        var showConfirmDelete by remember { mutableStateOf(false) }

                        // Map real Trip domain object to visual DummyTrip layout helper
                        val visualTrip = remember(trip) {
                            val seed = trip.destination.hashCode()
                            // Deterministic color assignment based on name hash code
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

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Box {
                                TripCard(
                                    trip = visualTrip,
                                    onClick = { onNavigateToTripDetail(trip.id) }
                                )

                                // Premium Delete Button in top right
                                IconButton(
                                    onClick = { showConfirmDelete = true },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                                        .size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Dynamic sliding confirmation layout
                            AnimatedVisibility(
                                visible = showConfirmDelete,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "Hapus perjalanan ini?",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            TextButton(
                                                onClick = { showConfirmDelete = false },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                                                )
                                            ) {
                                                Text("Batal")
                                            }
                                            Button(
                                                onClick = {
                                                    viewModel.deleteTrip(trip.id)
                                                    showConfirmDelete = false
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.error,
                                                    contentColor = MaterialTheme.colorScheme.onError
                                                ),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text("Hapus")
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
}

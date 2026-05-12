package com.example.pantaujompo.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pantaujompo.presentation.navigation.Route
import com.example.pantaujompo.presentation.theme.*
import com.example.pantaujompo.domain.model.FitnessLog
import com.example.pantaujompo.domain.model.LogType
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.koin.compose.viewmodel.koinViewModel // Pastikan libs setara Koin-ViewModels-Compose
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem


@Composable
fun BerandaDashboardScreen(
    onNavigateToPemindai: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // MAIN tema gelap & neon visual Part 1: Bar status konsistensi
    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("BERANDA DASHBOARD", color = NeonGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = BackgroundDark,
        bottomBar = {
            // MAINtema gelap & neon visual Part 5: Bottom Navigation
            BottomNavBar(navController = null) // NavHost setup di AppNavHost
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Card Konsistensi Status
            ConsistencyStatusCard(status = uiState.consistencyStatus, targetPercent = uiState.weeklyGoalStatus)

            Spacer(modifier = Modifier.height(24.dp))

            // MAIN tema gelap & neon visual Part 2: Pelacakan Satelit (GPS) section
            Text("PELACAKAN SATELIT (GPS)", color = GreyMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            SatelliteTrackingCard(
                trackingState = uiState.satelliteTrackingState,
                onStart = { viewModel.startTracking() },
                onStop = { viewModel.stopTracking() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // MAIN tema gelap & neon visual Part 3: Terakhir Sinkronisasi list
            Text("CATATAN SINKRONISASI TERAKHIR", color = GreyMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(uiState.lastActivities) { activity ->
                    LastSyncActivityCard(activity = activity)
                }
            }
        }
    }
}

// MAIN Komponen untuk kartu status konsistensi
@Composable
fun ConsistencyStatusCard(status: String, targetPercent: Float) {
    Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Status Konsistensi: Pejuang Sehat", color = WhiteLight)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Target Aktivitas Mingguan", color = WhiteLight)
                Text("${(targetPercent * 100).toInt()}%", color = NeonGreen)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(progress = targetPercent, color = NeonGreen, trackColor = Color.DarkGray, modifier = Modifier.fillMaxWidth())
        }
    }
}

// MAIN Komponen untuk kartu pelacakan satelit (GPS)
@Composable
fun SatelliteTrackingCard(
    trackingState: SatelliteTrackingState,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Placeholder untuk Native MapView Actual Declaration di AndroidMain
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
            ) {
                // native MapView call placeholder, e.g. ExpectMapView(...)
                Text("Memuat Koordinat Peta...", color = GreyMuted, modifier = Modifier.align(Alignment.Center))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // MAINtema gelap & neon visual Part 4: Tombol MULAI PELACAKAN AKTIVITAS
            Button(
                onClick = { if (trackingState is SatelliteTrackingState.Idle) onStart() else onStop() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (trackingState is SatelliteTrackingState.Tracking) Color.Red else NeonGreen,
                    contentColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("▶", modifier = Modifier.padding(end = 8.dp)) // Placeholder icon
                    Text(
                        text = when (trackingState) {
                            SatelliteTrackingState.Idle -> "MULAI PELACAKAN AKTIVITAS"
                            SatelliteTrackingState.RequestingLocation -> "MEMINTA LOKASI..."
                            SatelliteTrackingState.Tracking -> "BERHENTI & SIMPAN AKTIVITAS"
                            is SatelliteTrackingState.Error -> "GPS ERROR"
                        }
                    )
                }
            }
        }
    }
}

// MAIN Komponen kartu aktivitas terakhir
@Composable
fun LastSyncActivityCard(activity: FitnessLog) {
    Card(colors = CardDefaults.cardColors(containerColor = DarkSurface), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Icon Placeholder
            Text("🏃", fontSize = 24.sp, modifier = Modifier.padding(end = 16.dp))
            Column {
                Text(activity.title, color = WhiteLight, fontWeight = FontWeight.Bold)
                // Format metrik bahasa Indonesia sesuai UI
                val metrikString = activity.details.entries.joinToString(" • ") { "${it.value}" }
                Text("$metrikString • Hari Ini, 07:00", color = GreyMuted, fontSize = 12.sp)
            }
        }
    }
}

// MAIN Komponen Bottom Nav Bar - setup di AppNavHost
@Composable
fun BottomNavBar(navController: NavHostController?) {
    // Implementasi proper Material3 NavigationBar dengan icon dan route di Sprint 2
    // val navBackStackEntry by navController?.currentBackStackEntryAsState()
    // val currentDestination = navBackStackEntry?.destination
    NavigationBar(containerColor = BackgroundDark, contentColor = GreyMuted) {
        NavigationBarItem(selected = true, onClick = {}, icon = { Text("🏠") }, label = { Text("Beranda") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Text("📸") }, label = { Text("Pemindai") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Text("📝") }, label = { Text("Riwayat") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Text("📰") }, label = { Text("Artikel") })
        NavigationBarItem(selected = false, onClick = {}, icon = { Text("👤") }, label = { Text("Profil") })
    }
}
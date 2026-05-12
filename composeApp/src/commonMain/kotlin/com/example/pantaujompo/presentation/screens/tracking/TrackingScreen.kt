package com.example.pantaujompo.presentation.screens.tracking

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

val DarkBackground = Color(0xFF121212)
val CardSurface = Color(0xFF1E1E1E)
val NeonGreen = Color(0xFF39FF14)

@SuppressLint("MissingPermission")
@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel,
    onNavigateBack: () -> Unit
) {
    val trackingState by viewModel.state.collectAsState()
    var isTracking by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }

    // Launcher Izin GPS
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasPermission = isGranted }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Default kamera map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-5.3582, 105.3148), 15f) // Koordinat default
    }

    // Handle State Success (Kembali ke Home setelah save)
    LaunchedEffect(trackingState) {
        if (trackingState is TrackingState.Success) {
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBackground).padding(16.dp)
    ) {
        Text("Live Tracking", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // MAPS BOX
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(24.dp)).background(CardSurface)
        ) {
            if (hasPermission) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true, mapType = MapType.NORMAL),
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                )
            } else {
                Text("Menunggu Izin GPS...", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // INFO BOX
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            InfoCard(title = "Jarak", value = if (isTracking) "2.5 km" else "0.0 km")
            InfoCard(title = "Waktu", value = if (isTracking) "15:00" else "00:00")
            InfoCard(title = "Kalori", value = if (isTracking) "210 kcal" else "0 kcal")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ACTION BUTTON
        Button(
            onClick = {
                if (isTracking) {
                    // Berhenti dan trigger CREATE CRUD ke database (simulasi data statis untuk contoh)
                    viewModel.saveActivity(distance = 2.5, duration = 15, calories = 210)
                } else {
                    isTracking = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTracking) Color.Red else NeonGreen,
                contentColor = if (isTracking) Color.White else Color.Black
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (trackingState is TrackingState.Saving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(if (isTracking) "BERHENTI & SIMPAN" else "MULAI LARI", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RowScope.InfoCard(title: String, value: String) {
    Column(
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp).clip(RoundedCornerShape(16.dp)).background(CardSurface).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = Color.Gray, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
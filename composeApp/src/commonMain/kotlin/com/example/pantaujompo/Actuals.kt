package com.example.pantaujompo

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// MAIN platform-specific actual declaration untuk MapView menggunakan Google Maps native composable
// panggil ini di SatelliteTrackingCard di 'commonMain'
// expect fun ExpectMapView(...)

@Composable
fun ExpectMapView() {
    // GPS native actual declaration Part 2: Native Map View
    // setup koordinat ITERA Lampung
    val lampungItera = LatLng(-5.3582, 105.3148)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(lampungItera, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // native marker actual declaration
        Marker(position = lampungItera, title = "Memuat Koordinat Peta...")
    }
}

// MAIN platform-specific actual declaration untuk LocationProvider native Android
// expect class PlatformLocationProvider(...)

actual class PlatformLocationProvider(context: Context) {
    // GPS native actual declaration Part 1: Native Location Provider
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    actual fun requestSingleUpdate(callback: (Double, Double) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback(location.latitude, location.longitude)
            } else {
                // native location request actual declaration jika lastLocation null
            }
        }
    }
}
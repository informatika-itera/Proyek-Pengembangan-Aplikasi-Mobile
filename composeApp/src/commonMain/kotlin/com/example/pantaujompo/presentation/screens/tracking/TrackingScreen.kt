package com.example.pantaujompo.presentation.screens.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.MapTileIndex
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.example.pantaujompo.core.util.AppStrings
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.presentation.theme.MeshBackground
import com.example.pantaujompo.presentation.theme.glassCard
import com.example.pantaujompo.domain.TrackingManager
import com.example.pantaujompo.core.location.LocationServiceController
import org.koin.compose.koinInject

@SuppressLint("MissingPermission")
@Composable
fun TrackingScreen(
    jenis: String = "Lari",
    onNavigateBack: () -> Unit,
    onNavigateToSave: (jenis: String, jarak: Double, kalori: Int, durasiMenit: Int, pace: String) -> Unit
) {
    val context = LocalContext.current
    val userPreferences: UserPreferences = koinInject()
    val language = userPreferences.language.collectAsState("id").value
    val userWeight = userPreferences.userWeight.collectAsState(60f).value
    fun str(key: String) = AppStrings.get(key, language)

    val jenisColor = when (jenis.lowercase()) {
        "jalan" -> Color(0xFF00BCD4)
        "sepeda" -> Color(0xFFFF9100)
        else -> Color(0xFF00E676)
    }

    val isDark by userPreferences.isDarkMode.collectAsState(true)
    val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textPrimary = if (isDark) Color.White else Color.Black
    val textSecondary = if (isDark) Color.LightGray else Color.Gray

    // Inisialisasi konfigurasi OSMDroid sekali saja
    remember {
        val sharedPref = context.getSharedPreferences("osmdroid_pref", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, sharedPref)
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().cacheMapTileCount = 100
        Configuration.getInstance().cacheMapTileOvershoot = 100
    }

    val mapView = remember { MapView(context) }
    var userMarker by remember { mutableStateOf<Marker?>(null) }
    var isMapCenteredOnUser by remember { mutableStateOf(false) }
    var useSatellite by remember { mutableStateOf(false) }

    val standardTileSource = remember {
        object : OnlineTileSourceBase("GoogleMaps", 1, 20, 256, ".png", arrayOf("https://mt0.google.com/vt/lyrs=m&hl=id&z=", "https://mt1.google.com/vt/lyrs=m&hl=id&z=", "https://mt2.google.com/vt/lyrs=m&hl=id&z=", "https://mt3.google.com/vt/lyrs=m&hl=id&z=")) {
            override fun getTileURLString(pMapTileIndex: Long): String = baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex)
        }
    }
    val satelliteTileSource = remember {
        object : OnlineTileSourceBase("GoogleSatellite", 1, 20, 256, ".png", arrayOf("https://mt0.google.com/vt/lyrs=s&hl=id&z=", "https://mt1.google.com/vt/lyrs=s&hl=id&z=", "https://mt2.google.com/vt/lyrs=s&hl=id&z=", "https://mt3.google.com/vt/lyrs=s&hl=id&z=")) {
            override fun getTileURLString(pMapTileIndex: Long): String = baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex)
        }
    }

    // State tracking aktivitas dari Singleton
    val seconds by TrackingManager.seconds.collectAsState()
    val isRunning by TrackingManager.isTracking.collectAsState()
    val hasStarted by TrackingManager.hasStarted.collectAsState()
    val totalDistanceInMeters by TrackingManager.totalDistanceMeters.collectAsState()
    val routePoints by TrackingManager.routePoints.collectAsState()
    
    var hasLocationPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, 
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) 
    }
    var isGpsEnabled by remember { mutableStateOf(false) }
    var lastKnownGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var isMapInitialized by remember { mutableStateOf(false) }
    
    // Check GPS Status periodically
    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        while (true) {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || 
                           locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            kotlinx.coroutines.delay(2000)
        }
    }

    BackHandler(enabled = hasStarted) {
        onNavigateBack() // Minimize ke home, jangan stop
    }

    // Menggunakan Google Maps Standard & Satellite
    // Variabel mapLayerIndex tidak lagi dibutuhkan

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Update local last geo point from tracking manager for button centering
    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            lastKnownGeoPoint = routePoints.last()
        }
    }

    // Timer Coroutine (DIHAPUS KARENA SUDAH DI-HANDLE OLEH LocationTrackingService)
    // Supaya waktu tidak berjalan 2x lipat lebih cepat.

    // Location Updates Coroutine
    LaunchedEffect(hasLocationPermission, isGpsEnabled) {
        if (hasLocationPermission && isGpsEnabled) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        lastKnownGeoPoint = GeoPoint(loc.latitude, loc.longitude)
                    }
                }
            } catch (e: SecurityException) { }

            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateDistanceMeters(1f)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    for (location in result.locations) {
                        val geoPoint = GeoPoint(location.latitude, location.longitude)
                        lastKnownGeoPoint = geoPoint
                        
                        if (isRunning) { 
                            val currentRoute = TrackingManager.routePoints.value
                            val distanceDelta = if (currentRoute.isNotEmpty()) {
                                val lastLocation = Location("").apply {
                                    latitude = currentRoute.last().latitude
                                    longitude = currentRoute.last().longitude
                                }
                                location.distanceTo(lastLocation).toDouble()
                            } else 0.0

                            TrackingManager.addLocationPoint(geoPoint, distanceDelta)
                        }
                    }
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                // Ignore
            }

            suspendCancellableCoroutine<Unit> { cont ->
                cont.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }
    }

    // Kalkulasi statistik real-time
    val distanceInKm = totalDistanceInMeters / 1000.0
    val durationHours = seconds / 3600.0
    val calculatedKcal = if (distanceInKm > 0.005) {
        val multiplier = when (jenis.lowercase()) {
            "jalan" -> 0.73
            "sepeda" -> 0.50
            else -> 1.036
        }
        (multiplier * userWeight * distanceInKm).toInt()
    } else 0
    val formatTime = String.format(Locale.US, "%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60)
    val paceDouble = if (distanceInKm > 0) (seconds / 60.0) / distanceInKm else 0.0
    val paceMin = paceDouble.toInt()
    val paceSec = ((paceDouble - paceMin) * 60).toInt()
    val formatPace = if (distanceInKm > 0.01) String.format(Locale.US, "%d'%02d\"", paceMin, paceSec) else "0'00\""

    // Ikon aktivitas berdasarkan jenis (Material Icons / Stickman style)
    val jenisIconVector = when (jenis.lowercase()) {
        "jalan" -> Icons.Default.DirectionsWalk
        "sepeda" -> Icons.Default.DirectionsBike
        else -> Icons.Default.DirectionsRun
    }

    MeshBackground(modifier = Modifier.fillMaxSize()) {

        // ======== PETA UTAMA ========
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 140.dp) // Leave just enough space for the bottom panel slightly overlapping
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
        ) {
            AndroidView(
                factory = {
                    mapView.apply {
                        setMultiTouchControls(true)
                        setBuiltInZoomControls(false)
                        isTilesScaledToDpi = false
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { map ->
                    // Setel ke Google Maps (Standard atau Satelit)
                    map.setTileSource(if (useSatellite) satelliteTileSource else standardTileSource)

                    if (!isMapInitialized) {
                        map.controller.setZoom(18.0)
                        if (lastKnownGeoPoint != null) {
                            map.controller.setCenter(lastKnownGeoPoint)
                            isMapInitialized = true // HANYA inisialisasi kalau sudah dapat titik
                        } else {
                            map.controller.setCenter(GeoPoint(-6.2088, 106.8456)) // Default to Jakarta sementara
                        }
                    }

                    // Hapus overlay lama kecuali tile
                    map.overlays.removeAll { it !is org.osmdroid.views.overlay.TilesOverlay }

                    // Marker posisi user (tampilkan selalu jika koordinat GPS diketahui, baik sebelum maupun saat jalan)
                    if (lastKnownGeoPoint != null) {
                        if (userMarker == null) {
                            userMarker = Marker(map)
                            userMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            userMarker?.icon = com.example.pantaujompo.core.util.MapUtils.createCustomMarkerDrawable(context)
                        }
                        userMarker?.position = lastKnownGeoPoint
                        map.overlays.add(userMarker!!)

                        // Auto-center ke posisi user (baik saat standby maupun saat tracking, asalkan isMapCenteredOnUser true)
                        if (isMapCenteredOnUser) {
                            map.controller.animateTo(lastKnownGeoPoint)
                        }
                    }

                    // Gambar garis rute HANYA jika sedang/sudah ada tracking
                    if (routePoints.isNotEmpty()) {
                        val line = Polyline(map)
                        line.setPoints(routePoints.toList())
                        line.outlinePaint.color = when (jenis.lowercase()) {
                            "jalan" -> android.graphics.Color.parseColor("#00BCD4")
                            "sepeda" -> android.graphics.Color.parseColor("#FF9100")
                            else -> android.graphics.Color.parseColor("#00E676")
                        }
                        line.outlinePaint.strokeWidth = 14f
                        line.outlinePaint.isAntiAlias = true
                        line.outlinePaint.strokeJoin = Paint.Join.ROUND
                        line.outlinePaint.strokeCap = Paint.Cap.ROUND
                        map.overlays.add(line)
                    }
                    map.invalidate()
                }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(surfaceColor.copy(alpha = 0.95f), CircleShape)
                        .border(1.5.dp, if (useSatellite) Color.Green.copy(alpha=0.6f) else textPrimary.copy(alpha=0.25f), CircleShape)
                        .clickable { useSatellite = !useSatellite },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Layers, null, tint = if (useSatellite) Color.Green else textPrimary, modifier = Modifier.size(22.dp))
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(surfaceColor.copy(alpha = 0.95f), CircleShape)
                        .border(1.5.dp, textPrimary.copy(alpha=0.25f), CircleShape)
                        .clickable { mapView.controller.zoomIn() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = textPrimary, modifier = Modifier.size(24.dp))
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(surfaceColor.copy(alpha = 0.95f), CircleShape)
                        .border(1.5.dp, textPrimary.copy(alpha=0.25f), CircleShape)
                        .clickable { mapView.controller.zoomOut() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Remove, null, tint = textPrimary, modifier = Modifier.size(24.dp))
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(surfaceColor.copy(alpha = 0.95f), CircleShape)
                        .border(1.5.dp, jenisColor.copy(alpha=0.6f), CircleShape)
                        .clickable {
                            lastKnownGeoPoint?.let {
                                mapView.controller.animateTo(it)
                                mapView.controller.setZoom(19.0)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MyLocation, null, tint = jenisColor, modifier = Modifier.size(22.dp))
                }
            }
        }

        // ======== HEADER: TOMBOL BACK ========
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(surfaceColor.copy(alpha = 0.9f), CircleShape)
                    .border(1.dp, Color.LightGray.copy(alpha=0.2f), CircleShape)
                    .clickable {
                        onNavigateBack() // Minimize to home
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = str("kembali"), tint = textPrimary)
            }

            // Modern Glassmorphism GPS Status Indicator
            val gpsText: String
            val gpsColor: Color
            when {
                !hasLocationPermission -> {
                    gpsText = "Izinkan Lokasi"
                    gpsColor = Color(0xFFFF9100) // Orange
                }
                !isGpsEnabled -> {
                    gpsText = "Hidupkan GPS"
                    gpsColor = Color.Red
                }
                lastKnownGeoPoint == null -> {
                    gpsText = "Mencari GPS..."
                    gpsColor = Color(0xFFFF9100)
                }
                else -> {
                    gpsText = "GPS ON"
                    gpsColor = Color(0xFF00E676)
                }
            }

            Box(
                modifier = Modifier
                    .background(surfaceColor.copy(alpha = 0.9f), CircleShape)
                    .border(1.dp, Color.LightGray.copy(alpha=0.2f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MyLocation, null, tint = gpsColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (!isGpsEnabled) "Hidupkan GPS" else if (!hasLocationPermission) "Izinkan Lokasi" else if (lastKnownGeoPoint == null) "Mencari GPS..." else "GPS Siap",
                        color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                }
            }
        }

        // ======== PANEL STATISTIK BAWAH (Modern Glassmorphism) ========
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(surfaceColor.copy(alpha = 0.98f)) // Dynamic surface color for light/dark
                .padding(top = 12.dp, start = 24.dp, end = 24.dp, bottom = 32.dp)
                .navigationBarsPadding()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .background(textSecondary.copy(alpha = 0.3f), CircleShape)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(jenisIconVector, contentDescription = null, tint = jenisColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "TRACKING ${jenis.uppercase(Locale.US)} • WAKTU",
                        color = textSecondary, fontSize = 12.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Huge Timer
                Text(
                    formatTime,
                    color = textPrimary,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-1.5).sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Metrics Row with dots
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetricWithDot("JARAK", String.format(Locale.US, "%.2f", distanceInKm), "km", Color(0xFF00E676), textPrimary, textSecondary, Modifier.weight(1f))
                    MetricWithDot("PACE", formatPace, "/km", Color(0xFF00BCD4), textPrimary, textSecondary, Modifier.weight(1f))
                    MetricWithDot("KALORI", calculatedKcal.toString(), "kcal", Color(0xFFFF9100), textPrimary, textSecondary, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Bottom Buttons
                if (!hasStarted) {
                    Button(
                        onClick = {
                            TrackingManager.startTracking(jenis)
                            LocationServiceController.start(context)
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = jenisColor),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Text("MULAI", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, letterSpacing = 2.sp)
                    }
                } else if (isRunning) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Pause Button (Rounded Rectangle)
                        OutlinedButton(
                            onClick = { TrackingManager.pauseTracking() },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFFFB300).copy(alpha = 0.2f), contentColor = Color(0xFFFF9100)),
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Color(0xFFFF9100), modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("PAUSE", color = Color(0xFFFF9100), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                        
                        // Finish Button
                        OutlinedButton(
                            onClick = {
                                LocationServiceController.stop(context)
                                val durasiMenit = (seconds / 60).coerceAtLeast(1)
                                onNavigateToSave(jenis, distanceInKm, calculatedKcal, durasiMenit, formatPace)
                            },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFE53935).copy(alpha = 0.2f), contentColor = Color(0xFFE53935)),
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = "Selesai", tint = Color(0xFFE53935), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("SELESAI", color = Color(0xFFE53935), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        // Resume Button
                        OutlinedButton(
                            onClick = { TrackingManager.resumeTracking() },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF00E676).copy(alpha = 0.2f), contentColor = Color(0xFF00E676)),
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Lanjut", tint = Color(0xFF00E676), modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("LANJUT", color = Color(0xFF00E676), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                        
                        // Finish Button
                        OutlinedButton(
                            onClick = {
                                LocationServiceController.stop(context)
                                val durasiMenit = (seconds / 60).coerceAtLeast(1)
                                onNavigateToSave(jenis, distanceInKm, calculatedKcal, durasiMenit, formatPace)
                            },
                            modifier = Modifier.weight(1f).height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFE53935).copy(alpha = 0.2f), contentColor = Color(0xFFE53935)),
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = "Selesai", tint = Color(0xFFE53935), modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("SELESAI", color = Color(0xFFE53935), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }
}

@Composable
fun MetricWithDot(label: String, value: String, unit: String, dotColor: Color, textColor: Color, labelColor: Color, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(dotColor, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = labelColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = textColor, fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            if (unit.isNotEmpty()) {
                Text(unit, color = Color.Gray.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp, start = 2.dp))
            }
        }
    }
}
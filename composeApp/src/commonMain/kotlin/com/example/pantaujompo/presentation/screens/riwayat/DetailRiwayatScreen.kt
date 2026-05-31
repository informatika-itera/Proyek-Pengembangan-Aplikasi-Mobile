package com.example.pantaujompo.presentation.screens.riwayat

import android.content.Context
import android.graphics.Paint
import android.graphics.Color as AndroidColor
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.pantaujompo.data.local.room.RiwayatEntity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

// Tile sources Google Maps & Satelit
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

import java.text.SimpleDateFormat
import java.util.*

import org.koin.compose.viewmodel.koinViewModel
import com.example.pantaujompo.presentation.theme.MeshBackground
import com.example.pantaujompo.presentation.theme.glassCard
import com.example.pantaujompo.core.util.AppStrings
import com.example.pantaujompo.presentation.theme.DarkBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRiwayatScreen(
    riwayat: RiwayatEntity,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    viewModel: RiwayatViewModel = koinViewModel()
) {
    val language by viewModel.languageState.collectAsState()
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onSurface

    // Tweak Performa RAM peta history
    remember {
        val sharedPref = context.getSharedPreferences("osmdroid_pref", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, sharedPref)
        Configuration.getInstance().userAgentValue = context.packageName

        Configuration.getInstance().cacheMapTileCount = 100
        Configuration.getInstance().cacheMapTileOvershoot = 100
    }

    val mapView = remember { MapView(context) }
    var useSatellite by remember { mutableStateOf(false) }
    var isMapInitialized by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(riwayat.photoUri.isNullOrBlank()) }

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

    val listKoordinatRute = remember(riwayat.ruteString) {
        if (riwayat.ruteString.isBlank()) emptyList()
        else {
            riwayat.ruteString.split("|").mapNotNull {
                val bagian = it.split(",")
                if (bagian.size == 2) {
                    GeoPoint(bagian[0].toDouble(), bagian[1].toDouble())
                } else null
            }
        }
    }

    val tanggalFormat = SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm", Locale("id", "ID")).format(Date(riwayat.tanggal))

    val jenisColor = when (riwayat.jenis?.lowercase()) {
        "jalan" -> Color(0xFF00BCD4)
        "sepeda" -> Color(0xFFFF9100)
        else -> Color(0xFF00E676)
    }

    val jenisLabel = when (riwayat.jenis?.lowercase()) {
        "jalan" -> if (language == "en") "Walking" else "Jalan Santai"
        "sepeda" -> if (language == "en") "Cycling" else "Bersepeda"
        else -> if (language == "en") "Running" else "Lari / Jogging"
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 350.dp,
        sheetContainerColor = surfaceColor.copy(alpha = 0.98f),
        sheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
            ) {
                // Header (No toggle needed anymore since it's a bottom sheet)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(jenisColor.copy(0.15f)), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (riwayat.jenis?.lowercase()) {
                                "sepeda" -> Icons.Default.DirectionsBike
                                "jalan" -> Icons.Default.DirectionsWalk
                                else -> Icons.Default.DirectionsRun
                            },
                            null,
                            tint = jenisColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val displayTitle = riwayat.judul.ifBlank { "$jenisLabel • ${AppStrings.get("detail_aktivitas", language)}" }
                        Text(displayTitle, color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("${AppStrings.get("waktu_mulai", language)}: $tanggalFormat", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (riwayat.deskripsi.isNotBlank()) {
                        Text(riwayat.deskripsi, color = textPrimary.copy(alpha = 0.8f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailMetricItem(label = "JARAK TEMPUH", value = String.format(Locale.US, "%.2f", riwayat.jarak), unit = "Km", textColor = textPrimary)
                            DetailMetricItem(label = "RATA-RATA PACE", value = riwayat.pace, unit = "/Km", textColor = textPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailMetricItem(label = "WAKTU TOTAL", value = "${riwayat.durasi}", unit = "Menit", textColor = textPrimary)
                            DetailMetricItem(label = "KALORI TERBAKAR", value = "${riwayat.kalori}", unit = "Kcal", textColor = textPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = textPrimary.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Menampilkan Multiple Photos jika ada
                    if (!riwayat.photoUri.isNullOrBlank()) {
                        val uris = riwayat.photoUri.split("|").filter { it.isNotBlank() }
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uris) { uri ->
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxWidth(0.85f)
                                        .height(240.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .border(1.dp, Color.Gray.copy(alpha=0.2f), RoundedCornerShape(24.dp))
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Foto Aktivitas",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    var showDeleteConfirm by remember { mutableStateOf(false) }
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(0.5f))
                    ) {
                        Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("HAPUS RIWAYAT INI", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }

                    if (showDeleteConfirm) {
                        AlertDialog(
                            onDismissRequest = { showDeleteConfirm = false },
                            title = { Text("Hapus Aktivitas?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                            text = { Text("Riwayat ini akan dihapus permanen dan tidak bisa dikembalikan.", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            confirmButton = {
                                Button(
                                    onClick = { onDeleteClick(); showDeleteConfirm = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                                ) { Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold) }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            },
                            containerColor = if (isDark) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    ) { paddingValues ->
        MeshBackground(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Box(modifier = Modifier.fillMaxSize()) {
                // ==================== 1. PETA GOOGLE MAPS SUPER HD ====================
            Box(modifier = Modifier.fillMaxWidth()) {
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
                        map.setTileSource(if (useSatellite) satelliteTileSource else standardTileSource)
                        map.overlays.removeAll { it !is org.osmdroid.views.overlay.TilesOverlay }

                        if (listKoordinatRute.isNotEmpty()) {
                            val line = Polyline(map)
                            line.setPoints(listKoordinatRute)
                            line.outlinePaint.color = when (riwayat.jenis?.lowercase()) {
                                "jalan" -> android.graphics.Color.parseColor("#00BCD4")
                                "sepeda" -> android.graphics.Color.parseColor("#FF9100")
                                else -> android.graphics.Color.parseColor("#00E676")
                            }
                            line.outlinePaint.strokeWidth = 14f
                            line.outlinePaint.isAntiAlias = true
                            line.outlinePaint.strokeJoin = Paint.Join.ROUND
                            line.outlinePaint.strokeCap = Paint.Cap.ROUND
                            map.overlays.add(line)

                            val markerStart = Marker(map).apply {
                                position = listKoordinatRute.first()
                                icon = com.example.pantaujompo.core.util.MapUtils.createCustomMarkerDrawable(context)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                title = "Start"
                            }
                            map.overlays.add(markerStart)

                            if (listKoordinatRute.size > 1) {
                                val markerEnd = Marker(map).apply {
                                    position = listKoordinatRute.last()
                                    icon = com.example.pantaujompo.core.util.MapUtils.createCustomMarkerDrawable(context)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                    title = "Finish"
                                }
                                map.overlays.add(markerEnd)
                            }

                            if (!isMapInitialized) {
                                map.post {
                                    if (listKoordinatRute.size > 1) {
                                        try {
                                            map.zoomToBoundingBox(org.osmdroid.util.BoundingBox.fromGeoPoints(listKoordinatRute), true, 100)
                                            if (map.zoomLevelDouble > 18.5) {
                                                map.controller.setZoom(18.5)
                                            }
                                        } catch (e: Exception) {
                                            map.controller.setCenter(listKoordinatRute.last())
                                            map.controller.setZoom(18.5)
                                        }
                                    } else {
                                        map.controller.setCenter(listKoordinatRute.last())
                                        map.controller.setZoom(18.5)
                                    }
                                }
                                isMapInitialized = true
                            }
                        } else {
                            map.controller.setCenter(GeoPoint(-5.397140, 105.266789))
                            map.controller.setZoom(15.0)
                        }
                        map.invalidate()
                    }
                )

                // ======== KONTROL PETA (kanan tengah) ========
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(surfaceColor.copy(alpha = 0.95f), CircleShape)
                            .border(1.5.dp, if (useSatellite) Color.Green.copy(alpha = 0.6f) else textPrimary.copy(alpha = 0.25f), CircleShape)
                            .clickable { useSatellite = !useSatellite; isMapInitialized = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Layers",
                            tint = if (useSatellite) Color.Green else textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ==================== 2. TOMBOL BACK (NGAMBANG) ====================
            Box(
                modifier = Modifier
                    .padding(top = 48.dp, start = 24.dp)
                    .size(44.dp)
                    .background(surfaceColor.copy(alpha = 0.95f), CircleShape)
                    .border(1.dp, textPrimary.copy(alpha = 0.15f), CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary, modifier = Modifier.size(22.dp))
            }
        }
    }
}
}

// Fungsi Desain Metrik
@Composable
fun DetailMetricItem(label: String, value: String, unit: String, textColor: Color) {
    Column(modifier = Modifier.width(150.dp)) {
        Text(
            label,
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = textColor, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                unit,
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }
    }
}
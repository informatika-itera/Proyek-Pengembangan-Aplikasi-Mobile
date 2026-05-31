package com.example.pantaujompo.presentation.screens.tracking

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import com.example.pantaujompo.core.util.AppStrings
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.presentation.theme.MeshBackground
import com.example.pantaujompo.presentation.theme.glassCard
import org.koin.compose.koinInject
import java.util.Locale
import android.graphics.Paint
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveActivityScreen(
    jenis: String,
    jarak: Double,
    kalori: Int,
    durasi: Int,
    pace: String,
    onNavigateBack: () -> Unit,
    onSaveClick: (judul: String, deskripsi: String, jenis: String, jarak: Double, kalori: Int, durasi: Int, pace: String, rute: String, photoUri: String?) -> Unit,
    userPreferences: UserPreferences = koinInject()
) {
    val language by userPreferences.language.collectAsState("id")
    val isDark by userPreferences.isDarkMode.collectAsState(true)
    
    val bgColor = if (isDark) Color(0xFF121212) else Color(0xFFF9F9F9)
    val surfaceColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textPrimary = if (isDark) Color.White else Color.Black
    val textSecondary = if (isDark) Color.LightGray else Color.Gray
    val primaryColor = MaterialTheme.colorScheme.primary

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<String>>(emptyList()) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                val savedUris = uris.mapNotNull { uri ->
                    saveImageToInternalStorage(context, uri, null)
                }
                selectedImageUris = savedUris
            }
        }
    )

    val routePoints by com.example.pantaujompo.domain.TrackingManager.routePoints.collectAsState()

    MeshBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Simpan Aktivitas", fontWeight = FontWeight.Bold, color = textPrimary) },
                    navigationIcon = {
                        IconButton(onClick = { 
                            com.example.pantaujompo.domain.TrackingManager.stopAndClear()
                            onNavigateBack() 
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = textPrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { 
                            com.example.pantaujompo.domain.TrackingManager.stopAndClear()
                            onNavigateBack() 
                        }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = Color.Red)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                    Button(
                        onClick = { 
                            val finalTitle = if (title.isBlank()) "Aktivitas $jenis" else title
                            val ruteTeksString = routePoints.joinToString("|") { "${it.latitude},${it.longitude}" }
                            val combinedUris = selectedImageUris.joinToString("|").takeIf { it.isNotEmpty() }
                            onSaveClick(finalTitle, description, jenis, jarak, kalori, durasi, pace, ruteTeksString, combinedUris) 
                            com.example.pantaujompo.domain.TrackingManager.stopAndClear()
                            showSuccessDialog = true
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).glassCard(shape = RoundedCornerShape(16.dp), neonColor = primaryColor, borderWidth = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(AppStrings.get("simpan", language), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // TextFields with Glass feel
                Text(AppStrings.get("judul", language), color = textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text(AppStrings.get("masukkan_judul", language), color = textSecondary) },
                    modifier = Modifier.fillMaxWidth().glassCard(shape = RoundedCornerShape(16.dp), neonColor = primaryColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Text(AppStrings.get("deskripsi", language), color = textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text(AppStrings.get("bagaimana_hasilnya", language), color = textSecondary) },
                    modifier = Modifier.fillMaxWidth().height(120.dp).glassCard(shape = RoundedCornerShape(16.dp), neonColor = primaryColor),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 5
                )

                // Kategori Label
                Text(AppStrings.get("kategori", language), color = textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassCard(shape = RoundedCornerShape(16.dp), neonColor = primaryColor)
                        .padding(16.dp)
                ) {
                    Icon(
                        if (jenis.lowercase() == "lari") Icons.Default.DirectionsRun else if (jenis.lowercase() == "sepeda") Icons.Default.DirectionsBike else Icons.Default.DirectionsWalk, 
                        null, 
                        tint = primaryColor
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(jenis, color = textPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }

                // Summary Box
                Text(AppStrings.get("ringkasan_aktivitas", language), color = textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().glassCard(shape = RoundedCornerShape(16.dp), neonColor = primaryColor).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("JARAK", color = textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(String.format(Locale.US, "%.2f", jarak), color = textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("km", color = textSecondary, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WAKTU", color = textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$durasi", color = textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("mnt", color = textSecondary, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PACE", color = textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(pace, color = textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("/km", color = textSecondary, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("KALORI", color = textSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("$kalori", color = textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("kcal", color = textSecondary, fontSize = 10.sp)
                    }
                }

                // Map Preview Placeholder
                if (routePoints.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .glassCard(shape = RoundedCornerShape(16.dp), neonColor = primaryColor)
                            .clip(RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                MapView(ctx).apply {
                                    setMultiTouchControls(false)
                                    setBuiltInZoomControls(false)
                                    
                                    // Use Google Maps Standard tile source for modern look
                                    setTileSource(object : org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase("GoogleMaps", 1, 20, 256, ".png", arrayOf("https://mt0.google.com/vt/lyrs=m&hl=id&z=", "https://mt1.google.com/vt/lyrs=m&hl=id&z=", "https://mt2.google.com/vt/lyrs=m&hl=id&z=", "https://mt3.google.com/vt/lyrs=m&hl=id&z=")) {
                                        override fun getTileURLString(pMapTileIndex: Long): String = baseUrl + org.osmdroid.util.MapTileIndex.getZoom(pMapTileIndex) + "&x=" + org.osmdroid.util.MapTileIndex.getX(pMapTileIndex) + "&y=" + org.osmdroid.util.MapTileIndex.getY(pMapTileIndex)
                                    })
                                    
                                    // Disable interaction for preview
                                    setOnTouchListener { _, _ -> true }

                                    val line = Polyline(this)
                                    line.setPoints(routePoints.toList())
                                    line.outlinePaint.color = when (jenis.lowercase()) {
                                        "jalan" -> android.graphics.Color.parseColor("#00BCD4")
                                        "sepeda" -> android.graphics.Color.parseColor("#FF9100")
                                        else -> android.graphics.Color.parseColor("#00E676")
                                    }
                                    line.outlinePaint.strokeWidth = 10f
                                    line.outlinePaint.isAntiAlias = true
                                    line.outlinePaint.strokeJoin = Paint.Join.ROUND
                                    line.outlinePaint.strokeCap = Paint.Cap.ROUND
                                    overlays.add(line)

                                    if (routePoints.isNotEmpty()) {
                                        controller.setCenter(routePoints[routePoints.size / 2])
                                        controller.setZoom(16.0)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Photo/Video Upload Box
                Text(AppStrings.get("media", language), color = textSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .glassCard(shape = RoundedCornerShape(16.dp), neonColor = primaryColor)
                        .clickable { photoPickerLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUris.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedImageUris) { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Selected Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.width(130.dp).fillMaxHeight().clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, null, tint = primaryColor, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(AppStrings.get("tambah_foto", language) + " (Max 3)", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        // Success Dialog Animation
        if (showSuccessDialog) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2500) // Animasi sedikit lebih lama biar lebih terasa
                showSuccessDialog = false
                onNavigateBack()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = showSuccessDialog,
                    enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .glassCard(shape = RoundedCornerShape(32.dp), neonColor = primaryColor)
                            .padding(40.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle, 
                            contentDescription = "Success", 
                            tint = Color(0xFF00E676), 
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            "Luar Biasa!", 
                            color = textPrimary, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Aktivitasmu berhasil disimpan.", 
                            color = textSecondary, 
                            fontSize = 15.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

fun saveImageToInternalStorage(context: android.content.Context, uri: android.net.Uri?, bitmap: android.graphics.Bitmap?): String? {
    try {
        val file = java.io.File(context.filesDir, "IMG_ACT_${System.currentTimeMillis()}_${(0..1000).random()}.jpg")
        file.outputStream().use { out ->
            if (bitmap != null) {
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            } else if (uri != null) {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    input.copyTo(out)
                }
            } else {
                return null
            }
        }
        return file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

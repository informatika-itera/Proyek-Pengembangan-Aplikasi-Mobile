package com.example.pantaujompo.presentation.screens.pemindai

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pantaujompo.data.local.room.MakananEntity
import com.example.pantaujompo.data.remote.api.GeminiService
import com.example.pantaujompo.presentation.screens.riwayat.RiwayatViewModel
import com.example.pantaujompo.presentation.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemindaiScreen(
    profilData: String,
    viewModel: RiwayatViewModel,
    onSimpanClick: (MakananEntity) -> Unit
) {
    val makananList by viewModel.makananHariIniState.collectAsState()
    val targetKalori by viewModel.targetKalori.collectAsState()
    
    var showScanner by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(false) }
    var detailMakanan by remember { mutableStateOf<MakananEntity?>(null) }

    val userPreferences: com.example.pantaujompo.data.local.datastore.UserPreferences = org.koin.compose.koinInject()
    val language by userPreferences.language.collectAsState(initial = "id")
    fun str(key: String) = com.example.pantaujompo.core.util.AppStrings.get(key, language)
    val context = LocalContext.current

    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = if (isDark) Color(0xFF131313) else MaterialTheme.colorScheme.surface

    val totalKalori = makananList.sumOf { it.protein * 4 + it.karbo * 4 + it.lemak * 9 }
    val totalProtein = makananList.sumOf { it.protein }
    val totalKarbo = makananList.sumOf { it.karbo }

    MeshBackground(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(str("nutrisi"), color = textPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        Text(
                            str("hari_ini_tgl") + SimpleDateFormat("dd MMM yyyy", Locale(if (language == "en") "en" else "id", if (language == "en") "US" else "ID")).format(Date()),
                            color = accentColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Calorie Ring (Clickable to Set Target)
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(surfaceColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                        .clickable { showTargetDialog = true }
                        .padding(24.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(str("kalori_hari_ini"), color = textSecondary, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("$totalKalori", color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("/ $targetKalori kcal", color = textSecondary, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                                }
                            }
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                                CircularProgressIndicator(
                                    progress = { (totalKalori.toFloat() / targetKalori).coerceAtMost(1f) },
                                    modifier = Modifier.size(72.dp),
                                    color = accentColor,
                                    trackColor = accentColor.copy(0.15f),
                                    strokeWidth = 7.dp
                                )
                                Text(
                                    "${((totalKalori.toFloat() / targetKalori) * 100).toInt()}%",
                                    color = textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            MacroProgressItem(str("protein"), totalProtein, 80, Color(0xFF00E676), Modifier.weight(1f))
                            MacroProgressItem(str("karbo"), totalKarbo, 200, Color(0xFF00BCD4), Modifier.weight(1f))
                            MacroProgressItem(str("lemak"), makananList.sumOf { it.lemak }, 65, Color(0xFFFF9100), Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Removed Redundant Macro Cards (Protein, Karbo, Makanan)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isDark) Color(0xFF0A1A0A) else Color(0xFFE8F5E9))
                        .border(1.dp, accentColor.copy(0.5f), RoundedCornerShape(24.dp))
                        .clickable { showScanner = true }
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = Color.Black, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(str("cek_kalori_makanan"), color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text(str("gunakan_kamera_ketik"), color = textSecondary, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = accentColor)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(str("asupan_hari_ini"), color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (makananList.isNotEmpty()) {
                        Text("${makananList.size} ${str("makanan")}", color = textSecondary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (makananList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.NoFood, null, tint = textSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(str("belum_ada_asupan"), color = textSecondary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(str("tap_kamera"), color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(makananList, key = { it.id }) { makanan ->
                    DailyMealCard(
                        makanan = makanan,
                        isDark = isDark,
                        accentColor = accentColor,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        surfaceColor = surfaceColor,
                        onClick = { detailMakanan = makanan },
                        onDelete = { viewModel.deleteMakanan(makanan.id) }
                    )
                }
            }
        }
    }

    if (showScanner) {
        ScannerBottomSheet(
            profilData = profilData,
            onClose = { showScanner = false },
            onSimpanClick = { makanan ->
                onSimpanClick(makanan)
                showScanner = false
            }
        )
    }

    if (showTargetDialog) {
        var inputKalori by remember { mutableStateOf(targetKalori.toString()) }
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text(str("set_target_kalori"), color = textPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = inputKalori,
                    onValueChange = { inputKalori = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text(str("contoh_kalori")) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary,
                        focusedBorderColor = accentColor
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { 
                        inputKalori.toIntOrNull()?.let { viewModel.updateTargetKalori(it) }
                        showTargetDialog = false 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text(str("simpan"), color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTargetDialog = false }) {
                    Text(str("batal"), color = textSecondary)
                }
            },
            containerColor = surfaceColor
        )
    }

    detailMakanan?.let { mkn ->
        ModalBottomSheet(
            onDismissRequest = { detailMakanan = null },
            containerColor = surfaceColor
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(str("detail_makanan"), color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(16.dp))
                if (!mkn.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = mkn.photoUri,
                        contentDescription = "Food",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(mkn.namaMakanan, color = textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NutrientResultChip(str("protein"), mkn.protein, "g", Color(0xFF00E676), Modifier.weight(1f))
                    NutrientResultChip(str("karbo"), mkn.karbo, "g", Color(0xFF00BCD4), Modifier.weight(1f))
                    NutrientResultChip(str("lemak"), mkn.lemak, "g", Color(0xFFFF9100), Modifier.weight(1f))
                    NutrientResultChip(str("kalori"), mkn.protein * 4 + mkn.karbo * 4 + mkn.lemak * 9, "kcal", Color(0xFFFF5252), Modifier.weight(1.3f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (mkn.info.isNotBlank()) {
                    Text(str("insight_ai"), color = accentColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(mkn.info, color = textSecondary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun MacroProgressItem(label: String, value: Int, target: Int, color: Color, modifier: Modifier) {
    val progress = (value.toFloat() / target).coerceAtMost(1f)
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            Text("${value}g", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(0.15f)
        )
    }
}

@Composable
fun NutritionStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, surfaceColor: Color, modifier: Modifier) {
    Box(
        modifier = modifier.height(90.dp)
            .background(surfaceColor, RoundedCornerShape(18.dp))
            .border(1.dp, color.copy(0.2f), RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Column {
                Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun DailyMealCard(
    makanan: MakananEntity,
    isDark: Boolean, accentColor: Color, textPrimary: Color, textSecondary: Color, surfaceColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val formatter = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    val timeStr = formatter.format(Date(makanan.tanggal))
    val fallbackIcon = when (makanan.kategori) {
        "Sarapan" -> "🍳"
        "Makan Siang" -> "🍱"
        "Makan Malam" -> "🍲"
        else -> "🍿"
    }
    
    val kalori = makanan.protein * 4 + makanan.karbo * 4 + makanan.lemak * 9

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 5.dp)
            .background(surfaceColor, RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha=0.3f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(52.dp).background(
                    if (isDark) Color(0xFF1A1A1A) else Color(0xFFF5F5F5),
                    RoundedCornerShape(14.dp)
                ).clip(RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!makanan.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = makanan.photoUri,
                        contentDescription = "Food",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(fallbackIcon, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(makanan.namaMakanan, color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(3.dp))
                Text("${makanan.kategori} • $timeStr • $kalori kcal", color = textSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniChip("P:${makanan.protein}g", Color(0xFF00E676))
                    MiniChip("K:${makanan.karbo}g", Color(0xFF00BCD4))
                    MiniChip("L:${makanan.lemak}g", Color(0xFFFF9100))
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFFF5252).copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun MiniChip(text: String, color: Color) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val adjustedColor = if (isDark) {
        color
    } else {
        when (color) {
            Color(0xFF00E676) -> Color(0xFF1B5E20)
            Color(0xFF00BCD4) -> Color(0xFF006064)
            Color(0xFFFF9100) -> Color(0xFFE65100)
            Color(0xFFFF5252) -> Color(0xFFB71C1C)
            else -> color
        }
    }
    Text(
        text, color = adjustedColor, fontSize = 10.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.background(adjustedColor.copy(0.12f), RoundedCornerShape(6.dp)).padding(horizontal = 5.dp, vertical = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerBottomSheet(
    profilData: String,
    onClose: () -> Unit,
    onSimpanClick: (MakananEntity) -> Unit
) {
    val userPreferences: com.example.pantaujompo.data.local.datastore.UserPreferences = org.koin.compose.koinInject()
    val language by userPreferences.language.collectAsState(initial = "id")
    fun str(key: String) = com.example.pantaujompo.core.util.AppStrings.get(key, language)

    val coroutineScope = rememberCoroutineScope()
    val geminiService = remember { GeminiService() }
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary

    var isAnalyzing by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var hasResult by remember { mutableStateOf(false) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    
    // Save URI if from gallery
    var savedUri by remember { mutableStateOf<String?>(null) }

    var namaMakanan by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf(0) }
    var karbo by remember { mutableStateOf(0) }
    var lemak by remember { mutableStateOf(0) }
    var kesimpulan by remember { mutableStateOf("") }
    var selectedKategori by remember { mutableStateOf("Camilan") }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            capturedImage = bitmap
            savedUri = null
            inputText = ""
            hasResult = false
        }
    }
    
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            savedUri = uri.toString()
            capturedImage = null
            inputText = ""
            hasResult = false
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) cameraLauncher.launch(null)
    }

    fun proses() {
        if (inputText.isBlank() && capturedImage == null && savedUri == null) return
        
        // Reset SEMUA hasil sebelum panggil API agar tidak ada data lama tertinggal
        namaMakanan = ""
        protein = 0
        karbo = 0
        lemak = 0
        kesimpulan = ""
        hasResult = false
        isAnalyzing = true
        
        // Simpan snapshot input agar tidak berubah saat request berlangsung
        val snapshotText = inputText
        val snapshotImage = capturedImage
        
        coroutineScope.launch {
            try {
                val hasil = geminiService.analisaNutrisi(snapshotText, snapshotImage, profilData)
                namaMakanan = hasil.nama
                protein = hasil.protein
                karbo = hasil.karbo
                lemak = hasil.lemak
                kesimpulan = hasil.info
                hasResult = true
            } catch (e: Exception) {
                // Tampilkan pesan error yang ramah
                namaMakanan = if (snapshotText.isNotBlank()) snapshotText else str("tidak_terdeteksi")
                kesimpulan = str("gagal_analisis")
                hasResult = true
            } finally {
                isAnalyzing = false
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = if (isDark) Color(0xFF0F0F0F) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
        ) {
            Text(str("ai_nutrition_scanner"), color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(str("foto_atau_ketik"), color = textSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // Jika ada teks, sembunyikan kotak kamera (dan sebaliknya)
            if (inputText.isBlank()) {
                // Camera preview box
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isDark) Color(0xFF181818) else Color(0xFFF0F0F0))
                        .border(1.dp, accentColor.copy(0.4f), RoundedCornerShape(24.dp))
                ) {
                    if (capturedImage != null) {
                        Image(
                            capturedImage!!.asImageBitmap(),
                            null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
                        )
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                                .size(34.dp).clip(CircleShape).background(Color.Black.copy(0.6f))
                                .clickable {
                                    capturedImage = null
                                    hasResult = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    } else if (savedUri != null) {
                         AsyncImage(
                            model = savedUri,
                            contentDescription = "Food",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
                        )
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                                .size(34.dp).clip(CircleShape).background(Color.Black.copy(0.6f))
                                .clickable {
                                    savedUri = null
                                    hasResult = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                                Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(accentColor.copy(0.15f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CameraAlt, null, tint = accentColor, modifier = Modifier.size(30.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(str("kamera"), color = accentColor, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { galleryLauncher.launch("image/*") }) {
                                Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color(0xFF00BCD4).copy(0.15f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Photo, null, tint = Color(0xFF00BCD4), modifier = Modifier.size(30.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(str("galeri"), color = Color(0xFF00BCD4), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (capturedImage == null && savedUri == null) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = {
                        inputText = it
                        hasResult = false
                    },
                    placeholder = { Text(str("atau_ketik"), color = textSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { proses() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(16.dp),
                enabled = !isAnalyzing && (inputText.isNotBlank() || capturedImage != null || savedUri != null)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(str("menganalisis"), color = Color.Black, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.Black, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(str("analisis_dengan_ai"), color = Color.Black, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (hasResult) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(
                            if (isDark) Color(0xFF0A1A0A) else Color(0xFFE8F5E9),
                            RoundedCornerShape(24.dp)
                        )
                        .border(1.dp, accentColor.copy(0.4f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = accentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(namaMakanan, color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NutrientResultChip(str("protein"), protein, "g", Color(0xFF00E676), Modifier.weight(1f))
                            NutrientResultChip(str("karbo"), karbo, "g", Color(0xFF00BCD4), Modifier.weight(1f))
                            NutrientResultChip(str("lemak"), lemak, "g", Color(0xFFFF9100), Modifier.weight(1f))
                            NutrientResultChip(str("kalori"), protein * 4 + karbo * 4 + lemak * 9, "kcal", Color(0xFFFF5252), Modifier.weight(1.3f))
                        }
                        if (kesimpulan.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(str("insight_ai"), color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(kesimpulan, color = textPrimary, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Kategori Selection
                        Text("Kategori", color = textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val kategoriList = listOf("Sarapan", "Makan Siang", "Makan Malam", "Camilan")
                            kategoriList.forEach { kat ->
                                val isSelected = kat == selectedKategori
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) accentColor else Color.Transparent)
                                        .border(1.dp, if (isSelected) accentColor else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                        .clickable { selectedKategori = kat }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        kat.replace("Makan ", ""), 
                                        color = if (isSelected) Color.Black else textPrimary, 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        val context = LocalContext.current
                        
                        Button(
                            onClick = {
                                val finalUri = saveImageToInternalStorage(
                                    context = context,
                                    uri = savedUri?.let { Uri.parse(it) },
                                    bitmap = capturedImage
                                )
                                onSimpanClick(
                                    MakananEntity(
                                        namaMakanan = namaMakanan,
                                        protein = protein,
                                        karbo = karbo,
                                        lemak = lemak,
                                        info = kesimpulan,
                                        photoUri = finalUri ?: "", // PERMANENT URI
                                        kategori = selectedKategori
                                    )
                                )
                                inputText = ""
                                capturedImage = null
                                savedUri = null
                                hasResult = false
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Save, null, tint = Color.Black, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(str("simpan_ke_riwayat"), color = Color.Black, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

fun saveImageToInternalStorage(context: android.content.Context, uri: Uri?, bitmap: Bitmap?): String? {
    try {
        val file = java.io.File(context.filesDir, "IMG_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out ->
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
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

@Composable
fun NutrientResultChip(label: String, value: Int, unit: String, color: Color, modifier: Modifier) {
    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val adjustedColor = if (isDark) {
        color
    } else {
        when (color) {
            Color(0xFF00E676) -> Color(0xFF1B5E20)
            Color(0xFF00BCD4) -> Color(0xFF006064)
            Color(0xFFFF9100) -> Color(0xFFE65100)
            Color(0xFFFF5252) -> Color(0xFFB71C1C)
            else -> color
        }
    }
    Box(
        modifier = modifier.background(adjustedColor.copy(0.12f), RoundedCornerShape(16.dp)).padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$value$unit", color = adjustedColor, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}

package com.example.pantaujompo.presentation.screens.profil

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.pantaujompo.data.local.datastore.UserPreferences
import com.example.pantaujompo.presentation.screens.home.DashboardViewModel
import com.example.pantaujompo.presentation.theme.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import java.util.Locale
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    onNavigateToSettings: () -> Unit = {},
    profilViewModel: ProfilViewModel = koinViewModel(),
    dashboardViewModel: DashboardViewModel = koinViewModel(),
    userPreferences: UserPreferences = koinInject()
) {
    val language by userPreferences.language.collectAsState(initial = "id")
    fun str(key: String) = com.example.pantaujompo.core.util.AppStrings.get(key, language)

    val quotes = listOf(
        str("quote_1"),
        str("quote_2"),
        str("quote_3"),
        str("quote_4"),
        str("quote_5"),
        str("quote_6")
    )
    val dynamicQuote = remember(language) { quotes.random() }

    val coroutineScope = rememberCoroutineScope()
    val daftarRiwayat by dashboardViewModel.riwayatList.collectAsState(initial = emptyList())
    val savedImageUri by userPreferences.profileImageUri.collectAsState(initial = "")

    val isDark = MaterialTheme.colorScheme.background == DarkBackground
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val surfaceColor = if (isDark) Color(0xFF151515) else MaterialTheme.colorScheme.surface

    // Computed stats
    val totalKm = daftarRiwayat.sumOf { it.jarak }
    val totalKalori = daftarRiwayat.sumOf { it.kalori }
    val totalWaktu = daftarRiwayat.sumOf { it.durasi }

    val bmi = remember(profilViewModel.beratKg, profilViewModel.tinggiCm) {
        val b = profilViewModel.beratKg.toFloatOrNull() ?: 0f
        val t = (profilViewModel.tinggiCm.toFloatOrNull() ?: 1f) / 100f
        if (t > 0f && b > 0f) b / t.pow(2) else 0f
    }
    val (bmiKategori, bmiWarna) = when {
        bmi == 0f -> str("belum_diisi") to Color.Gray
        bmi < 18.5f -> str("kurus") to Color(0xFF00BCD4)
        bmi < 25f -> str("ideal") to Color(0xFF00E676)
        bmi < 30f -> str("overweight") to Color(0xFFFFC107)
        else -> str("obesitas") to Color(0xFFFF5252)
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    // PP State - use savedUri from DataStore as initial, local bitmap for camera preview
    var localBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var showPpDialog by remember { mutableStateOf(false) }

    fun saveBitmapToCache(bitmap: android.graphics.Bitmap): String? {
        return try {
            val cacheDir = context.cacheDir
            val file = java.io.File(cacheDir, "profile_pic_${System.currentTimeMillis()}.jpg")
            val out = java.io.FileOutputStream(file)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            localBitmap = null
            coroutineScope.launch {
                userPreferences.setProfileImage(uri.toString())
            }
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            localBitmap = bitmap
            val savedUri = saveBitmapToCache(bitmap)
            if (savedUri != null) {
                coroutineScope.launch {
                    userPreferences.setProfileImage(savedUri)
                }
            }
        }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    val infiniteTransition = rememberInfiniteTransition()
    val glowOffset by infiniteTransition.animateFloat(
        initialValue = -50f, targetValue = 50f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse)
    )

    Box(modifier = Modifier.fillMaxSize().background(if (isDark) Color(0xFF080808) else Color(0xFFF0F2F5))) {
        // Glowing Background Orbs
        Box(
            modifier = Modifier
                .offset(x = glowOffset.dp, y = (-100).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00E676).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-glowOffset).dp, y = 100.dp)
                .size(350.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00BCD4).copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        
        MeshBackground(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(48.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(str("profil"), color = textPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                    Text("Pantau Jompo", color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = { onNavigateToSettings() }) {
                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(surfaceColor)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, null, tint = textSecondary, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ===== PROFILE CARD =====
            Box(
                modifier = Modifier.fillMaxWidth()
                    .glassCard(shape = RoundedCornerShape(28.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    // Avatar
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier.size(100.dp).clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFF0F0F0))
                                .border(3.dp, accentColor, CircleShape)
                                .clickable { showPpDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                localBitmap != null -> Image(
                                    bitmap = localBitmap!!.asImageBitmap(),
                                    contentDescription = "PP",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                                savedImageUri.isNotBlank() -> AsyncImage(
                                    model = savedImageUri,
                                    contentDescription = "PP",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                                else -> {
                                    val defaultIcon = if (profilViewModel.gender == "Perempuan") Icons.Default.Face4 else Icons.Default.Face
                                    Icon(defaultIcon, null, tint = accentColor, modifier = Modifier.size(48.dp))
                                }
                            }
                        }
                        // Edit badge
                        Box(
                            modifier = Modifier.size(30.dp).clip(CircleShape).background(accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, null, tint = Color.Black, modifier = Modifier.size(15.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(str("ganti_foto_profil"), color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showPpDialog = true })

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name field
                    OutlinedTextField(
                        value = profilViewModel.nama,
                        onValueChange = { profilViewModel.nama = it },
                        placeholder = { Text(str("nama_pengguna"), color = textSecondary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textPrimary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            cursorColor = accentColor
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "\"$dynamicQuote\"",
                        color = textSecondary,
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    )

                    // Stats removed per user request
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== BIOMETRICS CARD =====
            Box(
                modifier = Modifier.fillMaxWidth()
                    .glassCard(shape = RoundedCornerShape(28.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonitorHeart, null, tint = accentColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(str("data_biometrik"), color = textSecondary, fontSize = 11.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProfilInputField(str("usia"), profilViewModel.usia, { profilViewModel.usia = it }, str("th"), Modifier.weight(1f), accentColor, textPrimary, textSecondary)
                        ProfilInputField(str("berat"), profilViewModel.beratKg, { profilViewModel.beratKg = it }, str("kg"), Modifier.weight(1f), accentColor, textPrimary, textSecondary)
                        ProfilInputField(str("tinggi"), profilViewModel.tinggiCm, { profilViewModel.tinggiCm = it }, str("cm"), Modifier.weight(1f), accentColor, textPrimary, textSecondary)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Kotak Indeks Massa Tubuh (BMI)
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(
                                if (isDark) Color(0xFF0D0D0D) else Color(0xFFF5F5F5),
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, bmiWarna.copy(0.4f), RoundedCornerShape(12.dp))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Judul BMI
                            Text(str("indeks_massa_tubuh"), color = textSecondary, fontSize = 9.sp,
                                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            
                            // Nilai BMI diperkecil agar tidak terlalu besar
                            Text(
                                if (bmi > 0f) String.format(Locale.US, "%.1f", bmi) else "-",
                                color = textPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black
                            )
                            
                            // Kategori BMI (Ideal, Obesitas, dll)
                            Box(
                                modifier = Modifier.background(bmiWarna, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(bmiKategori, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { profilViewModel.saveProfile() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Icon(Icons.Default.Save, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(str("simpan_profil"), color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
    }

    // PP Dialog
    if (showPpDialog) {
        AlertDialog(
            onDismissRequest = { showPpDialog = false },
            title = {
                Text(str("ganti_foto_profil"), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            containerColor = if (isDark) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.surface,
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            showPpDialog = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(accentColor.copy(0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CameraAlt, null, tint = accentColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(str("buka_kamera"), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            Text(str("preview_saja"), color = textSecondary, fontSize = 11.sp)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            showPpDialog = false
                            galleryLauncher.launch("image/*")
                        }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF00BCD4).copy(0.15f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Photo, null, tint = Color(0xFF00BCD4), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(str("pilih_dari_galeri"), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            Text(str("tersimpan_permanen"), color = accentColor, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPpDialog = false }) {
                    Text(str("batal"), color = textSecondary)
                }
            }
        )
    }
}

@Composable
fun ProfilStatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
    }
}

@Composable
fun ProfilInputField(
    label: String, value: String, onValueChange: (String) -> Unit, unit: String,
    modifier: Modifier, accentColor: Color, textPrimary: Color, textSecondary: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("$label ($unit)", fontSize = 9.sp) },
        modifier = modifier,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = textPrimary
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textPrimary,
            unfocusedTextColor = textPrimary,
            focusedBorderColor = accentColor,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedLabelColor = accentColor,
            unfocusedLabelColor = textSecondary,
            cursorColor = accentColor
        ),
        shape = RoundedCornerShape(14.dp)
    )
}
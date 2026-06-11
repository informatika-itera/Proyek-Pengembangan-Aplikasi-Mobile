package com.example.hujjah.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hujjah.presentation.components.hujjah.HujjahMenuItem
import com.example.hujjah.presentation.components.hujjah.HujjahSprint2MenuBar
import com.example.hujjah.presentation.theme.LocalHujjahColors
import com.example.hujjah.core.util.rememberImagePickerLauncher
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLens: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onNavigateToHadith: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToNotes: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val arabicFontSize by viewModel.arabicFontSize.collectAsStateWithLifecycle()
    val profileImageBase64 by viewModel.profileImageBase64.collectAsStateWithLifecycle()
    val readingDurationSeconds by viewModel.readingDurationSeconds.collectAsStateWithLifecycle()
    val currentStreakDays by viewModel.currentStreakDays.collectAsStateWithLifecycle()

    val colors = LocalHujjahColors.current

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName) }

    // Sync input field when database value loads
    LaunchedEffect(userName) {
        editedName = userName
    }

    @OptIn(ExperimentalEncodingApi::class)
    val imagePickerLauncher = rememberImagePickerLauncher { bytes ->
        if (bytes != null) {
            val base64 = Base64.encode(bytes)
            viewModel.updateProfileImage(base64)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil Saya",
                        fontWeight = FontWeight.Bold,
                        color = colors.goldHighlight
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            HujjahSprint2MenuBar(
                currentItem = HujjahMenuItem.PROFILE,
                onNavigateToHome = onNavigateToHome,
                onNavigateToLens = onNavigateToLens,
                onNavigateToQuran = onNavigateToQuran,
                onNavigateToHadith = onNavigateToHadith,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // ==================== A. HERO SECTION (AVATAR & EDIT NAME) ====================
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(120.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(colors.goldHighlight.copy(alpha = 0.2f))
                            .border(3.dp, colors.goldHighlight, CircleShape)
                            .clickable { imagePickerLauncher() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageBase64.isNotEmpty()) {
                            @OptIn(ExperimentalEncodingApi::class)
                            val imageBytes = try {
                                Base64.decode(profileImageBase64)
                            } catch (e: Exception) {
                                null
                            }
                            AsyncImage(
                                model = imageBytes,
                                contentDescription = "Profile Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                            )
                        } else {
                            Text(
                                text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "H",
                                color = colors.goldHighlight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 48.sp
                            )
                        }
                    }

                    // Edit Photo Badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(colors.islamicGreen)
                            .border(2.dp, colors.goldHighlight, CircleShape)
                            .clickable { imagePickerLauncher() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ganti Foto Profil",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (profileImageBase64.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hapus Foto",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.deleteProfileImage() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showEditNameDialog = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = userName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (colors.isDarkTheme) Color.White else colors.islamicGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Nama",
                        tint = colors.goldHighlight,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==================== B. PAPAN STATISTIK PENCAPAIAN ====================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
                    ),
                    border = BorderStroke(1.dp, colors.goldHighlight.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = colors.goldHighlight,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currentStreakDays Hari",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                            )
                            Text(
                                text = "Streak Mengaji",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }

                        // Divider vertical
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = "Waktu Baca",
                                tint = colors.goldHighlight,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val durationMinutes = readingDurationSeconds / 60
                            Text(
                                text = "$durationMinutes Menit",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                            )
                            Text(
                                text = "Total Waktu",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==================== C. PREFERENCES & ACCESSIBILITY ====================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) colors.islamicGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(0.5.dp, colors.goldHighlight.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Mode Gelap OLED",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                )
                                Text(
                                    text = "Ubah latar belakang menjadi hitam pekat",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }

                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = viewModel::setDarkMode,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = colors.goldHighlight,
                                    checkedTrackColor = colors.islamicGreen,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Slider: Arabic Font Size
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ukuran Font Arab",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (colors.isDarkTheme) Color.White else colors.islamicGreen
                                )
                                Text(
                                    text = "$arabicFontSize sp",
                                    fontWeight = FontWeight.Bold,
                                    color = colors.goldHighlight
                                )
                            }
                            Slider(
                                value = arabicFontSize.toFloat(),
                                onValueChange = { viewModel.setArabicFontSize(it.toInt()) },
                                valueRange = 16f..36f,
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.goldHighlight,
                                    activeTrackColor = colors.goldHighlight,
                                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            )

                            // Live Preview Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (colors.isDarkTheme) Color.Black else Color(0xFFF9F9FB))
                                    .border(0.5.dp, colors.goldHighlight.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    fontSize = arabicFontSize.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.goldHighlight,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==================== D. GROUPED NAVIGATION 1 ====================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) colors.islamicGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(0.5.dp, colors.goldHighlight.copy(alpha = 0.2f))
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Outlined.BookmarkBorder,
                            title = "Khazanah Dalil Tersimpan",
                            onClick = onNavigateToBookmarks,
                            tintColor = colors.goldHighlight
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(
                            icon = Icons.Outlined.ChatBubbleOutline,
                            title = "Riwayat Konseling",
                            onClick = onNavigateToLens,
                            tintColor = colors.goldHighlight
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(
                            icon = Icons.Outlined.Description,
                            title = "Catatan Harian Saya",
                            onClick = onNavigateToNotes,
                            tintColor = colors.goldHighlight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==================== GROUPED NAVIGATION 2 ====================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (colors.isDarkTheme) colors.islamicGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(0.5.dp, colors.goldHighlight.copy(alpha = 0.2f))
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Outlined.Info,
                            title = "Tentang Hujjah",
                            onClick = { showAboutDialog = true },
                            tintColor = colors.goldHighlight
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(
                            icon = Icons.Outlined.ExitToApp,
                            title = "Keluar & Reset Akun",
                            onClick = { showLogoutConfirmDialog = true },
                            tintColor = MaterialTheme.colorScheme.error,
                            textColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // ==================== EXECUTIVE FOOTER ====================
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 36.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Hujjah Mobile App Versi 1.1.0",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = "Crafted by Awi & Bimas",
                    fontSize = 11.sp,
                    color = colors.goldHighlight,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Ubah Nama", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { if (it.length <= 25) editedName = it },
                    label = { Text("Nama Pengguna") },
                    singleLine = true,
                    supportingText = { Text("${editedName.length}/25") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.goldHighlight,
                        focusedLabelColor = colors.goldHighlight
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            viewModel.updateUserName(editedName)
                        }
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.goldHighlight)
                ) {
                    Text("Simpan", color = MaterialTheme.colorScheme.background)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Tentang Hujjah", fontWeight = FontWeight.Bold, color = colors.goldHighlight) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Hujjah adalah aplikasi konseling spiritual dan khazanah dalil Al-Qur'an & Hadits yang dirancang dengan antarmuka Apple Premium.",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Aplikasi ini mempermudah pencarian dalil shahih dan interaksi tanya jawab berbasis teknologi AI asisten spiritual yang aman.",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Hak Cipta © 2026. Dikembangkan oleh Awi & Bimas.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.goldHighlight)
                ) {
                    Text("Tutup", color = MaterialTheme.colorScheme.background)
                }
            }
        )
    }

    // Logout Confirm Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("Keluar & Reset Data?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = {
                Text("Tindakan ini akan mengembalikan profil Anda ke nama 'Hamba Allah', menghapus foto profil, dan mereset seluruh statistik mengaji Anda dari awal.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logoutAndReset()
                        showLogoutConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Keluar & Reset", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    tintColor: Color,
    textColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

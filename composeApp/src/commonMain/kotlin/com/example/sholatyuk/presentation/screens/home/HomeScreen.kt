package com.example.sholatyuk.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.domain.model.PrayerTime
import com.example.sholatyuk.presentation.theme.*
// 👇 Import ProfileViewModel ditambahkan di sini
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToShalat: () -> Unit = {},
    onNavigateToDoa: () -> Unit = {},
    onNavigateToIslamAI: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel() // 👇 ProfileViewModel diinjeksi di sini
) {
    val uiState by viewModel.uiState.collectAsState()
    // 👇 Mengambil data nama pengguna dari ProfileViewModel
    val userName by profileViewModel.userName.collectAsState()

    // Pop-up Dialog GPS
    if (uiState.showGpsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissGpsDialog() },
            title = { Text(text = "GPS Belum Aktif", fontWeight = FontWeight.Bold) },
            text = { Text("Aplikasi SholatYuk membutuhkan lokasi (GPS) untuk menghitung jadwal sholat yang akurat di tempat Anda berdiri. Mohon aktifkan GPS sekarang.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onOpenGpsSettings()
                        viewModel.dismissGpsDialog()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentYellow)
                ) {
                    Text("Buka Pengaturan", color = DeepBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissGpsDialog() }) {
                    Text("Nanti Saja", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            titleContentColor = DeepBlue,
            textContentColor = Color.DarkGray
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onHomeClick = {},
                onShalatClick = onNavigateToShalat,
                onDoaClick = onNavigateToDoa,
                onIslamAIClick = onNavigateToIslamAI
            )
        },
        containerColor = DeepBlue
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkTeal, DeepBlue),
                        startY = 0f,
                        endY = 1200f
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 👇 Oper userName ke dalam HeaderSection
                item {
                    HeaderSection(
                        userName = userName,
                        onProfileClick = onNavigateToProfile
                    )
                }

                // SMART BANNER: Ketuk untuk Refresh
                if (uiState.isLoading) {
                    item {
                        Text(
                            text = "Mencari lokasi dan jadwal sholat...",
                            color = AccentYellow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (uiState.error != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.fetchPrayerTimes() },
                            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = AccentYellow,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.error ?: "Terjadi kesalahan",
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ketuk area ini untuk mencoba lagi",
                                    color = AccentYellow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item { PrayerClockCard(uiState.prayerTime) }
                item { PrayerTimesRow(uiState.prayerTime) }

                item { VideoBanner() }
                item { MenuGrid() }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

// ====================================================================

@Composable
fun HeaderSection(userName: String = "Umar Faruq", onProfileClick: () -> Unit = {}) { // 👇 Parameter userName ditambahkan
    // 👇 Logika untuk mengambil maksimal 2 huruf inisial dari nama
    val initials = userName.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .takeIf { it.isNotEmpty() } ?: "U"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "BEKAL ISLAM",
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "الإسلام",
                color = TextWhite,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Brightness3,
                contentDescription = null,
                tint = AccentYellow,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials, // 👇 Gunakan inisial dinamis di sini
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PrayerClockCard(prayerTime: PrayerTime?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(prayerTime?.cityName ?: "Mencari Lokasi...", color = TextWhite.copy(alpha = 0.9f), fontSize = 13.sp)
                Text(prayerTime?.date?.toString() ?: "-", color = TextWhite.copy(alpha = 0.9f), fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = prayerTime?.dhuhr ?: "--:--",
                color = TextWhite,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-2).sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = TextWhite.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Waktu Dzuhur Hari Ini",
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun PrayerTimesRow(prayerTime: PrayerTime?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PrayerTimeItem("Subuh", prayerTime?.fajr ?: "--:--", Icons.Default.WbSunny)
        PrayerTimeItem("Dzuhur", prayerTime?.dhuhr ?: "--:--", Icons.Default.WbSunny)
        PrayerTimeItem("Ashar", prayerTime?.asr ?: "--:--", Icons.Default.Cloud)
        PrayerTimeItem("Maghrib", prayerTime?.maghrib ?: "--:--", Icons.Default.Waves)
        PrayerTimeItem("Isya", prayerTime?.isha ?: "--:--", Icons.Default.NightsStay)
    }
}

@Composable
fun PrayerTimeItem(label: String, time: String, icon: ImageVector, isNext: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isNext) AccentYellow else TextWhite.copy(alpha = 0.7f),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = if (isNext) AccentYellow else TextWhite.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = time,
            color = if (isNext) AccentYellow else TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun VideoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Video UFA",
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = TextWhite,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun MenuGrid() {
    val items = listOf(
        MenuItem("Dzikir & Doa", Icons.Default.BackHand),
        MenuItem("Al-Quran", Icons.AutoMirrored.Filled.MenuBook),
        MenuItem("Sirah", Icons.Default.HistoryEdu),
        MenuItem("Rukun Islam", Icons.Default.NightsStay),
        MenuItem("Rukun Iman", Icons.Default.AutoAwesome),
        MenuItem("Asmaul Husna", Icons.Default.Star),
        MenuItem("Penuntut Ilmu", Icons.Default.Lightbulb),
        MenuItem("Fatwa Al-'Utsaimin", Icons.AutoMirrored.Filled.MenuBook),
        MenuItem("Tanya Ustadz", Icons.Default.QuestionAnswer),
        MenuItem("Umrah & Haji", Icons.Default.LocationCity),
        MenuItem("UFA Official", Icons.Default.AccountCircle),
        MenuItem("Maheer Travel", Icons.Default.AirplanemodeActive)
    )

    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        for (i in items.indices step 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (j in 0 until 4) {
                    if (i + j < items.size) {
                        MenuIconItem(items[i + j], modifier = Modifier.weight(1f))
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MenuIconItem(item: MenuItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .clickable { },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(CardBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = AccentYellow,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.title,
            color = TextWhite,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

data class MenuItem(val title: String, val icon: ImageVector)

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onShalatClick: () -> Unit = {},
    onDoaClick: () -> Unit = {},
    onIslamAIClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = DeepBlue,
        contentColor = TextWhite,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Beranda") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow,
                selectedTextColor = AccentYellow,
                unselectedIconColor = TextWhite.copy(alpha = 0.5f),
                unselectedTextColor = TextWhite.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "shalat",
            onClick = onShalatClick,
            icon = { Icon(Icons.Default.Mosque, contentDescription = null) },
            label = { Text("Shalat") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow, selectedTextColor = AccentYellow,
                unselectedIconColor = TextWhite.copy(alpha = 0.5f), unselectedTextColor = TextWhite.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "doa",
            onClick = onDoaClick,
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
            label = { Text("Doa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow, selectedTextColor = AccentYellow,
                unselectedIconColor = TextWhite.copy(alpha = 0.5f), unselectedTextColor = TextWhite.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "islamAI",
            onClick = onIslamAIClick,
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
            label = { Text("IslamAI") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentYellow, selectedTextColor = AccentYellow,
                unselectedIconColor = TextWhite.copy(alpha = 0.5f), unselectedTextColor = TextWhite.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
    }
}
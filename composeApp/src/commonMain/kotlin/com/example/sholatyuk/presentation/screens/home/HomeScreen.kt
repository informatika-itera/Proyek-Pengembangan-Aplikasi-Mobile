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
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToShalat: () -> Unit = {},
    onNavigateToDoa: () -> Unit = {},
    onNavigateToIslamAI: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToKajianNotes: () -> Unit = {},
    onNavigateToQibla: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userName by profileViewModel.userName.collectAsState()
    val isLightModeEnabled by profileViewModel.isLightModeEnabled.collectAsState()

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
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "home",
                onHomeClick = {},
                onShalatClick = onNavigateToShalat,
                onDoaClick = onNavigateToDoa,
                onIslamAIClick = onNavigateToIslamAI,
                isLightMode = isLightModeEnabled
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = if (isLightModeEnabled) {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE0F2F1), Color(0xFFF5F5F5)),
                            startY = 0f,
                            endY = 1200f
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(DarkTeal, DeepBlue),
                            startY = 0f,
                            endY = 1200f
                        )
                    }
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    HeaderSection(
                        userName = userName,
                        onProfileClick = onNavigateToProfile,
                        isLightMode = isLightModeEnabled
                    )
                }

                if (uiState.isLoading) {
                    item {
                        Text(
                            text = "Mencari lokasi dan jadwal sholat...",
                            color = if (isLightModeEnabled) DeepBlue else AccentYellow,
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
                                    tint = if (isLightModeEnabled) Color.Red else AccentYellow,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.error ?: "Terjadi kesalahan",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ketuk area ini untuk mencoba lagi",
                                    color = if (isLightModeEnabled) DeepBlue else AccentYellow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item { 
                    PrayerClockCard(
                        currentTime = uiState.currentTime,
                        prayerTime = uiState.prayerTime, 
                        isLightMode = isLightModeEnabled,
                        onClick = onNavigateToShalat
                    ) 
                }
                item { PrayerTimesRow(uiState.prayerTime, isLightModeEnabled) }

                item {
                    KajianNotesEntryCard(onClick = onNavigateToKajianNotes, isLightMode = isLightModeEnabled)
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            title = "Doa",
                            subtitle = "Kumpulan Doa",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToDoa,
                            isLightMode = isLightModeEnabled
                        )
                        ActionCard(
                            title = "Kiblat",
                            subtitle = "Arah Ka'bah",
                            icon = Icons.Default.Explore,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToQibla,
                            isLightMode = isLightModeEnabled
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun KajianNotesEntryCard(onClick: () -> Unit, isLightMode: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLightMode) Color.White else CardBackground
        ),
        border = BorderStroke(1.dp, (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLightMode) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background((if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    tint = if (isLightMode) DeepBlue else AccentYellow,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Catatan Kajian",
                    color = if (isLightMode) Color.Black else TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kelola ringkasan ilmu di sini",
                    color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isLightMode) DeepBlue else AccentYellow
            )
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isLightMode: Boolean
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLightMode) Color.White else CardBackground
        ),
        border = BorderStroke(1.dp, (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLightMode) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background((if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isLightMode) DeepBlue else AccentYellow,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = if (isLightMode) Color.Black else TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// ====================================================================

@Composable
fun HeaderSection(userName: String = "Umar Faruq", onProfileClick: () -> Unit = {}, isLightMode: Boolean) {
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
                text = "SholatYuk",
                color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "SholatYuk",
                color = if (isLightMode) Color.Black else TextWhite,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isLightMode) Icons.Default.LightMode else Icons.Default.Brightness3,
                contentDescription = null,
                tint = if (isLightMode) DeepBlue else AccentYellow,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isLightMode) DeepBlue else Color.Black)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PrayerClockCard(
    currentTime: String,
    prayerTime: PrayerTime?, 
    isLightMode: Boolean,
    onClick: () -> Unit = {}
) {
    val nextPrayerReminder = getNextPrayerReminder(prayerTime)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = (if (isLightMode) Color.Black else Color.White).copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, (if (isLightMode) Color.Black else Color.White).copy(alpha = 0.1f))
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
                Text(
                    prayerTime?.cityName ?: "Mencari Lokasi...", 
                    color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.9f), 
                    fontSize = 13.sp
                )
                Text(
                    prayerTime?.date?.toString() ?: "-", 
                    color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.9f), 
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = currentTime,
                color = if (isLightMode) Color.Black else TextWhite,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
            
            if (nextPrayerReminder != null) {
                Text(
                    text = nextPrayerReminder,
                    color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Waktu Lokal Saat Ini",
                    color = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun PrayerTimesRow(prayerTime: PrayerTime?, isLightMode: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val prayers = listOf(
            Triple("Subuh", prayerTime?.fajr ?: "--:--", Icons.Default.WbSunny),
            Triple("Dzuhur", prayerTime?.dhuhr ?: "--:--", Icons.Default.WbSunny),
            Triple("Ashar", prayerTime?.asr ?: "--:--", Icons.Default.Cloud),
            Triple("Maghrib", prayerTime?.maghrib ?: "--:--", Icons.Default.Waves),
            Triple("Isya", prayerTime?.isha ?: "--:--", Icons.Default.NightsStay)
        )

        prayers.forEach { (label, time, icon) ->
            PrayerTimeItem(
                label = label,
                time = time,
                icon = icon,
                remaining = if (time != "--:--") calculateRemainingFor(time, label) else null,
                isLightMode = isLightMode
            )
        }
    }
}

@Composable
fun PrayerTimeItem(
    label: String, 
    time: String, 
    icon: ImageVector, 
    isNext: Boolean = false, 
    remaining: String? = null,
    isLightMode: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isNext) (if (isLightMode) DeepBlue else AccentYellow) else (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.7f),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = if (isNext) (if (isLightMode) DeepBlue else AccentYellow) else (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = time,
            color = if (isNext) (if (isLightMode) DeepBlue else AccentYellow) else (if (isLightMode) Color.Black else TextWhite),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        if (remaining != null) {
            Text(
                text = remaining,
                color = (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.5f),
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                lineHeight = 9.sp,
                modifier = Modifier.padding(top = 2.dp).widthIn(max = 60.dp)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onShalatClick: () -> Unit = {},
    onDoaClick: () -> Unit = {},
    onIslamAIClick: () -> Unit = {},
    isLightMode: Boolean = false
) {
    NavigationBar(
        containerColor = if (isLightMode) Color.White else DeepBlue,
        contentColor = if (isLightMode) Color.Black else TextWhite,
        tonalElevation = 8.dp
    ) {
        val selectedColor = if (isLightMode) DeepBlue else AccentYellow
        val unselectedColor = (if (isLightMode) Color.Black else TextWhite).copy(alpha = 0.5f)
        
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Beranda") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor,
                selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "shalat",
            onClick = onShalatClick,
            icon = { Icon(Icons.Default.Mosque, contentDescription = null) },
            label = { Text("Shalat") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor, selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor, unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "doa",
            onClick = onDoaClick,
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
            label = { Text("Doa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor, selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor, unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentRoute == "islamAI",
            onClick = onIslamAIClick,
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
            label = { Text("IslamAI") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = selectedColor, selectedTextColor = selectedColor,
                unselectedIconColor = unselectedColor, unselectedTextColor = unselectedColor,
                indicatorColor = Color.Transparent
            )
        )
    }
}

private fun getNextPrayerReminder(prayerTime: PrayerTime?): String? {
    if (prayerTime == null) return null
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentMinutes = now.hour * 60 + now.minute
    
    val prayers = listOf(
        "Subuh" to prayerTime.fajr,
        "Dzuhur" to prayerTime.dhuhr,
        "Ashar" to prayerTime.asr,
        "Maghrib" to prayerTime.maghrib,
        "Isya" to prayerTime.isha
    )
    
    val next = prayers.find {
        val parts = it.second.split(":")
        if (parts.size < 2) false
        else (parts[0].trim().toInt() * 60 + parts[1].trim().toInt()) > currentMinutes
    } ?: prayers.first()
    
    val parts = next.second.split(":")
    val pMinutes = parts[0].trim().toInt() * 60 + parts[1].trim().toInt()
    
    var diff = pMinutes - currentMinutes
    if (diff < 0) diff += 24 * 60
    
    if (diff == 0) return "Waktunya ${next.first}"
    
    val h = diff / 60
    val m = diff % 60
    
    return buildString {
        if (h > 0) append("$h jam ")
        if (m > 0) append("$m menit ")
        append("lagi menuju waktu ${next.first.lowercase()}")
    }
}

private fun calculateRemainingFor(timeStr: String, label: String): String? {
    try {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinutes = now.hour * 60 + now.minute
        val parts = timeStr.split(":")
        val pMinutes = parts[0].trim().toInt() * 60 + parts[1].trim().toInt()
        
        var diff = pMinutes - currentMinutes
        if (diff < 0) diff += 24 * 60
        
        if (diff == 0) return "Waktunya"
        
        val h = diff / 60
        val m = diff % 60
        
        return buildString {
            if (h > 0) append("${h}j ")
            if (m > 0) append("${m}m ")
            append("lagi")
        }
    } catch (e: Exception) {
        return null
    }
}

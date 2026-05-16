package com.example.sholatyuk.presentation.screens.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.sholatyuk.presentation.screens.home.BottomNavigationBar
import com.example.sholatyuk.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    onNavigateToHome: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Jadwal Shalat",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkTeal, Color.Transparent)
                    )
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "shalat",
                onHomeClick = onNavigateToHome,
                onShalatClick = {}
            )
        },
        containerColor = DeepBlue
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { PrayerHeaderCard() }
            item { DateNavigationSection() }
            item { PrayerTimeList() }
        }
    }
}

@Composable
fun PrayerHeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "20:36:48",
                color = TextWhite,
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Surabaya, Kecamatan Kedaton - Lampung",
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { }
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = LightTeal, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Ganti Lokasi",
                    color = LightTeal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = TextWhite),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = DeepBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kalender", color = DeepBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = TextWhite),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Mosque, contentDescription = null, tint = DeepBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Kiblat", color = DeepBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DateNavigationSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = LightTeal, modifier = Modifier.size(32.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sabtu, 16 Mei 2026",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "29 Dhu Al-Qi'dah 1447H",
                color = TextWhite.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = LightTeal, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun PrayerTimeList() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            PrayerListItem("Subuh", "04:41", Icons.Default.WbSunny, true)
            PrayerListItem("Terbit (Syuruq) + 15 Menit", "06:14", Icons.Default.WbSunny, false, isSpecial = true)
            PrayerListItem("Dzuhur", "11:58", Icons.Default.WbSunny, true)
            PrayerListItem("Ashar", "15:21", Icons.Default.WbCloudy, true)
            PrayerListItem("Maghrib", "17:54", Icons.Default.Waves, true)
            PrayerListItem("Isya", "19:07", Icons.Default.NightsStay, true)
            PrayerListItem("Tengah Malam", "23:54", Icons.Default.NightsStay, false, isRed = true)
            PrayerListItem("1/3 Malam Terakhir", "01:29", Icons.Default.NightsStay, false, isRed = true)
        }
    }
}

@Composable
fun PrayerListItem(
    name: String,
    time: String,
    icon: ImageVector,
    isAlarmOn: Boolean,
    isSpecial: Boolean = false,
    isRed: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSpecial) AccentYellow else if (isRed) TextWhite.copy(alpha = 0.5f) else LightTeal,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            color = if (isSpecial) AccentYellow else TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = time,
            color = if (isSpecial) AccentYellow else TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = if (isAlarmOn) Icons.Outlined.NotificationsActive else Icons.Default.NotificationsOff,
            contentDescription = null,
            tint = if (isSpecial) AccentYellow else if (isRed) Color.Red.copy(alpha = 0.6f) else TextWhite.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

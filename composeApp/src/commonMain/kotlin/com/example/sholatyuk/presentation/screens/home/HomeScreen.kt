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
import com.example.sholatyuk.presentation.theme.*

@Composable
fun HomeScreen(
    onNavigateToAddNote: () -> Unit = {},
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToAI: () -> Unit = {}
) {
    Scaffold(
        bottomBar = { BottomNavigationBar() },
        containerColor = DeepBlue
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DarkTeal, DeepBlue),
                        startY = 0f,
                        endY = 1200f
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item { HeaderSection() }
                item { PrayerClockCard() }
                item { PrayerTimesRow() }
                item { VideoBanner() }
                item { MenuGrid() }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun HeaderSection() {
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
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "UF",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PrayerClockCard() {
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
                Text("Sabtu, 16 Mei 2026", color = TextWhite.copy(alpha = 0.9f), fontSize = 13.sp)
                Text("29 Dhu Al-Qi'dah 1447H", color = TextWhite.copy(alpha = 0.9f), fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "20:36",
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
                    text = "± 8 jam 4 menit lagi menuju waktu Subuh",
                    color = TextWhite.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun PrayerTimesRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        PrayerTimeItem("Subuh", "04:41", Icons.Default.WbSunny, isNext = true)
        PrayerTimeItem("Dzuhur", "11:58", Icons.Default.WbSunny)
        PrayerTimeItem("Ashar", "15:21", Icons.Default.Cloud)
        PrayerTimeItem("Maghrib", "17:54", Icons.Default.Waves)
        PrayerTimeItem("Isya", "19:07", Icons.Default.NightsStay)
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
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = DeepBlue,
        contentColor = TextWhite,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
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
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Mosque, contentDescription = null) },
            label = { Text("Shalat") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextWhite.copy(alpha = 0.5f),
                unselectedTextColor = TextWhite.copy(alpha = 0.5f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Kajian") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextWhite.copy(alpha = 0.5f),
                unselectedTextColor = TextWhite.copy(alpha = 0.5f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Apps, contentDescription = null) },
            label = { Text("Lainnya") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextWhite.copy(alpha = 0.5f),
                unselectedTextColor = TextWhite.copy(alpha = 0.5f)
            )
        )
    }
}

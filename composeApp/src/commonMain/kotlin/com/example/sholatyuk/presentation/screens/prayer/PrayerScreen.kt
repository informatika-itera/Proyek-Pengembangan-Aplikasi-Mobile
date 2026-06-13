package com.example.sholatyuk.presentation.screens.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.domain.model.PrayerTime
import com.example.sholatyuk.presentation.screens.home.BottomNavigationBar
import com.example.sholatyuk.presentation.theme.*
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PrayerScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToShalat: () -> Unit = {},
    onNavigateToDoa: () -> Unit = {},
    onNavigateToIslamAI: () -> Unit = {},
    viewModel: PrayerViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLightModeEnabled by profileViewModel.isLightModeEnabled.collectAsState()
    
    // States for interactive list
    var manuallySelectedPrayer by remember { mutableStateOf<String?>(null) }
    val checkedPrayers = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "shalat",
                onHomeClick = onNavigateToHome,
                onShalatClick = onNavigateToShalat,
                onDoaClick = onNavigateToDoa,
                onIslamAIClick = onNavigateToIslamAI,
                isLightMode = isLightModeEnabled
            )
        },
        containerColor = if (isLightModeEnabled) Color(0xFFF5F5F5) else DeepBlue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Section with Clock and Next Prayer
            TopHeaderSection(uiState.prayerTime, isLightModeEnabled)

            // Main Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(if (isLightModeEnabled) Color.White else CardBackground)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)
                ) {
                    // Hijri Date Selector
                    item {
                        HijriDateRow(
                            date = uiState.prayerTime?.hijriDate ?: "Loading...",
                            isLightMode = isLightModeEnabled,
                            onPreviousClick = { viewModel.onPreviousDate() },
                            onNextClick = { viewModel.onNextDate() }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    // Prayer List
                    uiState.prayerTime?.let { time ->
                        val currentActivePrayer = getCurrentPrayer(time)
                        
                        val prayers = listOf(
                            Triple("Subuh", time.fajr, Icons.Default.NightsStay),
                            Triple("Matahari terbit", time.sunrise, Icons.Default.WbTwilight),
                            Triple("Dhuhr", time.dhuhr, Icons.Default.WbSunny),
                            Triple("Asr", time.asr, Icons.Default.WbSunny),
                            Triple("Maghrib", time.maghrib, Icons.Default.WbCloudy),
                            Triple("Isha", time.isha, Icons.Default.NightsStay)
                        )

                        prayers.forEach { (name, pTime, icon) ->
                            val isCurrentTimeActive = currentActivePrayer == (if (name == "Matahari terbit") "Subuh" else name)
                            val isHighlighted = manuallySelectedPrayer == name || (manuallySelectedPrayer == null && isCurrentTimeActive)

                            item {
                                PrayerItemRow(
                                    name = name,
                                    time = pTime,
                                    icon = icon,
                                    isActive = isHighlighted,
                                    isChecked = checkedPrayers[name] ?: false,
                                    isLightMode = isLightModeEnabled,
                                    onItemClick = { manuallySelectedPrayer = name },
                                    onActionClick = { /* Toggle volume logic here if needed */ },
                                    onCheckedChange = { checkedPrayers[name] = it }
                                )
                            }
                        }
                    }

                    if (uiState.isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = if (isLightModeEnabled) DeepBlue else AccentYellow)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeaderSection(prayerTime: PrayerTime?, isLightMode: Boolean) {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentTime = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
    val currentPrayer = prayerTime?.let { getCurrentPrayer(it) } ?: "---"
    val nextReminder = prayerTime?.let { getNextReminderText(it) } ?: "Memuat..."

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isLightMode) {
                        listOf(Color(0xFF4DB6AC), Color(0xFF00897B))
                    } else {
                        listOf(DarkTeal, DeepBlue)
                    }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentPrayer,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-2).sp
            )
            Text(
                text = nextReminder,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun HijriDateRow(
    date: String,
    isLightMode: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "Previous Day",
                tint = if (isLightMode) Color.DarkGray else AccentYellow
            )
        }
        Text(
            text = date,
            color = if (isLightMode) Color.Black else TextWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(onClick = onNextClick) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Next Day",
                tint = if (isLightMode) Color.DarkGray else AccentYellow
            )
        }
    }
}

@Composable
fun PrayerItemRow(
    name: String,
    time: String,
    icon: ImageVector,
    isActive: Boolean,
    isChecked: Boolean,
    isLightMode: Boolean,
    onItemClick: () -> Unit,
    onActionClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val highlightColor = if (isLightMode) Color(0xFFE0F2F1) else AccentYellow.copy(alpha = 0.15f)
    val contentColor = if (isActive) {
        if (isLightMode) Color(0xFF00695C) else AccentYellow
    } else {
        if (isLightMode) Color.Black else TextWhite
    }

    val iconTintColor = when (name) {
        "Subuh" -> Color(0xFF27AE60)
        "Matahari terbit" -> Color(0xFF2ECC71)
        "Dhuhr" -> Color(0xFF1ABC9C)
        "Asr" -> Color(0xFF3498DB)
        "Maghrib" -> Color(0xFF2980B9)
        "Isha" -> Color(0xFF34495E)
        else -> contentColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isActive) highlightColor else Color.Transparent)
            .clickable { onItemClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTintColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            color = if (isLightMode) Color.Black else TextWhite,
            fontSize = 17.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        
        // Volume/Notification toggle button
        IconButton(
            onClick = onActionClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (name != "Matahari terbit") Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.Notifications,
                contentDescription = "Toggle Notification",
                tint = (if (isLightMode) DeepBlue else (if (isActive) AccentYellow else Color.Gray)).copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp)
            )
        }
        
        Text(
            text = time,
            color = if (isLightMode) Color.Black else TextWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Checkbox implementation
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 2.dp,
                    color = if (isChecked) (if (isLightMode) DeepBlue else AccentYellow) else Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp)
                )
                .background(if (isChecked) (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.1f) else Color.Transparent)
                .clickable { onCheckedChange(!isChecked) },
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = if (isLightMode) DeepBlue else AccentYellow,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun getCurrentPrayer(time: PrayerTime): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentMinutes = now.hour * 60 + now.minute
    
    val prayers = listOf(
        "Subuh" to time.fajr,
        "Terbit" to time.sunrise,
        "Dhuhr" to time.dhuhr,
        "Asr" to time.asr,
        "Maghrib" to time.maghrib,
        "Isya" to time.isha
    )
    
    var lastMatch = "Isya"
    for (prayer in prayers) {
        val parts = prayer.second.split(":")
        if (parts.size == 2) {
            val pMinutes = parts[0].trim().toInt() * 60 + parts[1].trim().toInt()
            if (currentMinutes >= pMinutes) {
                lastMatch = prayer.first
            } else {
                break
            }
        }
    }
    return if (lastMatch == "Terbit") "Subuh" else lastMatch
}

private fun getNextReminderText(time: PrayerTime): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentMinutes = now.hour * 60 + now.minute
    
    val prayers = listOf(
        "Subuh" to time.fajr,
        "Dhuhr" to time.dhuhr,
        "Asr" to time.asr,
        "Maghrib" to time.maghrib,
        "Isya" to time.isha
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
    
    val h = diff / 60
    val m = diff % 60
    
    return "Sholat berikutnya ${h.toString().padStart(2, '0')} : ${m.toString().padStart(2, '0')}"
}

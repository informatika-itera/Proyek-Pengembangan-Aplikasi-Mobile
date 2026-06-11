package com.example.sholatyuk.presentation.screens.prayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.presentation.screens.home.BottomNavigationBar
import com.example.sholatyuk.presentation.theme.*
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToDoa: () -> Unit = {},
    onNavigateToIslamAI: () -> Unit = {},
    viewModel: PrayerViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLightModeEnabled by profileViewModel.isLightModeEnabled.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "shalat",
                onHomeClick = onNavigateToHome,
                onShalatClick = {},
                onDoaClick = onNavigateToDoa,
                onIslamAIClick = onNavigateToIslamAI,
                isLightMode = isLightModeEnabled
            )
        },
        containerColor = Color.Transparent // Background handle by Box with Gradient
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Header (Consistently styled with Home, Doa, and IslamAI)
                item {
                    Column(
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                    ) {
                        Text(
                            text = "JADWAL",
                            color = (if (isLightModeEnabled) Color.Black else TextWhite).copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Shalat",
                            color = if (isLightModeEnabled) Color.Black else TextWhite,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Tampilan Loading atau Error
                if (uiState.isLoading) {
                    item {
                        Text(
                            text = "Mencari lokasi...",
                            color = if (isLightModeEnabled) DeepBlue else AccentYellow,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (uiState.error != null) {
                    item {
                        Text(
                            text = uiState.error ?: "Terjadi kesalahan",
                            color = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Tampilan Jadwal Sholat
                uiState.prayerTime?.let { time ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = (if (isLightModeEnabled) Color.Black else Color.White).copy(alpha = 0.05f)
                            ),
                            border = BorderStroke(1.dp, (if (isLightModeEnabled) Color.Black else Color.White).copy(alpha = 0.1f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Lokasi: ${time.cityName}",
                                    color = if (isLightModeEnabled) Color.Black else TextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tanggal: ${time.date}",
                                    color = (if (isLightModeEnabled) Color.Black else TextWhite).copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    item { PrayerTimeRow("Imsak", time.imsak, isLightModeEnabled) }
                    item { PrayerTimeRow("Subuh", time.fajr, isLightModeEnabled) }
                    item { PrayerTimeRow("Dzuhur", time.dhuhr, isLightModeEnabled) }
                    item { PrayerTimeRow("Ashar", time.asr, isLightModeEnabled) }
                    item { PrayerTimeRow("Maghrib", time.maghrib, isLightModeEnabled) }
                    item { PrayerTimeRow("Isya", time.isha, isLightModeEnabled) }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun PrayerTimeRow(name: String, time: String, isLightMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLightMode) Color.White else CardBackground
        ),
        border = BorderStroke(1.dp, (if (isLightMode) DeepBlue else AccentYellow).copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLightMode) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                color = if (isLightMode) Color.Black else TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = time,
                color = if (isLightMode) DeepBlue else AccentYellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

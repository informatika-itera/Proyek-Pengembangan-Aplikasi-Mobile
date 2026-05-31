package com.example.sholatyuk.presentation.screens.prayer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.presentation.screens.home.BottomNavigationBar
import com.example.sholatyuk.presentation.theme.DeepBlue
import com.example.sholatyuk.presentation.theme.TextWhite
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToDoa: () -> Unit = {},
    onNavigateToIslamAI: () -> Unit = {},
    viewModel: PrayerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Jadwal Shalat", color = TextWhite, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DeepBlue)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "shalat",
                onHomeClick = onNavigateToHome,
                onShalatClick = {},
                onDoaClick = onNavigateToDoa,
                onIslamAIClick = onNavigateToIslamAI
            )
        },
        containerColor = DeepBlue
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tampilan Loading atau Error
            if (uiState.isLoading) {
                item { Text("Mencari lokasi...", color = Color.Yellow) }
            } else if (uiState.error != null) {
                item { Text(uiState.error ?: "", color = Color.Red) }
            }

            // Tampilan Jadwal Sholat
            uiState.prayerTime?.let { time ->
                item { Text("Lokasi: ${time.cityName}", color = TextWhite, fontSize = 18.sp) }
                item { Text("Tanggal: ${time.date}", color = TextWhite.copy(alpha = 0.7f)) }
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item { PrayerTimeRow("Imsak", time.imsak) }
                item { PrayerTimeRow("Subuh", time.fajr) }
                item { PrayerTimeRow("Dzuhur", time.dhuhr) }
                item { PrayerTimeRow("Ashar", time.asr) }
                item { PrayerTimeRow("Maghrib", time.maghrib) }
                item { PrayerTimeRow("Isya", time.isha) }
            }
        }
    }
}

@Composable
fun PrayerTimeRow(name: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.1f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Text(time, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
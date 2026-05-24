package com.example.fitkos.presentation.screens.watertracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.example.fitkos.domain.model.WaterLog
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerScreen(
    onNavigateBack: () -> Unit,
    viewModel: WaterTrackerViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val waterCount = state.amount
    val target = state.target

    val waterBlue = Color(0xFF2196F3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tracker Air", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Target Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Target Harian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$target gelas per hari",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Circular Progress Section
            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF0F0F0),
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                
                // Progress Foreground
                CircularProgressIndicator(
                    progress = { if (target > 0) (waterCount.toFloat() / target).coerceIn(0f, 1f) else 0f },
                    modifier = Modifier.fillMaxSize(),
                    color = waterBlue,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Glass Icon (Using a custom box to look like the UI)
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = null,
                        tint = waterBlue.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "$waterCount/$target",
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "gelas",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = "Kamu sudah minum ${waterCount * 250} ml",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Button(
                onClick = { viewModel.addGlass() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = waterBlue
                )
            ) {
                Text("+ 1 Gelas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // History Section (Riwayat Hari Ini)
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Riwayat Hari Ini",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(8) { index ->
                        val isFilled = index < waterCount
                        Icon(
                            imageVector = Icons.Default.LocalDrink,
                            contentDescription = null,
                            tint = if (isFilled) waterBlue else Color(0xFFE0E0E0),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                // Optional: Numbers below icons to match UI exactly if needed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(8) { index ->
                        Text(
                            text = "${index + 1}",
                            modifier = Modifier.width(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}



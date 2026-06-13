package com.example.sholatyuk.presentation.screens.doa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.presentation.theme.*
import com.example.sholatyuk.presentation.screens.profile.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoaDetailScreen(
    doaId: Long,
    onNavigateBack: () -> Unit,
    viewModel: DoaViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val isLightModeEnabled by profileViewModel.isLightModeEnabled.collectAsState()

    // Mencari data Doa berdasarkan ID yang diklik
    val doaList by viewModel.filteredDoaList.collectAsState()
    val doa = doaList.find { it.id.toLong() == doaId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Doa", color = if (isLightModeEnabled) Color.Black else Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = if (isLightModeEnabled) Color.Black else Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = if (isLightModeEnabled) Color(0xFFF5F5F5) else DeepBlue
    ) { paddingValues ->
        if (doa == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Judul Doa
                Text(
                    text = doa.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLightModeEnabled) DeepBlue else AccentYellow
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Kartu Tulisan Arab
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isLightModeEnabled) Color.White else CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isLightModeEnabled) 4.dp else 0.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = doa.arabic,
                            fontSize = 32.sp, // Ukuran teks Arab diperbesar
                            color = if (isLightModeEnabled) Color.Black else TextWhite,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = 48.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Kartu Tulisan Latin
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isLightModeEnabled) Color.White else CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isLightModeEnabled) 4.dp else 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Latin:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLightModeEnabled) DeepBlue else AccentYellow
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = doa.latin,
                            fontSize = 16.sp,
                            color = if (isLightModeEnabled) Color.Black else TextWhite,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Kartu Arti
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isLightModeEnabled) Color.White else CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isLightModeEnabled) 4.dp else 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Artinya:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLightModeEnabled) DeepBlue else AccentYellow
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = doa.translation,
                            fontSize = 16.sp,
                            color = if (isLightModeEnabled) Color.DarkGray else TextWhite.copy(alpha = 0.8f),
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
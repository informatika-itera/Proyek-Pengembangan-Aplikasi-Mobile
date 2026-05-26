package com.example.foodsaver.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodsaver.presentation.theme.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Pengguna", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = TextMain) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // User Header
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = PrimaryGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("FoodSaver User", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextMain)
            Text("user@foodsaver.id", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                color = PrimaryGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Belum Login", 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Statistik Inventory", fontWeight = FontWeight.Bold, color = TextMain)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("Aktif", state.totalItems.toString(), TextMain)
                        StatItem("Aman", state.safeCount.toString(), PrimaryGreen)
                        StatItem("Hampir", state.nearlyExpiredCount.toString(), WarningOrange)
                        StatItem("Expired", state.expiredCount.toString(), ExpiredRed)
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = BackgroundLight)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Dikonsumsi", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text("${state.consumedCount} Makanan", fontWeight = FontWeight.Bold, color = PrimaryGreen)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Dibuang", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text("${state.discardedCount} Makanan", fontWeight = FontWeight.Bold, color = ExpiredRed)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Pengaturan", fontWeight = FontWeight.Bold, color = TextMain, modifier = Modifier.padding(bottom = 12.dp, start = 8.dp))
                
                SettingsItem(icon = Icons.Outlined.Notifications, title = "Notifikasi & Reminder", subtitle = "Atur waktu pengingat kedaluwarsa")
                SettingsItem(icon = Icons.Outlined.Palette, title = "Tema Aplikasi", subtitle = "Terang / Gelap")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { /* Placeholder */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Login / Sign In", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(CardWhite, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = TextMain)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}

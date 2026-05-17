package com.example.metaforge.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToDraftSetup: () -> Unit, onNavigateToHeroList: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("METAFORGE", color = Color.White, fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineMedium, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center) {
            Text("COMMAND CENTER", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(24.dp))
            PremiumMenuCard("DRAFT SIMULATOR", "Simulate 5v5 draft with AI Assistant", Icons.Default.AutoAwesome, MaterialTheme.colorScheme.primary, onNavigateToDraftSetup)
            Spacer(modifier = Modifier.height(16.dp))
            PremiumMenuCard("HERO ENCYCLOPEDIA", "Meta Tier List, Counters & Synergies", Icons.Default.Leaderboard, MaterialTheme.colorScheme.secondary, onNavigateToHeroList)
        }
    }
}

@Composable
fun PremiumMenuCard(title: String, subtitle: String, icon: ImageVector, accentColor: Color, onClick: () -> Unit) {
    val gradient = Brush.horizontalGradient(colors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface))
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(gradient).border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .clickable { onClick() }.padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(accentColor.copy(alpha = 0.2f)).border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(subtitle, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
        }
    }
}
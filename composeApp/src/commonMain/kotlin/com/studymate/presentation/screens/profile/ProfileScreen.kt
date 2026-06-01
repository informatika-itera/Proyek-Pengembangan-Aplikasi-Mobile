package com.studymate.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studymate.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil Saya", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(
                        onClick = onThemeToggle,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, 
                            null,
                            tint = if (isDarkTheme) Color.Yellow else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // 1. Header Profil
            ProfileHeader(name = "Tengku Hafid", nim = "123140043", major = "Teknik Informatika")

            // 2. Warning Card (Countdown Ujian)
            WarningCard(examName = "UTS Pemrograman Mobile", daysLeft = 3)

            // 3. Learning Heatmap
            HeatmapSection()

            // 4. Badges Section
            BadgesSection()
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileHeader(name: String, nim: String, major: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(8.dp, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(3.dp, PrimaryLight)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Person, 
                        contentDescription = null, 
                        modifier = Modifier.size(64.dp), 
                        tint = PrimaryLight.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Edit Button Overlay
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { /* Edit */ },
                color = PrimaryLight,
                shadowElevation = 4.dp
            ) {
                Icon(
                    Icons.Default.Edit, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(
            "$major • $nim", 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun WarningCard(examName: String, daysLeft: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFF72585), Color(0xFFB5179E))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PriorityHigh, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Ujian Terdekat", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.labelMedium)
                Text(examName, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Black)
            }
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "$daysLeft Hari", 
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.Black, 
                    color = Color(0xFFF72585),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HeatmapSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Aktivitas Belajar", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
            Text("30 Hari Terakhir", style = MaterialTheme.typography.labelSmall, color = PrimaryLight)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Mockup Grid 7x15
                repeat(7) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 3.dp)) {
                        repeat(15) { col ->
                            val intensity = (0..4).random()
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (intensity == 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) 
                                        else SuccessStreak.copy(alpha = intensity * 0.25f)
                                    )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kurang", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    repeat(5) { i ->
                        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(SuccessStreak.copy(alpha = i * 0.25f + 0.1f)))
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rajin", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun BadgesSection() {
    val badges = listOf(
        BadgeItem("Pejuang Kuis", Icons.Default.EmojiEvents, true, Color(0xFFFFD60A)),
        BadgeItem("Master AI", Icons.Default.AutoAwesome, true, AIColorLight),
        BadgeItem("Konsisten", Icons.Default.LocalFireDepartment, false, Color(0xFFFF5400)),
        BadgeItem("Reviewer", Icons.Default.RateReview, false, PrimaryLight)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Pencapaian", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(badges) { badge ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(85.dp)) {
                    Surface(
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(if (badge.isEarned) 4.dp else 0.dp, CircleShape),
                        shape = CircleShape,
                        color = if (badge.isEarned) badge.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (badge.isEarned) BorderStroke(2.dp, badge.color) else null
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                badge.icon, 
                                contentDescription = null, 
                                tint = if (badge.isEarned) badge.color else Color.Gray.copy(alpha = 0.5f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        badge.name, 
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (badge.isEarned) FontWeight.Bold else FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = if (badge.isEarned) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }
            }
        }
    }
}

data class BadgeItem(val name: String, val icon: ImageVector, val isEarned: Boolean, val color: Color)

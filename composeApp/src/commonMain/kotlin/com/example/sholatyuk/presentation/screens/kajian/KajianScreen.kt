package com.example.sholatyuk.presentation.screens.kajian

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sholatyuk.presentation.screens.home.BottomNavigationBar
import com.example.sholatyuk.presentation.theme.*

@Composable
fun KajianScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToShalat: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = "islamAI",
                onHomeClick = onNavigateToHome,
                onShalatClick = onNavigateToShalat,
                onIslamAIClick = {}
            )
        },
        containerColor = DeepBlue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DeepBlue)
        ) {
            KajianHeader()
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(2) }) {
                    FeaturedKajianCard()
                }
                
                items(kajianItems) { item ->
                    KajianGridItem(item)
                }
            }
        }
    }
}

@Composable
fun KajianHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkTeal, DeepBlue)
                )
            )
            .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dzikir & Doa",
                color = TextWhite,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Kumpulan dzikir dan doa untuk semua aktivitas dan waktu",
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun FeaturedKajianCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Dzikir & Doa Pilihan Syaikh Abdur\nRazzaq Hafizahullah",
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.PanTool,
                contentDescription = null,
                tint = AccentYellow,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}

@Composable
fun KajianGridItem(item: KajianItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.title,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = AccentYellow,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

data class KajianItem(
    val title: String,
    val icon: ImageVector
)

val kajianItems = listOf(
    KajianItem("Dzikir\nPagi", Icons.Default.WbSunny),
    KajianItem("Dzikir\nPetang", Icons.Default.NightsStay),
    KajianItem("Kegiatan\nSehari-\nhari", Icons.Default.Restaurant),
    KajianItem("Ketika\nHaji &\nUmrah", Icons.Default.LocationCity),
    KajianItem("Dzikir &\nDoa\nShalat", Icons.Default.Mosque),
    KajianItem("Bacaan\nRuqyah", Icons.AutoMirrored.Filled.MenuBook),
    KajianItem("Doa\nKarena\nSebab", Icons.Default.PanTool),
    KajianItem("Dzikir\nSetiap\nSaat", Icons.Default.AccessTime),
    KajianItem("Dzikir\nSetelah\nShalat", Icons.Default.AutoAwesome),
    KajianItem("Dzikir\nyang\nDianjurka", Icons.Default.ThumbUp)
)

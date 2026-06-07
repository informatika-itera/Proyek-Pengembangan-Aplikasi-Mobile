package id.pusakakata.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToCollection: () -> Unit
) {
    val tokens by viewModel.tokens.collectAsState()
    val totalWords by viewModel.totalWords.collectAsState()
    val favoriteCount by viewModel.favoriteCount.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PROFIL PENGGUNA", style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 1.sp)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(androidx.compose.ui.graphics.Color.Transparent, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Placeholder
            Surface(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Pengguna Aplikasi", 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Belajar Kosakata Nusantara",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                label = "KOLEKSI",
                value = "$totalWords",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surface
            )
            StatCard(
                label = "FAVORIT",
                value = "$favoriteCount ❤️",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        StatCard(
            label = "JUMLAH TOKEN",
            value = "$tokens 🪙",
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToCollection,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.HistoryEdu, null)
            Spacer(Modifier.width(12.dp))
            Text("Galeri Mitologi", fontWeight = FontWeight.Bold)
        }
            
        Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "Versi 1.0.0 (Sprint 4 Polish)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        }
    }
}

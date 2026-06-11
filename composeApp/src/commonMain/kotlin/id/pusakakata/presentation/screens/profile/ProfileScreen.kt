package id.pusakakata.presentation.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
                title = { Text("PROFIL PENGGUNA", style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Black)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Pengaturan", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Enhanced Profile Section
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Pengembara Kata", 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Melestarikan Bahasa Nusantara",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Stats Section
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
                    value = "$favoriteCount",
                    icon = "❤️",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surface
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Smoother Token Wallet Card
            TokenWalletCard(tokens = tokens, onNavigateToCollection = onNavigateToCollection)
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "Pusaka Kata v1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TokenWalletCard(tokens: Long, onNavigateToCollection: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(12.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
        ) {
            // Background Decoration
            Icon(
                Icons.Default.Wallet, 
                null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = 20.dp),
                tint = Color.White.copy(alpha = 0.1f)
            )

            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "DOMPET TOKEN", 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedContent(
                            targetState = tokens,
                            transitionSpec = {
                                (slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut())
                                    .using(SizeTransform(clip = false))
                            }
                        ) { targetTokens ->
                            Text(
                                text = "$targetTokens",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("🪙", fontSize = 28.sp)
                    }
                }

                Button(
                    onClick = onNavigateToCollection,
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.HistoryEdu, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Galeri", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: String? = null,
    modifier: Modifier = Modifier,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                if (icon != null) {
                    Spacer(Modifier.width(4.dp))
                    Text(icon, fontSize = 16.sp)
                }
            }
        }
    }
}

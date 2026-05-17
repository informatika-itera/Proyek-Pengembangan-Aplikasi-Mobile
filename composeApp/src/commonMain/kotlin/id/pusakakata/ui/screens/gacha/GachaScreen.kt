package id.pusakakata.ui.screens.gacha

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.domain.model.Rarity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaScreen(
    viewModel: GachaViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusaka Gacha") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is GachaUiState.Idle -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Tarik Pusaka Keberuntunganmu!",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { viewModel.drawCard() },
                            modifier = Modifier.height(56.dp).fillMaxWidth(0.7f),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Panggil Pusaka", fontSize = 18.sp)
                        }
                    }
                }
                is GachaUiState.Drawing -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(strokeWidth = 6.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Membuka Gerbang Mitologi...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                is GachaUiState.Result -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        GachaResultCard(card = state.card)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { viewModel.reset() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GachaResultCard(card: LegendaryCard) {
    val rarityColor = when (card.rarity) {
        Rarity.MYTHIC -> Color(0xFFFFD700)
        Rarity.EPIC -> Color(0xFF9C27B0)
        Rarity.RARE -> Color(0xFF2196F3)
        Rarity.COMMON -> Color(0xFF9E9E9E)
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(rarityColor.copy(alpha = 0.2f), rarityColor.copy(alpha = 0.05f))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, rarityColor, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .background(gradientBrush)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = rarityColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = card.rarity.displayName.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (card.rarity == Rarity.MYTHIC) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = card.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Asal: ${card.origin}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = rarityColor.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = card.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

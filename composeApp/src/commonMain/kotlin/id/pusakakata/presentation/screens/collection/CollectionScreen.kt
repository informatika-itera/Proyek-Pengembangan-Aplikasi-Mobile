package id.pusakakata.presentation.screens.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.domain.model.LegendaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedCard by remember { mutableStateOf<LegendaryCard?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Galeri Mitologi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            items(uiState.allCards) { card ->
                val isCollected = uiState.collectedIds.contains(card.id)
                GalleryItem(
                    card = card,
                    isCollected = isCollected,
                    onClick = { if (isCollected) selectedCard = card }
                )
            }
        }

        if (selectedCard != null) {
            StoryDialog(
                card = selectedCard!!,
                onDismiss = { selectedCard = null }
            )
        }
    }
}

@Composable
fun GalleryItem(
    card: LegendaryCard,
    isCollected: Boolean,
    onClick: () -> Unit
) {
    val rarityColor = when (card.rarity.name) {
        "MYTHIC" -> Color(0xFFD4AF37)
        "EPIC" -> Color(0xFF9C27B0)
        "RARE" -> Color(0xFF2196F3)
        else -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(enabled = isCollected) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCollected) MaterialTheme.colorScheme.surface else Color.LightGray.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCollected) 4.dp else 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isCollected) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = rarityColor,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = card.rarity.displayName.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontSize = 8.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = card.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Ketuk untuk cerita",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 10.sp
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Lock, 
                        null, 
                        modifier = Modifier.size(32.dp).alpha(0.5f),
                        tint = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Terkunci", 
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StoryDialog(card: LegendaryCard, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Legenda ${card.name}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = card.fullStory,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                    textAlign = TextAlign.Justify
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        }
    )
}

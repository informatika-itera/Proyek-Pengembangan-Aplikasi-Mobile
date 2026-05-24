package id.pusakakata.ui.screens.gacha

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.pusakakata.domain.model.LegendaryCard
import id.pusakakata.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GachaScreen(
    viewModel: GachaViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val tokens by viewModel.tokens.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pusaka Gacha") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    Text("$tokens 🪙", modifier = Modifier.padding(16.dp))
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val state = uiState) {
                is GachaUiState.Idle -> {
                    Text("Tarik Pusaka Keberuntunganmu!", style = MaterialTheme.typography.titleLarge)
                    Text("Biaya: 1 Token", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.drawCard() },
                        enabled = tokens > 0,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("Tarik Gacha (1 🪙)")
                    }
                }
                is GachaUiState.Drawing -> {
                    LoadingIndicator()
                    Text("Memanggil Pusaka...")
                }
                is GachaUiState.Result -> {
                    CardResult(card = state.card)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.reset() }) {
                        Text("Tarik Lagi")
                    }
                }
                is GachaUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) { Text("Cari Token di Kuis") }
                }
            }
        }
    }
}

@Composable
fun CardResult(card: LegendaryCard) {
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "ANDA MENDAPATKAN:", style = MaterialTheme.typography.labelSmall)
            Text(text = card.rarity.displayName, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = card.name, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = card.description, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Asal: ${card.origin}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

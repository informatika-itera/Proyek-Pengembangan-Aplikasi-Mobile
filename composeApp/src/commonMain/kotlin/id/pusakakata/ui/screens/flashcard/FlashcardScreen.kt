package id.pusakakata.ui.screens.flashcard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.pusakakata.ui.components.EmptyState
import id.pusakakata.ui.components.LoadingIndicator
import id.pusakakata.ui.components.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asah Pusaka") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is FlashcardUiState.Loading -> LoadingIndicator()
                is FlashcardUiState.Empty -> EmptyState(
                    message = "Semua pusaka sudah diasah! Datang lagi besok.",
                    onAction = onBack,
                    actionLabel = "Kembali"
                )
                is FlashcardUiState.Success -> {
                    if (state.isFinished) {
                        EmptyState(
                            message = "Sesi belajar selesai!",
                            onAction = onBack,
                            actionLabel = "Kembali ke Menu"
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Kartu ${state.currentIndex + 1} dari ${state.words.size}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(Modifier.height(32.dp))
                            
                            val currentWord = state.currentWord
                            if (currentWord != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().height(300.dp),
                                    onClick = { viewModel.flipCard() }
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (state.isFlipped) {
                                            Text(
                                                currentWord.definition,
                                                modifier = Modifier.padding(24.dp),
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        } else {
                                            Text(
                                                currentWord.term,
                                                style = MaterialTheme.typography.displayMedium
                                            )
                                        }
                                    }
                                }
                                
                                if (state.isFlipped) {
                                    Spacer(Modifier.height(32.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Button(onClick = { viewModel.nextCard(1) }) { Text("Sulit") }
                                        Button(onClick = { viewModel.nextCard(3) }) { Text("Sedang") }
                                        Button(onClick = { viewModel.nextCard(5) }) { Text("Mudah") }
                                    }
                                } else {
                                    Spacer(Modifier.height(32.dp))
                                    Text("Ketuk kartu untuk melihat arti", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

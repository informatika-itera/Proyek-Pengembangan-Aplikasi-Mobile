package id.pusakakata.ui.screens.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.pusakakata.ui.components.EmptyState
import id.pusakakata.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kuis Pusaka AI") },
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
                is QuizUiState.Loading -> LoadingIndicator()
                is QuizUiState.Empty -> EmptyState(
                    message = "Pusaka anda belum cukup kuat. Cari minimal 3 kata di beranda untuk memulai kuis!",
                    onAction = onBack,
                    actionLabel = "Kembali ke Beranda"
                )
                is QuizUiState.Question -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            state.quizMessage, 
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Apa arti dari kata:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            state.word.term,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(32.dp))

                        state.options.forEach { option ->
                            OutlinedButton(
                                onClick = { viewModel.submitAnswer(option) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(option, modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
                is QuizUiState.Finished -> {
                    AlertDialog(
                        onDismissRequest = { /* Force selection */ },
                        title = { 
                            Text(if (state.isCorrect) "Jawaban Benar! 🎉" else "Jawaban Kurang Tepat ❌")
                        },
                        text = { 
                            Column {
                                Text(if (state.isCorrect) "Selamat! Anda mendapat +10 Token untuk Gacha." else "Jangan menyerah, terus asah ingatan anda!")
                                Spacer(Modifier.height(16.dp))
                                Text("Seberapa sulit tantangan ini bagi Anda?", style = MaterialTheme.typography.labelLarge)
                            }
                        },
                        confirmButton = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.updateSrsAndNext(1) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) { Text("Mudah") }
                                Button(
                                    onClick = { viewModel.updateSrsAndNext(2) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) { Text("Sedang") }
                                Button(
                                    onClick = { viewModel.updateSrsAndNext(3) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                                ) { Text("Susah") }
                            }
                        }
                    )
                }
            }
        }
    }
}

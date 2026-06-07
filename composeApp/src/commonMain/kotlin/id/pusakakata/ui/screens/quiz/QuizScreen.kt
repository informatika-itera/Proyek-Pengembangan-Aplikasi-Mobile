package id.pusakakata.ui.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                title = { Text("Asah Pengetahuan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                    )
                )
        ) {
            when (val state = uiState) {
                is QuizUiState.Loading -> LoadingIndicator()
                is QuizUiState.Empty -> EmptyState(
                    message = "Pusaka anda belum cukup kuat. Cari minimal 3 kata di beranda untuk memulai kuis!",
                    onAction = onBack,
                    actionLabel = "Kembali ke Beranda"
                )
                is QuizUiState.Question -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "TANTANGAN KATA", 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        Text("Apa makna dari kata:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            state.word.term,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black
                        )
                        
                        Spacer(Modifier.height(48.dp))

                        state.options.forEach { option ->
                            OptionCard(
                                text = option,
                                onClick = { viewModel.submitAnswer(option) }
                            )
                        }
                    }
                }
                is QuizUiState.Finished -> {
                    ResultDialog(
                        isCorrect = state.isCorrect,
                        onUpdateSrs = viewModel::updateSrsAndNext
                    )
                }
            }
        }
    }
}

@Composable
fun OptionCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text, 
            modifier = Modifier.padding(24.dp), 
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ResultDialog(isCorrect: Boolean, onUpdateSrs: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        shape = RoundedCornerShape(28.dp),
        title = { 
            Text(
                if (isCorrect) "Jawaban Benar! 🎉" else "Jawaban Kurang Tepat ❌",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = { 
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (isCorrect) "Selamat! Anda mendapat +10 Token." 
                    else "Jangan menyerah, terus asah ingatan Anda!",
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Text("Bagaimana tingkat kesulitannya?", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DifficultyOption("Mudah", 5, onUpdateSrs)
                DifficultyOption("Biasa", 3, onUpdateSrs)
                DifficultyOption("Sulit", 1, onUpdateSrs)
            }
        }
    )
}

@Composable
fun DifficultyOption(label: String, quality: Int, onClick: (Int) -> Unit) {
    Button(
        onClick = { onClick(quality) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when(quality) {
                5 -> MaterialTheme.colorScheme.primary
                3 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.tertiary
            }
        )
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}

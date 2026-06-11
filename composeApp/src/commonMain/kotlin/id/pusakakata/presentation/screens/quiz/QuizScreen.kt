package id.pusakakata.presentation.screens.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pusakakata.presentation.components.EmptyState
import id.pusakakata.presentation.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("TANTANGAN KATA", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
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
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                }
            ) { state ->
                when (state) {
                    is QuizUiState.Loading -> LoadingIndicator()
                    is QuizUiState.Empty -> EmptyState(
                        message = "Pusaka anda belum cukup kuat. Cari minimal 3 kata di beranda untuk memulai kuis!",
                        onAction = onBack,
                        actionLabel = "Kembali ke Beranda"
                    )
                    is QuizUiState.Question -> {
                        QuizQuestionContent(state, viewModel)
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
}

@Composable
fun QuizQuestionContent(state: QuizUiState.Question, viewModel: QuizViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.Help, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    "TEBAK DEFINISI", 
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Text(
            "Apa makna dari pusaka kata ini?", 
            style = MaterialTheme.typography.titleMedium, 
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), Color.Transparent)
                        )
                    )
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    state.word.term,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Black
                )
            }
        }
        
        Spacer(Modifier.height(48.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            state.options.forEach { option ->
                OptionCard(
                    text = option,
                    onClick = { viewModel.submitAnswer(option) }
                )
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun OptionCard(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(12.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {}
            
            Spacer(Modifier.width(16.dp))
            
            Text(
                text = text, 
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ResultDialog(isCorrect: Boolean, onUpdateSrs: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        shape = RoundedCornerShape(32.dp),
        icon = {
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
            )
        },
        title = { 
            Text(
                if (isCorrect) "Luar Biasa!" else "Belum Tepat",
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = { 
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    if (isCorrect) "Anda berhasil menebak dengan benar dan mendapatkan +1 Token Pusaka! 🪙"
                    else "Jangan berkecil hati, pusaka ini memang butuh waktu untuk dipahami.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.height(32.dp))
                HorizontalDivider(modifier = Modifier.alpha(0.1f))
                Spacer(Modifier.height(24.dp))
                Text(
                    "Bagaimana tingkat hafalannya?", 
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DifficultyOption("Sangat Hafal", 5, onUpdateSrs)
                DifficultyOption("Cukup Ingat", 3, onUpdateSrs)
                DifficultyOption("Masih Lupa", 1, onUpdateSrs)
            }
        }
    )
}

@Composable
fun DifficultyOption(label: String, quality: Int, onClick: (Int) -> Unit) {
    Button(
        onClick = { onClick(quality) },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when(quality) {
                5 -> MaterialTheme.colorScheme.primary
                3 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.tertiary
            },
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        )
    }
}

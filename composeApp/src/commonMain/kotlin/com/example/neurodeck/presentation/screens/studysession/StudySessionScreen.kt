package com.example.neurodeck.presentation.screens.studysession

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.neurodeck.domain.model.ReviewRating
import com.example.neurodeck.domain.usecase.CalculateNextReviewUseCase
import com.example.neurodeck.presentation.components.EmptyState
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.LoadingIndicator
import com.example.neurodeck.presentation.components.RatingButtonRow
import kotlinx.datetime.Clock
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Study Session screen — INTI aplikasi NeuroDeck.
 *
 * User flow:
 * 1. Load due cards untuk [deckId]
 * 2. Untuk setiap kartu:
 *    a. Tampilkan front, user pikirkan jawaban
 *    b. Tap kartu → reveal back
 *    c. User self-rate jawaban (Lupa/Sulit/Oke/Mudah)
 *    d. SM-2 algorithm hitung next due date
 *    e. Lanjut ke kartu berikutnya
 * 3. Selesai semua kartu → tampilkan summary
 *
 * Navigation contract:
 * - [onExit]: panggil saat user tap back button atau "Selesai"
 *
 * ViewModel di-inject dengan parameter [deckId]:
 *   koinViewModel { parametersOf(deckId) }
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudySessionScreen(
    deckId: Long,
    onExit: () -> Unit,
    viewModel: StudySessionViewModel = koinViewModel { parametersOf(deckId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Belajar") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when (val state = uiState) {
                StudySessionUiState.Loading -> LoadingIndicator()

                StudySessionUiState.NoCardsDue -> EmptyState(
                    emoji = "🎉",
                    title = "Tidak Ada Kartu Hari Ini",
                    description = "Semua kartu sudah ter-review. Kembali lagi nanti " +
                            "saat ada kartu yang due.",
                    primaryActionLabel = "Selesai",
                    onPrimaryAction = onExit,
                )

                is StudySessionUiState.ShowingCard -> ShowingCardContent(
                    state = state,
                    onFlip = viewModel::flipCard,
                    onRate = viewModel::rateCard,
                )

                is StudySessionUiState.Completed -> CompletedContent(
                    totalReviewed = state.totalReviewed,
                    onFinish = onExit,
                )

                is StudySessionUiState.Error -> ErrorMessage(
                    message = state.message,
                )
            }
        }
    }
}

@Composable
private fun ShowingCardContent(
    state: StudySessionUiState.ShowingCard,
    onFlip: () -> Unit,
    onRate: (com.example.neurodeck.domain.model.ReviewRating) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Progress bar di atas
        SessionProgress(
            current = state.cardsReviewed,
            total = state.totalCards,
        )

        // Kartu menempati ruang utama (weight 1f)
        Box(modifier = Modifier.weight(1f)) {
            Flashcard(
                front = state.currentCard.front,
                back = state.currentCard.back,
                showingBack = state.showingBack,
                onTap = onFlip,
            )
        }

        // Rating buttons di bawah, hanya muncul saat showingBack=true
        if (state.showingBack) {
            // Compute interval preview untuk setiap rating via SM-2 use case.
            // Stateless function — bisa dipanggil 4x tanpa side effect.
            val previews = computeIntervalPreviews(state.currentCard.reviewState)
            RatingButtonRow(
                onRate = onRate,
                intervalPreviews = previews,
            )
        }
    }
}

/**
 * Compute interval preview untuk semua 4 ratings via SM-2 dry-run.
 *
 * Setiap rating → SM-2 hitung future CardReviewState (tanpa actually persist) →
 * extract intervalDays → format ke string ringkas ("<1m", "10m", "6h", "4d").
 *
 * Pure function — re-computed di setiap recomposition saat showingBack=true.
 * Cheap (4x stateless function calls), tidak perlu remember/cache.
 */
@Composable
private fun computeIntervalPreviews(
    currentState: com.example.neurodeck.domain.model.CardReviewState,
): Map<ReviewRating, String> {
    val useCase = remember { CalculateNextReviewUseCase() }
    val now = remember { Clock.System.now() }
    return ReviewRating.entries.associateWith { rating ->
        val nextState = useCase(currentState, rating, now)
        formatIntervalShort(nextState.intervalDays)
    }
}

/**
 * Format interval (dalam hari) ke string ringkas untuk button label.
 *
 * Aturan:
 *   - 0 hari (immediate re-test) → "<1m" (kalau sangat singkat, simulate "less than 1 minute")
 *   - <1 hari → "Xh" (jam) — tapi karena SM-2 minimum 1 hari, ini rare
 *   - 1-30 hari → "Xd"
 *   - >30 hari → "Xmo" (bulan, approx 30 hari)
 *   - >365 hari → "Xy"
 */
private fun formatIntervalShort(intervalDays: Int): String = when {
    intervalDays < 1 -> "<1m"
    intervalDays < 30 -> "${intervalDays}d"
    intervalDays < 365 -> "${intervalDays / 30}mo"
    else -> "${intervalDays / 365}y"
}

@Composable
private fun SessionProgress(current: Int, total: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Kartu ${current + 1} dari $total",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        LinearProgressIndicator(
            progress = { if (total > 0) current.toFloat() / total else 0f },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CompletedContent(
    totalReviewed: Int,
    onFinish: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🎯",
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            text = "Session Selesai!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = "Anda telah me-review $totalReviewed kartu. " +
                    "Bagus! Kartu akan dijadwalkan ulang berdasarkan rating Anda.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
        Button(
            onClick = onFinish,
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
            modifier = Modifier.padding(top = 32.dp),
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Text(
                text = "Selesai",
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
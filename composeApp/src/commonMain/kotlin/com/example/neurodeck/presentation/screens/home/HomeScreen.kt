package com.example.neurodeck.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.neurodeck.presentation.components.EmptyState
import com.example.neurodeck.presentation.components.ErrorMessage
import com.example.neurodeck.presentation.components.LoadingIndicator
import com.example.neurodeck.presentation.components.SectionTitle
import com.example.neurodeck.presentation.screens.home.components.GreetingCard
import com.example.neurodeck.presentation.screens.home.components.QuickActionsRow
import com.example.neurodeck.presentation.screens.home.components.RecentDeckItem
import com.example.neurodeck.presentation.screens.home.components.StatsRow
import com.example.neurodeck.presentation.screens.home.components.TipsCard
import org.koin.compose.viewmodel.koinViewModel

// ════════════════════════════════════════════════════════════════════════════
// HomeScreen.kt — commonMain
//
// Sprint 2 — Prioritas 3c.4 (Home Tab Screen)
//
// Composable utama untuk 🏠 Home Tab.
//
// Layout (top to bottom, dalam scrollable LazyColumn):
//   1. GreetingCard         — "Selamat Pagi, Mahasiswa"
//   2. StatsRow             — 3 mini cards: Due / Streak / Hari Ini
//   3. QuickActionsRow      — 2 tombol: Deck Baru / Belajar Sekarang
//   4. Section "Continue Learning"
//      └─ Recent decks list (max 3)
//      OR EmptyState kalau belum ada deck
//   5. TipsCard             — tips of the day
//
// State handling:
//   - Loading        → LoadingIndicator (center)
//   - Error          → ErrorMessage dengan retry
//   - Success        → render full dashboard
//
// Navigation callbacks:
//   - onCreateDeck     → CreateDeck screen
//   - onStudyNow       → Decks tab (user pilih deck → study)
//   - onDeckClick(id)  → CardList screen
// ════════════════════════════════════════════════════════════════════════════

/**
 * Home Tab screen — dashboard ringkas + Continue Learning.
 *
 * @param onCreateDeck Lambda untuk navigate ke "Buat Deck Baru" flow.
 * @param onStudyNow   Lambda untuk navigate ke Decks tab (lalu user pilih deck).
 * @param onDeckClick  Lambda saat user tap recent deck card. Pass deckId.
 * @param viewModel    Injected via Koin. Override only for preview/test.
 */
@Composable
fun HomeScreen(
    onCreateDeck: () -> Unit,
    onStudyNow: () -> Unit,
    onDeckClick: (deckId: Long) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingIndicator()

        is HomeUiState.Error -> ErrorMessage(
            message = state.message,
            // Tidak ada retry — state akan auto-recover saat deck flow emit
            // lagi (e.g. user tambah deck di tab lain → flow trigger ulang
            // → ViewModel recompute). Pesan error sebagai info, bukan dead-end.
        )

        is HomeUiState.Success -> HomeSuccessContent(
            state = state,
            onCreateDeck = onCreateDeck,
            onStudyNow = onStudyNow,
            onDeckClick = onDeckClick,
        )
    }
}

/**
 * Konten utama saat state = Success. Dipisah jadi private composable
 * supaya HomeScreen() bersih (cuma when-branching).
 */
@Composable
private fun HomeSuccessContent(
    state: HomeUiState.Success,
    onCreateDeck: () -> Unit,
    onStudyNow: () -> Unit,
    onDeckClick: (Long) -> Unit,
) {
    // LazyColumn untuk vertical scroll. Pakai LazyColumn (vs. Column +
    // verticalScroll) supaya kalau Continue Learning list nanti banyak,
    // tetap performant. Untuk Home sekarang itemCount = 6-ish total,
    // tidak ada masalah perf — tapi pattern konsisten.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Section 1: Greeting
        item {
            GreetingCard(
                greeting = state.greeting,
                userName = state.userName,
            )
        }

        // Section 2: Stats Row
        item {
            StatsRow(
                dueCount = state.dueCardsCount,
                streakDays = state.streakDays,
                reviewedToday = state.reviewedToday,
            )
        }

        // Section 3: Quick Actions
        item {
            QuickActionsRow(
                onCreateDeck = onCreateDeck,
                onStudyNow = onStudyNow,
            )
        }

        // Section 4: Continue Learning header
        item {
            SectionTitle(text = "Continue Learning")
        }

        // Section 4: Recent decks OR empty hint
        if (state.recentDecks.isEmpty()) {
            item {
                EmptyState(
                    title = "Belum Ada Deck",
                    description = "Mulai dengan membuat deck pertama atau generate cards dari materi pakai AI.",
                    emoji = "📚",
                    primaryActionLabel = "Buat Deck Pertama",
                    onPrimaryAction = onCreateDeck,
                    // Override fillMaxSize default supaya tidak ambil seluruh
                    // screen — hanya jadi inline section di dalam LazyColumn.
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }
        } else {
            items(
                items = state.recentDecks,
                key = { it.id },
            ) { deck ->
                RecentDeckItem(
                    deck = deck,
                    onClick = { onDeckClick(deck.id) },
                )
            }
        }

        // Section 5: Tips of the day
        item {
            Spacer(modifier = Modifier.height(8.dp))
            TipsCard(tip = state.tipOfTheDay)
        }

        // Bottom padding supaya last item tidak nempel bottom nav
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
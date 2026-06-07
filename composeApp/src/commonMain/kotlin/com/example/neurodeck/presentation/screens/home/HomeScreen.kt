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
import com.example.neurodeck.presentation.screens.home.components.HeroCard
import com.example.neurodeck.presentation.screens.home.components.QuickActionsRow
import com.example.neurodeck.presentation.screens.home.components.RecentDeckItem
import com.example.neurodeck.presentation.screens.home.components.StatsRow
import com.example.neurodeck.presentation.screens.home.components.TipsCard
import org.koin.compose.viewmodel.koinViewModel


/**
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

        )

        is HomeUiState.Success -> HomeSuccessContent(
            state = state,
            onCreateDeck = onCreateDeck,
            onStudyNow = onStudyNow,
            onDeckClick = onDeckClick,
        )
    }
}

@Composable
private fun HomeSuccessContent(
    state: HomeUiState.Success,
    onCreateDeck: () -> Unit,
    onStudyNow: () -> Unit,
    onDeckClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        //Greeting + avatar
        item {
            GreetingCard(
                greeting = state.greeting,
                userName = state.userName,
                avatarUri = state.avatarUri,
            )
        }

        //Hero card : kartu jatuh tempo
        item {
            HeroCard(
                dueCount = state.dueCardsCount,
                reviewedToday = state.reviewedToday,
                onStudyNow = onStudyNow,
            )
        }

        //Stats Row (streak + kartu hari ini)
        item {
            StatsRow(
                streakDays = state.streakDays,
                reviewedToday = state.reviewedToday,
            )
        }

        //Quick Actions
        item {
            QuickActionsRow(
                onCreateDeck = onCreateDeck,
                onStudyNow = onStudyNow,
            )
        }

        //Deck Saya header
        item {
            SectionTitle(text = "Deck Saya")
        }

        //Recent decks OR empty hint
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
                key = { it.deck.id },
            ) { recentDeck ->
                RecentDeckItem(
                    deck = recentDeck.deck,
                    dueCount = recentDeck.dueCount,
                    onClick = { onDeckClick(recentDeck.deck.id) },
                )
            }
        }

        //Tips of the day
        item {
            Spacer(modifier = Modifier.height(8.dp))
            TipsCard(tip = state.tipOfTheDay)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

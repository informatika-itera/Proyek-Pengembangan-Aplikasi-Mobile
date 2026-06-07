package com.example.neurodeck.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.repository.ReviewRecordRepository
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        combine(
            deckRepository.observeAllDecks(),
            userPreferencesRepository.observeProfile(),
        ) { decks, profile -> decks to profile }
            .mapLatest { (decks, profile) ->
                computeSuccessState(decks, profile)
            }
            .catch { e ->
                emit(HomeUiState.Error(e.message ?: "Gagal memuat data Home"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading,
            )

    private suspend fun computeSuccessState(
        decks: List<Deck>,
        profile: UserProfile,
    ): HomeUiState {
        val now = Clock.System.now()
        val localNow = now.toLocalDateTime(TimeZone.currentSystemDefault())

        val dueCount = cardRepository.countAllDueCards(now).toInt()
        val streak = reviewRecordRepository.getStreakDays(now)
        val reviewedToday = reviewRecordRepository.getReviewedToday(now)

        // Top deck + jumlah due per deck
        val recentDecks = decks.take(MAX_RECENT_DECKS).map { deck ->
            val dueInDeck = cardRepository.observeDueCards(deck.id, now).first().size
            RecentDeckUi(deck = deck, dueCount = dueInDeck)
        }

        return HomeUiState.Success(
            greeting = computeGreeting(localNow),
            userName = profile.name,
            avatarUri = profile.avatarUri,
            dueCardsCount = dueCount,
            streakDays = streak,
            reviewedToday = reviewedToday,
            recentDecks = recentDecks,
            tipOfTheDay = computeTipOfTheDay(localNow),
        )
    }

    private companion object {
        const val MAX_RECENT_DECKS = 3
    }
}

// PURE HELPERS
internal fun computeGreeting(now: LocalDateTime): String = when (now.hour) {
    in 4..10 -> "Selamat Pagi"
    in 11..14 -> "Selamat Siang"
    in 15..17 -> "Selamat Sore"
    else -> "Selamat Malam"  // 18-03
}

internal fun computeTipOfTheDay(now: LocalDateTime): String {
    val index = now.dayOfYear % TIPS_POOL.size
    return TIPS_POOL[index]
}

private val TIPS_POOL: List<String> = listOf(
    "💡 Belajar 15 menit setiap hari lebih efektif daripada 2 jam sekali seminggu.",
    "🧠 SM-2 menjadwal review tepat saat kamu HAMPIR lupa — itulah kekuatan spaced repetition.",
    "✍️ Buat flashcard dari kata-katamu sendiri, jangan copy-paste dari materi.",
    "🎯 Fokus pada konsep yang sulit dulu — sisanya akan menyusul lebih mudah.",
    "⏰ Review kartu sebelum tidur — memori konsolidasi lebih baik saat REM sleep.",
    "🤖 Pakai AI Tutor untuk minta analogi sederhana kalau konsep terasa abstrak.",
    "📚 Pecah deck besar jadi sub-deck per topik — easier to manage & track progress.",
    "🔁 Konsistensi > intensitas. Streak 30 hari masing-masing 10 menit > marathon weekend.",
    "❓ Kalau jawab Again 3x berturut-turut, edit kartunya — mungkin pertanyaan kurang jelas.",
    "🌱 New cards dibatasi otomatis supaya tidak overwhelm — kualitas > kuantitas.",
)

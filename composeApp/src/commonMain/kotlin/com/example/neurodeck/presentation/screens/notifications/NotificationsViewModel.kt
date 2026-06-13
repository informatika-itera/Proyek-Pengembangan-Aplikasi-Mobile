package com.example.neurodeck.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock

/**
 * Satu notifikasi "deck siap dipelajari" — deck yang punya >= 1 kartu due.
 *
 * @property deck      Deck terkait (untuk navigasi ke study session).
 * @property dueCount  Jumlah kartu yang sudah waktunya di-review.
 */
data class StudyNotification(
    val deck: Deck,
    val dueCount: Int,
)

/**
 * State layar Notifikasi.
 *
 * @property isLoading      True saat masih memuat data awal.
 * @property notifications  Daftar deck yang siap dipelajari (dueCount > 0).
 */
data class NotificationsUiState(
    val isLoading: Boolean = true,
    val notifications: List<StudyNotification> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && notifications.isEmpty()
    val totalDueCards: Int get() = notifications.sumOf { it.dueCount }
}

/**
 * ViewModel layar Notifikasi.
 *
 * Logika: observe semua deck, lalu untuk tiap deck hitung jumlah kartu due
 * via [CardRepository.observeDueCards]. Deck dengan dueCount > 0 dijadikan
 * notifikasi "siap dipelajari". List reaktif — auto-update saat user belajar
 * (kartu jadi tidak due) atau saat ada kartu baru yang due.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NotificationsViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
) : ViewModel() {

    val uiState: StateFlow<NotificationsUiState> =
        deckRepository.observeAllDecks()
            .flatMapLatest { decks ->
                // Untuk tiap deck, hitung due cards pada waktu "sekarang".
                kotlinx.coroutines.flow.flow {
                    val now = Clock.System.now()
                    val result = decks.mapNotNull { deck ->
                        val dueCount = cardRepository
                            .observeDueCards(deck.id, now)
                            .first()
                            .size
                        if (dueCount > 0) StudyNotification(deck, dueCount) else null
                    }
                    emit(
                        NotificationsUiState(
                            isLoading = false,
                            notifications = result.sortedByDescending { it.dueCount },
                        ),
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NotificationsUiState(isLoading = true),
            )
}
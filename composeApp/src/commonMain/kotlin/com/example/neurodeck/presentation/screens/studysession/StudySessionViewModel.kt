package com.example.neurodeck.presentation.screens.studysession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.Card
import com.example.neurodeck.domain.model.ReviewRating
import com.example.neurodeck.domain.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * State untuk Study Session screen.
 *
 * Flow normal:
 * Loading → ShowingCard (front side) → ShowingCard (back side) → ShowingCard (next card) → ... → Completed
 *
 * showingBack: track apakah user sudah flip kartu. Tombol rating cuma muncul
 * saat showingBack=true (mencegah user rating tanpa lihat jawaban).
 */
sealed interface StudySessionUiState {
    data object Loading : StudySessionUiState
    data object NoCardsDue : StudySessionUiState
    data class ShowingCard(
        val currentCard: Card,
        val showingBack: Boolean,
        val totalCards: Int,
        val cardsReviewed: Int,
    ) : StudySessionUiState
    data class Completed(val totalReviewed: Int) : StudySessionUiState
    data class Error(val message: String) : StudySessionUiState
}

class StudySessionViewModel(
    private val deckId: Long,
    private val cardRepository: CardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudySessionUiState>(StudySessionUiState.Loading)
    val uiState: StateFlow<StudySessionUiState> = _uiState.asStateFlow()

    /**
     * Cards di-snapshot sekali di awal session, tidak reactive ke DB change.
     * Reason: kalau pas user mid-session lalu cardsList Flow emit ulang
     * (karena reviewCard update DB), UI akan jump-jump. Snapshot = stable session.
     */
    private var sessionCards: List<Card> = emptyList()
    private var currentIndex: Int = 0

    init {
        loadDueCards()
    }

    private fun loadDueCards() {
        viewModelScope.launch {
            try {
                val now = Clock.System.now()
                // .first() = ambil emisi pertama Flow lalu cancel collection
                sessionCards = cardRepository.observeDueCards(deckId, now).first()

                if (sessionCards.isEmpty()) {
                    _uiState.value = StudySessionUiState.NoCardsDue
                } else {
                    showCurrentCard()
                }
            } catch (e: Exception) {
                _uiState.value = StudySessionUiState.Error(
                    e.message ?: "Failed to load cards",
                )
            }
        }
    }

    private fun showCurrentCard() {
        _uiState.value = StudySessionUiState.ShowingCard(
            currentCard = sessionCards[currentIndex],
            showingBack = false,
            totalCards = sessionCards.size,
            cardsReviewed = currentIndex,
        )
    }

    /** User tap kartu untuk lihat jawaban. */
    fun flipCard() {
        val state = _uiState.value as? StudySessionUiState.ShowingCard ?: return
        _uiState.value = state.copy(showingBack = true)
    }

    /** User pilih rating setelah lihat jawaban. */
    fun rateCard(rating: ReviewRating) {
        val state = _uiState.value as? StudySessionUiState.ShowingCard ?: return
        if (!state.showingBack) return  // safety: harus flip dulu sebelum rate

        viewModelScope.launch {
            try {
                cardRepository.reviewCard(
                    cardId = state.currentCard.id,
                    rating = rating,
                    now = Clock.System.now(),
                )
                advanceToNextCard()
            } catch (e: Exception) {
                _uiState.value = StudySessionUiState.Error(
                    e.message ?: "Failed to save review",
                )
            }
        }
    }

    private fun advanceToNextCard() {
        currentIndex++
        if (currentIndex >= sessionCards.size) {
            _uiState.value = StudySessionUiState.Completed(
                totalReviewed = sessionCards.size,
            )
        } else {
            showCurrentCard()
        }
    }
}
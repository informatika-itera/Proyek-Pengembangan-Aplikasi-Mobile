package com.example.neurodeck.presentation.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.repository.ReviewRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * ViewModel untuk Stats Tab.
 *
 * Tugas:
 *   1. Fetch all stats berdasarkan period filter (default: 7 hari).
 *   2. Re-fetch saat user ganti period.
 *   3. Hitung CardStatusBreakdown in-memory dari semua kartu (SM-2 fields).
 *
 * Tidak pakai Flow.combine seperti HomeVM karena Stats benar-benar snapshot
 * (user buka tab → render → tidak perlu auto-update saat data berubah).
 * Re-fetch manual saat ganti period filter.
 */
class StatsViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats(StatsPeriod.DEFAULT)
    }

    /**
     * Public method untuk ganti period filter. Triggered dari chip onClick.
     */
    fun changePeriod(newPeriod: StatsPeriod) {
        loadStats(newPeriod)
    }

    private fun loadStats(period: StatsPeriod) {
        viewModelScope.launch {
            _uiState.value = StatsUiState.Loading
            try {
                val now = Clock.System.now()
                val tz = TimeZone.currentSystemDefault()

                // Tentukan range start berdasarkan period
                val rangeStart: Instant = if (period.days != null) {
                    // Pakai midnight start dari N hari lalu (inclusive hari ini = 7 hari window)
                    val startDate = now.toLocalDateTime(tz).date
                        .minus(period.days.toLong() - 1, DateTimeUnit.DAY)
                    startDate.atStartOfDayIn(tz)
                } else {
                    // All time = epoch
                    Instant.fromEpochMilliseconds(0L)
                }

                // Parallel fetch — bisa sequential karena query lokal cepat
                val totalReviews = reviewRecordRepository.countReviewsInRange(rangeStart, now)
                val streak = reviewRecordRepository.getStreakDays(now)
                val accuracy = reviewRecordRepository.getAccuracyInRange(rangeStart, now)

                // Activity bar chart — selalu 7 hari (mobile screen lebih cocok 7 bar
                // daripada 30 bar yang jadi terlalu kecil per bar).
                val activityByDay = reviewRecordRepository.getDailyActivity(daysBack = 7, now = now)

                // Deck & cards stats
                val decks = deckRepository.observeAllDecks().first()
                val totalDecks = decks.size
                val totalCards = decks.sumOf { it.cardCount }

                // Card status breakdown — fetch all cards lalu group in-memory
                val statusBreakdown = computeCardStatusBreakdown(decks.map { it.id })

                _uiState.value = StatsUiState.Success(
                    period = period,
                    totalReviews = totalReviews,
                    streakDays = streak,
                    totalDecks = totalDecks,
                    totalCards = totalCards,
                    accuracyPercent = accuracy * 100.0,
                    activityByDay = activityByDay,
                    cardsByStatus = statusBreakdown,
                )
            } catch (e: Exception) {
                _uiState.value = StatsUiState.Error(
                    e.message ?: "Gagal memuat statistik",
                )
            }
        }
    }

    /**
     * Hitung breakdown status kartu dari semua deck.
     *
     * Klasifikasi:
     *   - New:      repetitions = 0 (belum pernah benar)
     *   - Learning: repetitions >= 1 AND intervalDays < MASTERED_THRESHOLD_DAYS
     *   - Mastered: repetitions >= 1 AND intervalDays >= MASTERED_THRESHOLD_DAYS
     *
     * Threshold 21 hari = konvensi Anki/SuperMemo (kartu dianggap stabil
     * di long-term memory setelah interval review 3 minggu).
     */
    private suspend fun computeCardStatusBreakdown(deckIds: List<Long>): CardStatusBreakdown {
        if (deckIds.isEmpty()) return CardStatusBreakdown()

        var newCount = 0
        var learningCount = 0
        var masteredCount = 0

        // Loop semua deck, fetch cards-nya (flow.first() = snapshot)
        for (deckId in deckIds) {
            val cards = cardRepository.observeCardsByDeck(deckId).first()
            for (card in cards) {
                when {
                    card.reviewState.repetitions == 0 -> newCount++
                    card.reviewState.intervalDays >= MASTERED_THRESHOLD_DAYS -> masteredCount++
                    else -> learningCount++
                }
            }
        }

        return CardStatusBreakdown(
            newCount = newCount,
            learningCount = learningCount,
            masteredCount = masteredCount,
        )
    }

    private companion object {
        /** Interval (hari) di mana kartu dianggap "Mastered". Konvensi Anki: 21. */
        const val MASTERED_THRESHOLD_DAYS = 21
    }
}
package com.example.neurodeck.presentation.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.Deck
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.repository.ReviewRecordRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * ViewModel untuk Stats Tab — REACTIVE (Sprint 3 upgrade).
 *
 * PERUBAHAN dari versi snapshot:
 *   Dulu: loadStats() dipanggil sekali di init pakai .first() snapshot.
 *         Stats TIDAK update saat user review kartu — harus re-enter tab.
 *   Sekarang: observe deckRepository.observeAllDecks() sebagai reactive trigger.
 *
 * Kenapa observeAllDecks() jadi trigger yang tepat:
 *   - Query `selectAllWithCardCount` melakukan LEFT JOIN CardEntity
 *   - SQLDelight invalidate Flow di level TABEL (bukan row diff)
 *   - Saat user review kartu → CardEntity ter-update (intervalDays, repetitions,
 *     easeFactor, dueDate) → Flow re-emit → Stats recompute OTOMATIS
 *   - ReviewRecord row baru juga ter-insert sebelum card update, jadi suspend
 *     reads (countReviewsInRange dll) ikut pick up data terbaru
 *
 * Arsitektur Flow:
 *   combine(observeAllDecks, _period)        ← trigger: cards change ATAU period change
 *     .flatMapLatest { computeStats(...) }   ← recompute, cancel hitungan lama kalau ada emit baru
 *     .catch { Error }
 *     .stateIn(WhileSubscribed)              ← hot StateFlow, stop saat UI tidak observe
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StatsViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
) : ViewModel() {

    // Period filter sebagai StateFlow — ganti value → re-trigger compute
    private val _period = MutableStateFlow(StatsPeriod.DEFAULT)

    val uiState: StateFlow<StatsUiState> =
        combine(
            deckRepository.observeAllDecks(),
            _period,
        ) { decks, period -> decks to period }
            .flatMapLatest { (decks, period) ->
                flow {
                    emit(computeStats(decks, period))
                }
            }
            .catch { e ->
                emit(StatsUiState.Error(e.message ?: "Gagal memuat statistik"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = StatsUiState.Loading,
            )

    /**
     * Ganti period filter. Update _period → combine re-fire → recompute.
     */
    fun changePeriod(newPeriod: StatsPeriod) {
        _period.value = newPeriod
    }

    /**
     * Compute semua stats untuk period tertentu. Suspend karena baca repository.
     *
     * Dipanggil ulang setiap kali trigger Flow emit (cards berubah / period ganti).
     */
    private suspend fun computeStats(
        decks: List<Deck>,
        period: StatsPeriod,
    ): StatsUiState {
        val now = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()

        // Tentukan range start berdasarkan period
        val rangeStart: Instant = if (period.days != null) {
            val startDate = now.toLocalDateTime(tz).date
                .minus(period.days.toLong() - 1, DateTimeUnit.DAY)
            startDate.atStartOfDayIn(tz)
        } else {
            Instant.fromEpochMilliseconds(0L)
        }

        val totalReviews = reviewRecordRepository.countReviewsInRange(rangeStart, now)
        val streak = reviewRecordRepository.getStreakDays(now)
        val accuracy = reviewRecordRepository.getAccuracyInRange(rangeStart, now)
        val activityByDay = reviewRecordRepository.getDailyActivity(daysBack = 7, now = now)

        val totalDecks = decks.size
        val totalCards = decks.sumOf { it.cardCount }
        val statusBreakdown = computeCardStatusBreakdown(decks.map { it.id })

        return StatsUiState.Success(
            period = period,
            totalReviews = totalReviews,
            streakDays = streak,
            totalDecks = totalDecks,
            totalCards = totalCards,
            accuracyPercent = accuracy * 100.0,
            activityByDay = activityByDay,
            cardsByStatus = statusBreakdown,
        )
    }

    /**
     * Hitung breakdown status kartu dari semua deck.
     *
     * Klasifikasi:
     *   - New:      repetitions = 0 (belum pernah benar)
     *   - Learning: repetitions >= 1 AND intervalDays < MASTERED_THRESHOLD_DAYS
     *   - Mastered: repetitions >= 1 AND intervalDays >= MASTERED_THRESHOLD_DAYS
     */
    private suspend fun computeCardStatusBreakdown(deckIds: List<Long>): CardStatusBreakdown {
        if (deckIds.isEmpty()) return CardStatusBreakdown()

        var newCount = 0
        var learningCount = 0
        var masteredCount = 0

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
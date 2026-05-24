package com.example.neurodeck.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.repository.CardRepository
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.repository.ReviewRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * ViewModel untuk Home Tab.
 *
 * Tugas:
 *   1. Observe stream `DeckRepository.observeAllDecks()` untuk recent decks.
 *   2. Fetch (one-shot) due count, reviewedToday, streak — refresh saat
 *      data deck berubah (cheaper than another flow).
 *   3. Compute greeting & tips lokal (no IO).
 *   4. Expose [HomeUiState] sebagai StateFlow.
 *
 * Catatan strategy: kita observe DECK flow (reactive), tapi STATS (due,
 * streak, reviewedToday) cuma di-fetch sekali setiap deck flow emit.
 * Trade-off:
 *   - Stats tidak super real-time (kalau user review kartu di session lain
 *     sambil Home tab terbuka, angka mungkin telat update).
 *   - TAPI: simpel, no flow combine complexity, dan dalam praktik user akan
 *     leave Home → study session → balik ke Home → recompute saat re-enter.
 *
 * Alternative (overkill untuk Sprint 2):
 *   - Subscribe ke review flow juga, combine 3 source via combine{}.
 *   - Tapi belum ada `ReviewRecordRepository.observeXxx()` flow API, butuh
 *     refactor besar. Defer ke Sprint 3+ kalau dibutuhkan.
 */
class HomeViewModel(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    /**
     * Subscribe ke deck flow. Setiap kali list deck berubah (insert, update,
     * delete deck), re-fetch stats juga supaya semua angka selalu sinkron
     * sama list deck yang ditampilkan.
     */
    private fun observeData() {
        deckRepository.observeAllDecks()
            .catch { e ->
                _uiState.value = HomeUiState.Error(
                    e.message ?: "Gagal memuat data Home",
                )
            }
            .onEach { decks ->
                // Stats fetch async di scope yang sama supaya cancellable
                // saat ViewModel cleared.
                viewModelScope.launch {
                    refreshDashboard(decks)
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Compose seluruh Success state dari multiple sumber:
     *   - decks (parameter dari deck flow)
     *   - due cards count (fetch one-shot)
     *   - streak (fetch one-shot)
     *   - reviewedToday (fetch one-shot)
     *   - greeting (computed dari current time)
     *   - tip of the day (computed dari day of year)
     */
    private suspend fun refreshDashboard(decks: List<com.example.neurodeck.domain.model.Deck>) {
        try {
            val now = Clock.System.now()

            // Parallel fetch — kalau ada 3 query yang independent, run async.
            // Untuk Sprint 2 ini cukup sequential — angka kecil, query cepat.
            val dueCount = cardRepository.countAllDueCards(now).toInt()
            val streak = reviewRecordRepository.getStreakDays(now)
            val reviewedToday = reviewRecordRepository.getReviewedToday(now)

            _uiState.value = HomeUiState.Success(
                greeting = computeGreeting(now.toLocalDateTime(TimeZone.currentSystemDefault())),
                userName = DEFAULT_USERNAME,  // TODO Sprint 3: wire ke UserPreferencesRepository
                dueCardsCount = dueCount,
                streakDays = streak,
                reviewedToday = reviewedToday,
                recentDecks = decks.take(MAX_RECENT_DECKS),
                tipOfTheDay = computeTipOfTheDay(now.toLocalDateTime(TimeZone.currentSystemDefault())),
            )
        } catch (e: Exception) {
            _uiState.value = HomeUiState.Error(
                e.message ?: "Gagal refresh statistik",
            )
        }
    }

    private companion object {
        const val DEFAULT_USERNAME = "Mahasiswa"
        const val MAX_RECENT_DECKS = 3
    }
}

// ════════════════════════════════════════════════════════════════════════════
// PURE HELPERS — Pisah dari class supaya bisa di-test tanpa ViewModel setup
// ════════════════════════════════════════════════════════════════════════════

/**
 * Greeting dinamis berdasarkan jam lokal user.
 *
 * Mapping (24h format):
 *   - 04:00 - 10:59  → "Selamat Pagi"
 *   - 11:00 - 14:59  → "Selamat Siang"
 *   - 15:00 - 17:59  → "Selamat Sore"
 *   - 18:00 - 03:59  → "Selamat Malam"
 *
 * Pure function (no IO, deterministic) — mudah di-unit-test kalau perlu.
 */
internal fun computeGreeting(now: LocalDateTime): String = when (now.hour) {
    in 4..10 -> "Selamat Pagi"
    in 11..14 -> "Selamat Siang"
    in 15..17 -> "Selamat Sore"
    else -> "Selamat Malam"  // 18-03
}

/**
 * Tips of the day — rotate dari array TIPS_POOL berdasarkan day-of-year.
 * Tidak random supaya tip yang sama selama sehari penuh (user buka Home
 * berkali-kali tetap lihat tip yang sama → predictable, tidak menggangu).
 */
internal fun computeTipOfTheDay(now: LocalDateTime): String {
    // dayOfYear = 1-366. Modulo size pool jadi index 0..size-1.
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

// ════════════════════════════════════════════════════════════════════════════
// (End of file — launchIn imported from kotlinx.coroutines.flow above)
// ════════════════════════════════════════════════════════════════════════════
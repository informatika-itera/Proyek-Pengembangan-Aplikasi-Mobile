package com.example.neurodeck.presentation.screens.stats

/**
 * Periode filter untuk Stats Tab — chip group di atas screen.
 *
 * @property days  Berapa hari ke belakang dari hari ini. `null` untuk "All Time"
 *                 (semua data sejak app diinstall).
 * @property label Display label untuk chip UI.
 */
enum class StatsPeriod(val days: Int?, val label: String) {
    Week(7, "7 Hari"),
    Month(30, "30 Hari"),
    Quarter(90, "90 Hari"),
    AllTime(null, "Semua");

    companion object {
        val DEFAULT = Week
    }
}

/**
 * UI State untuk Stats Tab.
 *
 * Sealed interface dengan 3 variants:
 *   - Loading: initial fetch
 *   - Success: data ready untuk render
 *   - Error: gagal load (rare karena semua data lokal)
 */
sealed interface StatsUiState {

    data object Loading : StatsUiState

    /**
     * Sukses load semua stats.
     *
     * @property period            Active filter (untuk highlight chip).
     * @property totalReviews      Jumlah review dalam periode ini.
     * @property streakDays        Current streak hari (lifetime, tidak terikat periode).
     * @property totalDecks        Total deck (lifetime).
     * @property totalCards        Total kartu (lifetime).
     * @property accuracyPercent   % accuracy dalam periode ini (0-100).
     * @property activityByDay     Map<dayOffset, reviewCount> untuk bar chart.
     *                             Key 0 = hari ini, 1 = kemarin, dst.
     *                             Selalu fixed length (7 untuk weekly view).
     * @property cardsByStatus     Breakdown jumlah kartu by status (New/Learning/Mastered).
     */
    data class Success(
        val period: StatsPeriod,
        val totalReviews: Int,
        val streakDays: Int,
        val totalDecks: Int,
        val totalCards: Int,
        val accuracyPercent: Double,           // 0.0 - 100.0
        val activityByDay: Map<Int, Int>,
        val cardsByStatus: CardStatusBreakdown,
    ) : StatsUiState

    data class Error(val message: String) : StatsUiState
}

/**
 * Breakdown jumlah kartu per status SM-2:
 *   - **New**:      kartu yang belum pernah di-review (repetitions = 0).
 *   - **Learning**: kartu sedang dipelajari (1-2 successful reviews, interval pendek).
 *   - **Mastered**: kartu sudah hafal (3+ reviews, interval panjang ≥ 21 hari).
 *
 * Threshold untuk Mastered: interval >= 21 hari = standar SM-2 conventional
 * (Anki pakai threshold serupa). Bisa di-tweak Sprint 4 polish.
 */
data class CardStatusBreakdown(
    val newCount: Int = 0,
    val learningCount: Int = 0,
    val masteredCount: Int = 0,
) {
    val total: Int get() = newCount + learningCount + masteredCount
}
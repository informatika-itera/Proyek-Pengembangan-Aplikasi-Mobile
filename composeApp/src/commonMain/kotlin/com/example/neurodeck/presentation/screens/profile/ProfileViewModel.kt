package com.example.neurodeck.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neurodeck.domain.model.ThemeMode
import com.example.neurodeck.domain.model.UserProfile
import com.example.neurodeck.domain.repository.DeckRepository
import com.example.neurodeck.domain.repository.ReviewRecordRepository
import com.example.neurodeck.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * UI state untuk Profile Tab.
 *
 * @property profile        User profile (name, username, bio, avatar, memberSince).
 * @property themeMode      Current theme mode (untuk highlight di settings).
 * @property totalDecks     Achievement: jumlah deck yang dibuat user.
 * @property totalCards     Achievement: jumlah kartu total across all decks.
 * @property totalReviews   Achievement: jumlah review lifetime.
 * @property streakDays     Achievement: current streak.
 * @property isLoading      True saat initial load atau saat reset in-progress.
 * @property snackbarMessage One-shot message (consume by Screen, lalu clear).
 */
data class ProfileUiState(
    val profile: UserProfile = UserProfile(),
    val themeMode: ThemeMode = ThemeMode.System,
    val totalDecks: Int = 0,
    val totalCards: Int = 0,
    val totalReviews: Int = 0,
    val streakDays: Int = 0,
    val isLoading: Boolean = true,
    val snackbarMessage: String? = null,
)

/**
 * ViewModel untuk Profile Tab.
 *
 * State adalah combine dari:
 *   - UserPreferencesRepository.observeProfile() (reactive — auto-update saat edit)
 *   - UserPreferencesRepository.observeThemeMode() (reactive)
 *   - One-shot fetch achievement stats (di refresh saat init + after reset)
 *
 * Achievement stats di-fetch sekali di init (bukan reactive flow) karena:
 *   - Tidak ada flow API yet di ReviewRecordRepository
 *   - User biasanya tidak refresh tab Profile berkali-kali
 *   - Acceptable trade-off Sprint 2 (sama pattern dengan HomeViewModel)
 */
class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val deckRepository: DeckRepository,
    private val reviewRecordRepository: ReviewRecordRepository,
) : ViewModel() {

    // Internal mutable state — combine profile flow + achievement (one-shot).
    private val _achievements = MutableStateFlow(Achievements())
    private val _snackbar = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(
        userPreferencesRepository.observeProfile(),
        userPreferencesRepository.observeThemeMode(),
        _achievements,
        _snackbar,
    ) { profile, themeMode, achievements, snackbarMsg ->
        ProfileUiState(
            profile = profile,
            themeMode = themeMode,
            totalDecks = achievements.totalDecks,
            totalCards = achievements.totalCards,
            totalReviews = achievements.totalReviews,
            streakDays = achievements.streakDays,
            isLoading = false,
            snackbarMessage = snackbarMsg,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true),
    )

    init {
        refreshAchievements()
    }

    /**
     * Re-fetch achievement stats. Dipanggil saat init dan setelah reset data.
     * Public supaya Screen bisa pull-to-refresh (Sprint 3+ feature).
     */
    fun refreshAchievements() {
        viewModelScope.launch {
            try {
                val now: Instant = Clock.System.now()
                val decks = deckRepository.observeAllDecks().first()
                val totalDecks = decks.size
                val totalCards = decks.sumOf { it.cardCount }
                val totalReviews = reviewRecordRepository.getTotalReviews()
                val streak = reviewRecordRepository.getStreakDays(now)

                _achievements.value = Achievements(
                    totalDecks = totalDecks,
                    totalCards = totalCards,
                    totalReviews = totalReviews,
                    streakDays = streak,
                )
            } catch (e: Exception) {
                _snackbar.value = "Gagal memuat statistik: ${e.message ?: "unknown error"}"
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // SETTINGS ACTIONS
    // ════════════════════════════════════════════════════════════════════════

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setThemeMode(mode)
            } catch (e: Exception) {
                _snackbar.value = "Gagal mengganti theme: ${e.message ?: "unknown"}"
            }
        }
    }

    /**
     * Reset all data — preferences + (TODO future: database).
     *
     * Sprint 2 scope: HANYA reset preferences (profile + theme).
     * Sprint 3+ extension: tambah hapus semua decks/cards/reviews via
     * additional repository calls. Untuk sekarang, reset partial dulu.
     */
    fun resetAllData() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.resetPreferences()
                _snackbar.value = "Preferensi berhasil di-reset"
                refreshAchievements()  // re-fetch karena data mungkin berubah
            } catch (e: Exception) {
                _snackbar.value = "Gagal reset: ${e.message ?: "unknown"}"
            }
        }
    }

    /** Clear snackbar message after Screen consumed it. */
    fun consumeSnackbar() {
        _snackbar.value = null
    }

    // ════════════════════════════════════════════════════════════════════════
    // INTERNAL DATA HOLDER
    // ════════════════════════════════════════════════════════════════════════

    private data class Achievements(
        val totalDecks: Int = 0,
        val totalCards: Int = 0,
        val totalReviews: Int = 0,
        val streakDays: Int = 0,
    )
}